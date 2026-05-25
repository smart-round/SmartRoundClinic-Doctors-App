# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

# SmartRound Clinic — Doctor App

Kotlin Multiplatform project targeting **Android** and **iOS** using Compose Multiplatform.

## Commands

```bash
# Android
./gradlew :composeApp:assembleDebug        # Build debug APK
./gradlew :composeApp:assembleRelease      # Build release APK

# Tests
./gradlew :composeApp:commonTest           # Run shared tests

# KSP (re-run after changing Room entities/DAOs)
./gradlew :composeApp:kspDebugKotlinAndroid
./gradlew :composeApp:kspKotlinIosArm64
./gradlew :composeApp:kspKotlinIosSimulatorArm64

# iOS — open in Xcode after building the framework
open iosApp/iosApp.xcodeproj
```

## Module structure

```
composeApp/
  src/
    commonMain/   — shared business logic, UI, and platform-agnostic setup
    androidMain/  — Android actuals; Android Context provided via Koin androidContext()
    iosMain/      — iOS actuals using NSFileManager / Darwin engine
  schemas/        — Room auto-generated migration JSON (commit these)
```

## Package root

`ke.co.smartroundclinic.doctor`

## Tech stack & versions

| Library | Version | Purpose |
|---|---|---|
| Kotlin | 2.3.21 | Language |
| Compose Multiplatform | 1.10.3 | UI |
| androidx.lifecycle | 2.10.0 | ViewModel (`org.jetbrains.androidx.lifecycle`) |
| Koin | 4.1.0 | Dependency injection |
| Navigation3 (`org.jetbrains.androidx.navigation3`) | 1.1.0 | Navigation — JetBrains KMP port, NOT `androidx.navigation3` |
| Room | 2.8.4 | Local database — KMP via `BundledSQLiteDriver` |
| SQLite Bundled | 2.6.2 | Cross-platform SQLite driver for Room |
| Ktor | 3.0.3 | HTTP client |
| kotlinx.serialization | 1.11.0 | JSON serialization |
| kotlinx.coroutines | 1.10.2 | Async |
| Coil 3 | 3.4.0 | Image loading — uses `coil-network-ktor3` (NOT `coil-network-okhttp`) |
| DataStore (`datastore-preferences-core`) | 1.2.1 | Key-value preferences — KMP `-core` variant |
| KVault | 1.12.0 | Secure storage (Android Keystore / iOS Keychain) |
| FileKit | 0.14.0 | Cross-platform file/photo picking |
| Napier | 2.7.1 | Multiplatform logging (used by HTTP client) |
| KSP | 2.3.7 | Annotation processing for Room |

## App entry points

- **Android:** `SmartRoundApp` (Application) initializes Koin, then `MainActivity` renders `App()`
- **iOS:** `iOSApp.swift` (SwiftUI `@main`) → `ContentView` wraps `MainViewController()`, which calls `doInitKoin()` then renders `App()`
- `App()` renders `SmartRoundTheme { Box { NavigationRoot(); SnackbarHost(...) } }` — the `SnackbarHost` floats over all content and is driven by the global `SnackbarController` (see below)

## Navigation

Uses Navigation3 with a `NavDisplay` + mutable back stack. The pattern used throughout:

```kotlin
// retain {} survives recomposition without saving to Bundle — NOT rememberSaveable
val backStack = retain { mutableStateListOf<NavKey>(InitialDestination) }

NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    entryProvider = entryProvider { entry<MyDestination> { MyScreen() } }
)
```

**Destination objects** live in a `destinations/Screens.kt` file per feature and are `data object` types (implement `NavKey`).

**CRITICAL — never put `ByteArray` in a `@Serializable` NavKey data class.** Navigation3 serializes the entire back stack to an Android Bundle for state restoration. A `ByteArray` field (photo, file) serializes as base64 JSON and will cause `TransactionTooLargeException` (Android's 1 MB Bundle limit) when the file is large. Instead, hold binary data in a dedicated in-memory ViewModel (see `SignUpFilesViewModel` pattern below) and keep NavKey data classes limited to primitive / String fields only.

**Navigation hierarchy:**
```
NavigationRoot           — top-level NavDisplay (Splash → Onboarding → SignUp → Auth → Main)
  ├── SplashScreen       — checks onboarding flag, routes to Onboarding or SignUp
  ├── OnboardingRoot     — nested NavDisplay with 3 onboarding screens
  ├── SignUpRoot          — nested NavDisplay: SignUp → Specialization → BankDetails → Verification → UnderReview
  ├── AuthRoot           — nested NavDisplay: SignIn → ForgotPassword → VerifyEmail → CreateNewPassword
  └── MainRoot           — bottom-nav shell; tab destinations: Home, Bookings, Articles, Chat
        ├── HomeRoot     — nested NavDisplay: HomeList
        ├── BookingsRoot — nested NavDisplay: BookingList → BookingDetail(bookingId)
        ├── ArticlesRoot — nested NavDisplay: ArticleList → WriteArticle | ArticleDetail(articleId)
        └── ChatRoot     — nested NavDisplay: ChatList → Conversation(chatId) → Call(chatId, isVideo)
```

Each root composable owns its own `NavDisplay` and back stack. Cross-root navigation is done by passing lambdas (`onSignIn`, `onSignUp`, etc.) up to `NavigationRoot`.

### Bottom-nav tab architecture (standard pattern for this app)

Every bottom-nav tab follows the same structure. **This is the required pattern — do not use monolithic screen files or local `var view` state for in-tab navigation.**

```
presentation/main/<tab>/
  destinations/
    Screens.kt          — @Serializable NavKey objects/classes for this tab's screens
  ui/
    <Tab>ListScreen.kt  — root list/home screen for the tab (no sub-nav state)
    <Detail>Screen.kt   — one file per sub-screen
  <Tab>Root.kt          — owns retain{} back stack + NavDisplay; defines placeholder data models
```

**`<Tab>Root.kt` pattern:**
```kotlin
@Composable
fun BookingsRoot(modifier: Modifier = Modifier, onAtRootChanged: (Boolean) -> Unit = {}) {
    val backStack = retain { mutableStateListOf<NavKey>(BookingList) }
    val isAtRoot = backStack.size == 1  // read in composition scope to create reactive subscription

    // Drives MainRoot's shared header/navbar visibility
    SideEffect { onAtRootChanged(isAtRoot) }

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<BookingList> {
                BookingListScreen(bookings = allBookings, onBookingClick = { backStack.add(BookingDetail(it.id)) })
            }
            entry<BookingDetail> { dest ->
                AppointmentDetailScreen(booking = allBookings.first { it.id == dest.bookingId }, onBack = { backStack.removeLastOrNull() })
            }
        },
    )
}
```

**`destinations/Screens.kt` pattern (primitives only in data classes):**
```kotlin
@Serializable data object BookingList : NavKey
@Serializable data class BookingDetail(val bookingId: Int) : NavKey  // ID only — never full objects
```

**Sub-screens** call `statusBarsPadding()` / `navigationBarsPadding()` themselves since `MainRoot`'s shared header and bottom bar are hidden when `backStack.size > 1`.

**`MainRoot`** renders the four tab Roots and hides/shows the shared `DashboardHeader` and `BottomNavBar` based on `onAtRootChanged`:
```kotlin
entry<Home> { HomeRoot(onAtRootChanged = { isAtRoot = it }) }
entry<Bookings> { BookingsRoot(onAtRootChanged = { isAtRoot = it }) }
entry<Articles> { ArticlesRoot(onAtRootChanged = { isAtRoot = it }) }
entry<Chat> { ChatRoot(onAtRootChanged = { isAtRoot = it }) }
```

## Feature layer conventions

New features follow a three-layer pattern inside `commonMain`:

```
data/        — repositories, remote/local data sources, DTOs
domain/      — use cases, domain models
presentation/ — ViewModels (org.jetbrains.androidx.lifecycle), Composable screens
```

Each feature owns a Koin module registered via `initKoin(extraModules = [...])`. ViewModels are injected with `koinViewModel()`.

### Clean Architecture data flow

```
Network response DTO  ──►  toDomain()  ──►  Domain Model  ──►  ViewModel / UI
Room Entity           ──►  toDomain()  ──►  Domain Model  ──►  ViewModel / UI

ViewModel / UI  ──►  Domain Model  ──►  toEntity()  ──►  Room Entity  (persist)
ViewModel / UI  ──►  Domain Model  ──►  toRequest()  ──►  Request DTO  (send)
```

**Layer rules:**
- `data/remote/dto/` — network DTOs (`@Serializable`); never leave the data layer
- `core/database/entity/` — Room `@Entity` classes; never leave the data layer
- `domain/model/` — pure Kotlin data classes; no framework annotations; the only type that crosses layer boundaries
- `domain/repository/` — interfaces typed to domain models and `Resource<DomainModel>`
- `domain/usecase/` — orchestrate repositories, return `Resource<DomainModel>`; no DTO or entity imports
- `presentation/` — ViewModels consume domain models from use cases; map to UI state if needed

**Mapping lives on the source type, not in the repository:**
- Extension functions `fun FooResponse.toDomain()` live in the DTO file itself
- Extension functions `fun FooEntity.toDomain()` and `fun Foo.toEntity()` live in the entity file itself
- Repository impls import and call these; they contain no private mapping helpers

### Repository isolation rule

**A repository must be single-source — either remote (Ktor) or local (Room), never both.**

- `FooRepository` / `FooRepositoryImpl` — remote only: one Ktor call, returns `Resource<List<DomainModel>>`
- `FooLocalRepository` / `FooLocalRepositoryImpl` — local only: wraps the Room DAO, maps `Entity ↔ DomainModel`
- Cache-first orchestration (check local → fetch remote if empty → persist → return) belongs in a **use case**, not in a repository

Example for a cached list feature:
```
domain/model/Foo.kt                         — domain model (pure Kotlin)
domain/repository/FooRepository.kt          — remote interface → Resource<List<Foo>>
domain/repository/FooLocalRepository.kt     — local interface → List<Foo>
data/repository/FooRepositoryImpl.kt        — Ktor impl + ResponseDto→Foo mapping
data/repository/FooLocalRepositoryImpl.kt   — Room DAO impl + Entity↔Foo mapping
domain/usecase/foo/GetFooUseCase.kt         — cache-first logic, returns Resource<List<Foo>>
domain/usecase/foo/SearchFooUseCase.kt      — ensure cached, then query local
```

Register both repository bindings in `RepositoryModule` and the use cases in `UseCaseModule`.

### Resource wrapper

All async results use `Resource<T>` (`common/Resource.kt`):

```kotlin
sealed class Resource<T> {
    class Loading<T> : Resource<T>()
    class Success<T>(data: T?) : Resource<T>()
    class Error<T>(message: String) : Resource<T>()
}
```

ViewModels expose `StateFlow<Resource<T>>` and collect it in Composables.

## Core infrastructure (`core/`)

### Database — Room KMP
- `core/database/AppDatabase.kt` — `@Database` class + `AppDatabaseConstructor` expect (Room KSP generates the actual; do **not** add a manual actual in `iosSimulatorArm64Main`)
- `core/database/DatabaseFactory.kt` — `expect fun getDatabaseBuilder()` + `createDatabase()`
- Android actual: uses Android `Context` (resolved via Koin) + `BundledSQLiteDriver`
- iOS actual: uses `NSFileManager` document directory + `BundledSQLiteDriver`
- KSP processors declared per-target in `dependencies {}`: `kspAndroid`, `kspIosArm64`, `kspIosSimulatorArm64`
- `fallbackToDestructiveMigration(true)` is enabled — bump `version` and add a proper `Migration` before production

### HTTP Client — Ktor
- `core/network/HttpClientFactory.kt` — `buildHttpClient(engine)` + `expect fun createHttpClient()`
- Android actual: `OkHttp` engine; iOS actual: `Darwin` engine
- Shared config: `ContentNegotiation` + JSON (`ignoreUnknownKeys`, `isLenient`, `explicitNulls = false`), Napier logging, `HttpRequestRetry` (3 retries, exponential backoff up to 15 s)
- Base URL: `https://api.smartroundclinic.co.ke/` — constant `BASE_URL` in `common/Constants.kt`

### DataStore — Preferences
- `core/datastore/DataStoreFactory.kt` — `expect fun createDataStore()`
- File name: `app_prefs.preferences_pb`; accessed via `DatastoreRepository` (string key-value)

### Secure Storage — KVault
- `core/storage/SecureStorageFactory.kt` — `expect fun createKVault()`
- Android: `KVault(context, fileName = null)` — EncryptedSharedPreferences
- iOS: `KVault(serviceName = "ke.co.smartroundclinic.doctor")` — Keychain

### Media / File picking
- `core/media/PhotoPickerBottomSheet.kt` — shared composable bottom sheet wrapping FileKit; lets users pick from camera or gallery

### Global Snackbar
- `core/snackbar/SnackbarController.kt` — Koin `single {}` holding a `SharedFlow<String>`; call `snackbarController.show("message")` from any ViewModel
- Collected in `App()` via `LaunchedEffect`; drives `SnackbarHostState` rendered in a `Box` overlay over `NavigationRoot`
- Inject into ViewModels via Koin `get()`: `viewModel { MyViewModel(get(), get()) }` where one `get()` resolves `SnackbarController`
- Use this instead of per-screen error StateFlows for transient API / network errors

### SignUpFilesViewModel pattern
For multi-step flows that collect binary files (photos, documents) across several screens:
- Create a dedicated `ViewModel` with `var fieldBytes by mutableStateOf<ByteArray?>(null)` — no `SavedStateHandle`, no Bundle
- Register it as `viewModel { MyFilesViewModel() }` in Koin
- Inject it **once** at the flow root composable with `koinViewModel()` and pass it down explicitly to child screens
- The NavKey data classes for those steps carry only primitive/String fields; the ViewModel holds the bytes in memory for the lifetime of the flow

## Koin modules

| Module | File | Contents |
|---|---|---|
| `coreModule` | `koin/CoreModule.kt` | `AppDatabase`, `HttpClient`, `DataStore`, `KVault`, `SnackbarController` |
| `repositoryModule` | `koin/RepositoryModule.kt` | All remote + local repository bindings |
| `useCaseModule` | `koin/UseCaseModule.kt` | All use cases + ViewModels |

All three are loaded by `initKoin()`. Extra feature modules can be passed via `initKoin(extraModules = [...])`.

## Multipart file uploads (Ktor)

When sending files via `MultiPartFormDataContent`, always set `Content-Type` from the actual filename extension — never hardcode `application/octet-stream`. Use a helper that maps `.pdf → application/pdf`, `.jpg/.jpeg → image/jpeg`, `.png → image/png`, `.webp → image/webp`, `.docx → application/vnd.openxmlformats-officedocument.wordprocessingml.document`.

Keep the license **number** (e.g. "MED-12345") and the license **filename** (e.g. "license.pdf") as separate fields in the domain model — the number is a form text field; the filename drives the MIME type and `Content-Disposition` header.

## Critical KMP rules

1. **Never put `room-compiler` in `implementation()`** — KSP processor only, declared via `add("ksp<Target>", ...)`.
2. **Use `org.jetbrains.androidx.*` for KMP ports** — `androidx.navigation3`, `androidx.datastore` (non-core) are Android-only.
3. **DataStore KMP uses `-core` artifacts** — `datastore-preferences-core`, NOT `datastore-preferences`.
4. **Coil networking uses `coil-network-ktor3`** — `coil-network-okhttp` has no iOS target.
5. **KVault is an `expect class`** — instantiate via `createKVault()` factory, never directly in commonMain.
6. **`Dispatchers.IO`** — import from `kotlinx.coroutines.IO` in commonMain (not `kotlinx.coroutines.Dispatchers`).
7. **Room KSP generates `AppDatabaseConstructor` actual** — never add a manual actual in any `iosXxxMain` source set; doing so causes a KSP `PROCESSING_ERROR`.

## Adding a Room entity

1. Create `@Entity` data class in `core/database/entity/`
2. Add to `@Database(entities = [YourEntity::class, ...])`
3. Create `@Dao` interface in `core/database/dao/`
4. Add `abstract val yourDao: YourDao` to `AppDatabase`
5. Bump `version` and add a `Migration` (or keep `fallbackToDestructiveMigration` for pre-production)
6. Re-run KSP: `./gradlew :composeApp:kspDebugKotlinAndroid`
