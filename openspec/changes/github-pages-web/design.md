## Context

Bitcoin Bites ships on Android with no backend. Every fact lives in
`content/facts/*.md`. At build time `content-compiler` validates them (taxonomy
categories, `related` IDs, required frontmatter) and writes one `facts.json` that the
app bundles. Interactive state (favorites, guided/random progress, app-open log for
the streak, notification history) lives in a local SQLDelight database in `shared/`.

The repository is `walshe/bitcoin-bites`. Free-tier GitHub Pages serves only static
files from a public repo, at `https://walshe.github.io/bitcoin-bites/`, so every URL
lives under the `/bitcoin-bites/` sub-path. Nothing can run on the server side: no
API, no database, no scheduled jobs.

The Android UI's formatting rules are relevant here: `FactContent.kt` splits the
trailing `**Analogy:**` paragraph out of the body and renders `**bold**`.
Category display names live in `androidApp/.../CategoryDisplay.kt`, not in
`:taxonomy`.

## Goals / Non-Goals

**Goals:**
- A static site built from the same content source and the same validation, so a
  fact that would break the app build also breaks the site build.
- A real, crawlable, shareable URL for every fact, readable with JavaScript disabled.
- Feature parity with the app's core reading experience: All Bites with category
  filter, fact detail with related facts, Guided Bites, Random Bites, favorites,
  streak, share.
- Installable and fully usable offline after the first visit.
- Zero-cost hosting and hands-off deployment on merge to `main`.

**Non-Goals:**
- Daily push notifications (Web Push needs a sending server).
- Sync between devices or between the web and the Android app.
- The tip purchase or cosmetic rewards.
- Running the Compose UI on the web (Compose/Wasm), or a JS framework or bundler.
- Per-fact generated social preview images (one shared brand image for now).

## Decisions

### 1. Generator is a new JVM Gradle module (`:web`) that calls `ContentCompiler` in-process
`:web` depends on `:content-compiler` and calls `ContentCompiler.compile(content/facts)`
directly, so validation and `related`-link mirroring stay identical. It writes the
site to `web/build/site/`, including a copy of `facts.json` for the client-side
journeys. The Gradle task is `:web:buildSite`.

*Alternatives:* Hugo, Jekyll, Astro or Eleventy would bring a second toolchain and a
second frontmatter parser that could drift from the compiler's validation. Reading the
`facts.json` Android generates would tie the site to the Android build.

### 2. HTML from Kotlin string templates with one escaping helper
The site has a handful of page shapes (layout, home, list, fact, journey shell,
favorites, 404). Kotlin raw-string templates plus one `html()` escape function keep
this dependency-free and easy to read. Every interpolated content value goes through
the escaper. The only HTML that bypasses it comes from the `**bold**` renderer, which
escapes first and only then turns bold markers into `<strong>`.

*Alternative:* `kotlinx.html` gives type safety, but it's a new dependency and harder
to scan for a site this small. It's worth revisiting if the template count grows.

### 3. Body formatting mirrors `FactContent.kt`
The generator uses the same rules: split paragraphs on blank lines, pull out the
`**Analogy:**` paragraph into its own styled block, render `**bold**`. These rules are
a few lines long, so they are reimplemented in `:web` with their own tests rather than
moved out of `androidApp`. Category display names get the same treatment: `:web`
holds its own `Category → label` map, and a test fails if any `Category` lacks one.

*Alternative:* moving the formatting and labels into `:taxonomy` or `shared/` would
mean changing Android code. That's a reasonable follow-up refactor, but it isn't
needed for this change.

### 4. Pre-rendered pages first, JavaScript as an enhancement
| URL | Pre-rendered content | JS adds |
|---|---|---|
| `/` | hero, the three entry buttons | streak, "continue reading" card |
| `/facts/` | full list of every fact (title, categories) | category filter chips, favorite toggles |
| `/facts/<id>/` | full fact, analogy, related links (sources hidden, like the app's `SHOW_SOURCES`) | favorite toggle, share, records progress |
| `/guided/`, `/random/` | shell with a no-JS fallback link to `/facts/` | the swipeable journey, built from `facts.json` |
| `/favorites/` | shell | list read from storage |

Fact pages and the list page work fully without JavaScript. Only journeys and
favorites need it, since they depend on per-user state anyway.

The published `facts.json` is the compiled index plus pre-rendered fields
(`html`, `shareText`, `path`), so the client never formats or escapes fact
content itself.

### 5. Vanilla ES modules, logic reimplemented in JS instead of a Kotlin/JS target
The client code is a few small native ES modules with no build step: `storage.js`,
`journey.js`, `streak.js`, `share.js`, `filter.js`. The only shared logic worth
reusing is `StreakCalculator` (~15 lines) and shuffle/reshuffle. Porting that is
cheaper than adding a `js()` target to `shared/`, which would pull SQLDelight into the
JS build and add a Kotlin/JS bundle download. The JS streak module is tested against
the same cases as `StreakCalculatorTest`, run with Node's built-in test runner
(`node --test`, no npm dependencies).

*Alternative:* a Kotlin/JS target gives a single source of truth, but at the cost of
build complexity and payload, which isn't justified for ~30 lines of logic.

### 6. Browser storage: namespaced, versioned `localStorage` that never breaks the page
One JSON value per concern under a `bb.v1.` prefix: `favorites` (array of IDs),
`progress.guided` / `progress.random` (last fact ID), `opens` (local-time epoch days,
capped at the last 400). Every read and write is wrapped in try/catch and falls back
to in-memory state, so private browsing or blocked storage degrades without breaking
anything. IndexedDB isn't needed at this size.

### 7. Journeys match the Android semantics
Guided Bites follows the canonical `order` and resumes at the stored last fact. Random
Bites shuffles, then reshuffles and loops at the end. Navigation is by swipe (pointer
events), arrow keys, and visible Previous/Next buttons. The current fact ID is stored
in the URL hash (`/guided/#<id>`) so the browser back button and reloads behave
sensibly. Each card's title links to the fact's own page.

### 8. URLs: relative internally, absolute only where required
Internal links are relative, so the site works under `/bitcoin-bites/`, at a custom
domain later, or in a local preview. Canonical links, `og:url`, `og:image` and
`sitemap.xml` need absolute URLs. These come from a `siteBaseUrl` Gradle property
that CI fills from `actions/configure-pages`' `base_url` output, with
`https://walshe.github.io/bitcoin-bites/` as the default.

### 9. Offline: generated service worker with a content-hashed cache name
At build time the generator writes `sw.js` with the full list of files to precache
and a cache name derived from a hash of the site's contents. Pages are served
network-first with a cache fallback, so a new deploy shows up as soon as the user is
online. Static assets are served cache-first. When a new service worker activates, it
deletes caches with other names. The whole site is well under 1 MB, so precaching
everything is cheap.

### 10. Share: Web Share API with a copy-link fallback
The shared text matches Android's (title, body with bold markers stripped, "— Shared
from Bitcoin Bites"), but the link is the fact's own web URL instead of the Play
Store. Where `navigator.share` doesn't exist (most desktop browsers), the button copies
the link to the clipboard and shows a short confirmation.

### 11. Deployment: official Pages actions, built output never committed
`.github/workflows/pages.yml` runs on pushes to `main` and on `workflow_dispatch`. The
steps are checkout, JDK 17, `actions/configure-pages`, `./gradlew :web:buildSite :web:test`,
`node --test web/src/test/js`, `actions/upload-pages-artifact` (from `web/build/site`),
then `actions/deploy-pages`. It sets `pages: write` and `id-token: write`
permissions, plus a `concurrency` group so overlapping deploys don't race. The
"GitHub Actions" Pages source deploys straight from the artifact, so no
`gh-pages` branch or committed HTML is needed.

### 12. Visual design matches the app
Dark theme with the same tokens as `Color.kt` (Bitcoin orange `#F7931A` on
`#121212`), system font stack, readable line length, phone-first layout. Icons and the
manifest icon come from the existing `bitcoin-bites-*-assets/` artwork.

### 13. "Get the app" target is a build-time setting, not hard-coded
The site header and home page show a "Get the Android app" link. Its target comes
from an `appDownloadUrl` Gradle property, defaulting to
`https://github.com/walshe/bitcoin-bites/releases/latest/download/bitcoin-bites.apk`.
At Play launch, the default (or the CI value) switches to the Play Store listing,
and the next site deploy picks it up. No app release is needed.

To keep that APK link stable, `release.yml` uploads a second copy of each release's
APK named `bitcoin-bites.apk` alongside the versioned one. GitHub's
`releases/latest/download/<name>` then always resolves to the newest build.

*Alternatives:* the `releases/latest` page is stable too, but it adds a step and
shows source archives that confuse non-developers. Pointing at the Play Store now
fails for anyone who isn't an opted-in tester.

### 14. Android share footer links to the fact's web page
`shareFact()` replaces the Play Store URL with
`https://walshe.github.io/bitcoin-bites/facts/<id>/`, built from a constant base URL
plus `fact.id`. Fact IDs are already the stable file-name slugs, so the links never
need to change. The attribution line stays. This works for every recipient right
away, including iPhone users and people who won't install anything, and the page's
"Get the app" link funnels Android users to the app.

The Android release with this footer must ship **after** the site is live, or early
shares would link to a 404.

*Alternatives:* keeping the Play Store URL means a broken link for most recipients
until launch. Including both links makes the share text longer, and a sideload-APK
link in shared text puts people off. The web page can offer the app instead.

## Risks / Trade-offs

- **The repo must be public for free Pages.** → Confirm this before enabling Pages.
  The fallback is a separate public repo that only receives the built site, which
  needs a deploy token and adds one more moving part.
- **`main` is 74 commits behind `impl/bitcoin-facts-app-v1`.** The site deploys from
  `main`, so it goes live only once this work, and the app content it builds on, is
  merged. → Merge order is a release decision. `workflow_dispatch` allows a manual
  deploy from `main` at any time.
- **Formatting rules and category labels are duplicated between `androidApp` and
  `:web`.** → Tests on both sides pin the behavior. Moving them into a shared module is
  a follow-up.
- **The streak logic lives in both Kotlin and JS.** → The JS tests use the same cases
  as `StreakCalculatorTest`.
- **Browser storage can be cleared by the user or evicted by the browser** (Safari
  evicts it after 7 days without a visit for sites that aren't installed). → It holds
  only favorites, progress and streak, never content. Installing the PWA makes it more
  durable. This limitation is documented, not engineered around.
- **A stale service worker could keep serving an old site.** → Network-first pages,
  hash-versioned caches, and `skipWaiting`/`clients.claim` when a new worker installs.
- **The web base URL is now baked into shipped Android builds.** A later custom
  domain doesn't break old shares, because GitHub Pages automatically redirects
  `walshe.github.io/bitcoin-bites/...` to the custom domain. → Never rename or delete
  fact IDs. Treat them as permanent URLs.
- **The sideloaded APK and the Play build are signed with different keys** (Play App
  Signing). Anyone who installs the GitHub APK must uninstall it, losing local
  favorites and progress, before they can install from Play. → After launch, the
  "Get the app" link switches to the Play Store, so this affects only early
  adopters. The site's download link can carry a short note about it.
- **Sideloading is harder** (unknown-sources prompt, Play Protect warnings, and
  Google's developer verification requirement for sideloaded apps). → It's an
  interim path only. The fact page itself is the main experience for recipients.
- **Pages caches responses for about 10 minutes.** → Fine for a content site. No
  mitigation needed.

## Migration Plan

1. Land `:web` and the workflow on this branch. Verify locally with
   `./gradlew :web:buildSite` and a static file server rooted at a `bitcoin-bites/`
   path, to exercise the sub-path.
2. Make the repo public, if it isn't already, and set Settings → Pages → Source to
   "GitHub Actions".
3. Merge to `main`. The workflow deploys, and the site goes live at
   `https://walshe.github.io/bitcoin-bites/`.
4. Only then tag an Android release that includes the new share footer. The tag also
   produces the fixed-name `bitcoin-bites.apk` that the site links to. Until that
   first tag, `releases/latest/download/bitcoin-bites.apk` doesn't exist, so either
   cut the tag before the site deploys or temporarily set `appDownloadUrl` to the
   `releases/latest` page.
5. At Play launch: set `appDownloadUrl` to the Play Store listing and redeploy the
   site.
6. Rollback: disable Pages, or re-run the workflow on an earlier commit. Nothing else
   depends on the site.

## Open Questions

- Custom domain now or later? The design already supports one through `siteBaseUrl`
  and relative links.
- Any privacy-friendly analytics, or none? The current assumption is none, to match
  the app's no-network stance.
