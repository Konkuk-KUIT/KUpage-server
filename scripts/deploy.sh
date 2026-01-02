#!/bin/bash

cd /home/ubuntu

# --- discord-bot (always-on) update strategy --- #
# Bot은 Gateway 이벤트를 실시간으로 받기 때문에, 배포 때마다 불필요하게 재시작하지 않는 것이 정합성/안정성에 유리합니다.
# bot 코드가 변경되어 새로운 이미지가 pull 된 경우에만 컨테이너를 재생성(up -d)
echo "### discord-bot update check ###"

# 현재 실행 중인 discord-bot 컨테이너가 있다면, 컨테이너가 사용하는 이미지 ID(sha)를 저장
CURRENT_BOT_IMAGE_ID=""
if docker ps --format '{{.Names}}' | grep -q '^discord-bot$'; then
  CURRENT_BOT_IMAGE_ID=$(docker inspect -f '{{.Image}}' discord-bot 2>/dev/null || true)
fi

docker compose -f "$COMPOSE_FILE" pull discord-bot >/dev/null 2>&1 || true
NEW_BOT_IMAGE_ID=$(docker compose -f "$COMPOSE_FILE" images -q discord-bot 2>/dev/null | head -n 1)

if [ -z "$CURRENT_BOT_IMAGE_ID" ]; then
  echo "discord-bot is not running. starting..."
  docker compose -f "$COMPOSE_FILE" up -d discord-bot
elif [ -n "$NEW_BOT_IMAGE_ID" ] && [ "$CURRENT_BOT_IMAGE_ID" != "$NEW_BOT_IMAGE_ID" ]; then
  echo "discord-bot image changed. recreating..."
  docker compose -f "$COMPOSE_FILE" up -d --no-deps discord-bot
else
  echo "discord-bot unchanged. skip restart."
fi


# Ensure monitoring network exists (for Prometheus/Grafana/blue-green app communication)
if ! docker network ls --format '{{.Name}}' | grep -q '^monitoring$'; then
  echo "0. create monitoring network"
  docker network create monitoring
fi

IS_GREEN=$(docker ps | grep green)

if [ -z "$IS_GREEN"  ];then     # green라면

  echo "### BLUE => GREEN ###"

  echo "1. get green image"
  docker compose -f /home/ubuntu/docker-compose.yml pull green # green으로 이미지를 내려받습니다.

  echo "2. green container up"
  docker compose -f /home/ubuntu/docker-compose.yml up -d green # green 컨테이너 실행

  # connect green container to monitoring network (ignore error if already connected)
  docker network connect monitoring green 2>/dev/null || true

  while [ 1 = 1 ]; do
  echo "3. green health check..."
  sleep 3

  REQUEST=$(curl http://localhost:8081) # green으로 request
    if [ -n "$REQUEST" ]; then # 서비스 가능하면 health check 중지
            echo "health check success"
            break ;
            fi
  done;

  echo "4. reload nginx"
  sudo cp /etc/nginx/conf.d/app/nginx-green.conf /etc/nginx/nginx.conf
  sudo nginx -s reload

  echo "5. blue container down"
  docker compose -f /home/ubuntu/docker-compose.yml stop blue
else
  echo "### GREEN => BLUE ###"

  echo "1. get blue image"
  docker compose -f /home/ubuntu/docker-compose.yml pull blue

  # connect blue container to monitoring network (ignore error if already connected)
  docker network connect monitoring blue 2>/dev/null || true

  echo "2. blue container up"
  docker compose -f /home/ubuntu/docker-compose.yml up -d blue

  while [ 1 = 1 ]; do
    echo "3. blue health check..."
    sleep 3
    REQUEST=$(curl http://localhost:8080) # blue로 request

    if [ -n "$REQUEST" ]; then # 서비스 가능하면 health check 중지
      echo "health check success"
      break ;
    fi
  done;
  echo "4. reload nginx"
  sudo cp /etc/nginx/conf.d/app/nginx-blue.conf /etc/nginx/nginx.conf
  sudo nginx -s reload

  echo "5. green container down"
  docker compose -f /home/ubuntu/docker-compose.yml stop green
fi

echo "6. prune unused docker images"
sudo docker image prune -a -f