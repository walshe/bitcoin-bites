## Context

Greenfield project, no existing code. Two people-level constraints shape every decision below more than technical ones do: (1) the author is learning Kotlin/KMP and wants real hands-on practice, not a framework that writes the logic for them; (2) the app's value is credibility with a technically literate, correction-happy audience (Bitcoiners), so content correctness and coherent design matter more than growth mechanics.

## Goals / Non-Goals

**Goals:**
- A fully offline-capable app: all fact content ships inside the binary, zero server dependency.
- A shared Kotlin core that contains all the interesting logic (content loading, filtering, related-fact resolution, notification selection, favorites/history), so Android and (later) iOS share behavior instead of reimplementing it twice.
- A closed, validated category taxonomy so filtering stays coherent as content grows past a handful of facts.
- Content authored in a human-readable, git-diffable format so facts can be reviewed like code (important given the accuracy bar).
- Ship Android first, end to end, before touching iOS — validate the shared core and content pipeline in pure Kotlin before adding Swift interop friction.

**Non-Goals (this change):**
- iOS app — deferred to a follow-up change once the shared core is proven on Android.
- Any server, remote content sync, or post-install content updates — v1 facts are frozen at build time.
- User accounts, cloud sync, social/leaderboard features.
- Paid content tiers — the only monetization is a non-gating tip IAP.
- Notification personalization (category filters, curated learning paths) — v1 notification selection is pure random-no-repeat.

## Decisions

### 1. Content source of truth: Markdown + YAML frontmatter, one file per fact
Facts live at `content/facts/<id>.md`. Frontmatter carries structured fields (`id`, `title`, `categories`, `difficulty`, `related`, `sources`, `notify_eligible`); the Markdown body carries the fact text and analogy.

**Alternatives considered:**
- *Embedded SQLite as source of truth* — rejected for v1: not git-diffable, hard to code-review fact-by-fact, awkward to hand-edit. A generated SQLite (or JSON) as a *build output* is still fine, and may be exactly what the runtime uses — the decision here is only about the authoring/source-of-truth layer.

### 2. Build-time content compiler validates and compiles all facts
A build step (run as part of Gradle, or a pre-build script invoked by Gradle) parses every `content/facts/*.md` file and produces a single runtime index (JSON, or a generated SQLite file) that ships inside the app. This step:
- Validates every `categories` entry against a single canonical taxonomy constant (fixed list, ~10-15 entries). An unlisted category **fails the build** — this is the mechanism that prevents tag sprawl (e.g. `keys` vs `key-management` vs `private-keys` all meaning the same thing).
- Auto-mirrors `related` links: if fact A declares `related: [B]`, the compiler adds A to B's related list too, so authors declare a link once.
- Fails the build on: duplicate fact IDs, a `related` ID that doesn't exist, malformed frontmatter.

**Alternatives considered:**
- *No validation, trust authors* — rejected: with a single author this might hold up, but the entire point of the canonical-taxonomy decision is to make sprawl a build failure, not a code-review nit.
- *Runtime validation instead of build-time* — rejected: catching a bad category at build time is strictly better than shipping it and discovering it via a broken filter UI.

### 3. Shared Kotlin Multiplatform core
A `shared` KMP module holds: `Fact`/`Category` models, the compiled-index loader, filter/search logic, the related-facts resolver, the daily-notification selection decision (pure logic — "which fact ID is today's pick," not the actual OS notification call), and a SQLDelight-backed store for favorites + seen-fact history.

Platform-specific code is limited to `expect`/`actual` for: (a) firing the actual local notification (Android: WorkManager/AlarmManager; iOS, later: UNUserNotificationCenter), and (b) the SQLDelight platform driver.

**Alternatives considered:**
- *Fully separate native codebases (no KMP)* — rejected: doubles the maintenance surface for filter/notification logic and risks behavioral drift between platforms, though it would mean more total Kotlin *and* Swift practice.
- *Cross-platform UI framework (Flutter/RN)* — rejected: cuts against the explicit Kotlin-learning goal, and native UI (Compose/SwiftUI) is wanted for the "nice Bitcoin look and feel" polish goal.

### 4. Daily notification selection: random, no-repeat until exhausted
The shared core tracks which fact IDs have been shown (reusing the favorites/history store), picks uniformly at random from the not-yet-shown pool eligible for notifications (`notify_eligible: true`), and reshuffles (clears the shown-set) once the pool is exhausted. This logic is pure and testable in the shared module; only the OS-level "fire a notification at time X" is platform code.

**Alternatives considered:**
- *User-curated by category* — deferred: adds a settings screen and interacts with filtering UI; not needed for v1's single-persona (default) experience.
- *Curated learning-path sequence* — deferred: requires designing an actual curriculum ordering, which is a content-design task bigger than this change's scope.

### 5. No server; content frozen at build time
All facts are compiled into the app binary. There is no fetch-on-launch, no CDN, no static-JSON-refresh mechanism in this version.

**Alternatives considered:**
- *Bundled + static JSON refresh from a static host (e.g. GitHub Pages)* — explicitly deferred, not rejected. If post-launch fact corrections turn out to be slow/painful via app-store review, this is the natural next step, and the content pipeline (validated, ID-keyed facts) is already shaped to support it later without rework.

### 6. Monetization: free app + one-time non-gating tip IAP
A single consumable-or-non-consumable IAP (e.g. "Buy me a coffee ⚡") unlocks a cosmetic reward (alternate app icon, or a theme). No fact content or feature is ever paywalled.

**Alternatives considered:**
- *Paid upfront (e.g. $0.99)* — rejected: niche trivia apps lose almost all organic discovery behind a paywall, and this project isn't optimizing for revenue.
- *Fully free, no monetization at all* — considered viable, but a tip jar costs little (one small IAP integration) and offers a "give back" path for the exact audience (Bitcoiners) most likely to appreciate the ethos of "the content itself is never for sale."

### 7. Build/ship order: shared core + Android first, iOS as a later change
This change's task list ends at a working Android app. iOS work (Xcode/Gradle integration, `.xcframework` consumption, SwiftUI screens, Flow-to-Swift interop wrappers) is intentionally excluded so KMP/interop debugging doesn't get tangled with shared-logic debugging.

### 8. Share a fact via the platform's native share sheet
Fact detail exposes a "share" action that hands the fact's title + a short excerpt (plus, ideally, a link back to the app's store listing) to the OS share sheet (Android `Intent.ACTION_SEND`). No custom share UI, no image generation, no deep-link/analytics tracking in v1 — just the plain OS mechanism.

**Alternatives considered:**
- *Generate a shareable image/card* — rejected for v1: real design and rendering work for a feature that's meant to be nearly free; plain text sharing captures most of the "send this to a friend" value at a fraction of the cost. Could revisit later if shared facts turn out to be a meaningful acquisition channel.

### 9. Daily-open streak counter derived from a durable open-day log (corrected during task 4.1)
**Correction found during implementation:** the original plan below turned out to have a real bug, caught before any code was written. It's kept here (struck through) so the reasoning trail isn't lost, followed by the fix.

~~The streak is computed on read from the same seen-fact-history timestamps already persisted for notification no-repeat tracking — no new table, no new write path.~~ **Bug:** that table is *cleared* by the notification no-repeat cycle reset (`daily-fact-notifications`'s "all eligible facts shown → reset" requirement) — so finishing the fact pool would silently zero out the user's streak too, even though they'd opened the app every day. Sharing one table conflated two different lifetimes: "facts shown in the current notification cycle" (meant to be wiped periodically) and "which calendar days the user was active" (meant to never be wiped).

**Fix:** two separate append-only tables instead of one, each still a plain timestamp/day log (not a mutable counter, so Decision 9's original objection — a second source of truth that can drift — still doesn't apply to either):
- `notificationShownFact` (cycle-scoped, cleared on cycle reset) — no-repeat tracking only, used by `daily-fact-notifications`.
- `appOpenDay` (durable, never cleared) — one row per calendar day the user opened the app or received a notification fact; the streak is derived read-only from this table.

**Alternatives considered:**
- *Store a dedicated streak counter field, incremented/reset on each app open* — still rejected for the original reason: a mutable counter is a second source of truth that can drift from the underlying history.
- *Keep one shared table, exclude it from the cycle-reset clear* — rejected: would require the no-repeat query to filter by "current cycle start time" instead of just "table is empty," adding complexity to the no-repeat logic to work around a modeling problem, when two small tables are simpler and each stays single-purpose.

### 10. Content-writing guideline: name the pre-Bitcoin gap
Not a code feature — a discipline applied during fact authoring (Task group 10, "Initial content authoring"): every fact should explicitly name what was impossible, or required a trusted third party, before Bitcoin, and how Bitcoin closes that gap. This is what turns a neutral fact ("supply is capped at 21 million") into the intended "aha, nothing like this existed before" reaction, and costs nothing architecturally — it only shapes how facts get written and reviewed.

### 11. Compiled index carries a `schemaVersion` field
The runtime index (`facts.json`) is wrapped in an envelope — `{ "schemaVersion": 1, "facts": [...] }` — rather than being a bare array. This is a deliberate, minimal forward-compatibility hook for Decision 5's deferred remote-refresh idea: v1 has no fetch mechanism and no versioning logic, but *if* a later change adds one, the app can check this field before attempting to parse a remote file, without the compiler itself needing rework. `CURRENT_SCHEMA_VERSION` bumps only when `FactOutput`'s shape changes in a way older app versions couldn't parse.

**Alternatives considered:**
- *Design the full remote-refresh mechanism now (versioning, fetch/merge strategy, hosting)* — rejected: still genuinely deferred per Decision 5; speculative design for a feature that may never be needed if app-store review turnaround turns out fine in practice.
- *No hook at all* — rejected: this one field costs nothing today and avoids a schema migration headache if remote refresh is ever added later.

### 12. Locale-suffix filename convention reserved (not implemented)
No multi-language support exists in v1, and none is planned near-term — but to avoid a painful rename/restructure later, a naming convention is reserved and documented in `content/facts/README.md`: a future translation of `fact-021.md` would live alongside it as `fact-021.es.md` (etc.), sharing the same `id`/`categories`/`related`/`sources` structure, with only title and body text translated. Nothing about the compiler or runtime index changes today — a `*.<locale>.md` file created now would simply fail the build's filename/id-match check, which is the correct (safe) failure mode until real locale support is built.

**Alternatives considered:**
- *Design real multi-locale content/compiler support now* — rejected: no actual translation plan exists yet; this would be speculative scope on a project explicitly trying to stay lean.
- *Do nothing, not even document a convention* — rejected: the filename convention is the one thing that's expensive to change retroactively once dozens of facts exist (renaming files, updating `related` references); documenting it now is free.

### 13. Journey mode reuses the list screen's category filter instead of a separate picker
Rather than build a dedicated "choose a journey" screen with its own category-selection UI, the "Start Journey" action on the list screen simply reads whatever categories are already checked in the existing `FilterChip` row (empty selection = all facts) and launches the swipeable Journey screen scoped to that same set. "Random Journey" from the new home/menu screen is just the same flow with an empty category selection, reached directly. Both entry points shuffle the matching fact pool once per Journey session and swipe through it via `HorizontalPager`; reaching the end reshuffles and loops rather than stopping.

**Alternatives considered:**
- *A separate "Journey Setup" screen with its own category chips* — rejected: would duplicate the exact selection UI/logic the list screen already has, and need to be kept in sync with it as the taxonomy evolves. Reusing the same hoisted `selectedCategories` state (already shared across the nav graph in `MainActivity`) costs nothing extra.
- *Hardcoded per-category "journeys" (e.g. a fixed menu entry per taxonomy category)* — rejected: doesn't scale as categories are added/renamed, and duplicates the taxonomy list in a second place.
- *Stop at the end of a journey with a "you've seen them all" card* — rejected in favor of reshuffle-and-loop: matches the casual, endless-browsing framing of "a journey" better than a hard stop, and needs no extra empty/end-state screen.

### 14. Taxonomy starts empty and grows organically from real content
Decision 1 committed to a *closed* taxonomy (build fails on an unlisted category) but originally seeded it with a guessed draft list of ~14 categories before any real content existed. In practice, once real content authoring started, the taxonomy was reset to genuinely empty and rebuilt one category at a time, adding an entry only when an actual fact needed it - starting with chapter 1 of the user's own book source rather than a speculative list. The two placeholder example facts used through tasks 1-12 (`fact-001`/`fact-002`, testing the app itself) were retired at this point since they existed only to exercise the pipeline, not as real content.

**Alternatives considered:**
- *Keep the original guessed draft list and just reuse it* — rejected: a taxonomy invented before any real fact existed is exactly the kind of guess decision 1 already worried about ("too rigid or picked wrong early"); better to let real source material decide what categories are actually needed.
- *Keep the two placeholder facts as permanent library entries* — rejected: they were authored to test UI/pipeline behavior, not to reflect real book content, and keeping them would mean the "real" library starts with non-book-sourced facts mixed in.

### 15. Fact content is sourced from real books, extracted and reworded collaboratively
Rather than the user authoring every fact alone (task group 10's original plan) or Claude inventing facts unprompted, the actual workflow that emerged: the user pastes a chapter from a real Bitcoin book they trust, Claude identifies the strongest individual facts within it, rewords each into plain language with a fresh analogy (per decision 10's "name the pre-Bitcoin gap" rule), assigns categories (adding new ones to the taxonomy as needed per decision 14), and cites the source book (plus, where a fact draws on a direct quote, the original primary source too - e.g. Satoshi's own writing).

**Alternatives considered:**
- *User writes every fact text themselves* — this was task group 10's original plan, superseded once the user chose to work chapter-by-chapter from real books with Claude doing the extraction/rewording/citation work instead.
- *Generate facts without a specific source text* — rejected: given the project's credibility goal, grounding every fact in a specific, named, real book (with a citation) is a stronger accuracy/trust story than facts synthesized from general knowledge with no traceable source.

### 16. Explicit sparse `order` field controls Browse All / Journey sequence
Before this decision, `facts.json` was sorted alphabetically by `id` (a semantic slug like `what-is-bitcoin`), which meant Browse All and Journey mode presented facts in an arbitrary order unrelated to how they read in the source chapter. Every fact now declares a required integer `order` in its frontmatter; the compiler sorts the compiled index by it (falling back to `id` only to break an exact tie), and the app's list/Journey ordering just follows whatever order `facts.json` is already in. The first batch (chapter 1) uses sparse round numbers - 1000, 2000, 3000... - so a fact discovered later that belongs between two existing ones can take a number in the gap (e.g. 1500) instead of forcing a renumber of everything after it.

**Alternatives considered:**
- *Keep alphabetical-by-id ordering* — rejected: fine as a stable, zero-maintenance default, but actively works against the goal of Journey mode reading like the source material as more chapters/books get added.
- *Preserve file/insertion order instead of an explicit field* — rejected: fragile (depends on filesystem iteration or git history, both unreliable/opaque signals) and gives no way to express "insert this fact between these two" without touching unrelated files.
- *Densely-numbered order (1, 2, 3...)* — rejected: any future insertion between two adjacent facts would require renumbering every fact after the insertion point.

### 17. Sources are hidden from the fact detail/Journey UI (data still required)
The `sources` field stays required in frontmatter and is still validated by the compiler - it's not being removed from the content model. Only the rendering is toggled off, via a single `SHOW_SOURCES` constant in the shared `FactBodyAndSources` composable, while citation UI/UX (placement, styling, whether it's inline vs. a separate screen) gets decided later rather than shipped as an afterthought.

**Alternatives considered:**
- *Remove sources from the schema entirely* — rejected: the project's credibility goal (design.md intro) depends on every fact being traceable to a real source even if that isn't surfaced in the UI yet; removing the field would lose that discipline during content authoring.
- *Redesign the sources UI now instead of just hiding it* — deferred: no clear design intent yet for how citations should look/read in-app; hiding behind a flag keeps the data ready without blocking content work on an unresolved UI question.

### 18. App icon and in-app branding use the bunny mascot artwork only
Two logo concepts existed (`bitcoin-bites-assets/`, a bunny reading a Bitcoin-branded book, and `bitcoin-bites-book-icon-assets/`, a flatter stacked-books glyph) - the user chose to use the bunny exclusively, everywhere. It's wired in as: the Android launcher icon (legacy `ic_launcher`/`ic_launcher_round` at all five densities, generated directly from the source artwork - see below for why not a true adaptive icon), a hero image on the Home screen above the "Browse All"/"Random Journey" buttons, and the Favorites empty-state illustration.

**Why legacy icon assets instead of Android's adaptive icon format:** the source PNG is a fully-flattened, full-bleed rounded square (its flat edges touch the canvas edge; only the four corners are transparent) with its own baked-in drop shadow, meant to sit on a plain light background. Layering it as an adaptive-icon *foreground* over a solid-color *background* (the standard two-layer adaptive icon approach) produced a visible double-ring artifact - the source's own shadow/halo showing up as an ugly outline against the near-identical orange background color. Using the artwork directly as a legacy icon (letting Android/launchers apply their own automatic masking to the single flattened image, as they do for any pre-adaptive-icon app) avoided the artifact entirely; a direct circular crop test confirmed no important content (the bunny's face, the book) gets clipped by common mask shapes.

**Alternatives considered:**
- *True adaptive icon (separate inset foreground + solid background layers)* — rejected after visual testing showed a double-background/shadow-ring artifact; would require a version of the artwork without its own baked-in background/shadow, which doesn't exist yet.
- *Use the book-glyph icon for the launcher and the bunny only in-app* — rejected per explicit user preference: bunny everywhere, no mixed branding.

## Risks / Trade-offs

- **[Risk] Canonical taxonomy is too rigid or picked wrong early, requiring a painful re-tag of many facts later.** → Mitigation: keep the taxonomy list itself in one small, easy-to-edit constant; the build-time validator makes a taxonomy *change* (not just a fact change) a fast, safe, all-or-nothing operation (rename a category, rebuild, see every failure).
- **[Risk] KMP/Android setup friction (first-time Gradle multi-module + SQLDelight config) slows the learning-focused momentum.** → Mitigation: front-load a minimal "hello shared module" spike before writing real fact-loading logic, so multiplatform build plumbing is solved independently of content-pipeline logic.
- **[Risk] Content accuracy errors ship because there's no formal review process yet.** → Mitigation: require a `sources` entry on every fact at the schema level (even if enforcement is just a build-time non-empty-array check), so at minimum every claim is citation-backed and reviewable later even without a formal review workflow.
- **[Risk] Deferring iOS means the KMP shared-module API might need rework once real Swift interop constraints appear (e.g. Flow wrapping, sealed class exposure).** → Mitigation: accepted trade-off — the alternative (building both platforms simultaneously) was judged riskier for the learning goal; the shared module's public API surface is small enough that a later interop-driven revision is cheap.
- **[Risk] "Nice Bitcoin look and feel" is undefined — could cause scope creep or decision paralysis during Android UI implementation.** → Mitigation: out of scope for this design doc; treat as a task-level decision made during Android UI implementation (pick a palette/typography quickly, iterate visually rather than over-planning here).

## Open Questions

- Should the build-time content compiler be a Kotlin/Gradle task (consistent language, more idiomatic KMP) or a lightweight script in another language (e.g. Python/Node) invoked from Gradle? Leaning Kotlin for consistency and more Kotlin practice, but not yet decided.
- Exact canonical category list is not finalized (a draft of ~10-15 was discussed: mining, custody, proof-of-work, keys, multisig, monetary-policy, history, fiat-comparison, scaling, privacy, nodes-network, wallets) — should be finalized as part of the `fact-content-pipeline` spec/tasks, not this design doc.
- ~~Runtime bundled format: plain JSON parsed at app startup vs. a build-generated SQLite file~~ **Resolved (task 2.5): plain JSON**, via kotlinx.serialization. The compiler writes `facts.json` into a generated Android assets source dir; the shared module's runtime loader (task group 3) parses it into in-memory `Fact` objects at startup. SQLDelight (already wired in task 1.3) is reserved for the favorites/seen-history *write* store (task group 4), not for fact content itself — content is read-only and small enough that JSON-at-startup is simpler than generating and querying a second SQLite database just to hold static data.
