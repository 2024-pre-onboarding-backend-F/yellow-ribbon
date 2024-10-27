<div align="center">

# Yellow-Ribbon

### ✨ Backend TechStack ✨
![Java](https://img.shields.io/badge/-Java-FF7800?style=flat&logo=Java&logoColor=white)
![SpringBoot](https://img.shields.io/badge/-SpringBoot-6DB33F?style=flat&logo=SpringBoot&logoColor=white)
![SpringDataJPA](https://img.shields.io/badge/SpringDataJpa-236DB33F?style=flat&logo=spring&logoColor=white)
![SpringSecurity](https://img.shields.io/badge/Spring%20Security-6DB33F?style=flat&logo=Spring%20Security&logoColor=white)
![SpringBatch](https://img.shields.io/badge/SpringBatch-%236DB33F.svg?style=for-the-flat&logo=spring&logoColor=white)
![Querydsl](https://img.shields.io/badge/Querydsl-EB5424?style=flat&logo=auth0&logoColor=white)
</br>
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat&logo=MySQL&logoColor=white)
![Redis](https://img.shields.io/badge/redis-FF4438?style=flat&logo=redis&logoColor=white)
![Amazonrds](https://img.shields.io/badge/amazonRDS-527FFF?style=flat&logo=amazonrds&logoColor=white)
![Amazon ec2](https://img.shields.io/badge/amazonEC2-FF9900?style=flat&logo=amazonec2&logoColor=white)
![Docker](https://img.shields.io/badge/docker-2496ED?style=flat&logo=docker&logoColor=white)
![Githubactions](https://img.shields.io/badge/githubactions-2088FF?style=flat&logo=githubactions&logoColor=white)

### 🍀 서비스 개요 🍀
공공데이터를 활용한 위치 기반 지역 음식점 조회 및 추천 서비스 옐로리본(Yellow Ribbon) </br>
옐로리본에서 음식점 정보를 조회하고, 리뷰를 남겨보세요! </br>
유저들에게 인기 많은 음식점이 궁금하시나요? 인기 맛집 및 인기 급상승 맛집도 찾아볼 수 있습니다. </br>
뿐만 아니라, 사용자 위치 기반 맛집 조회 및 추천 서비스까지! </br>

### ⏰ 개발 기간 ⏰
2024.10.18 ~ 서비스 운영중 </br>
(V2) 2024.10.01 - 2024.10.18 </br>
(V1) 2024.08.31 - 2024.09.03 </br>
</div>

</br>

### 🙋‍♀️ 팀원
| 이름 | Github | 담당 |
|------|--------|------|
| 김영주 | [K-0joo](https://github.com/K-0joo) | 회원 API 전반 |
| 정은경 | [jeongeungyeong](https://github.com/jeongeungyeong) | 데이터 파이프라인 전반 |
| 이지원 | [jw427](https://github.com/jw427) | 시군구 목록, 맛집 상세 정보, 맛집 평가 생성, 인기 맛집 조회 API |
| 정진희 | [rhaehf](https://github.com/rhaehf) | 사용자 위치 기반 맛집 조회, 추천 API |

## [목차]
1. [📁 ERD 및 디렉터리 구조](#-erd-및-디렉터리-구조)
2. [🖥️ 서비스 아키텍처 및 배포](#%EF%B8%8F-서비스-아키텍처-및-배포)
3. [📑 구현 내용](#-구현-내용)
4. [💌 API 명세](#-api-명세)
5. [🤔 고민 흔적](#-고민-흔적)
6. [🙀 트러블 슈팅](#-트러블-슈팅)

## 📁 ERD 및 디렉터리 구조
<details>
<summary><strong>ERD</strong></summary>
<div markdown="1">
<img width="718" alt="맛집 erd" src="https://github.com/user-attachments/assets/5dc2ea78-b6e7-445c-b8f2-5f978205016d" width="90%" height="100%">
</br>
</details>

<details>
<summary><strong>디렉터리 구조</strong></summary>
<div markdown="1">
  
```
src
├───main
│   ├───generated
│   │   └───wanted
│   │       └───ribbon
│   │           ├───datapipe
│   │           │   └───domain
│   │           │           QGenrestrt.java
│   │           │           
│   │           ├───store
│   │           │   └───domain
│   │           │           QCity.java
│   │           │           QReview.java
│   │           │           QStore.java
│   │           │           
│   │           └───user
│   │               └───domain
│   │                       QRefreshToken.java
│   │                       QUser.java
│   │                       
│   ├───java
│   │   └───wanted
│   │       └───ribbon
│   │           │   RibbonApplication.java
│   │           │   
│   │           ├───datapipe
│   │           │   ├───component
│   │           │   │       DataFetchTasklet.java
│   │           │   │       DataPipeJobScheduler.java
│   │           │   │       DataPipeTasklet.java
│   │           │   │       JobCompletionNotificationListener.java
│   │           │   │       
│   │           │   ├───config
│   │           │   │       AppConfig.java
│   │           │   │       BatchConfig.java
│   │           │   │       DataPipeJobConfig.java
│   │           │   │       OpenApiJobConfig.java
│   │           │   │       
│   │           │   ├───controller
│   │           │   │       OpenApiController.java
│   │           │   │       
│   │           │   ├───domain
│   │           │   │       Genrestrt.java
│   │           │   │       
│   │           │   ├───dto
│   │           │   │       GenrestrtApiResponse.java
│   │           │   │       GyeongGiList.java
│   │           │   │       RawData.java
│   │           │   │       
│   │           │   ├───mapper
│   │           │   │       RawDataRowMapper.java
│   │           │   │       StoreDataRowMapper.java
│   │           │   │       
│   │           │   ├───repository
│   │           │   │       GenrestrtRepository.java
│   │           │   │       
│   │           │   └───service
│   │           │           DataProcessor.java
│   │           │           GenrestrtService.java
│   │           │           RawDataSaveService.java
│   │           │           RawDataService.java
│   │           │           
│   │           ├───exception
│   │           │   │   BadRequestException.java
│   │           │   │   BaseException.java
│   │           │   │   ConflictException.java
│   │           │   │   ErrorCode.java
│   │           │   │   ErrorResponse.java
│   │           │   │   ForbiddenException.java
│   │           │   │   GenrestrtException.java
│   │           │   │   InternalServerException.java
│   │           │   │   NotFoundException.java
│   │           │   │   
│   │           │   └───handler
│   │           │           GlobalExceptionHandler.java
│   │           │           
│   │           ├───global
│   │           │   ├───config
│   │           │   │       QueryDslConfig.java
│   │           │   │       RedisConfig.java
│   │           │   │       SwaggerConfig.java
│   │           │   │       
│   │           │   └───response
│   │           │           ResponseCode.java
│   │           │           ResponseDto.java
│   │           │           
│   │           ├───store
│   │           │   ├───controller
│   │           │   │       CityController.java
│   │           │   │       ReviewController.java
│   │           │   │       StoreController.java
│   │           │   │       
│   │           │   ├───domain
│   │           │   │       Category.java
│   │           │   │       City.java
│   │           │   │       Review.java
│   │           │   │       Store.java
│   │           │   │       
│   │           │   ├───dto
│   │           │   │       CityResponseDto.java
│   │           │   │       CreateReviewRequestDto.java
│   │           │   │       CreateReviewResponseDto.java
│   │           │   │       PopularStoreListResponseDto.java
│   │           │   │       ReviewListResponseDto.java
│   │           │   │       RisingPopularStoreListResponseDto.java
│   │           │   │       RisingStoreResponseDto.java
│   │           │   │       StoreDetailResponseDto.java
│   │           │   │       StoreListResponseDto.java
│   │           │   │       StoreResponseDto.java
│   │           │   │       
│   │           │   ├───repository
│   │           │   │       CityRepository.java
│   │           │   │       ReviewRepository.java
│   │           │   │       StoreRepository.java
│   │           │   │       StoreRepositoryCustom.java
│   │           │   │       StoreRepositoryImpl.java
│   │           │   │       
│   │           │   └───service
│   │           │           CityService.java
│   │           │           ReviewService.java
│   │           │           StoreService.java
│   │           │           
│   │           └───user
│   │               ├───config
│   │               │   │   TokenAuthenticationFilter.java
│   │               │   │   TokenProvider.java
│   │               │   │   WebSecurityConfig.java
│   │               │   │   
│   │               │   └───jwt
│   │               │           JwtProperties.java
│   │               │           
│   │               ├───controller
│   │               │       TokenApiController.java
│   │               │       UserApiController.java
│   │               │       UserOauthController.java
│   │               │       
│   │               ├───domain
│   │               │       RefreshToken.java
│   │               │       SocialType.java
│   │               │       User.java
│   │               │       
│   │               ├───dto
│   │               │       CreateAccessTokenResponse.java
│   │               │       CreateRefreshTokenRequest.java
│   │               │       LoginRequest.java
│   │               │       LoginResponse.java
│   │               │       ProfileRequest.java
│   │               │       ProfileResponse.java
│   │               │       SignUpRequest.java
│   │               │       SignUpResponse.java
│   │               │       UpdateProfileRequest.java
│   │               │       UpdateProfileResponse.java
│   │               │       
│   │               ├───repository
│   │               │       RefreshTokenRepository.java
│   │               │       UserRepository.java
│   │               │       
│   │               └───service
│   │                       CustomUserDetail.java
│   │                       KakaoOAuth2UserService.java
│   │                       RefreshTokenService.java
│   │                       TokenService.java
│   │                       UserDetailService.java
│   │                       UserOauthService.java
│   │                       UserService.java
│   │                       
│   └───resources
│       │   application-secret.yml
│       │   application.yml
│       │   
│       ├───static
│       └───templates
└───test
    └───java
        └───wanted
            └───ribbon
                │   RibbonApplicationTests.java
                │   
                └───user
                    └───config
                        └───jwt
                                JwtFactory.java
```

</details>

## 🖥️ 서비스 아키텍처 및 배포
### 서비스 아키텍처
<img alt="서비스 아키텍쳐" src="https://github.com/user-attachments/assets/ba43a837-28ff-432b-9869-03f82e02c582" width="80%" height="100%">

### 배포 이미지
<img alt="배포gif" src="https://github.com/user-attachments/assets/cc6bd6c5-485b-4ed7-8824-bdd18e41ab84" width="80%" height="100%">
<img alt="Swagger API 캡쳐 전체" src="https://github.com/user-attachments/assets/0a5f3657-2057-4a29-9dea-d9c0f7188f73" width="80%" height="100%">

## 📑 구현 내용
### 로그인/회원가입
- 일반 로그인/회원가입 기능
- 카카오 로그인/회원가입 기능
- Access 및 Refresh Token 생성
- 경도 및 위도, 점심 추천 프로필(정보 업데이트/조회) 기능

### 데이터 파이프라인
- 데이터 파이프라인은 자동화 시스템을 통해 처리
- 경기도 공공데이터 수집 및 원본 데이터 저장
- 원본 데이터 전처리
- 운영테이블로 가공 데이터 저장
    - 신규 데이터는 JDBC BATCH 도입
    - 기존 데이터는 JPA UPDATE 도입
- 정보의 최신화를 위해 스케줄러 도입
    - 공공데이터 수집 JOB: 매주 월요일 새벽 3시
    - 데이터 전처리 및 운영 JOB: 매주 월요일 새벽 5시

### 시군구 목록 조회
- 시군구 모든 목록 조회

### 맛집 조회
- 회원은 맛집 목록 조회 가능
- 맛집 상세 정보 조회 시 맛집의 모든 필드와 해당 맛집의 평가 목록 조회
- 평가 목록 조회 시 작성한 회원의 계정명, 평가 점수, 평가 내용 출력

### 인기 맛집 및 인기 급상승 맛집 조회
- 회원은 인기 맛집 및 인기 급상승 맛집 목록 조회 가능
- 인기 맛집의 기준은 리뷰 개수와 평점
- 전체 인기 맛집 외에 카테고리별, 지역별 인기 맛집 조회 가능
- 인기 급상승 맛집 기준은 전날 생성된 리뷰 개수
- 인기 맛집과 인기 급상승 맛집은 자주 조회되고 값이 자주 변경되지 않는 데이터이기 때문에 조회 성능 향상을 위해 redis cache를 적용
- 정보의 최신화를 위해 캐시 만료기간을 다음날 자정까지로 설정

### 사용자 위치 기반 맛집 조회
- 경도(lon)와 위도(lat)로 설정된 사용자의 위치를 기준으로, 주어진 범위(range) 내에서 검색하고, 정렬하여 맛집의 개수와 모든 필드를 응답
- 정렬 기준
    - 거리순: 사용자 위치와 맛집 사이의 거리가 짧은 순서로 정렬 (기본 정렬 값)
    - 평점순: 맛집의 평점이 높은 순서로 정렬
- 주어진 범위 내에서 맛집을 검색하기 위해 공간 데이터 타입 `Point`와 공간 함수를 활용

### 사용자 위치 기반 맛집 추천
- 사용자 위치에서 1km 반경 내의 맛집을 검색하고, 평점과 리뷰 개수로 계산한 식의 기준을 넘으면 추천
- 평점과 리뷰 개수를 활용한 계산식을 적용해 추천 맛집을 필터링
    - 계산식: `평점 * 0.7 + (리뷰 개수 / 3000) * 0.3`
    - 리뷰 개수가 3,000개를 초과할 경우, `(리뷰 개수 / 3000)`는 1로 처리
    - 계산식 결과가 3.0 이상인 경우에만 추천

## 💌 API 명세
### [🔗 Postman API 문서](https://documenter.getpostman.com/view/21360094/2sAY4rEQbJ)

| **대분류** | **기능** | **Method** | **URL** | **담당자** |
| --- | --- | :---: | :--- | --- |
| 유저 | 사용자 프로필 업데이트 | `PUT` | `/api/users/settings/{userId}` | 김영주 |
| 유저 | 토큰 생성 | `POST` | `/api/users/token` | 김영주 |
| 유저 | 사용자 회원가입 | `POST` | `/api/users/signup` | 김영주 |
| 유저 | 사용자 로그인 | `POST` | `/api/users/login` | 김영주 |
| 유저 | 사용자 프로필 확인 | `GET` | `/api/users/profile/{userId}` | 김영주 |
| 유저 | 사용자 카카오톡 회원가입 | `GET` | `/api/oauth/kakao/login` | 김영주 |
| 맛집 조회 | 시군구 목록 조회 | `GET` | `/api/city` | 이지원 |
| 맛집 조회 | 맛집 상세 정보 조회 | `GET` | `/api/stores/:storeId` | 이지원 |
| 평가 | 맛집 평가 생성 | `POST` | `/api/reviews/:storeId` | 이지원 |
| 맛집 조회 | 인기 맛집 조회 | `GET` | `/api/stores/popular` | 이지원 |
| 맛집 조회 | 인기 급상승 맛집 조회 | `GET` | `/api/stores/rising` | 이지원 |
| 맛집 조회 | 사용자 위치 기반 맛집 조회 | `GET` | 거리순 : `/api/stores?lon=127.1108000763&lat=37.3503950812&range=1` <br/>평점순 : `/api/stores?lon=127.1108000763&lat=37.3503950812&range=1&orderBy=rating` | 정진희 |
| 맛집 조회 | 사용자 위치 기반 맛집 추천 | `GET` | `/api/stores/recommend?lon=127.1108000763&lat=37.3503950812` | 정진희 |

## 🤔 고민 흔적
### 김영주
<details>
<summary><strong>💭 카카오톡 Token을 저장하는 것 vs 직접 Token을 생성하는 것 중 어떤게 좋을까?</strong></summary>
<div markdown="1">

### 🗨 카카오톡 API만 사용

- **장점**
    - 카카오 측에서 발행한 토큰은 이미 인증과 권한 부여가 완료된 상태이기 때문에, 별도의 인증 로직을 구현하지 않고 그대로 사용할 수 있다.
    - OAuth 2.0과 같은 외부 서비스 토큰을 사용하면 보안성이 더 높고, 인증 서버를 운영할 필요가 없다. 또한, 카카오에서 제공하는 보안 정책(토큰 갱신, 만료 등)이 그대로 적용된다.
- **단점**
    - 카카오의 토큰 관리 정책에 의존해야 하므로, 토큰 갱신 주기나 만료 정책 등에서 유연성이 떨어진다.
    - 카카오 외의 다른 로그인 수단을 사용할 경우(예: 자체 로그인), 다른 방식의 토큰 관리가 필요해질 수 있다.
    - 카카오에서 제공하는 토큰은 API 사용에 적합하지만, 내부 시스템에서 여러 서비스 간의 인증이 필요하다면 추가 로직이 복잡해질 수 있다.

---

### 💙 JWT를 이용한 직접 Token 생성

- **장점**
    - 독립적으로 사용 가능한 인증 수단이며, 사용자 정보(예 : 권한, 유효 기간)를 자체적으로 포함할 수 있다. 그래서 마이크로서비스 환경에서 빠르게 인증을 처리할 수 있다.
    - 카카오 외의 다양한 로그인 수단을 통합적으로 관리하기 용이하며, 토큰 발급 및 만료 정책을 유연하게 설정할 수 있다.
    - Refresh 토큰을 통해 토큰 만료 시 새로운 Access 토큰을 쉽게 발급할 수 있으며, 이를 자동으로 관리할 수 있다.
- **단점**
    - 인증 서버를 직접 관리해야 하므로, 추가적인 보안 설정(예: 토큰 암호화, 저장소 관리)이 필요하다.
    - 토큰 관리와 보안에 대한 추가적인 책임이 생기며, 잘못된 설정 시 보안 취약점이 생길 수 있다.

---

### ➕ 두 방식 혼합

- 카카오 API에서 발급된 토큰을 사용해 초기 인증을 진행하고, 이후에는 자체적으로 발급한 JWT를 관리하는 방식이 있다.
- 카카오 API와 통합과 내부적인 사용자 인증을 모두 안전하게 처리할 수 있다.

---

### 👩‍💻 ChatGPT가 알려준 추가 의견

- 일반적으로 외부 OAuth 인증을 사용할 때 외부 Access/Refresh 토큰을 사용하는 것이 보안과 편의성을 높일 수 있다.
- 내부적으로 여러 인증 수단을 관리해야 하거나, 토큰 사용 범위가 더 넓은 경우에는 JWT를 사용하는 것이 유리할 수 있다.
- 따라서 카카오 토큰을 사용하여 OAuth 인증을 완료한 후, 내부 시스템에서는 자체 JWT 토큰을 발급하여 관리하는 방식이 보안성을 유지하면서 유연한 토큰 관리를 가능하게 한다.
</details>

<details>
<summary><strong>💭 카카오톡 로그인 시 패스워드 정보는 주지 않는다. 어떻게 관리해야 할까?</strong></summary>
<div markdown="1">

### 🗨 카카오톡 API 계정만 비밀번호 비워두기

- 카카오 API로 로그인하는 사용자의 경우, 별도의 비밀번호 없이 소셜 로그인만으로 인증이 이루어지도록 설정할 수 있다. 이때, 소셜 로그인으로 들어온 사용자 정보를 따로 관리하고, 비밀번호는 비워둔다.
- 소셜 로그인과 일반 로그인을 모두 사용해야 할 경우에는 구분해 관리하는 것이 필요하다. 예를 들어, 카카오 로그인으로 처음 회원가입 시 소셜 로그인 플래그를 설정하고, 자체 로그인 시 비밀번호를 필수적으로 입력받는 식으로 구현할 수 있다.

---

### 📱 외부 OAuth 사용자에게 비밀번호 요구

- 외부 OAuth 로그인 성공 후 비밀번호 설정 페이지로 리다이렉트하여 사용자가 직접 비밀번호를 설정하도록 유도한다. 사용자는 로그인 시 카카오 소셜 로그인 외에도 자체 로그인 시스템을 이용할 수 있도록 선택의 폭을 넓힐 수 있다.

---

### 🎨 직접 사용자의 비밀번호를 랜덤으로 생성 후 저장

- **장점**
    - 통일된 관리 : 하나의 인증 체계로 관리가 통일될 수 있다.
    - 이중 인증 가능 : 소셜 로그인 뿐만 아니라 비밀번호 기반의 인증을 원하는 경우 유연하게 전환이 가능하다. 자체 로그인이 가능하다.
- **단점 및 주의점**
    - 보안 문제 : 랜덤 생성된 비밀번호를 만약 전달할 경우 어떻게 전달하고 관리할지가 문제이다. 비밀번호는 절대 노출되어서는 안 되며, 전달 과정에서의 보안이 매우 중요하다.
    - 의미 없는 비밀번호 관리 : 소셜 로그인을 사용한 사용자에게 비밀번호가 필요하지 않을 경우, 굳이 랜덤 비밀번호를 생성하고 저장하는 것은 불필요한 작업일 수 있다. 사용자 인증은 이미 카카오에서 보장되므로, 불필요한 복잡성을 더할 가능성이 있다.
</details>

### 정은경
<details>
<summary><strong>💭 대용량 데이터 처리 시 JDBC Batch Insert 만으로 충분할까?</strong></summary>
<div markdown="1">

초기에 데이터 파이프라인을 구축할 때, 대용량 데이터를 분석하고 처리해야 했기 때문에 JDBC Batch Insert를 활용했다. 대량의 데이터를 빠르게 삽입할 수 있어 효율적이라고 판단했기 때문이다.

하지만 데이터 파이프라인 자동화 이후, JDBC를 사용한 기존 데이터 업데이트 방식에서 비효율성이 드러났다.  각 레코드의 모든 필드를 일일이 비교하고 업데이트해야 했기 때문이다. 이로 인해 불필요한 데이터베이스 접근, 복잡한 코드 로직, 대량 데이터 처리 시 성능 저하 등의 문제가 예상됐다. 

그래서 다른 로직을 고민하던 중 JPA 변경 감지 기능을 전략적으로 활용하기로 했다. JPA는 엔티티의 상태 변화를 자동으로 추적하여 필요한 업데이트만 수행한다. 이를 통해 코드 복잡성 감소, 최적화된 쿼리 생성, 개발 생산성 향상 등의 이점을 얻을 수 있었다. 또한 JPA의 유니크 키 기능으로 데이터 중복 삽입 문제도 해결했다. 

JDBC Batch Insert와 JPA 변경 감지를 결합한 하이브리드 접근 방식을 채택했다:

- JDBC: 새로운 대량 데이터 삽입에 활용
- JPA: 기존 데이터 업데이트에 활용

초기 JDBC Batch만 적용했을 때의 데이터 처리 시간이다.
![image](https://github.com/user-attachments/assets/4e8015f4-c6bf-40ae-b67a-6c7fa8c3b183)


그리고 하이브리드 방식을 적용한 후의 처리 시간이다. 
![image](https://github.com/user-attachments/assets/0b1be6e6-73b0-405f-892b-1eaf1796440b)

**데이터 처리 시간이 약 23초에서 15초로 약 34.8% 단축**된 것을 확인할 수 있다.

이는 중국집 카테고리 하나만을 대상으로 한 테스트 결과이다. 실제 서비스에서는 10개 이상의 다양한 카테고리를 수집하므로, 이 최적화 전략을 전체 시스템에 적용할 경우 성능 개선 효과가 더욱 극대화될 것으로 예상된다.
</details>

<details>
<summary><strong>💭 데이터 자동화 시간은 어떻게 설정하는 게 좋을까?</strong></summary>
<div markdown="1">

### 안건
- 서버부하를 막으면서 데이터의 신선도 유지하는 법

### 여러 가지 해결법
- 공공데이터 갱신 주기에 따라 데이터 자동화

### 선택한 방법
1. 공공데이터의 일주일 갱신 주기를 참고해 서비스도 1주일 단위로 설정
2. 대부분이 점심메뉴를 찾는 이용자임을 고려해 평일 새벽 시간대로 설정
3. 공공데이터가 자주 갱신되지 않는다는 점을 고려
4. 공공데이터 수집과 전처리 시간을 분리해 서버 부하 방지
</details>

### 이지원
<details>
<summary><strong>💭 인기 맛집을 매번 조회하는 방법 말고 다른 방법 없을까?</strong></summary>
<div markdown="1">

팀원들과 인기 맛집을 조회하는 기준을 평점과 리뷰 개수로 정했다.

이후 구현 방법을 고민하면서 가장 걱정됐던 것은 인기 맛집의 경우 하루나 일주일 단위로 보여줄 텐데 DB에 저장된 목록을 조회하는 방식을 사용하면 매번 쿼리를 실행해야 해서 성능 면에서 비효율적일 것 같다는 점이었다.

그래서 다른 방법을 찾아보던 중 Redis의 캐시를 사용하는 전략을 발견했다.

캐시는 DB에 자주 접근해서 가져오는 데이터를 임시로 저장해 둔 뒤, 이후 동일한 데이터 요청이 들어왔을 때 DB에 접근하지 않고 미리 저장해 둔 데이터를 주는 방식이다.

인기 맛집은 일정 기간 동안 같은 값을 조회하기 때문에 캐시를 사용하면 쿼리 실행을 줄일 수 있어서 적용하기 알맞은 방법이라는 생각이 들었다.

캐시의 유효기간을 하루로 설정해 인기 맛집이 하루 단위로 변동되게 설정했다.

캐시를 사용하기 전의 응답 시간이다.</br>
<img alt="캐시를 사용하기 전의 응답 시간" src="https://github.com/user-attachments/assets/bbc0d7c3-7baf-4438-8fd2-869dd632ffc4" width="30%" height="100%">

그리고 캐시를 사용한 후의 응답 시간이다.</br>
<img alt="캐시를 사용한 후의 응답 시간" src="https://github.com/user-attachments/assets/c1583f5a-f16f-4d66-a903-7452906e8d3f" width="30%" height="100%">

**응답 시간이 67ms에서 16ms로 약 75% 단축**된 것을 확인할 수 있다.

임시로 적은 데이터로 테스트 했을 때 나온 결과이다. 대용량 데이터로 캐시를 적용한다면 성능 면에서 더 효과적일 것이라 생각한다.
</details>

<details>
<summary><strong>💭 인기 급상승 맛집 조회는 어떤 방법으로 구현할까?</strong></summary>
<div markdown="1">

인기 급상승 맛집 조회 기준은 전날 생성된 리뷰 개수로 정했다.

고민한 구현 방법은 2가지. store 엔티티에 하루동안 추가된 리뷰 개수 담는 필드를 추가하는 방법과 review 엔티티에 작성일 필드를 추가해 전날 작성된 리뷰 개수를 조회하는 방법.

먼저 하루동안 추가된 리뷰 개수 담는 필드를 추가하는 방법은 조회 시 별도의 계산이 필요가 없어서 성능 면에서 좋고, 쿼리도 간단해진다는 장점이 있었다. 하지만 리뷰가 추가될 때마다 필드를 업데이트해줘야 하고, 매일 해당 필드를 0으로 초기화해줘야 하는 번거로움이 존재했다. store 데이터가 적다면 괜찮은 방법이었겠지만 대용량 데이터에 속하기 때문에 성능 저하가 발생될 것이라는 생각이 들었다.

따라서 review 엔티티에 작성일 필드를 추가하는 방법을 채택했다. 리뷰가 추가될 때마다 추가작업을 할 필요가 없어 읽기 성능만 개선하면 된다는 이점이 있어서다.

다음으로 고민하게 된 읽기 성능 개선 방법. 해당 방법은 조회 시 리뷰의 작성 날짜를 기준으로 집계해야 하므로 성능이 비교적 느리다는 단점이 존재한다. 따라서 Redis 캐시를 사용하기로 결정했다. 캐시 갱신 시점에만 조회하게 되므로 성능 부담이 덜할 것이라고 판단했다. 또한, 전날의 기록으로 데이터를 정렬하기 때문에 캐시 사용이 의미가 있으리라는 생각이 들었다.
</details>

### 정진희
<details>
<summary><strong>💭 사용자 위치와 맛집 간의 거리를 계산하는 방법으로 어떤 것이 가장 적합할까?</strong></summary>
<div markdown="1">

### **고민**

처음 사용자 위치 기반 맛집 조회 기능을 개발할 때, 두 지리적 좌표 사이의 거리를 계산하는 방법에 집중했다.

검색을 통해 두 지점 간의 구면 거리를 계산하는 수학 공식인 “하버사인 공식”을 알게 되었고, 이를 Querydsl과 함께 사용하여 사용자 위치에서 주어진 범위 내의 맛집을 검색할 수 있었다.

하지만 쿼리가 복잡해지고, 유지보수가 어려울 것 같다는 판단이 들어 다른 방법을 찾기로 했다.

### 해결 방법 탐색

공간 데이터베이스에서 제공하는 **공간 데이터 타입**과 **공간 함수**를 사용할 수 있다는 것을 알게 되었다.

공간 함수를 사용하면 정확한 지리적 거리 계산이 가능하며, 공간 인덱스를 적용하면 성능도 크게 향상시킬 수 있다.

쿼리에 어떤 공간 함수를 사용할 지 고민했다.

1. `ST_DISTANCE` : SRID 4326을 사용하는 경우, 하버사인 공식과 유사한 계산을 수행하여 적합해 보였지만, 공간 인덱스를 사용할 수 없어 선택하지 않았다.
2. `ST_Contains` + `ST_Buffer` : 공간 인덱스를 적용할 수 있는 이 방법을 선택했다.

### 선택한 방법

- 데이터베이스에 저장된 경도와 위도를 활용하여, 공간 데이터 타입인 **Point**를 사용해 위치(location) 데이터를 구성했다.
- `ST_Contains`와 `ST_Buffer` 함수를 사용하여 쿼리를 작성했다.
    
    ```java
    // 사용자 위치 기반 맛집 조회 목록 - 거리순으로 정렬
    @Query("SELECT s FROM Store s " +
        "WHERE ST_Contains(ST_Buffer(:userLocation, :meterRange), s.location) " + // ST_Buffer와 ST_Contains로 spatial index 활용
        "ORDER BY ST_Distance(s.location, :userLocation) ASC") // 사용자와 가까운 맛집부터 정렬
    ```
</details>

<details>
<summary><strong>💭 사용자 위치 기반 맛집 추천은 어떤 기준으로 추천을 정할까?</strong></summary>
<div markdown="1">

### **고민**

맛집 추천을 위한 초기 계산식은 `평점 * 0.7 + 리뷰 개수 * 0.3` 이었다.

하지만 리뷰 개수의 범위가 너무 넓어 크기 조정이 필요하다고 생각했다.

### 해결 방법 탐색

조정을 위한 계산식을 만들기 위해서 네이버 지도 앱에서 맛집 리뷰 개수를 조사했다.

대형 프랜차이즈 매장의 경우 리뷰 개수가 5,000개를 쉽게 넘기고, 10,000개 이상인 곳도 있었다.

반면, 개인이 운영하는 맛집은 보통 1,000개에서 2,000개 사이이며, 가끔 2,000개를 넘는 경우도 있지만 3,000개 이상은 드물었다.

따라서 **리뷰 개수를 0과 1 사이 값으로 정규화하기 위해 3,000을 기준**으로 삼기로 했다.

### 선택한 방법

- 계산식 = `평점 * 0.7 + (리뷰 개수 / 3000) * 0.3`
- 리뷰 개수가 3,000개 초과할 경우, `(리뷰 개수 / 3000)`은 1로 처리한다.
- 계산식 결과가 3.0 이상이면 추천하는 맛집으로 정했다.

```java
double rating = 4.2; // 평점
int reviewCount = 1000; // 맛집의 리뷰 개수
int maxReviewCount = 3000; // 정규화 할 리뷰 개수의 최대값

// 리뷰 개수 정규화 (0~1 사이 값으로 변환)
// 리뷰 개수가 3000을 초과하면 normalizedReviewCount는 1로 처리
double normalizedReviewCount = reviewCount > maxReviewCount ? 1.0 : (double) reviewCount / maxReviewCount;

// 최종 점수 계산 (평점에 70%, 리뷰 개수에 30% 가중치)
// score = 3.04
double score = (rating * 0.7) + (normalizedReviewCount * 0.3);
```
</details>

## 🙀 트러블 슈팅
### 김영주
<details>
<summary><strong>⚡ 카카오 로그인 DB 계정 삭제</strong></summary>
<div markdown="1">

### **문제 상황**
- 카카오 로그인 성공 이후 계정 회원가입 재시도 중 MySQL에서 해당 계정 삭제가 되지 않는 문제 발생

### **원인 분석**
- Safe Lock이 걸려져 있어서 테스트할 계정이 삭제가 되지 않았던 것

### **해결 과정**
- Safe Mode를 해당 쿼리문을 실행하는 동안 껐다가 켜줌
```sql
-- Safe update mode 비활성화
SET SQL_SAFE_UPDATES = 0;

-- DELETE 쿼리 실행
DELETE FROM users WHERE social_type = 'KAKAO';

-- Safe update mode 다시 활성화
SET SQL_SAFE_UPDATES = 1;
```

### **결과**
![image](https://github.com/user-attachments/assets/b451cc07-b588-461a-8294-75fae84e2a9a)
![image](https://github.com/user-attachments/assets/29942138-ac90-4679-957b-7f5c7d338bcb)

</details>

### 정은경
<details>
<summary><strong>⚡ Spring Boot 3.0 이상 Spring Batch 오류</strong></summary>
<div markdown="1">

### **문제 상황**
1. 메타 데이터 테이블 생성 오류 발생으로 Job이 실행되지 않음
2. 여러 개의 Job 생성 시 실행 오류

### **원인 분석**
- Spring Batch 5.0과 Spring Boot 3.0의 변경사항으로 자동 설정 비활성화
- @EnableBatchProcessing springBoot 3.0 권장 x → 자동 설정 모두 비활성화

### **해결 과정**
- 새로운 `BatchConfig` 클래스 생성
```java
@Configuration
@EnableConfigurationProperties(BatchProperties.class)
public class BatchConfig {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "spring.batch.job", name = "enabled", havingValue = "true", matchIfMissing = true)
    public JobLauncherApplicationRunner jobLauncherApplicationRunner(JobLauncher jobLauncher, JobExplorer jobExplorer,
                                                                     JobRepository jobRepository, BatchProperties properties) {
        JobLauncherApplicationRunner runner = new JobLauncherApplicationRunner(jobLauncher, jobExplorer, jobRepository);
        String jobNames = properties.getJob().getName();
        if (StringUtils.hasText(jobNames)) {
            runner.setJobName(jobNames);
        }
        return runner;
    }
}
```

- `DataPipeJobConfig`와 `OpenApiJobConfig` 수정:
    - `@EnableScheduling` 어노테이션 제거 (스케줄러 클래스로 이동)
    - `@Configuration` 유지
- `DataPipeJobScheduler` 수정

```java
@Component
@RequiredArgsConstructor
@EnableScheduling
@Slf4j
public class DataPipeJobScheduler {
    private final JobLauncher jobLauncher;
    private final Job openApiJob;
    private final Job dataPipeJob;

    @Scheduled(cron = "0 0 3 * * 1")  // 매주 월요일 새벽 3시 1번
    public void collectRawData(){
        runJob(openApiJob,"공공데이터 수집");
    }

    @Scheduled(cron = "0 0 5 * * 1") // 매주 월요일 새벽 5시 1번
    public void processedDataUpdate(){
        runJob(dataPipeJob,"운영 데이터 전처리");
    }

    private void runJob(Job job, String jobDescription) {
        log.info("{} Job을 실행합니다.",jobDescription);
        try {
            // 각 실행마다 고유한 JobParameters 생성
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();
            // 작업 실행
            JobExecution jobExecution = jobLauncher.run(job, jobParameters);
            // 작업 실행 결과
            log.info(" {} Job 결과입니다: {}", jobDescription,jobExecution.getStatus());
        } catch (Exception e) {
            // 예외 발생 시
            log.error("{} Job에 예외가 발생했습니다: {}",jobDescription, e.getMessage());
        }
    }
}
```

- `application-secret.yml` 수정
```
 batch:
    job:
      enabled: false #어플리케이션 시작 시 자동 시작 방지
    jdbc:
      initialize-schema: always
```

### **결과**
- 메타테이블 성공적으로 생성
- 여러 개의 Job이 스케줄러 설정에 맞게 성공적으로 실행
</details>

### 이지원
<details>
<summary><strong>⚡ JPQL 사용 시 데이터 조회 개수 설정</strong></summary>
<div markdown="1">

### **문제 상황**
 - 인기 맛집 조회를 구현했으나 조회 개수 설정이 작동하지 않은 상황

### **원인 분석**
- `@Query`어노테이션을 사용해서 메서드명 `findTop`이 작동하지 않음

### **해결 과정**
1. `Pageable` 인터페이스를 사용
2. 원하는 조회 개수를 `PageRequest`로 `Pageable`에 담아 조회

### **결과**
- 원하는 개수만큼 조회 성공
</details>

### 정진희
<details>
<summary><strong>⚡ Point 자료형의 거리 계산할 때 SRID 설정</strong></summary>
<div markdown="1">

### **문제 상황**
- 오류 메시지
    java.sql.SQLException: Binary geometry function st_distance given two geometries of different srids: 4326 and 0, which should have been identical.
    
### **원인 분석**
- 이 오류는 **`ST_DISTANCE` 함수**를 사용할 때 두 `geometry` 객체의 SRID(Spatial Reference System Identifier)가 서로 다르기 때문에 발생합니다. 즉, **`location`** 필드의 SRID와 `myLocation`의 SRID가 일치하지 않는 경우입니다.

### **해결 과정**
- location 필드에는 SRID가 설정되어 있지만 myLocation 필드에는 SRID를 설정하지 않았다.

### **결과**
```java
Point myLocation = geometryFactory.createPoint(new Coordinate(lon, lat));
// 아래 부분을 추가해주니까 해결됨
myLocation.setSRID(4326); // SRID 4326 (WGS 84 좌표계)로 설정
```
</details>
