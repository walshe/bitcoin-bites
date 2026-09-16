# Fact content format

Each Bitcoin fact lives in its own Markdown file in this directory: `content/facts/<id>.md`.

A build-time content compiler (see `tools/content-compiler` once implemented — task group 2)
parses every file here, validates it against the rules below, and produces a single runtime
index bundled into the app. There is no runtime network fetch of fact content in v1.

## Filename / ID convention

Filename is `<id>.md`, and `id` inside the frontmatter must match the filename (without
extension) exactly, e.g. `fact-021.md` must declare `id: fact-021`. This keeps the ID
visible in the filesystem/git history without needing to open the file.

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
```

## Canonical category taxonomy

This list is **closed** — the content compiler fails the build if any fact uses a
category outside this list. This is deliberate (see design.md decision 1 / risk
"taxonomy sprawl"): a small, fixed vocabulary keeps in-app filtering coherent as
the fact count grows. A fact may declare multiple categories.

To add or rename a category: edit the single source of truth (the `Category` enum
in the `taxonomy` Kotlin module — task 2.1's implementation, shared by both the
content compiler and the app's shared core), then re-run the build; every fact
using a stale category name will fail loudly and point you at the file to fix.

| Category            | Covers                                                        |
|----------------------|---------------------------------------------------------------|
| `history`            | Genesis block, whitepaper, Pizza Day, cypherpunk mailing list, other milestones |
| `money`               | What money is/does, pre-Bitcoin monetary history, gold, barter |
| `fiat`                | Central banks, inflation, currency debasement, comparisons to Bitcoin |
| `monetary-policy`     | 21M cap, halving schedule, issuance/emission                  |
| `proof-of-work`       | Mining as work, hash functions, difficulty adjustment          |
| `mining`              | Miners, pools, block rewards, energy, hardware                 |
| `nodes-network`       | Full nodes, P2P network, consensus, decentralization mechanics |
| `keys`                | Private/public keys, seed phrases, signatures                 |
| `wallets`             | Wallet types, hot/cold storage, hardware wallets                |
| `custody`             | Self-custody vs. third-party custody, "not your keys" risks     |
| `multisig`            | Multi-signature setups and use cases                            |
| `privacy`             | Pseudonymity, chain analysis, privacy techniques/limits          |
| `scaling`             | Block size, layer 2, Lightning Network                          |
| `scarcity`            | Fixed supply, stock-to-flow style scarcity concepts               |

(Draft list from design.md's open questions — confirm/adjust during task 2.1's
implementation, then keep this table and the `Category` enum in sync by hand;
the compiler validating against the enum is what actually enforces the taxonomy,
not this table.)
