## 1. Share text formatting

- [ ] 1.1 Add a small plain-text helper that strips `**bold**` markers (regex
      replace, not `parseBoldMarkdown()` - that returns an `AnnotatedString`,
      the wrong type for `Intent.EXTRA_TEXT`)
- [ ] 1.2 Add a constant for the Play Store URL
      (`https://play.google.com/store/apps/details?id=dev.emmett.bitcoinfacts.android`)

## 2. Update `shareFact()`

- [ ] 2.1 Build the shared text as: title, blank line, `fact.body` with bold
      markers stripped (paragraph breaks already present via `\n\n`, no
      reflow needed), blank line, "— Shared from Bitcoin Bites", the Play
      Store URL
- [ ] 2.2 Confirm the existing `Intent.ACTION_SEND` / `Intent.createChooser`
      plumbing is unchanged - only the text content changes

## 3. Verify

- [ ] 3.1 Build and run on-device: open a fact with an analogy, tap Share,
      confirm the share sheet's preview shows full title + body + analogy +
      footer, with no literal `**` characters
- [ ] 3.2 Confirm a fact's shared text via a target that shows plain text
      (e.g. Android's built-in "Copy to clipboard" share-sheet shortcut, or
      Notes) matches the expected shape from design.md
- [ ] 3.3 Run `./gradlew :content-compiler:test :shared:jvmTest :androidApp:assembleDebug`
