# MOMO FASHION - GhostTracker 데이터 수집용 쇼핑몰

GhostTracker 캡스톤 프로젝트를 위한 패션 쇼핑몰입니다.
비회원/회원의 행동 데이터 수집 대상 사이트로 활용됩니다.

## 기술 스택

| 구분 | 기술 |
|------|------|
| Frontend | React 18, React Router v6 |
| Backend | Spring Boot 3.2, Spring Security, JPA |
| DB | MySQL 8 |
| 인증 | JWT |

## 구현된 기능

### 사용자
- 회원가입 / 로그인 (JWT)
- 상품 목록 (카테고리 필터, 키워드 검색, 정렬, 페이지네이션)
- 상품 상세 (이미지 갤러리, 사이즈/색상 선택, 리뷰)
- 장바구니 (추가/수량 변경/삭제)
- 주문 / 모의 결제
- 주문 내역 조회 / 취소
- 마이페이지 (프로필 수정)
- 리뷰 작성/삭제

### 관리자
- 대시보드 (통계)
- 상품 등록/수정/삭제 (이미지 업로드)
- 주문 상태 관리

### GhostTracker SDK 연동 준비
`frontend/public/index.html`의 `GHOSTTRACKER_SDK_START ~ END` 사이에 SDK 스크립트를 삽입하면 됩니다.

---

## 실행 방법

### 1. MySQL DB 생성
```sql
CREATE DATABASE ghosttracker_shop CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 백엔드 설정
`backend/src/main/resources/application.properties`에서 DB 비밀번호 수정:
```properties
spring.datasource.password=your_password
```

### 3. 백엔드 실행
```bash
cd backend
./mvnw spring-boot:run
```
첫 실행 시 샘플 카테고리/상품/계정이 자동 생성됩니다.

**기본 계정**
- 관리자: `admin@ghost.com` / `admin1234!`
- 테스트: `test@test.com` / `test1234!`

### 4. 프론트엔드 실행
```bash
cd frontend
npm install
npm start
```

브라우저에서 http://localhost:3000 접속

---

## API 엔드포인트 요약

| 메서드 | 경로 | 설명 |
|--------|------|------|
| POST | /api/auth/register | 회원가입 |
| POST | /api/auth/login | 로그인 |
| GET  | /api/products | 상품 목록 |
| GET  | /api/products/:id | 상품 상세 |
| GET  | /api/cart | 장바구니 조회 |
| POST | /api/cart/items | 장바구니 추가 |
| POST | /api/orders | 주문 생성 |
| GET  | /api/admin/dashboard | 관리자 통계 |

## 프로젝트 구조

```
GhostTracker/
├── backend/
│   └── src/main/java/com/ghosttracker/shop/
│       ├── entity/         # JPA 엔티티
│       ├── repository/     # Spring Data JPA
│       ├── service/        # 비즈니스 로직
│       ├── controller/     # REST API
│       ├── dto/            # Request/Response DTO
│       ├── security/       # JWT 인증
│       ├── config/         # Security, CORS 설정
│       └── init/           # 초기 데이터
└── frontend/
    └── src/
        ├── api/            # Axios API 호출
        ├── context/        # AuthContext, CartContext
        ├── components/     # 공통 컴포넌트
        ├── pages/          # 페이지 컴포넌트
        └── styles/         # 전역 CSS
```
