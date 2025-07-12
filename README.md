<img src="https://capsule-render.vercel.app/api?type=waving&color=BDBDC8&height=150&section=header" width="100%" >

# 서비스 설명

CQRS 패턴으로 설계하는 이커머스 플랫폼

`개발기간: 25.06.12 ~ 25.07.12 (4주)`

# 목차
1. [서비스 소개](#-서비스-소개)
2. [기술 스택](#-기술-스택)
3. [ERD 및 아키텍처](#-ERD-및-아키텍처)
4. [기술 상세 설명](#-기술-상세-설명)
4. [API 명세서](#-API-명세서)

---

# ✨ 서비스 소개
## 기획 배경

원티드 프리온보딩 챌린지에서 학습한 CQRS를 실전에 적용하기 위해 QueryDSL로 리팩터링하고 Query·Command를 분리했습니다. 동시에 CDC를 활용해 RDB와 NoSQL 간 데이터 동기화를 구현하며 CQRS 실무 역량을 기르고자 프로젝트를 기획하게 되었습니다.


## 주요기능 🔍


### 📌 상품 관련 기능
- 상품을 등록, 조회, 삭제, 수정할 수 있습니다.
- 상품의 가격, 상세설명, 판매자, 브랜드, 카테고리, 옵션 그룹, 옵션들, 이미지, 평점, 관련 상품이 같이 조회됩니다. (다중 조인, flat 조회)
- 상품의 옵션, 이미지를 수정하거나 삭제할 수 있습니다.
- 카테고리 목록을 조회할 수 있습니다.
- 특정 카테고리 목록에 속한 상품 목록을 조회할 수 있습니다.
- 메인 페이지 상품 및 카테고리 목록을 조회할 수 있습니다.
- 상품 리뷰를 등록, 조회, 삭제, 수정할 수 있습니다.

### 📌 검색 필터 기능

- 최소 가격, 최대 가격, 카테고리, 판매자, 브랜드, 판매중, 키워드를 기반으로 상품을 검색할 수 있습니다.
- 페이징이 지원됩니다.
- MongoDB 기반의 필터 조회와 PostgreSQL 기반의 필터 조회 모두 구현되어 있습니다.

### 📌 CDC 기능
- PostgreSQL의 상품, 판매자, 브랜드, 카테고리, 태그 데이터의 Write-Ahead Log (WAL) 기록을 Debezium이 모니터링합니다.
- 변경 이벤트가 발생하면 Debezium이 Kafka 이벤트로 변환합니다.
- Spring Boot 애플리케이션이 이벤트를 구독하여 DME 핸들러를 통해 변환한 후, MongoDB에 데이터를 자동 적재합니다.

# 🛠 기술 스택
<div align="center">

### Backend

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-000?style=for-the-badge&logo=apachekafka)

### Database

![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
![MongoDB](https://img.shields.io/badge/MongoDB-%234ea94b.svg?style=for-the-badge&logo=mongodb&logoColor=white)

</div>

# 📚  ERD 및 아키텍처
### 📂 ERD

![](https://i.imgur.com/YRDUJSq.png)

자세한 내용은 아래 문서를 참고해주세요.

👉 [dbdiagram.io](https://dbdiagram.io/d/6871fc71f413ba350885f650)

### 📂 아키텍처
![](https://i.imgur.com/AfmEVpS.png)

# ✏️ 기술 상세 설명
### ✔️ CQRS 설계
![](https://i.imgur.com/5FvXlmG.png)

1. 클라이언트로부터 요청을 받은 후, **RequestMapper**를 사용해 request 객체를 Query 또는 Command 객체로 변환합니다.
2. 변환된 객체는 서비스 레이어로 전달되며, Query는 QueryService에서, Command는 CommandService에서 각자의 목적에 맞게 로직을 처리합니다.
3. 이때, Query는 **MongoDB를 통해 조회 작업**을 수행하고, Command는 **PostgreSQL을 통해 쓰기 작업**을 처리합니다.

### ✔️ RDB → NoSQL CDC 데이터 동기화
![](https://i.imgur.com/kZsT7VR.png)

1. PostgreSQL에서 발생한 **쓰기 작업은 WAL(Write-Ahead Logging)을 통해 Debezium에서 감지**되며, Debezium은 이를 Kafka 이벤트로 변환 및 발행합니다.
2. Spring 애플리케이션은 Kafka 이벤트를 구독하여 **RDB 데이터를 Document 모델로 변환**하고, 여러 handler를 거쳐 MongoDB에 최종 적재합니다.

# 💻 API 명세서
API 엔드포인트, 요청·응답 형식 등 자세한 내용은 아래 문서를 참고해주세요.

👉 [API SPEC 문서 보기](./api_spec.md)

	

