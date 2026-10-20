## 1. `:web` module setup

- [x] 1.1 Add a `web` JVM module to `settings.gradle.kts` with `web/build.gradle.kts`
      (kotlin-jvm, JDK 17 toolchain, `application`, depends on `:content-compiler`
      and `:taxonomy`, junit5 tests), mirroring `content-compiler/build.gradle.kts`
- [x] 1.2 Make sure `ContentCompiler.compile()` and its result/model types are
      callable from `:web`. Widen visibility only if needed, with no behavior change
- [x] 1.3 Register a `:web:buildSite` task that runs the generator with
      `--input content/facts --output web/build/site --base-url <siteBaseUrl>`,
      where `siteBaseUrl` is a Gradle property defaulting to
      `https://walshe.github.io/bitcoin-bites/`, plus `--app-download-url
      <appDownloadUrl>`, defaulting to
      `https://github.com/walshe/bitcoin-bites/releases/latest/download/bitcoin-bites.apk`.
      Inputs and outputs are declared so the task is up-to-date aware

## 2. Formatting and escaping helpers

- [x] 2.1 `html()` escape helper, with tests for `< > & " '`
- [x] 2.2 Body formatter mirroring `FactContent.kt`: paragraph split on blank lines,
      `**Analogy:**` paragraph pulled out as a separate block, `**bold**` →
      `<strong>` applied after escaping. Tests cover no analogy, analogy in the
      middle, and escaped text inside bold
- [x] 2.3 Plain-text excerpt helper for meta descriptions and share text (bold
      markers stripped, whitespace collapsed, trimmed at a word boundary to about
      160 characters), with tests
- [x] 2.4 `Category → display label` map matching `CategoryDisplay.kt`, plus a test
      that fails if any `Category` is missing a label

## 3. Page generation

- [x] 3.1 Shared layout template: `<title>`, meta description, canonical, Open
      Graph/Twitter tags, manifest link, theme-color, stylesheet, module script,
      header with home and favorites links and a "Get the Android app" link to
      `appDownloadUrl`. When that URL is a GitHub APK, a short note says it's a
      direct download and the Play version will need a reinstall. All internal URLs are relative, built
      from the page's depth
- [x] 3.2 Fact page `facts/<id>/index.html`: title, category chips, body, analogy
      block, sources as links, related-fact links (section omitted when empty),
      favorite and share buttons carrying `data-fact-id`
- [x] 3.3 All Bites page `facts/index.html`: every fact in canonical order with
      title, categories and `data-categories` for filtering, plus the category chip
      bar and a favorite toggle per row
- [x] 3.4 Home page `index.html`: three entry buttons, a "Get the Android app"
      call to action, plus placeholders for the
      streak and the continue-reading card that JS fills in
- [x] 3.5 Shells for `guided/`, `random/` and `favorites/` with `<noscript>`
      fallbacks linking to All Bites, and `404.html`
- [x] 3.6 Copy static assets from `web/src/main/site/` and write `facts.json` and
      `sitemap.xml` (home, All Bites, every fact page)
- [x] 3.7 Generator tests on a fixture fact set: every fact gets a page, related
      links resolve, no output path or link starts with `/`, `og:url` uses the
      base URL, the "Get the app" link uses `appDownloadUrl`, the sitemap lists
      every fact, and invalid content
      (`content/test-fixtures/invalid`) fails with a non-zero exit and writes no
      site

## 4. Styling and assets

- [x] 4.1 `web/src/main/site/styles.css`: dark theme tokens from `Color.kt`,
      system font stack, phone-first layout with a 16px gutter, readable line
      length, analogy block style, chips, buttons, journey card, focus styles
- [x] 4.2 Icons: 192/512 PNGs, a maskable icon, a favicon, and a 1200×630 Open
      Graph image, taken from the existing `bitcoin-bites-*-assets/` artwork
- [x] 4.3 `manifest.webmanifest` with relative `start_url` and `scope`, standalone
      display, and theme/background colors

## 5. Client-side JavaScript (vanilla ES modules, no build step)

- [x] 5.1 `storage.js`: namespaced `bb.v1.*` JSON get/set with try/catch and an
      in-memory fallback. Favorites, per-mode progress, and the opens log capped
      at 400 days
- [x] 5.2 `streak.js`: port of `StreakCalculator` using local-time epoch days, plus
      recording today's open on every page load
- [x] 5.3 `filter.js`: category chips on All Bites, using union semantics
- [x] 5.4 `favorites.js`: toggle buttons wherever `data-fact-id` appears, and
      rendering the Favorites page from `facts.json`, including its empty state
- [x] 5.5 `journey.js`: loads `facts.json`. Guided uses canonical order and resumes
      from stored progress. Random shuffles, then reshuffles and loops at the end.
      Previous/Next buttons, arrow keys, pointer-event swipe, the current fact in
      the URL hash, progress saved on every move, and each card links to its fact
      page
- [x] 5.6 `share.js`: Web Share API with the title, plain body, attribution and
      absolute URL. Falls back to copying the link to the clipboard with a
      "Link copied" toast
- [x] 5.7 Home page enhancements: show the streak, and the continue-reading card
      when Guided progress exists
- [x] 5.8 `node --test` tests in `web/src/test/js/` for the streak (same cases as
      `StreakCalculatorTest`), the shuffle-and-loop logic, the union filter, and
      storage fallback when `localStorage` throws

## 6. Offline / PWA

- [x] 6.1 Generate `sw.js` from a template, with the full precache list and a cache
      name taken from a hash of the site's contents
- [x] 6.2 Service worker behavior: precache on install, `skipWaiting` and
      `clients.claim`, delete old caches on activate, network-first for HTML,
      cache-first for assets, and serve `404.html` offline for unknown pages
- [x] 6.3 Register the service worker from the shared layout script, with a
      relative scope

## 7. GitHub Pages deployment

- [x] 7.1 `.github/workflows/pages.yml`: on push to `main` and `workflow_dispatch`.
      `pages: write` and `id-token: write` permissions, a `pages` concurrency
      group, JDK 17, Gradle cache, `actions/configure-pages`, then
      `./gradlew :web:test :web:buildSite -PsiteBaseUrl=${{ steps.pages.outputs.base_url }}/`,
      `node --test web/src/test/js`, `actions/upload-pages-artifact`
      (`web/build/site`), and `actions/deploy-pages`
- [x] 7.2 Add a "Web (GitHub Pages)" section to `README.md` covering the `:web`
      module, how to build and preview locally under a `bitcoin-bites/` sub-path,
      and the one-time repo setup (public repo, Pages source = GitHub Actions)

## 8. Fixed-name release APK

- [x] 8.1 Bring `.github/workflows/release.yml` onto this branch from `main` (it
      exists only there today). In its "collect APK" step, also copy the APK to
      `release-artifacts/bitcoin-bites.apk`, so both the versioned and the
      fixed-name file are attached to each release
- [ ] 8.2 After the next tag, confirm
      `https://github.com/walshe/bitcoin-bites/releases/latest/download/bitcoin-bites.apk`
      downloads that release's APK

## 9. Android share footer → fact web page

- [x] 9.1 In `FactDetailScreen.kt`, replace `PLAY_STORE_URL` with a
      `WEB_BASE_URL = "https://walshe.github.io/bitcoin-bites/"` constant. The
      footer becomes "— Shared from Bitcoin Bites" followed by
      `${WEB_BASE_URL}facts/${fact.id}/`
- [x] 9.2 Confirm the Guided and Random Bites share buttons (`JourneyScreen.kt`,
      which already calls `shareFact()`) produce the same footer
- [x] 9.3 Add a `:web` test asserting that every fact ID produces a page at
      `facts/<id>/index.html`, so the URL Android builds always exists
- [ ] 9.4 Gate: tag an Android release with this change only after the site is
      live and a shared link has been opened successfully on a phone without the
      app

## 10. Verify

- [x] 10.1 Run `./gradlew :content-compiler:test :web:test :web:buildSite` and
      `node --test web/src/test/js`, all passing
- [x] 10.2 Serve `web/build/site` under a `/bitcoin-bites/` sub-path locally and
      click through home, All Bites (filters), a fact page (related links, sources,
      favorite, share), Guided (resume after reload), Random (loops past the end),
      and Favorites. No requests should go to the domain root
- [x] 10.3 Disable JavaScript and confirm fact pages and All Bites are fully
      readable. Block storage and confirm journeys still work
- [x] 10.4 Go offline after one visit and confirm a never-visited fact page and
      Random Bites still load. Rebuild with a changed fact and confirm the update
      appears once back online
- [ ] 10.5 Run Lighthouse (or the browser's PWA checks) to confirm the site is
      installable, and check one fact's link preview with an Open Graph debugger
      once deployed
- [ ] 10.6 Confirm `./gradlew :androidApp:assembleDebug` still builds, and that
      sharing on a device ends with the fact's web URL and has no other changes
