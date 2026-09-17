# Fact content format

Each Bitcoin fact lives in its own Markdown file in this directory: `content/facts/<id>.md`.

A build-time content compiler (`content-compiler` Gradle module) parses every `*.md` file
here (this README is excluded by name) against the rules below, and produces a single
runtime index bundled into the app. There is no runtime network fetch of fact content in v1.

## Filename / ID convention

Filename is `<id>.md`, and `id` inside the frontmatter must match the filename (without
extension) exactly, e.g. `fact-021.md` must declare `id: fact-021`. This keeps the ID
visible in the filesystem/git history without needing to open the file.

**Future translations (not implemented — naming convention reserved only):** v1 is
English-only, and the compiler has no locale awareness. If a fact is ever translated,
the translated file should live alongside the English original using a locale suffix,
e.g. `fact-021.es.md` next to `fact-021.md` — sharing the same `id`, `categories`,
`related`, and `sources` structure, with only `title` and body text translated. This
is reserved purely so a future translation effort doesn't collide with the plain
`<id>.md` convention above; the compiler would need real changes (grouping by base id,
a locale dimension in the runtime index) before any `*.<locale>.md` file would actually
be picked up — don't create one yet.

## Frontmatter schema

```yaml
---
id: fact-021                  # required, unique, matches filename
title: "Why 21 million?"      # required
categories: [monetary-policy, scarcity]   # required, non-empty, each must be a
                                           # canonical category (see below)
sources:                      # required, non-empty
  - title: "The Bitcoin Standard, ch. 8"
    url: "https://..."
notify_eligible: true         # required boolean — can this fact be used as a
                               # daily push-notification pick?
order: 4500                   # required integer — controls Browse All / Journey
                               # ordering (see "Fact ordering" below)
difficulty: beginner          # optional: beginner | intermediate | advanced
related: [fact-007, fact-118] # optional — other fact IDs. Declaring the link on
                               # ONE side is enough; the compiler mirrors it onto
                               # the other fact automatically. Every ID here must
                               # correspond to a real fact file, or the build fails.
---

Body: the fact's explanation, in plain language, followed by a short analogy.

Per design.md decision 10, prefer naming the specific pre-Bitcoin gap this fact
closes — what was impossible, or required trusting a third party, before Bitcoin —
so the "nothing like this has existed before" reaction is explicit, not implicit.

## Fact ordering

`order` is a plain integer the compiler sorts `facts.json` by (ties break on
`id`), which is what determines the sequence facts appear in for Browse All
and Journey mode. It's deliberately sparse — the first chapter-1 batch uses
1000, 2000, 3000... — so a fact discovered later that belongs *between* two
existing facts can just take a number in the gap (e.g. 1500) instead of
forcing a renumber of everything after it. Only renumber a whole range if a
gap actually runs out.
```

## Canonical category taxonomy

This list is **closed** — the content compiler fails the build if any fact uses a
category outside this list. This is deliberate (design.md decision 1 / risk
"taxonomy sprawl"): a small, fixed vocabulary keeps in-app filtering coherent as
the fact count grows. A fact may declare multiple categories.

Unlike a typical closed taxonomy, this one didn't start from a guessed list —
it started **empty** and grows one category at a time, only when a real fact
actually needs a new one (design.md decision 14). There's no "add categories in
advance" step; if you're writing a fact and none of the existing categories fit,
add exactly one new entry to the `Category` enum in the `taxonomy` Kotlin module
(the single source of truth, shared by the compiler and the app's category
filter UI), update the table below to match, then re-run the build.

| Category               | Covers                                                                    |
|-------------------------|----------------------------------------------------------------------------|
| `history`               | Satoshi, the whitepaper, cypherpunk precursors, real-world monetary events |
| `money`                 | What money is/does, pre-Bitcoin monetary concepts                          |
| `fiat`                  | Central banks, legal tender, currency debasement                           |
| `monetary-policy`       | The 21M cap, issuance schedule, satoshis                                   |
| `scarcity`              | Digital vs. physical scarcity, predictable supply                          |
| `decentralization`      | No single company/server, P2P vs. centrally-controlled systems             |
| `network`               | The P2P network itself, nodes, distributed coordination                    |
| `double-spending`       | Why digital money is hard to make un-copyable, and how Bitcoin solves it   |
| `open-source`           | Verifiable, publicly readable/modifiable code                              |
| `privacy`               | Identity, data breaches, pseudonymity                                      |
| `custody`               | Self-custody vs. third-party custody, freezing/seizure risk                |
| `censorship-resistance` | Money/state separation, resistance to seizure or control                   |
| `cryptography`          | Proof vs. trust, verifiable claims                                         |

This first batch came entirely from chapter 1 of "Inventing Bitcoin" by Yan
Pritzker — the very first real content added to this app. Expect this table to
keep growing as more chapters/books get processed into facts; keeping it small
and demand-driven (rather than pre-guessed) is the whole point.
