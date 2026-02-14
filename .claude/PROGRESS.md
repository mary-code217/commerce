# 변환 진행 상황

## 현재 상태: 세션 5 완료 - core-api/domain Service 클래스 변환 완료

### 세션별 진행 현황

| 세션 | 내용 | 상태 | 완료일 | 비고 |
|:----:|------|:----:|--------|------|
| 0 | 문서화 + Git 셋업 | 완료 | 2025-02-08 | 컨텍스트 문서 + Git 초기화 완료 |
| 1 | 프로젝트 셋업 + core-enum | 완료 | 2025-02-08 | Gradle 셋업 + Enum 12개 + Docker MySQL |
| 2 | storage/db-core (Entity + Config) | 완료 | 2025-02-09 | Config 2개 + Converter 3개 + Entity 24개 + yml |
| 3 | storage/db-core (Repository) | 완료 | 2025-02-10 | Repository 21개 변환 |
| 4 | core-api/domain (모델 클래스) | 완료 | 2025-02-11 | 도메인 모델 27개 + support/error 4개 |
| 5 | core-api/domain (Service) | 완료 | 2026-02-14 | Service 21개 + support 2개 (OffsetLimit, Page) |
| 6 | core-api/controller + DTO | 대기 | - | |
| 7 | Support + Config + Test + 빌드 | 대기 | - | |

### 세션 5 상세 로그

- [x] OffsetLimit.java (support 선행 변환 - offset/limit → Pageable)
- [x] Page.java (support 선행 변환 - 제네릭 페이지 래퍼)
- [x] ProductService.java (상품 조회 위임)
- [x] ProductFinder.java (카테고리별/단건/섹션 조회 - findByIdOrNull+takeIf → findById+filter+orElseThrow)
- [x] ProductSectionService.java (섹션 목록 위임)
- [x] CartService.java (장바구니 CRUD - associateBy → Collectors.toMap, apply+엘비스 → if-null)
- [x] OrderKeyGenerator.java (UUID → Base64 URL-safe - apply/also → 명시적 호출)
- [x] OrderService.java (주문 생성/조회 - sumOf → Stream reduce, let → 지역변수)
- [x] PointHandler.java (포인트 적립/차감 - == BigDecimal.ZERO → compareTo)
- [x] PaymentService.java (결제 생성/성공/실패 - findByIdOrNull?.use() → findById.ifPresent)
- [x] CancelService.java (주문 취소 - 쿠폰 복원, 포인트 환불)
- [x] CouponService.java (상품별 쿠폰 조회 - 리스트 + → Stream.concat)
- [x] OwnedCouponService.java (보유 쿠폰 조회/다운로드/결제용 조회)
- [x] PointService.java (포인트 잔액/이력 조회)
- [x] ReviewFinder.java (리뷰 목록 + 평점 요약 - sumOf → Stream reduce)
- [x] ReviewManager.java (리뷰 CRUD)
- [x] ReviewPolicyValidator.java (리뷰 작성 정책 - firstOrNull+!in → Stream filter+findFirst)
- [x] ReviewService.java (리뷰 Facade - 작성 시 포인트 적립, 삭제 시 차감)
- [x] QnAService.java (QnA CRUD - let+엘비스 → 삼항 연산자, takeIf → null+isActive 체크)
- [x] FavoriteService.java (찜 CRUD - if/else 표현식 → if/else 문)
- [x] SettlementCalculator.java (object → final class + static 메서드)
- [x] SettlementTargetLoader.java (정산 대상 생성 - when → switch, associate → toMap)
- [x] SettlementService.java (정산 로드/계산/이체 - groupBy → Collectors.groupingBy, do-while 스코프 수정)
- [x] 빌드 확인 (BUILD SUCCESSFUL)

### 세션 4 상세 로그

- [x] Product.java (data class → @Getter @AllArgsConstructor)
- [x] Price.java (BigDecimal 필드 3개 - Value Object)
- [x] ProductSection.java (Enum 타입 필드)
- [x] Order.java (List<OrderItem> 포함)
- [x] OrderItem.java (BigDecimal 가격 필드)
- [x] OrderSummary.java (주문 요약 - items 없는 버전)
- [x] NewOrder.java (주문 생성 요청)
- [x] NewOrderItem.java (주문 항목 생성 요청)
- [x] Cart.java (toNewOrder 비즈니스 로직 - Kotlin filter/map → Stream API)
- [x] CartItem.java (Product 참조)
- [x] AddCartItem.java (장바구니 추가 요청)
- [x] ModifyCartItem.java (장바구니 수정 요청)
- [x] Coupon.java (CouponType Enum + LocalDateTime)
- [x] OwnedCoupon.java (Coupon 참조 + OwnedCouponState)
- [x] PaymentDiscount.java (init 블록 → 생성자 로직, Kotlin 연산자 → compareTo)
- [x] PointBalance.java (userId + balance)
- [x] PointHistory.java (PointType Enum + BigDecimal)
- [x] PointAmount.java (Kotlin object → final class + static 상수)
- [x] Review.java (ReviewTarget + ReviewContent 조합)
- [x] ReviewContent.java (rate + content)
- [x] ReviewTarget.java (ReviewTargetType Enum)
- [x] ReviewKey.java (User + key)
- [x] RateSummary.java (companion object EMPTY → static final)
- [x] QnA.java (Question + Answer 조합)
- [x] Question.java (id, userId, title, content)
- [x] QuestionContent.java (title + content)
- [x] Answer.java (companion object EMPTY → static final)
- [x] User.java (id만 가진 단순 모델)
- [x] Favorite.java (LocalDateTime 필드)
- [x] SettlementAmount.java (BigDecimal 4개 - Value Object)
- [x] CancelAction.java (orderKey만 가진 단순 모델)
- [x] ErrorCode.java (support/error - 선행 변환)
- [x] ErrorType.java (support/error - Enum + HttpStatus + LogLevel)
- [x] CoreException.java (support/error - RuntimeException 상속)
- [x] ErrorMessage.java (support/error - 에러 응답 DTO)
- [x] 빌드 확인 (BUILD SUCCESSFUL)

### 세션 3 상세 로그

- [x] AnswerRepository.java (findByQuestionIdIn)
- [x] CancelRepository.java (existsByPaymentId + findAllByCanceledAtBetween 오버로딩)
- [x] CartItemRepository.java (Nullable 리턴 - findByUserIdAndIdAndStatus, findByUserIdAndProductId)
- [x] CouponRepository.java (@Query JPQL - findApplicableCouponIds 복합 조인)
- [x] CouponTargetRepository.java (Collection 파라미터)
- [x] FavoriteRepository.java (Slice 페이징)
- [x] MerchantProductMappingRepository.java (Set 파라미터)
- [x] OrderItemRepository.java (@Query JPQL - findRecentOrderItemsForProduct 조인)
- [x] OrderRepository.java (OrderByIdDesc 메서드명 정렬)
- [x] OwnedCouponRepository.java (@Query JPQL - findOwnedCouponIds 복합 조인)
- [x] PaymentRepository.java (Slice 페이징)
- [x] PointBalanceRepository.java (단순 findByUserId)
- [x] PointHistoryRepository.java (단순 findByUserId)
- [x] ProductCategoryRepository.java (Slice + Collection 파라미터)
- [x] ProductRepository.java (Collection 파라미터)
- [x] ProductSectionRepository.java (단순 findByProductId)
- [x] QuestionRepository.java (Nullable 리턴 + Slice)
- [x] ReviewRepository.java (Collection + Slice)
- [x] SettlementRepository.java (Enum 파라미터)
- [x] SettlementTargetRepository.java (@Query JPQL - DTO Projection with new)
- [x] TransactionHistoryRepository.java (빈 인터페이스 - 기본 CRUD만)
- [x] 빌드 확인 (BUILD SUCCESSFUL)

### 세션 2 상세 로그

- [x] CoreDataSourceConfig.java (HikariCP DataSource 설정)
- [x] CoreJpaConfig.java (JPA + Repository 스캔 설정)
- [x] SecurityProperty.java (@ConfigurationProperties 바인딩)
- [x] AesHelper.java (AES 암호화/복호화)
- [x] AesConverter.java (JPA AttributeConverter)
- [x] BaseEntity.java (공통 부모 Entity - id, status, createdAt, updatedAt)
- [x] ProductEntity.java, CategoryEntity.java, ProductCategoryEntity.java, ProductSectionEntity.java
- [x] CartItemEntity.java (protected set + applyQuantity 패턴)
- [x] OrderEntity.java (상태 변경 메서드 + @Index), OrderItemEntity.java
- [x] PaymentEntity.java (Nullable 필드 + success() 메서드), CancelEntity.java
- [x] CouponEntity.java, CouponTargetEntity.java, OwnedCouponEntity.java (@Version 낙관적 잠금)
- [x] PointBalanceEntity.java (@Version + BigDecimal 연산), PointHistoryEntity.java, TransactionHistoryEntity.java
- [x] ReviewEntity.java, QuestionEntity.java, AnswerEntity.java
- [x] FavoriteEntity.java (부모 메서드 호출 패턴)
- [x] MerchantProductMappingEntity.java, SettlementEntity.java, SettlementTargetEntity.java
- [x] SettlementTargetSummary.java (Projection DTO - data class)
- [x] db-core.yml (프로파일별 DB 설정)
- [x] 빌드 확인 (BUILD SUCCESSFUL)

### 세션 1 상세 로그

- [x] Gradle Wrapper 복사 (gradlew, gradle-wrapper.jar)
- [x] gradle.properties 생성 (Java 21, Lombok, Spring Boot 3.5.5)
- [x] settings.gradle.kts 생성 (5개 모듈 정의)
- [x] build.gradle.kts 루트 생성 (java-library + Lombok)
- [x] 서브모듈 build.gradle.kts 생성 (core-api, db-core, logging, monitoring)
- [x] Docker MySQL 설정 (docker-compose.yml, init.sql)
- [x] Enum 12개 Java 변환 (EntityStatus, OrderState, PaymentState 등)
- [x] 빌드 확인 (BUILD SUCCESSFUL)
- [x] 커밋 완료

### 세션 1 커밋 이력
1. `chore: Java 멀티모듈 프로젝트 초기 셋업`
2. `chore: .gitignore에 CLAUDE.md 추가`
3. `chore: Docker MySQL 설정 추가`
4. `feat: core-enum 모듈 Java 변환 (12개 Enum)`

### 변환 통계

| 항목 | 전체 | 완료 | 진행률 |
|------|:----:|:----:|:------:|
| Enum | 12 | 12 | 100% |
| Entity | 24 | 24 | 100% |
| Config (db-core) | 5 | 5 | 100% |
| Repository | 21 | 21 | 100% |
| Domain Model | 27 | 27 | 100% |
| Support (error) | 4 | 4 | 100% |
| Service | 21 | 21 | 100% |
| Support (OffsetLimit, Page) | 2 | 2 | 100% |
| Controller | 13 | 0 | 0% |
| DTO (Req/Res) | 27 | 0 | 0% |
| Support (나머지) | 4 | 0 | 0% |
| Config (core-api) | 3 | 0 | 0% |
| Test | 11 | 0 | 0% |
| **전체** | **~170** | **116** | **68%** |

---

## 메모 / 이슈

> 세션 진행 중 발생한 이슈나 메모를 여기에 기록

- Gradle 빌드 시 `api` 스코프 사용하려면 `java-library` 플러그인 필요 (루트에서 적용 완료)
- core-api의 bootJar는 메인 클래스 없으면 실패 → `-x bootJar`로 우회 (세션 7에서 해결 예정)
- Kotlin `allOpen` 플러그인 → Java에서는 클래스가 기본 open이므로 제거
- Kotlin `internal` → Java package-private (접근제한자 생략)
- Kotlin `data class` (비-Entity) → `@Getter @AllArgsConstructor` (Lombok)
- Entity의 `protected set` 패턴 → setter 미생성 + 비즈니스 메서드로만 상태 변경
- BigDecimal 연산: Kotlin `+=` → Java `.add()` 메서드 + 재할당
- `@Version` 낙관적 잠금: OwnedCouponEntity, PointBalanceEntity에서 동시성 제어
- Kotlin Nullable 리턴(`?`) → Java에서는 그냥 nullable 리턴 (Optional 미사용, 원본과 동일한 패턴 유지)
- JPQL `@Query`는 Kotlin/Java 동일 - 문법 변경 없음
- Spring Data JPA 메서드명 쿼리: Kotlin `fun` → Java 메서드 선언 (거의 동일)
- Kotlin `data class` → `@Getter @AllArgsConstructor` + `private final` 필드 (equals/hashCode/toString은 필요 시 추가)
- Kotlin `object` (싱글턴) → `final class` + `private 생성자` + `static final` 상수
- Kotlin `companion object` → `static final` 필드/메서드
- Kotlin `init` 블록 → Java 생성자 내 로직
- Kotlin 연산자 오버로딩 (`>`, `-`, `+`) → `compareTo()`, `subtract()`, `add()`
- Kotlin `filter/map` 체이닝 → Java Stream API (`stream().filter().map().collect()`)
- Kotlin `firstOrNull` → `stream().filter().findFirst().map().orElseThrow()`
- Cart의 `toNewOrder`처럼 비즈니스 로직이 있는 data class는 `@AllArgsConstructor`만 사용하고 메서드를 직접 작성
- PaymentDiscount처럼 init 로직이 있는 경우 `@AllArgsConstructor` 대신 생성자를 직접 구현
- support/error 패키지를 세션 4에서 선행 변환 (Cart, PaymentDiscount가 CoreException에 의존)
- Kotlin `associateBy { }` → `Collectors.toMap(it -> it.getKey(), Function.identity())`
- Kotlin `sumOf { }` → `stream().map().reduce(BigDecimal.ZERO, BigDecimal::add)`
- Kotlin `groupBy { }` → `Collectors.groupingBy()`
- Kotlin `.associate { it.key to it.value }` → `Collectors.toMap(it -> it.getKey(), it -> it.getValue())`
- Kotlin `listA + listB` (리스트 합치기) → `Stream.concat(listA.stream(), listB.stream())`
- Kotlin `apply { ... }` + 엘비스 연산자 → `if (existing != null) { ... } else { ... }`
- Kotlin `?.let { } ?: default` → 삼항 연산자 또는 if/else
- Kotlin `findByIdOrNull(id)?.use()` → `findById(id).ifPresent(it -> it.use())`
- Kotlin `takeIf { it.isActive() }` → `if (found == null || !found.isActive())`
- Kotlin `when (type)` → Java `switch (type)`
- Kotlin do-while에서 `val`은 while 조건에서 접근 가능 → Java `var`는 do 블록 스코프로 제한 → boolean 플래그 분리
- `@RequiredArgsConstructor`로 생성자 주입 (Kotlin 주 생성자 + val 필드와 동일한 효과)
- support/OffsetLimit, Page를 세션 5에서 선행 변환 (Service 클래스 의존)
