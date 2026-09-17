# Invalid fact fixtures

Sample fact files that are deliberately malformed, used by the content compiler's
regression tests (task 2.7) to verify each validation rule actually rejects bad
input. These are **not** real content and must never be included in the
`content/facts/*.md` glob the compiler scans for a production build.

| File | Violates |
|------|----------|
| `bad-category.md` | Uses a category not in the canonical taxonomy |
| `duplicate-id-a.md` / `duplicate-id-b.md` | Two files declaring the same `id` |
| `dangling-related.md` | `related` references a fact ID that doesn't exist |
| `missing-sources.md` | Omits the required non-empty `sources` field |
| `non-string-category.md` | `categories` contains a non-string element (e.g. unquoted YAML `no`) |
| `non-map-source.md` | `sources` contains a bare string instead of a `{title, url}` object |
