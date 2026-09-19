# Bitcoin Bites

A no-server, offline-first Bitcoin facts/trivia app. Every fact is a short,
plainly-worded explanation with an analogy, sourced from real Bitcoin books
and pulled straight into the app at build time — no network calls, no
backend.

Currently ships on Android (Kotlin Multiplatform + Jetpack Compose), with iOS
planned as a fast-follow once the shared Kotlin core is stable.

## Development

For a guided walkthrough of how to actually read the code — where to start,
and how a fact flows from Markdown file to on-screen UI — see
[`dev-notes.md`](dev-notes.md).

### High-level folder guide

```
bitcoin-mobile-app/
├── content/            # Fact source content (Markdown + YAML)
├── content-compiler/   # Build-time tool: content/*.md -> facts.json
├── taxonomy/           # The canonical Category enum (KMP, shared with iOS)
├── shared/             # Cross-platform Kotlin core (KMP, shared with iOS)
├── androidApp/         # The Android app itself (Compose UI, not reused by iOS)
├── openspec/           # Change proposals, design decisions, specs
├── bitcoin-bites-*-assets/  # Exported app-icon artwork (design history)
└── gradle*, settings.gradle.kts, build.gradle.kts  # Gradle wiring
```

#### `content/`

The single source of truth for every fact in the app. One Markdown file per
fact, living in `content/facts/*.md`, each with a YAML frontmatter block
(id, title, categories, sources, order, related, ...) followed by the fact's
body text. See [`content/facts/README.md`](content/facts/README.md) for the
full authoring format, the category taxonomy table, and the fact-ordering
convention.

`content/test-fixtures/` holds intentionally-invalid fact files used by
`content-compiler`'s tests to check that bad content fails the build with a
useful error instead of shipping broken data.

Not code, and not KMP — this is data that both the Android and future iOS
build both compile from, so it's the one folder that's platform-agnostic by
definition rather than by module setup.

#### `content-compiler/`

A small JVM-only Gradle module (not part of the app itself) that runs at
build time: it reads every file in `content/facts/`, validates the
frontmatter (categories must exist in `taxonomy`, `related` IDs must point
at real facts, etc.), mirrors one-sided `related` links onto both facts, and
writes a single compiled `facts.json` that gets bundled into the app as an
asset. If a fact file is malformed, the build fails here with a specific
error rather than crashing the app later.

iOS won't reuse this module directly (it's a JVM/Gradle tool, not KMP), but
it will need to run the same way for an iOS build — compiling the same
`content/` folder into a `facts.json` iOS bundles as a resource, so both
platforms ship from one content source.

#### `taxonomy/`

A tiny Kotlin Multiplatform module holding just one thing: the `Category`
enum — the closed, canonical list of fact categories (e.g. `money`,
`proof-of-work`, `keys`, `custody`). It's split out from `shared` because
both `content-compiler` (a plain JVM tool) and the app itself need to agree
on the exact same category list, and this is the smallest module that can
sit underneath both.

**Reused by iOS as-is** — it's pure Kotlin with no Android dependency, so it
compiles straight into the iOS framework.

#### `shared/`

The cross-platform Kotlin core: fact loading (`FactIndexLoader`,
`FactIndexDto`), the in-memory `FactRepository` (filtering, related-fact
resolution), the `Fact` model, daily-notification selection logic
(`DailyFactPicker`, `DailyFactSelector`), and SQLDelight-backed local storage
for favorites, streak tracking, and notification history
(`shared/src/commonMain/sqldelight/`, plus the `history/` package). All of
this lives in `commonMain` and has no Android UI dependency.

`shared/src/androidMain/` holds the one Android-specific piece needed to
wire up the shared SQLDelight database on Android (`AndroidDatabaseFactory`)
— an iOS build would add a matching `iosMain` implementation of the same
`expect`/`actual` interface, not touch `commonMain` at all. The other
`expect`/`actual` seam reserved for iOS is firing the actual OS notification
(Android uses WorkManager; iOS would use `UNUserNotificationCenter`) — the
*decision* of which fact to notify about stays in `commonMain`, only the
platform API call to fire it varies.

**This is the module iOS is built around.** Per the project's KMP plan, iOS
consumes `shared` and `taxonomy` as a generated `.xcframework`, wrapped for
SwiftUI. Almost everything genuinely cross-platform about this app — content
loading, filtering, favorites/streaks, notification selection — is meant to
live here specifically so it never has to be rewritten for iOS.

#### `androidApp/`

The Android app itself: `MainActivity`, Jetpack Compose screens (`ui/`),
the app-level dependency container and local data wiring (`data/`), and
Android notification plumbing (`notifications/`). This is Android-only by
design — **iOS will have its own separate `iosApp/` module** with SwiftUI
screens built against the same `shared` core, not a port of this code.

#### `openspec/`

Process artifacts, not app code: change proposals, the running `design.md`
decision log, and specs for this project. Useful for understanding *why* a
given piece of the app looks the way it does, without needing to dig through
git history or chat logs.

#### `bitcoin-bites-*-assets/`

Exported app-icon artwork at various resolutions, kept as a record of the
icon design process (a generic Bitcoin icon, a "book" concept, and the
bunny-mascot version actually in use) rather than something the build
consumes directly — the app's real icon resources live under
`androidApp/src/main/res/`.

### Building

```
./gradlew :content-compiler:test :shared:jvmTest :androidApp:assembleDebug
```

Compiles and validates all fact content, runs the shared-core test suite,
and assembles the debug Android APK.

## Play Store preparation and deployment

### Release signing (one-time setup per machine)

Release builds (`bundleRelease`/`assembleRelease`) need a signing keystore
that deliberately lives **outside** the repo, since it can never be
committed:

1. Generate it once:
   ```
   keytool -genkeypair -v -keystore ~/android-keystores/bitcoin-bites-upload.jks \
     -alias bitcoin-bites-upload -keyalg RSA -keysize 2048 -validity 10000
   ```
2. Copy [`keystore.properties.example`](keystore.properties.example) to
   `keystore.properties` (already gitignored) in the repo root, and fill in
   the real `storeFile` path + passwords from step 1.

`androidApp/build.gradle.kts` loads `keystore.properties` if present and
wires it up as the `release` signing config; without the file, every other
build target (debug builds, tests) still works fine — only a *signed*
release build needs it.

### Building a release bundle

```
./gradlew bundleRelease
```

Produces `androidApp/build/outputs/bundle/release/androidApp-release.aab`
— the `.aab` (Android App Bundle) format Play Store requires, already
signed. Sanity-check the signature any time with:

```
jarsigner -verify -verbose:summary androidApp/build/outputs/bundle/release/androidApp-release.aab
```

### Shipping an update

1. Bump `versionCode` (must strictly increase every release) and
   `versionName` (human-readable, e.g. `"0.1"` → `"0.1.1"`) in
   `androidApp/build.gradle.kts`.
2. `./gradlew bundleRelease` for a fresh signed `.aab`.
3. Upload it to the existing app listing in Play Console, add release
   notes, and roll out — no new fee, no re-doing the store listing. Consider
   a staged rollout (e.g. 10-20% of users first) for anything riskier than a
   trivial fix, and/or installing via the Internal Testing track first to
   catch problems before any real users see them.

### Play Console account

One-time $25 registration fee, covers unlimited apps and unlimited updates
forever (Play only additionally takes a revenue cut on paid
apps/IAP — irrelevant here, this app is free with no IAP). Individual
account (not Organization — that needs a registered business/D-U-N-S
number, unnecessary for a personal project like this one). Expect a few
days for Google's identity verification to clear before you can create the
app listing; a physical Android device is also needed to verify device
access from the Play Console mobile app.

### Store listing requirements

- **Privacy policy URL** (required even though the app collects nothing):
  `https://walshe.github.io/cv/bitcoin-bites/` — source lives in the
  separate `walshe/cv` GitHub Pages repo, not this one.
- **App icon**: a 512×512 PNG uploaded directly in Play Console, separate
  from the APK/bundle's own launcher icon resources.
- **Screenshots**: phone screenshots in Play's required sizes, plus a
  feature graphic.
- **Data Safety form**: straightforward for this app — no data collected,
  no network calls, no third-party SDKs; everything (favorites, streak,
  journey progress) stays in local SQLite on-device (see
  [`shared/`](#shared) above).
- **Content rating questionnaire**: filled out in Console.
