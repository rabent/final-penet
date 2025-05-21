#!/bin/bash

# 기존 컨테이너 정리 (선택 사항)
docker-compose down

# 이미지 빌드 및 컨테이너 시작
docker-compose up -d --build

echo "애플리케이션이 시작되었습니다. http://localhost에서 접속 가능합니다."