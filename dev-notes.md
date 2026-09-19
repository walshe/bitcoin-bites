# Dev notes: navigating the source

The [README](README.md#high-level-folder-guide) explains *what* each folder
is. This doc is the complementary *how to actually follow the code* guide —
where to start reading, and how data flows from a fact's Markdown file to
something on screen.

Read this once, top to bottom, if you're new to the codebase and want a
mental model before you start poking around. After that, use it as a map to
jump back into.

## The one-sentence architecture

Facts are compiled from Markdown into a JSON asset at build time; the app
loads that JSON once at startup into an in-memory repository; Compose screens
read from that repository and from a local SQLite database (favorites,
streaks, notification history) that the app *does* write to at runtime.

Everything content-related is read-only at runtime. Everything
user-state-related (favorites, streaks, journey position) lives in SQLite via
SQLDelight, in `shared/`.

## Start here: trace a fact from Markdown to screen

This is the most useful path to walk if you want to understand the whole
app, because it touches almost every module.

1. **`content/facts/*.md`** — a fact starts life as one Markdown file with a
   YAML frontmatter block (id, title, categories, sources, order, related...)
   and a body. See [`content/facts/README.md`](content/facts/README.md) for
   the authoring format.
2. **`content-compiler/.../ContentCompiler.kt` + `FrontmatterParser.kt`** —
   at build time, a Gradle task (`compileFactContent`, wired into
   `androidApp/build.gradle.kts` so it runs before every app build) parses
   every fact file, validates it (categories must exist in `taxonomy`,
   `related` IDs must point at real facts), reflows the body into clean
   paragraphs (`FrontmatterParser.normalizeBody()`), and writes one
   `facts.json` — see `content-compiler/.../Main.kt` for the actual CLI
   entrypoint. A malformed fact fails the *build*, not the app at runtime.
3. **`androidApp/.../data/AppContainer.kt`** — at app startup, `facts.json`
   is bundled as an Android asset and loaded once
   (`context.assets.open("facts.json")`) into a `FactRepository`
   (`shared/.../FactRepository.kt`), held for the app's whole lifetime — this
   is the one place the JSON gets parsed.
4. **`shared/.../FactRepository.kt`** — the in-memory query layer over that
   list: `all()`, `findById()`, `filterByCategories()` (union semantics —
   picking two categories shows facts in *either*), `relatedFacts()`.
5. **`androidApp/.../ui/FactListScreen.kt` / `FactDetailScreen.kt` /
   `HomeScreen.kt` / `JourneyScreen.kt`** — Compose screens call into
   `container.factRepository` to get the facts they render. `FactDetailScreen`
   is the one to read for the full single-fact experience: title, body
   (`FactContent.kt` does the Markdown-bold rendering and the Analogy-card
   split — see its doc comment), favorite toggle, share.

If you're adding a new fact, you only ever touch step 1 — steps 2–5 are
generic machinery that picks it up automatically on the next build.

## App startup and navigation

- **`BitcoinFactsApplication.kt`** — builds one `AppContainer` for the
  process (`androidApp/.../data/AppContainer.kt`). This is manual DI, no
  framework: everything the app needs (fact repository, SQLDelight-backed
  stores, the daily-fact picker) is a `by lazy` property on `AppContainer`,
  built once and passed down.
- **`MainActivity.kt`** — the single Activity. Two things worth knowing:
  - It hosts the entire UI as one Compose `NavHost` (`BitcoinFactsNavHost`,
    defined at the bottom of the same file) — there's no second Activity to
    look for. Each screen is registered against a route constant
    (`ROUTE_HOME`, `ROUTE_LIST`, `ROUTE_DETAIL`, ...) near the top of the
    file; that block *is* the navigation graph.
  - It handles deep links from notification taps (`EXTRA_FACT_ID`) in both
    `onCreate` (cold start) and `onNewIntent` (app already running) — see the
    comment on `deepLinkFactId` for why both are needed.
- Screen-to-screen state that doesn't belong in the nav route itself
  (selected category filters, which journey mode was picked, the favorited
  set) lives as `remember { mutableStateOf(...) }` in `BitcoinFactsNavHost`
  and gets threaded into screens as parameters — there's no shared
  ViewModel/state-holder class. For an app this size that's a deliberate
  simplicity choice, not an oversight; don't be surprised there's no
  `ViewModel` to find.

## Local storage (favorites, streaks, journey, notification history)

All in `shared/src/commonMain/kotlin/.../history/`, backed by SQLDelight
(`.sq` files under `shared/src/commonMain/sqldelight/.../db/`, one per
table: `Favorites.sq`, `AppOpenLog.sq`, `JourneyProgress.sq`,
`NotificationHistory.sq`). Each table has a matching `*Store` class
(`FavoritesStore`, `AppOpenLogStore`, `JourneyProgressStore`,
`NotificationHistoryStore`) that wraps the generated SQLDelight queries in a
small Kotlin API — read a `*Store` class alongside its `.sq` file, they're
meant to be read together.

`AndroidDatabaseFactory.kt` (`shared/src/androidMain/`) is the only
Android-specific piece — it's the `actual` half of an `expect`/`actual` pair
that opens the SQLite driver. An iOS build would add a matching `iosMain`
implementation; nothing in `commonMain` changes.

`StreakCalculator.kt` is pure logic over `AppOpenLogStore` data (no I/O of
its own) — read it separately if you're touching streak behavior.

## Daily notifications, end to end

1. `DailyFactScheduler.kt` (`androidApp/.../notifications/`) schedules a
   recurring WorkManager job.
2. `DailyFactNotificationWorker.kt` runs once a day: it asks
   `container.dailyFactPicker.pickTodaysFact()` for *what* to notify about,
   then calls `showDailyFactNotification()` (`NotificationHelper.kt`) to fire
   the actual Android notification (with `EXTRA_FACT_ID` set for the
   deep-link tap to pick up later).
3. The picking *decision* lives in `shared/.../notifications/`
   (`DailyFactPicker.kt` + `DailyFactSelector.kt`) — no-repeat logic, reshuffle
   when the eligible pool is exhausted, persisted via
   `NotificationHistoryStore`. This split matters for iOS: only step 2 (firing
   the OS notification) needs a platform-specific rewrite; step 3 is shared
   as-is.

## Categories / taxonomy

`taxonomy/.../Category.kt` is the single enum every fact's `categories:`
frontmatter and every filter chip in the UI both reference. It's split into
its own tiny module (rather than living in `shared`) purely so the JVM-only
`content-compiler` can validate against it without depending on the rest of
`shared`. If you're adding a new category, this is the one place to add it —
`content-compiler` will then reject any fact file with a category *not* in
this enum, and `CategoryDisplay.kt` (`androidApp/.../ui/`) is where you'd add
its display name/icon for the filter UI.

## Where the tests live

- `content-compiler/src/test/` — validates the Markdown → JSON compilation
  itself (`ContentCompilerTest.kt`, `FrontmatterParserTest.kt`), including
  deliberately-broken fixtures under `content/test-fixtures/`.
- `shared/src/commonTest/` — pure-Kotlin logic with no platform dependency:
  `FactRepositoryTest.kt`, `FactIndexLoaderTest.kt`, `StreakCalculatorTest.kt`,
  `DailyFactSelectorTest.kt`.
- `shared/src/jvmTest/` — anything that needs a real SQLite driver to run
  against (the `*StoreTest.kt` files, `DailyFactPickerTest.kt`) — JVM tests
  can use SQLDelight's JVM driver without an Android emulator, which is why
  these are split out from `commonTest`.

Run all of it (plus a debug assemble) with the command in the README's
[Building](README.md#building) section.

## A few things that look like they should exist but don't (yet)

- No ViewModel layer — screen state lives in `MainActivity.kt`'s
  `BitcoinFactsNavHost` (see [App startup and navigation](#app-startup-and-navigation)).
  Revisit this if `androidApp/` grows past its current size.
- No dependency-injection framework — `AppContainer.kt` is hand-rolled, see
  its own doc comment for why.
- No `iosApp/` yet — `taxonomy` and `shared` are built KMP-ready for it, but
  the actual SwiftUI app doesn't exist yet. See the README's `shared/`
  section for which seams (`expect`/`actual`) are already in place for it.
