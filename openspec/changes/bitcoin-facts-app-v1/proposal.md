## Why

There's no simple, offline, no-nonsense app that teaches curious newcomers Bitcoin concepts through short, plain-language facts with analogies — most existing "Bitcoin trivia" content is either a scattered Twitter thread, a paywalled course, or a wall of jargon. This project builds one, doubling as a hands-on way to learn Kotlin/Kotlin Multiplatform (KMP) and produce a portfolio-quality piece for the Bitcoin community. Content correctness and design quality matter more here than growth or revenue — this is a credibility piece, not a growth play.

## What Changes

- New Markdown-based fact content format (one file per fact, YAML frontmatter: id, title, categories, difficulty, related, sources, notify_eligible) as the single source of truth for all app content.
- New build-time content pipeline that validates every fact's categories against a fixed canonical taxonomy, auto-mirrors symmetric `related` links, and compiles all facts into a runtime index bundled into the app (no server, no remote content fetch in v1).
- New shared Kotlin (KMP) core module containing: fact/category models, the content loader, category+text filtering, related-facts resolution, daily-notification selection logic (random, no-repeat until pool exhausted), and a SQLDelight-backed favorites/seen-history store.
- New Android app (Jetpack Compose) consuming the shared core: browse/filter facts by category, view fact detail (analogy, sources, related facts), favorite facts, receive one daily local notification.
- New "share a fact" action on the fact detail screen, using the platform's native share sheet — no new backend, just a share intent over content already in the app.
- New daily-open streak counter, derived entirely from the seen-fact history already persisted for notification no-repeat tracking — no new persistence layer, just a computed value.
- New content-writing guideline (documented in design.md, applied during content authoring, not a code feature): every fact should name the specific pre-Bitcoin gap it closes, to make the "nothing like this has existed before" reaction explicit rather than implicit.
- New optional one-time "tip/support" in-app purchase that unlocks a cosmetic reward (e.g. alternate app icon or theme) — no content is ever gated behind payment.
- New "Journey" swipeable browsing mode: a new home/menu screen offers "Browse All" (today's list) or "Random Journey" (an unfiltered shuffle through every fact, swiped one at a time via `HorizontalPager`); a "Start Journey" action on the list screen launches the same swipeable mode scoped to whatever categories are currently checked in the existing filter chips (no new filter UI). A journey reshuffles and loops forever rather than stopping at the end.
- iOS app (SwiftUI, consuming the same shared core) is explicitly **out of scope for this change** and deferred to a follow-up change once the shared core and Android app are stable — this keeps this change's task list focused on validating the content pipeline and shared logic in pure Kotlin first.
- Remote content sync/refresh, user accounts, social/leaderboard features, and notification filtering by category are explicitly deferred (not decided against) — candidates for later changes if they turn out to matter after v1 ships.

## Capabilities

### New Capabilities
- `fact-content-pipeline`: Markdown+frontmatter fact authoring format, canonical category taxonomy validation, related-fact symmetric linking, build-time compilation into a bundled runtime index.
- `fact-discovery`: Browsing, filtering (by category), viewing fact detail (analogy text, source citations, related facts), and sharing a fact via the platform's native share sheet — via the shared Kotlin core and the Android UI.
- `daily-fact-notifications`: Selecting and scheduling exactly one local notification per day, drawing randomly from the fact pool without repeats until exhausted, then reshuffling.
- `favorites-and-history`: Persisting user-favorited facts and a seen-fact history (used for notification no-repeat tracking, an in-app progress view, and a derived daily-open streak counter), stored locally via SQLDelight.
- `support-tip`: An optional, one-time, non-content-gating in-app purchase that unlocks a cosmetic reward as a thank-you.
- `journey-mode`: A swipeable, one-fact-at-a-time browsing mode reachable either unfiltered ("Random Journey" from the home screen) or scoped to the list screen's current category filter ("Start Journey"), reshuffling and looping forever rather than ending.

### Modified Capabilities
_None — this is a new project with no existing specs._

## Impact

- **New repo structure**: `content/facts/*.md` (source of truth), a build-time content-compiler tool/script, a KMP `shared/` module, and an `androidApp/` module. No `iosApp/` module yet.
- **New dependencies**: Kotlin Multiplatform + Gradle setup, SQLDelight, a Markdown/YAML-frontmatter parser for the build step, Jetpack Compose, Android's local notification APIs (WorkManager/AlarmManager), and an Android in-app billing library for the tip IAP.
- **No backend/server infrastructure** — the app is fully offline-capable; all content ships inside the app binary.
- **No breaking changes** — greenfield project.
