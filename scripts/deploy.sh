#!/bin/bash

IS_GREEN=$(docker ps | grep green)

if [ -z "$IS_GREEN"  ];then # green라면

  echo "### BLUE => GREEN ###"

  echo "1. get green image"
  docker compose -f /home/ubuntu/docker-compose.yml pull green # green으로 이미지를 내려받습니다.

  echo "2. green container up"
  docker compose -f /home/ubuntu/docker-compose.yml up -d green # green 컨테이너 실행
  while [ 1 = 1 ]; do
  echo "3. green health check..."
  sleep 3

  REQUEST=$(curl http://kuitee.p-e.kr:8081) # green으로 request
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

  echo "2. blue container up"
  docker compose -f /home/ubuntu/docker-compose.yml up -d blue

  while [ 1 = 1 ]; do
    echo "3. blue health check..."
    sleep 3
    REQUEST=$(curl http://kuitee.p-e.kr:8080) # blue로 request

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
sudo docker image prune -f