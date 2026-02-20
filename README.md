# Payment Assignment

회원/상품/주문을 기반으로 결제를 처리하고(외부 결제 연동 포함), 결제 결과에 따라 포인트 적립 및 멤버십 정책을 적용하는 Java 17 + Spring 기반 결제/구독 샘플 프로젝트입니다.  
JWT 인증과 예외 처리, 템플릿 기반의 간단한 화면(Shop/Orders/Subscribe 등)도 함께 포함되어 있습니다.

1.결제는 카드 결제만 가능하며, 환불은 전체 환불만 구현하였습니다.
2.회원가입시 가입축하 포인트 500이 지급됩니다.

---

## 주요 기능

- **사용자/인증**
  - JWT 기반 인증 처리(필터/토큰 프로바이더/커스텀 UserDetails)
  - 인증/인가 관련 예외 처리

- **상품/주문**
  - 상품 조회 및 주문 생성/조회
  - 주문 금액 검증 및 상태 전이 관련 오류 처리

- **결제**
  - 결제 요청/응답/상세 조회
  - 결제 상태 관리(결제 엔티티 및 상태 Enum)
  - 외부 결제사 연동 클라이언트(PortOne) 구성
  - 결제 금액 불일치, 결제 미존재 등 도메인 예외 처리

- **포인트/멤버십**
  - 결제 결과 기반 포인트 적립(정책 기반)
  - 멤버십 정책(등급/구간/리워드율) 적용

- **Webhook**
  - 결제사 Webhook 수신을 위한 도메인 구성(프로젝트 구조에 포함)

- **화면(UI)**
  - 템플릿 페이지 제공: home/login/register/shop/orders/subscribe/subscriptions/points/plans 등
  - 정적 리소스(css/js/favicon) 포함

---

## 기술 스택

- **Java 17**
- **Spring MVC**
- **Spring Data JPA**
- **Jakarta EE (`jakarta.*` imports)**
- **Lombok**
- **Gradle**
- (템플릿 엔진 기반 HTML 뷰 포함)
- 설정 파일: `application.yml`, `client-api-config.yml`

---

## 프로젝트 구조(요약)

- `common/` : 공통 DTO, 공통 엔티티 등
- `domain/` : 핵심 도메인별 패키지
  - `user/`, `product/`, `order/`, `payment/`, `point/`, `membership/`, `webhook/`
- `security/` : JWT 인증/인가 구성
- `exception/` : 도메인/인증 예외 및 에러 코드
- `resources/templates/` : 화면 템플릿
- `resources/static/` : 정적 리소스
- `resources/data.sql` : 초기 데이터(멤버십 정책 등) 시드

---

## 로컬 실행 방법

### 1) 사전 준비
- JDK 17 설치
- 사용 DB 준비(MySQL 등, `application.yml`의 datasource 설정에 맞게 구성)

### 2) DB 생성(예시)
프로젝트에서 사용하는 DB 이름 예시는 다음과 같습니다.

- DB: `payment_db`

필요 시 아래처럼 생성 후, 애플리케이션 설정의 계정/비밀번호/URL을 맞춰주세요.

### 3) 초기 데이터
`resources/data.sql`에 멤버십 정책 등급/구간/적립률에 대한 초기 데이터를 콘솔에서 한번 실행이 필요합니다.
(예: NORMAL/VIP/VVIP 구간별 reward rate)

### 4) 실행


IDE에서 메인 클래스 실행:
- `PaymentAssignmentApplication`

---

## 설정 파일 안내

- `src/main/resources/application.yml`
  - 서버 포트, DB 연결, JPA 설정 등
- `src/main/resources/client-api-config.yml`
  - 외부 결제 연동(PortOne) 관련 클라이언트 설정에 사용

환경별로 값이 달라질 수 있는 항목(예: API 키/시크릿, DB 비밀번호)은 로컬 환경에서 별도 관리하는 것을 권장합니다.

---

## 예외 처리

다음과 같은 상황을 도메인 예외로 명확히 분리해 처리합니다.

- 인증 실패 / 권한 없음
- 주문 미존재 / 금액 오류
- 결제 미존재 / 결제 금액 불일치
- 상태 전이 불가
- 멤버십 정책 미존재 등
