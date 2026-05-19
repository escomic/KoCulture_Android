# KoCulture Android 작업 가이드

## 기본 원칙

- 이 프로젝트는 Android/Kotlin 기반의 멀티모듈 앱이다.
- 기본 대화와 문서는 한국어로 작성한다.
- 코드의 패키지명, 클래스명, 함수명, 변수명, 주석은 영어를 유지한다.
- Android 권장 앱 아키텍처를 따른다.
- 화면 상태 관리는 MVI 패턴을 기본으로 한다.
- 기존 모듈 경계와 의존 방향을 우선한다. 새 기능을 추가할 때는 현재 구조를 먼저 확인하고 같은 패턴으로 확장한다.

## 프로젝트 구조

```text
app
core:data
core:designsystem
core:domain
core:navigation
core:network
core:ui
feature:seoul
feature:eventdetail
```

### app

- 앱 진입점과 전체 navigation graph 조립을 담당한다.
- `KoCultureApplication`은 Hilt application entry point다.
- `MainActivity`는 Compose content를 설정한다.
- `KoCultureApp`에서 Navigation3 `NavDisplay`, feature entry provider, `Navigator`를 연결한다.
- feature 간 직접 이동 함수는 app 계층의 `navigation/NavigatorExt.kt`에 둔다.

### core:domain

- 앱의 순수 도메인 모델과 repository interface를 둔다.
- 예: `CulturalEvent`, `KoCultureApiErrorCode`, `KoCultureApiException`, `CulturalEventRepository`.
- Android framework, Retrofit DTO, DataSource 구현에 의존하지 않는다.
- feature 모듈은 가능하면 domain 모델과 interface만 바라본다.

### core:data

- domain repository 구현체와 data mapping, PagingSource를 둔다.
- `CulturalEventRepositoryImpl`은 `Pager`를 생성한다.
- `CulturalEventPagingSource`는 Seoul API의 `START_INDEX`, `END_INDEX` 기반 paging을 Paging3로 변환한다.
- network DTO는 mapper를 통해 domain model로 변환한다.

### core:network

- Retrofit API, data source, network DTO, API 응답 파싱, Hilt network binding을 둔다.
- 서울시 문화행사 API service path는 `culturalEventInfo` 고정이다.
- 인증키는 `BuildConfig.SERVICE_KEY`를 통해 주입된다. 환경 변수 또는 `local.properties` 설정 흐름을 유지한다.
- API가 HTTP 200이어도 body의 `RESULT.CODE`가 실패일 수 있으므로 `getOrThrow()`로 domain exception 변환을 유지한다.
- optional path segment가 있는 API이므로 URL 생성 시 빈 중간 segment 보존에 주의한다.

### core:navigation

- Navigation3 기반의 공통 navigation state와 `Navigator`를 둔다.
- 각 feature는 자체 `NavKey`와 entry provider를 제공한다.
- feature 모듈끼리 직접 의존하지 않는다.

### core:ui

- 공통 UI infrastructure를 둔다.
- 현재 MVI 기반 `BaseViewModel<State, Event, Effect>`가 있다.
- feature ViewModel은 `BaseViewModel`을 상속하고 `UiState`, `UiEvent`, `UiEffect` contract를 명시한다.

### core:designsystem

- 앱 테마, 컬러, 타이포그래피, 공통 디자인 시스템 리소스를 둔다.
- 화면 UI는 Material3 Compose를 기본으로 사용하고, 디자인 시스템 테마 안에서 동작해야 한다.

### feature:seoul

- 서울 문화행사 리스트 화면을 담당한다.
- `SeoulCultureScreen`은 Scaffold, TopAppBar, category/free filter, Paging list를 구성한다.
- `SeoulCultureViewModel`은 `CulturalEventRepository.getCulturalEvents()` 결과를 PagingData로 노출한다.
- `SeoulContract`에 `SeoulUiState`, `SeoulUiEvent`, `SeoulUiEffect`, `SeoulCultureCategory`를 둔다.
- 카테고리 필터는 API의 `CODENAME` 파라미터로 조회한다.
- 무료 필터는 domain model의 `isFree`를 기준으로 `PagingData.filter`를 적용한다.
- PagingData는 재사용하면 런타임 예외가 날 수 있으므로 필터 조건 변경 시 새 paging stream을 만들도록 유지한다.

### feature:eventdetail

- 문화행사 상세 화면을 담당한다.
- 현재는 상세 navigation 대상 화면으로 구성되어 있다.
- 상세에 필요한 데이터 전달 방식은 app navigation 확장과 feature boundary를 고려해서 결정한다.

## 패키지 규칙

- 기본 패키지 prefix는 `com.devsimtaku.koculture`이다.
- 모듈별 패키지는 다음을 따른다.

```text
app                    com.devsimtaku.koculture
core:data              com.devsimtaku.koculture.core.data
core:designsystem      com.devsimtaku.koculture.core.designsystem
core:domain            com.devsimtaku.koculture.core.domain
core:navigation        com.devsimtaku.koculture.core.navigation
core:network           com.devsimtaku.koculture.core.network
core:ui                com.devsimtaku.koculture.core.ui
feature:seoul          com.devsimtaku.koculture.feature.seoul
feature:eventdetail    com.devsimtaku.koculture.feature.eventdetail
```

## MVI 작성 규칙

- feature 화면에는 contract 파일을 둔다.
- contract에는 기본적으로 다음을 정의한다.

```kotlin
data class XxxUiState(...)

sealed interface XxxUiEvent { ... }

sealed interface XxxUiEffect { ... }
```

- ViewModel은 `BaseViewModel<State, Event, Effect>`를 상속한다.
- UI 입력은 `sendEvent()`로 ViewModel에 전달한다.
- 화면 이동, snackbar, one-shot 동작은 `UiEffect`로 처리한다.
- 화면은 `uiState`를 collect하고, `uiEffect`는 `LaunchedEffect`에서 collect한다.
- repository 결과는 ViewModel에서 UI 모델에 맞게 조합하되, domain model을 불필요하게 변형하지 않는다.

## Paging3 주의사항

- repository는 `Flow<PagingData<T>>`를 반환한다.
- 필터 조건이 바뀌면 `flatMapLatest`로 새 `Pager` 흐름을 만든다.
- `PagingData` 인스턴스를 `combine` 등으로 다시 방출해 재사용하지 않는다.
- 서버 필터가 가능한 값은 repository/data source 파라미터로 전달한다.
- 서버 필터가 없는 값은 새 paging stream 내부에서 `PagingData.filter`를 적용한다.

## API 규칙

- 서울시 문화행사 API:

```text
http://openapi.seoul.go.kr:8088/{KEY}/json/culturalEventInfo/{START_INDEX}/{END_INDEX}/{CODENAME}/{TITLE}/{DATE}
```

- `TYPE`은 `json` 고정이다.
- `SERVICE` path는 `culturalEventInfo` 고정이다.
- `CODENAME`, `TITLE`, `DATE`는 optional path segment다.
- `DATE`는 `YYYY-MM-DD` 형식이다.
- API 응답의 `RESULT.CODE`를 반드시 확인한다.
- 정상 코드는 `INFO-000`이다.
- `INFO-200`은 데이터 없음으로 취급한다.

## 테스트 규칙

- 추가/수정된 core 로직에는 unit test를 작성한다.
- 테스트 함수명은 한국어 backtick 함수명을 사용한다.
- mapper, API error parsing, URL generation, PagingSource는 우선 테스트 대상이다.
- feature UI는 현재 프로젝트 패턴에 맞춰 필요 시 Compose UI test를 추가한다.

## 빌드와 검증

자주 사용하는 검증 명령:

```bash
./gradlew :app:compileDebugKotlin
./gradlew :core:domain:testDebugUnitTest
./gradlew :core:network:testDebugUnitTest
./gradlew :core:data:testDebugUnitTest
```

- feature 구현 후에는 최소 `:app:compileDebugKotlin`을 실행한다.
- core 로직 변경 시 해당 모듈 unit test를 함께 실행한다.

## 의존성 관리

- dependency version은 `gradle/libs.versions.toml`에서 관리한다.
- 모듈 간 의존성은 최대한 단방향을 유지한다.
- feature 모듈은 다른 feature 모듈을 직접 의존하지 않는다.
- 공통 UI/architecture 코드는 `core:ui`, 테마와 디자인 요소는 `core:designsystem`에 둔다.
- network DTO를 feature로 노출하지 않는다.

## UI 작성 기준

- Compose + Material3를 기본으로 한다.
- 화면 단위는 `Scaffold` 사용을 우선한다.
- 리스트 화면은 Paging3 Compose `LazyPagingItems`를 사용한다.
- 이미지 로딩은 Coil3 `AsyncImage`를 사용한다.
- 텍스트 overflow, loading, empty, error, append loading/error 상태를 명시적으로 처리한다.
- 필터, 정렬, 검색처럼 상태가 있는 UI는 `UiState`에 반영한다.

## 작업 시 주의사항

- 기존 사용자 변경사항을 되돌리지 않는다.
- unrelated refactor를 섞지 않는다.
- 새 모듈이나 패키지를 만들 때는 `settings.gradle.kts`, Gradle namespace, source package를 함께 맞춘다.
- API key, local.properties, IDE 설정 파일은 커밋하지 않는다.
- `.gitignore` 정책을 유지한다.
