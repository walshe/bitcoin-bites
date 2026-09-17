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

- [ ] 4.1 Design the SQLDelight schema for favorites (fact ID + favorited flag/timestamp) and seen-history (fact ID + shown timestamp, scoped to the current notification cycle).
- [ ] 4.2 Implement favorite/unfavorite read+write operations in the shared module.
- [ ] 4.3 Implement seen-history read+write operations (mark fact shown, query not-yet-shown eligible set, reset cycle).
- [ ] 4.4 Wire the Android SQLDelight driver (`actual` implementation) so the shared queries run against a real local database on-device.
- [ ] 4.5 Implement the streak calculation as a derived read over seen-history timestamps (consecutive-day count, reset on a missed day) — no new stored counter.

## 5. Shared core: daily notification selection (`daily-fact-notifications`)

- [ ] 5.1 Implement the pure selection function: given the eligible (`notify_eligible`) fact set and the seen-history, return today's fact ID (random from not-yet-shown; reset-and-reshuffle when exhausted).
- [ ] 5.2 Unit test the selection function's no-repeat-until-exhausted behavior and its reshuffle-on-exhaustion behavior.
- [ ] 5.3 Define the shared-module interface the platform layer calls daily (e.g. `pickTodaysFact(): Fact`) and that records the pick into seen-history.

## 6. Android app: fact browsing and detail (`fact-discovery`)

- [ ] 6.1 Set up the Android app's base Compose navigation shell (list screen, detail screen, favorites screen).
- [ ] 6.2 Establish the app's visual identity: color palette, typography, and basic Bitcoin-appropriate theming (treat as a fast creative pass, not a long design cycle).
- [ ] 6.3 Build the fact list screen consuming the shared loader, showing title + categories per fact.
- [ ] 6.4 Build the category filter UI (multi-select against the canonical taxonomy) wired to the shared filtering logic.
- [ ] 6.5 Build the fact detail screen: full text, analogy, tappable source links, and a related-facts section (hidden when empty) that navigates to another fact's detail on tap.
- [ ] 6.6 Add a share action to the fact detail screen that invokes Android's native share sheet (`Intent.ACTION_SEND`) with the fact's title and a short excerpt.

## 7. Android app: favorites UI and streak display

- [ ] 7.1 Add a favorite toggle control to both the list and detail screens, wired to the shared favorites read/write operations.
- [ ] 7.2 Build the favorites list screen showing only currently-favorited facts.
- [ ] 7.3 Display the current streak (from the shared streak calculation) somewhere visible on app open, e.g. the fact list screen header.

## 8. Android app: daily notifications

- [ ] 8.1 Implement the Android `actual` notification scheduler (WorkManager or AlarmManager) that calls the shared `pickTodaysFact()` once per day and fires a local notification with that fact's title.
- [ ] 8.2 Implement notification-tap deep link into the fact detail screen for the referenced fact.
- [ ] 8.3 Handle the Android notification permission request flow (Android 13+ runtime permission) with a reasonable in-app prompt.

## 9. Android app: tip/support purchase (`support-tip`)

- [ ] 9.1 Integrate an Android billing library and configure the one-time tip product.
- [ ] 9.2 Build the tip/support entry point in the UI (e.g. a settings or about screen) that is clearly optional and separate from all content.
- [ ] 9.3 On successful purchase, persist the purchase state locally and unlock the cosmetic reward (alternate icon/theme); verify purchase state survives app restart and billing library's restore-purchases flow.
- [ ] 9.4 Verify (manually) that every content/filtering/favorites/notification feature works fully with no purchase made.

## 10. Initial content authoring

- [ ] 10.1 Draft an initial batch of facts (aim for enough breadth to exercise every canonical category at least once) each with a citation-backed `sources` entry.
- [ ] 10.2 Apply the "name the pre-Bitcoin gap" writing rule (design.md Decision 10) to each fact — state what was impossible or required a trusted third party before Bitcoin, and how it's solved now.
- [ ] 10.3 Self-review (or informal outside review) the initial batch for technical accuracy before considering v1 content-complete.

## 11. End-to-end verification

- [ ] 11.1 Manually verify the full user flow on a real or emulated Android device: browse, filter, view detail with sources/related, share a fact, favorite, receive and tap a daily notification, view favorites list and streak, exercise the tip flow.
- [ ] 11.2 Confirm the app functions fully offline (airplane mode) end to end, since v1 has no server dependency.
