## Why

The fact detail screen already has a working "Share" action, but it only shares the
fact's title plus the first non-blank line of the body — a stub, not the actual fact.
It also does nothing to bring a share's recipient back to the app itself. With Play
Store publication now in progress (signing/release build already set up), every share
is a free, organic acquisition opportunity that's currently being wasted.

## What Changes

- The Share action now shares the **complete fact**: title, full body (main text +
  the Analogy paragraph), reusing the same paragraph-reflow/analogy-split logic
  `FactBodyAndSources` already uses, with markdown `**bold**` markers stripped since
  plain-text share targets (SMS, most messaging apps) don't render them.
- A short footer is appended to the shared text: an attribution line ("— Shared from
  Bitcoin Bites") and the app's Play Store URL, built from the app's already-fixed
  `applicationId` (`https://play.google.com/store/apps/details?id=dev.emmett.bitcoinfacts.android`).
  The link is included now even though the app isn't live yet, since the URL is
  deterministic and doesn't need to change later.
- Explicitly **not** adding a separate "Copy to clipboard" action/icon — Android's
  system share sheet already surfaces a one-tap copy shortcut for text shares on
  modern Android, so the single improved Share action covers that case without adding
  a third icon to the detail screen's top bar.

## Capabilities

### New Capabilities
- `fact-sharing`: Sharing a fact's full text (title + body + analogy) plus a
  promotional footer via the Android system share sheet, from the fact detail screen.

### Modified Capabilities
(none — no existing spec covers the previous stub-share behavior)

## Impact

- `androidApp/src/main/kotlin/dev/emmett/bitcoinfacts/android/ui/FactDetailScreen.kt`
  — `shareFact()` is the only function that changes.
- Possibly `FactContent.kt` if its existing body-formatting/analogy-splitting helpers
  are reused (preferred) rather than reimplemented.
- No changes to `JourneyScreen.kt` (no share action exists there today; out of scope),
  no changes to the `sources`/citations UI (still hidden, unrelated), no changes to
  shared/KMP modules (this is Android UI-only, plain-text formatting).
