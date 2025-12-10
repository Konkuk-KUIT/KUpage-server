# KUpage-server

## 서비스 소개

- **배포 주소(FE)**: https://konkuk-kuit.github.io/KUpage-FE/
- **설명**: KUITee 웹 서비스의 백엔드 서버 레포지토리입니다. 
- **주요 기술 스택**
  - Backend: Java 21, Spring Boot 3.4.3, Spring Data JPA, Spring Security
  - Database & Migration: MySQL, Flyway
  - Build: Gradle
  - Infrastructure: AWS EC2, RDS, S3, CloudFront

---

### 커밋 메시지 컨벤션

- `feat` : 새로운 기능 추가
- `fix` : 버그 수정
- `docs` : 문서 수정 (README, 주석 등)
- `style` : 코드 포맷팅, 세미콜론 누락 등 **기능 변경이 없는** 수정
- `refactor` : 코드 리팩터링(기능 변화 없이 구조만 개선)
- `test` : 테스트 코드 및 리팩터링된 테스트 코드 추가/수정
- `chore` : 빌드 설정, 패키지 매니저, 기타 잡다한 작업

---

### 브랜치 네이밍 컨벤션

1. 작업 전 항상 관련 이슈를 먼저 생성합니다.
2. 아래 규칙에 맞춰 브랜치를 생성합니다.
   - 기능 개발: `feature/KUIT-00-{짧은-설명}`
   - 버그 수정: `fix/KUIT-00-{짧은-설명}`
   - 리팩터링: `refactor/KUIT-00-{짧은-설명}`

여기서 `KUIT-00`은 GitHub Issue의 이슈 번호를 의미합니다.
