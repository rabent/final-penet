# 🌟 Hamgaja (함가자) 

<div align="center">
  
### *"함 가자!, 여행"* - Let's go on a trip!

![hamgaja_logo](https://github.com/user-attachments/assets/b8885d6f-fd8f-4980-862c-920f7e709b6d)


**🗺️ 한국의 숨겨진 보석같은 관광지를 발견하고, 완벽한 여행 계획을 세워보세요! 🗺️**

</div>

---

## 📋 목차

- [✨ 프로젝트 소개](#-프로젝트-소개)
- [🎯 주요 기능](#-주요-기능)
- [🛠️ 기술 스택](#️-기술-스택)
- [📱 스크린샷](#-스크린샷)
- [🏗️ 프로젝트 구조](#️-프로젝트-구조)
- [👥 팀](#-팀)

---

## ✨ 프로젝트 소개

**Hamgaja**는 한국의 아름다운 관광지를 탐색하고 맞춤형 여행 계획을 세울 수 있는 웹 플랫폼입니다. "함가자(한번 가보자)"라는 친근한 표현처럼, 사용자들이 쉽고 재미있게 여행을 계획하고 새로운 여행지를 발견할 수 있도록 도와줍니다.

### 🎨 프로젝트 철학
> *"모든 여행은 발견에서 시작됩니다."*

저희는 한국의 숨겨진 보석같은 관광지들을 발굴하고, 맞춤형 여행 계획을 세울 수 있도록 도와주며 의미있는 여행을 즐길 수 있도록 노력합니다.

---

## 🎯 주요 기능

### 🔍 **관광지 검색**
- 🗺️ **지역별 검색**: 전국 17개 시도별 관광지 탐색
- 🏷️ **카테고리 필터링**: 레포츠, 숙박, 쇼핑, 음식 등 다양한 테마별 검색
- ⭐ **AI 한줄 팁**: AI 한줄팁으로 관광지 꿀팁 얻기

### 📅 **여행 계획**
- 🤖 **일정 생성**: 여행 계획과 계획 내 세부 일정들을 생성하여 계획 관리
- ⏰ **시간 관리**: 각 일정마다 날짜와 시간을 기입 하여 시간 관리
- 💰 **예산 관리**: 계획된 여행의 예산을 기입하고 계산

### 🌟 **게시판 기능**
- 📝 **여행 후기 공유**: 생생한 여행 사진과 팁 공유
- 👥 **자유로운 글쓰기**: 여행이 아니더라도 커뮤니티로서의 기능 

---

## 🛠️ 기술 스택

<div align="center">

### Frontend
![Vue](https://img.shields.io/badge/Vue.js-4FC08D?style=for-the-badge&logo=Vue.js&logoColor=white)
![Vite](https://img.shields.io/badge/Vite-646CFF?style=for-the-badge&logo=vite&logoColor=white)

### Backend
![SpringBoot.js](https://img.shields.io/badge/SpringBoot-339933?style=for-the-badge&logo=SpringBoot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-2088FF?style=for-the-badge&logo=MySQL&logoColor=white)

### DevOps & Tools
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![AWS](https://img.shields.io/badge/AWS-232F3E?style=for-the-badge&logo=amazon-aws&logoColor=white)
![Nginx](https://img.shields.io/badge/Nginx-339933?style=for-the-badge&logo=Nginx&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-2088FF?style=for-the-badge&logo=github-actions&logoColor=white)

### API & Services
![KakaoMap API](https://img.shields.io/badge/KakaoMap-FFCD00?style=for-the-badge&logo=kakao&logoColor=black)
![한국관광공사 API](https://img.shields.io/badge/한국관광공사_API-FF6B35?style=for-the-badge&logoColor=white)

</div>

---

## 📱 스크린샷

<div align="center">

### 🏠 메인 페이지
![image](https://github.com/user-attachments/assets/fe56e4e4-e239-4ca2-b0de-2bf93e3e4418)

### 🔍 관광지 검색
![image](https://github.com/user-attachments/assets/651ba8be-6663-4a49-b70d-7443e1823350)

### 📅 여행 계획
![image](https://github.com/user-attachments/assets/4f45cba5-dde2-4b29-bb28-cca2dd9d95c2)

### 📖 게시판
![image](https://github.com/user-attachments/assets/26054c9f-b6a7-4d5d-8f81-9f7f31dde9d5)


</div>

---

## 🏗️ 프로젝트 구조

```
final/
├── 📁 client/                    # Frontend (Vue3)
│   ├── 📁 src/
│   │   ├── 📁 components/        # 재사용 가능한 컴포넌트
│   │   ├── 📁 router/            # 라우터
│   │   ├── 📁 views/             # 페이지 뷰
│   │   ├── 📁 utils/             # 유틸리티 함수
│   │   └── 📁 assets/            # 정적 자원
│   └── 📄 dockerfile
├── 📁 server/                    # Backend (Spring Boot)
│   ├── 📁 src/
│   │   ├── 📁 controller/        # 컨트롤러
│   │   ├── 📁 model/             # 데이터베이스 모델
│   │   ├── 📁 config/            # 설정
│   │   ├── 📁 repository/        # 레포지토리
|   |   ├── 📁 security/          # 스프링 시큐리티
│   │   ├── 📁 service/           # 비즈니스 로직
│   │   └── 📁 util/              # 유틸리티
│   └── 📄 dockerfile
|
├── 📄 docker-compose.yml         # Docker 구성
📄 README.md                  # 프로젝트 설명서

```

---
## 👥 팀

<div align="center">

### 🌟 개발팀 🌟

<table>
  <tr>
    <td align="center">
      <a href="https://github.com/ks027">
        <img src="https://avatars.githubusercontent.com/u/63335224?v=4" width="100px;" alt=""/>
        <br />
        <sub><b>ks0927</b></sub>
      </a>
      <br />
      <sub>🚀 Backend & Frontend Developer</sub>
    </td>
    <td align="center">
      <a href="https://github.com/rabent">
        <img src="https://avatars.githubusercontent.com/u/72433154?v=4" width="100px;" alt=""/>
        <br />
        <sub><b>rabent</b></sub>
      </a>
      <br />
      <sub>💻 Backend & Frontend Developer</sub>
    </td>
  </tr>
</table>

</div>

---

<div align="center">

### 🌟 Hamgaja와 함께 특별한 여행을 시작해보세요! 🌟

**Made with ❤️ in South Korea**

[⬆️ Back to Top](#-hamgaja-함가자)

</div>
