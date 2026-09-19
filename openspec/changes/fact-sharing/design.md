## Context

`FactDetailScreen.kt` already has a working `shareFact()` function wired to a Share
icon in the top bar:

```kotlin
private fun shareFact(context: android.content.Context, fact: Fact) {
    val excerpt = fact.body.lineSequence().firstOrNull { it.isNotBlank() } ?: ""
    val shareText = "${fact.title}\n\n$excerpt"
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
    context.startActivity(Intent.createChooser(intent, "Share fact"))
}
```

Two things worth noting about `fact.body` at this point in the pipeline, which change
the shape of this design from what the proposal assumed:

1. **It's already reflowed.** `content-compiler`'s `FrontmatterParser.normalizeBody()`
   collapses hard-wrapped source lines into single-line paragraphs (blank line =
   paragraph break) at *compile* time. There's no reflow work left to do at share time
   — `fact.body` is already exactly the paragraph structure that should be shared.
2. **The Analogy paragraph is not actually removed from `fact.body`.** The highlighted
   Analogy card in `FactContent.kt` is a pure rendering trick:
   `String.splitOutAnalogy()` (file-private to `FactContent.kt`) finds the paragraph
   starting with `**Analogy:**` and renders it separately, but `fact.body` itself still
   contains that paragraph as its last paragraph. So sharing the raw `fact.body` text
   already includes the analogy inline, with no need to call `splitOutAnalogy()` (which
   isn't even reachable from `FactDetailScreen.kt` — Kotlin top-level `private` is
   file-private, not package-private) or reconstruct the body from pieces.

The only real formatting problem: `fact.body` still contains literal `**bold**`
markers, which `Text()` + `parseBoldMarkdown()` render as styled `AnnotatedString` in
the app's own UI, but a plain-text `Intent.EXTRA_TEXT` share target (SMS, most
messaging apps) would just show the literal asterisks.

## Goals / Non-Goals

**Goals:**
- Share the complete fact (title + full body, analogy included) as clean plain text.
- Append a short attribution + Play Store link footer.

**Non-Goals:**
- Reflowing or re-parsing body text (already done at compile time).
- A separate "Copy to clipboard" UI affordance (covered by the system share sheet).
- Sharing from the Journey screen (no share action exists there today; unchanged).
- Surfacing `sources`/citations (still hidden from the UI; unrelated to this change).

## Decisions

**Strip `**bold**` markers with a small plain-text regex, not `parseBoldMarkdown()`.**
`parseBoldMarkdown()` (in `MarkdownBold.kt`) returns a Compose `AnnotatedString` for
on-screen rendering — the wrong type for `Intent.EXTRA_TEXT`, which needs a plain
`CharSequence`/`String`. A new small helper strips the markers instead of styling
them: `Regex("\\*\\*(.+?)\\*\\*").replace(body) { it.groupValues[1] }`. This is a
different concern from `parseBoldMarkdown()`, not a duplicate of it — one styles for
screen, the other strips for plain text — so it stays as its own tiny function rather
than trying to force one function to serve both.

**Don't touch `FactContent.kt` at all.** The proposal hedged on possibly reusing its
analogy-splitting logic; per the Context section above, that turned out to be
unnecessary once `fact.body`'s actual structure was checked. Keeping this change
scoped to a single function in `FactDetailScreen.kt` is simpler and has no risk of
disturbing the on-screen rendering path.

**Build the Play Store URL from the existing `applicationId` constant.**
`https://play.google.com/store/apps/details?id=dev.emmett.bitcoinfacts.android` —
written as a literal string constant in `FactDetailScreen.kt` (the `applicationId` in
`androidApp/build.gradle.kts` is a build-time Gradle property, not something trivially
readable from app Kotlin code without extra `BuildConfig` plumbing this change doesn't
otherwise need). If the applicationId ever changes, this string needs a matching
manual update — acceptable since applicationId is already treated as effectively
permanent once published (Play doesn't allow changing it after the first release
anyway).

**Share text shape:**
```
<title>

<body, with ** stripped, paragraphs preserved via existing \n\n breaks>

— Shared from Bitcoin Bites
https://play.google.com/store/apps/details?id=dev.emmett.bitcoinfacts.android
```

## Risks / Trade-offs

- **[Risk] The Play Store link is dead until the app is actually published** (Play
  Console identity verification is still pending as of this change). → Mitigation:
  accepted trade-off — the URL format is deterministic from the fixed applicationId,
  so there's no reason to block on publication or add a placeholder/feature-flag that
  would just need removing later.
- **[Risk] Very long facts could make for an unwieldy share, especially over SMS
  (which segments long messages) or apps with paste-length limits.** → Mitigation:
  accepted trade-off — fact bodies are already short (a few short paragraphs, written
  for a "bite-sized" trivia format), so this is unlikely to be a real problem in
  practice; no truncation logic added preemptively.
