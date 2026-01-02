FROM node:20-alpine

WORKDIR /app

# 의존성 설치
COPY package*.json ./
RUN npm ci --omit=dev

# 소스 복사
COPY . .

# 실행
CMD ["node", "index.js"]