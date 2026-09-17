---
id: soft-forks-vs-hard-forks
title: "Two ways to change Bitcoin's rules — one gentle, one not"
categories: [open-source, decentralization]
sources:
  - title: "Inventing Bitcoin, Yan Pritzker, ch. 9"
    url: "https://inventingbitcoin.com"
notify_eligible: true
order: 51000
difficulty: advanced
related: [bitcoin-is-a-protocol-not-a-program, no-one-can-force-a-bitcoin-upgrade]
---

A soft fork tightens the rules in a way old, un-upgraded software
still agrees with. In 2010, Bitcoin added a rule that blocks can't
exceed 1MB. An old node that never installed this rule still sees every
new, smaller block as perfectly valid — only the miners producing
blocks actually needed to upgrade, and everyone else kept working
without noticing anything changed.

A hard fork does the opposite: it makes something valid that used to
be invalid, like blocks bigger than 1MB. An old node that hasn't
upgraded rejects those blocks outright, because they break a rule it's
still enforcing. If literally everyone upgrades together, that's fine —
but if even some node operators refuse, the network doesn't
gracefully split into "new" and "old" versions of the same coin. It
splits into two separate coins, each with its own history from that
point forward.

**Analogy:** A soft fork is like a stricter dress code at a party that
nobody already dressed nicely even notices. A hard fork is like
suddenly requiring a wristband to get in — anyone without one is
simply locked out of the party they thought they were still at.
