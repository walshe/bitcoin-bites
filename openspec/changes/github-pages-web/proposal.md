## Why

Bitcoin Bites is already a no-server, offline-first app whose entire content is a
single build-time `facts.json`. That makes it a natural fit for a static website on
free GitHub Pages: no hosting cost, no backend, and the same `content/facts/*.md`
source of truth. A web version reaches people who won't install an app, and — just as
importantly — gives every fact a real, linkable URL, so the share action (which
currently points at the Play Store) can land recipients directly on the fact they
were sent.

## What Changes

- New static website generated at build time from the same content pipeline:
  `content/facts/*.md` → `content-compiler` → `facts.json` → pre-rendered HTML.
- One pre-rendered HTML page per fact at a stable URL (`/facts/<id>/`), plus a home
  page, an "All Bites" list page, and per-fact Open Graph / Twitter card metadata so
  shared links unfurl with a title and excerpt.
- A small, dependency-free client-side JavaScript layer (no framework, no bundler)
  for the interactive features that can't be pre-rendered: Guided Bites (canonical
  order, resumes where you left off), Random Bites (shuffle, reshuffle-and-loop),
  category filtering, favorites, and the daily-open streak — all persisted in the
  browser's `localStorage`.
- Installable, offline-capable Progressive Web App (web app manifest + service
  worker that precaches every page).
- Web share: the native Web Share API where available, falling back to copying the
  link; shared text links to the fact's own web URL.
- A "Get the Android app" link on the site, its target set at build time: the
  latest GitHub Release APK for now, switched to the Play Store listing at launch
  without an app update.
- The Android share footer links to the shared fact's web page instead of the Play
  Store URL, which only works for opted-in testers while the app is in testing.
  The new link works for every recipient on any device. Ships only after the site is
  live.
- The existing release workflow also uploads the APK under a fixed name
  (`bitcoin-bites.apk`), so `releases/latest/download/bitcoin-bites.apk` always
  points at the newest build.
- A GitHub Actions workflow that builds and deploys the site to GitHub Pages on
  pushes to `main` (Pages source = "GitHub Actions"). Built output is never
  committed; the repository's README view is unaffected.
- **Non-goals**: daily push notifications (Web Push requires a sending server),
  cross-device sync of favorites/streaks, the optional tip purchase, and reusing the
  Compose UI on the web (Compose/Wasm).

## Capabilities

### New Capabilities
- `static-site-generation`: Build-time generation of the static site from the
  compiled fact index — per-fact pages, home and list pages, link-preview metadata,
  sitemap, and base-path-aware URLs — failing the build on bad input just as the app
  build does.
- `web-fact-experience`: The in-browser experience — browsing and category filtering,
  fact detail with related facts, Guided and Random Bites, favorites, streak, and
  web sharing — with state stored locally in the browser.
- `web-offline-pwa`: Installability and offline use via a manifest and a service
  worker that precaches the whole site and updates on redeploy.
- `github-pages-deployment`: Automated build-and-deploy of the site to GitHub Pages
  via GitHub Actions, with no committed build output.

### Modified Capabilities
- `fact-sharing`: the shared text's footer links to the fact's web page
  (`https://walshe.github.io/bitcoin-bites/facts/<id>/`) instead of the Play Store
  URL. Nothing else about sharing changes.

## Impact

- **New Gradle module** `web/` (JVM, Kotlin): reads the compiled fact index and
  writes the static site to `web/build/site/`. Depends on `:content-compiler` and
  `:taxonomy` (for category display names); no changes to `shared/`.
- **`androidApp`**: only `shareFact()` in `FactDetailScreen.kt` changes. The
  Play Store URL constant is replaced by a web base URL constant plus the fact ID.
  The Guided and Random Bites share actions reuse `shareFact()`, so they pick up
  the change automatically.
- **`.github/workflows/release.yml`** (currently on `main` only): adds a
  fixed-name copy of the APK to each release.
- **New static assets** under `web/src/main/site/` (CSS, vanilla JS, icons,
  manifest, service worker template), reusing existing icon artwork from
  `bitcoin-bites-*-assets/`.
- **New workflow** `.github/workflows/pages.yml`.
- **Repository settings**: the repo must be public (free-tier Pages requirement) and
  Pages must be set to deploy from GitHub Actions — a one-time manual step.
- **`content-compiler`**: unchanged behavior; may expose its `compile()` result for
  direct reuse by the `web` module instead of re-reading `facts.json`.
