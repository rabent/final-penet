# 빌드 단계
FROM node:18 as build-stage

WORKDIR /app


COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build

# 최종 단계 - Nginx에서 빌드된 파일 사용
FROM nginx:alpine as production-stage

# 기본 nginx 설정 제거 (컴포즈에서 마운트할 것이므로)
RUN rm -rf /etc/nginx/conf.d/*

# 빌드된 Vue 앱을 nginx의 서빙 디렉토리로 복사
COPY --from=build-stage /app/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]