## 1. Project scaffolding

- [x] 1.1 Initialize git repo and base Gradle project structure (root build, version catalog) with empty `shared/` (KMP) and `androidApp/` modules — no `iosApp/` module in this change.
- [x] 1.2 Get a trivial "hello shared module" build working end to end (shared module compiles, Android app links against it and prints a value from shared code) before adding real logic, to isolate multiplatform build/config issues from content/logic work.
- [x] 1.3 Add SQLDelight Gradle plugin/config to the shared module (schema/driver setup only, no tables yet).

## 2. Fact content pipeline (`fact-content-pipeline`)

- [x] 2.1 Define the canonical category taxonomy as a single constant/enum (e.g. mining, custody, proof-of-work, keys, multisig, monetary-policy, history, fiat-comparison, scaling, privacy, nodes-network, wallets) and decide where it lives so both the compiler and the shared Kotlin core reference the same source. Implemented as `Category` enum in a standalone `taxonomy` Kotlin Multiplatform module (jvm + android targets, commonMain-only); `shared`'s commonMain depends on it, and the content compiler (task 2.3) will depend on its jvm target the same way.
- [x] 2.2 Define the fact frontmatter schema (`id`, `title`, `categories`, `sources`, `notify_eligible`, optional `difficulty`, optional `related`) and document it (e.g. a README in `content/facts/`).
- [x] 2.3 Implement the build-time content compiler: parses all `content/facts/*.md`, validates required fields present, validates categories against the canonical taxonomy, detects duplicate IDs, detects dangling `related` references. Implemented as the `content-compiler` Gradle module (JVM, SnakeYAML for frontmatter parsing, kotlinx.serialization for output) — see `ContentCompiler.kt`.
- [x] 2.4 Implement related-fact symmetric mirroring in the compiler (declaring the link on one fact adds the reverse on the other). Verified by `ContentCompilerTest`'s "related links are mirrored symmetrically" test.
- [x] 2.5 Implement compiled runtime index output (format per design.md's open question: JSON or generated SQLite) and wire it to be bundled into the Android app resources at build time. Resolved: plain JSON via kotlinx.serialization, written to `build/generated/assets/facts/facts.json` and registered as an extra Android assets source dir.
- [x] 2.6 Wire the compiler as a Gradle task that runs before the app build and fails the build on any validation error, with a clear error message naming the file and problem. `compileFacts` (JavaExec) added as a dependency of every `merge*Assets` task; verified a deliberately invalid fact fails `:androidApp:assembleDebug` with both problems named.
- [x] 2.7 Write a handful of intentionally-invalid sample fact files (bad category, duplicate ID, dangling related ID, missing sources) as a regression check that the compiler rejects them. Fixtures in `content/test-fixtures/invalid/`, exercised by `ContentCompilerTest` (all 6 tests passing).
- [x] 2.8 Add a `schemaVersion` field to the compiled index envelope (design.md decision 11) as a forward-compatibility hook for a possible future remote-refresh mechanism (decision 5, still deferred) — no fetch/versioning logic added, just the field.
- [x] 2.9 Document a reserved locale-suffix filename convention (`fact-021.es.md` alongside `fact-021.md`) in `content/facts/README.md` for possible future translations (design.md decision 12) — documentation only, no compiler/schema changes; a locale-suffixed file created today would correctly fail the build's filename/id-match check until real support is built.

## 3. Shared core: models and content loading

- [x] 3.1 Define `Fact` and `Category` models in the shared module matching the compiled index shape. `Fact`/`Source` domain models in `model/Fact.kt`; wire-shape DTOs (`FactIndexDto`/`FactDto`/`SourceDto`) kept separate/internal in `FactIndexDto.kt`.
- [x] 3.2 Implement the runtime loader that reads the bundled compiled index into in-memory `Fact` objects at app startup. `FactIndexLoader.parse(rawJson: String)` — pure function (platform layer owns reading the actual asset bytes), validates `schemaVersion`, maps DTOs to domain models. 3 passing tests.
- [x] 3.3 Implement category-based filtering logic (single and multi-category union filtering) over the loaded facts. `FactRepository.filterByCategories` — verified union (not intersection) semantics with a test.
- [x] 3.4 Implement the related-facts resolver (given a fact ID, return its related `Fact` objects). `FactRepository.relatedFacts`. 6 passing tests total for `FactRepository`.

## 4. Shared core: favorites and history persistence (`favorites-and-history`)

- [x] 4.1 Design the SQLDelight schema for favorites (fact ID + favorited flag/timestamp) and seen-history (fact ID + shown timestamp, scoped to the current notification cycle). Corrected during implementation into **three** tables, not two — see design.md decision 9's correction: `favoriteFact`, cycle-scoped `notificationShownFact`, and a durable `appOpenDay` log kept separate so the streak survives a notification-cycle reset.
- [x] 4.2 Implement favorite/unfavorite read+write operations in the shared module. `FavoritesStore` — 3 passing tests.
- [x] 4.3 Implement seen-history read+write operations (mark fact shown, query not-yet-shown eligible set, reset cycle). `NotificationHistoryStore` — 3 passing tests. (The "not-yet-shown eligible set" query itself lives in task group 5's selection logic, which combines this store's `shownFactIds()` with the eligible fact pool.)
- [x] 4.4 Wire the Android SQLDelight driver (`actual` implementation) so the shared queries run against a real local database on-device. `createBitcoinFactsDatabase(context)` in `shared/src/androidMain`; verified via a full `:androidApp:assembleDebug`. (A JVM JDBC driver was also added, `jvmTest`-only, purely so these stores are genuinely unit-testable without Android/Robolectric.)
- [x] 4.5 Implement the streak calculation as a derived read over seen-history timestamps (consecutive-day count, reset on a missed day) — no new stored counter. `StreakCalculator` (pure function) + `AppOpenLogStore` — 6 + 2 passing tests covering gaps, exact-today, and ends-yesterday-still-counts cases.

## 5. Shared core: daily notification selection (`daily-fact-notifications`)

- [x] 5.1 Implement the pure selection function: given the eligible (`notify_eligible`) fact set and the seen-history, return today's fact ID (random from not-yet-shown; reset-and-reshuffle when exhausted). `DailyFactSelector.selectNext` — no store dependency, injectable `Random`.
- [x] 5.2 Unit test the selection function's no-repeat-until-exhausted behavior and its reshuffle-on-exhaustion behavior. 4 tests across many seeds (property-style: asserts membership in the correct subset, not one hardcoded outcome).
- [x] 5.3 Define the shared-module interface the platform layer calls daily (e.g. `pickTodaysFact(): Fact`) and that records the pick into seen-history. `DailyFactPicker.pickTodaysFact()` — also calls `resetCycle()` when reshuffled and records the pick into `AppOpenLogStore` (a notification pick counts toward the streak, per the favorites-and-history spec). 4 integration tests against real (in-memory) SQLDelight stores, including a full-cycle-then-reshuffle end-to-end check.

## 6. Android app: fact browsing and detail (`fact-discovery`)

- [x] 6.1 Set up the Android app's base Compose navigation shell (list screen, detail screen, favorites screen). `navigation-compose` with 3 routes in `MainActivity.kt`; manual DI via `AppContainer` (no framework needed at this size).
- [x] 6.2 Establish the app's visual identity: color palette, typography, and basic Bitcoin-appropriate theming (treat as a fast creative pass, not a long design cycle). Dark-first Material3 theme, Bitcoin-orange (`#F7931A`) accent, bold headline weights - based on a style reference the user provided.
- [x] 6.3 Build the fact list screen consuming the shared loader, showing title + categories per fact. `FactListScreen.kt`.
- [x] 6.4 Build the category filter UI (multi-select against the canonical taxonomy) wired to the shared filtering logic. `FilterChip` row; verified union semantics visually on-device (selecting/deselecting correctly narrows and restores the list).
- [x] 6.5 Build the fact detail screen: full text, analogy, tappable source links, and a related-facts section (hidden when empty) that navigates to another fact's detail on tap. `FactDetailScreen.kt`. Found and fixed a real bug during on-device verification: `**bold**` markdown wasn't rendered (shown literally) - added `MarkdownBold.kt`, a small inline-bold parser, rather than pulling in a full markdown library for one construct. Verified related-fact navigation is correctly bidirectional (fact-001 ↔ fact-002).
- [x] 6.6 Add a share action to the fact detail screen that invokes Android's native share sheet (`Intent.ACTION_SEND`) with the fact's title and a short excerpt. Verified on-device - real share sheet opens with title+excerpt pre-populated.

## 7. Android app: favorites UI and streak display

- [x] 7.1 Add a favorite toggle control to both the list and detail screens, wired to the shared favorites read/write operations. Verified on-device on both screens; state stays consistent navigating between them (backed by the same hoisted `favoriteIds` state in `MainActivity`).
- [x] 7.2 Build the favorites list screen showing only currently-favorited facts. `FavoritesScreen.kt`, verified on-device.
- [x] 7.3 Display the current streak (from the shared streak calculation) somewhere visible on app open, e.g. the fact list screen header. Shown as a 🔥 badge in the list screen's top bar; `MainActivity.onCreate` records the app-open day.

## 8. Android app: daily notifications

- [x] 8.1 Implement the Android `actual` notification scheduler (WorkManager or AlarmManager) that calls the shared `pickTodaysFact()` once per day and fires a local notification with that fact's title. `DailyFactNotificationWorker` (CoroutineWorker) + `DailyFactScheduler` (daily `PeriodicWorkRequest`, `KEEP` policy). Verified on-device: permission granted, channel created, notification fires and displays correctly.
- [x] 8.2 Implement notification-tap deep link into the fact detail screen for the referenced fact. Found and fixed a real bug during on-device testing: the activity only read the `factId` intent extra in `onCreate`, so a tap while the app was already running (delivered via `onNewIntent`, not `onCreate`) was silently dropped. Fixed by holding the deep-link id in a `mutableStateOf` property that `onNewIntent` also updates. Verified end-to-end: notification tap while backgrounded correctly opens straight to that fact's detail screen.
- [x] 8.3 Handle the Android notification permission request flow (Android 13+ runtime permission) with a reasonable in-app prompt. Requested once via `ActivityResultContracts.RequestPermission` on first launch if not already granted; verified the system permission dialog appears. The app remains fully usable if denied (`showDailyFactNotification` checks and no-ops rather than crashing).

## 9. Android app: tip/support purchase (`support-tip`)

**Deferred** (user decision): real purchase-flow testing needs a signed build on a Play Console testing track with a test account, which isn't achievable in this local dev/emulator environment. Revisit once a Play Console listing exists, so the integration is built and verified in one pass rather than built now and verified much later.

- [ ] 9.1 Integrate an Android billing library and configure the one-time tip product.
- [ ] 9.2 Build the tip/support entry point in the UI (e.g. a settings or about screen) that is clearly optional and separate from all content.
- [ ] 9.3 On successful purchase, persist the purchase state locally and unlock the cosmetic reward (alternate icon/theme); verify purchase state survives app restart and billing library's restore-purchases flow.
- [ ] 9.4 Verify (manually) that every content/filtering/favorites/notification feature works fully with no purchase made.

## 10. Initial content authoring

**Deferred** (user decision): the user is building out the fact library themselves. The 2 example facts (`fact-001`, `fact-002`) authored during earlier task groups remain as working examples of the format and the "name the pre-Bitcoin gap" writing rule.

- [ ] 10.1 Draft an initial batch of facts (aim for enough breadth to exercise every canonical category at least once) each with a citation-backed `sources` entry.
- [ ] 10.2 Apply the "name the pre-Bitcoin gap" writing rule (design.md Decision 10) to each fact — state what was impossible or required a trusted third party before Bitcoin, and how it's solved now.
- [ ] 10.3 Self-review (or informal outside review) the initial batch for technical accuracy before considering v1 content-complete.

## 11. End-to-end verification

- [x] 11.1 Manually verify the full user flow on a real or emulated Android device: browse, filter, view detail with sources/related, share a fact, favorite, receive and tap a daily notification, view favorites list and streak, exercise the tip flow. Verified on an emulator throughout task groups 6-8: browsing, category filter (select/deselect), fact detail (markdown rendering, sources, bidirectional related-facts navigation), native share sheet, favoriting from both list and detail, the favorites screen, the streak badge, notification permission/firing, and notification-tap deep-linking (including the already-running-app case, where a real bug was found and fixed). The tip flow is excluded here since task group 9 is deferred.
- [x] 11.2 Confirm the app functions fully offline (airplane mode) end to end, since v1 has no server dependency. Verified on-device with wifi + mobile data disabled: cold app launch, full fact list, category filtering, and fact detail (including sources and related-facts, all bundled data) all rendered correctly with zero connectivity.

## 12. Journey mode (`journey-mode`)

- [x] 12.1 Build a new home/menu screen (list's top-bar streak/favorites carried over) with "Browse All" and "Random Journey" entry points; make it the nav graph's start destination. `HomeScreen.kt`; `selectedCategories` (list filter) kept separate from a new `journeyCategories` state so "Random Journey" is always unfiltered regardless of the list's current filter.
- [x] 12.2 Build the Journey screen: `HorizontalPager` over a shuffled fact list, each page showing the same full-detail content as the fact detail screen (title, body/analogy via `parseBoldMarkdown`, sources, favorite toggle) minus the related-facts section. `JourneyScreen.kt`; extracted `FactBodyAndSources` composable shared with `FactDetailScreen` to avoid duplicating that rendering.
- [x] 12.3 Implement reshuffle-and-loop: reaching the end of the shuffled order reshuffles the same fact set and continues, rather than stopping. Found and fixed a real bug during on-device testing: the initial virtual page count (`Int.MAX_VALUE / 2`) overflowed `HorizontalPager`'s internal pixel-based layout math and caused a genuine ANR ("Bitcoin Facts isn't responding") on swipe - not an environment/emulator flakiness issue, an actual app hang. Fixed by using a much smaller (100,000) but still practically-infinite virtual page count; verified stable across multiple reshuffle cycles afterward.
- [x] 12.4 Add a "Start Journey" action on the list screen that launches the Journey screen scoped to the list's current (possibly empty) category filter selection. `ExtendedFloatingActionButton` on `FactListScreen`.
- [x] 12.5 Handle an empty journey scope (category selection matching zero facts) with a clear message and a way back, instead of a crash or blank screen. `EmptyJourneyMessage`.
- [x] 12.6 Verify on-device: swiping through a journey, favoriting from within it, category-scoped vs. unfiltered journeys, reshuffle-on-loop, and the empty-scope message. All verified, including confirming a favorite toggle from within Journey via direct DB inspection (the on-screen heart icon is only reliably readable at pixel-zoom, not by eye at emulator screenshot scale).
