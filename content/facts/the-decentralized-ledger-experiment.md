---
id: the-decentralized-ledger-experiment
title: "What if everyone kept their own copy of the ledger?"
categories: [decentralization, network]
sources:
  - title: "Inventing Bitcoin, Yan Pritzker, ch. 2"
    url: "https://inventingbitcoin.com"
notify_eligible: true
order: 18000
difficulty: beginner
related: [distributed-vs-decentralized, why-friend-groups-dont-scale, three-pillars-of-bitcoin]
---

Imagine removing the bank entirely. Instead of one company holding
the ledger, a group of friends each keep their own copy. When Alice
pays Bob $2, she tells everyone in the group; each person writes the
transaction into their own ledger and confirms it. There's no single
ledger anywhere — there are dozens of identical copies, kept in sync
by everyone talking to each other.

**What Bitcoin fixes here:** This actually solves double-spending too:
if Alice tries to spend that same $2 again, everyone checks their own
copy, sees it's already gone, and rejects it — no central referee
required. It's a small-scale, low-tech preview of exactly what
Bitcoin's network does globally.

**Analogy:** Like a group chat where everyone screenshots every
payment as it happens — no one person's phone is the "official"
record, because everyone's screenshots already agree.
