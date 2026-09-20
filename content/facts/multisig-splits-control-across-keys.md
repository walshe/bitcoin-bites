---
id: multisig-splits-control-across-keys
title: "One Bitcoin address, several keys required to spend it"
categories: [custody]
sources:
  - title: "Inventing Bitcoin, Yan Pritzker, ch. 7"
    url: "https://inventingbitcoin.com"
notify_eligible: true
order: 42000
difficulty: intermediate
related: [a-wallet-holds-keys-not-coins, seed-phrases-back-up-your-whole-wallet]
---

An address doesn't have to depend on a single private key. A
"multisig" address can require any number of keys out of a set before
it will release funds — a 2-of-2 setup needs both parties to agree, a
2-of-3 can let two out of three signers move funds while a third acts
purely as a tiebreaker for disputes, and a 3-of-5 lets you lose up to
two of your own five keys and still recover everything.

**What Bitcoin fixes here:** None of these arrangements need a bank or
an escrow company sitting in the middle deciding anything — the rules
are baked into the address itself, enforced the way every other
Bitcoin rule is: by the math, not by a person's word.

**Analogy:** Like a safety deposit box built to need two or three
different keys turned at once — nobody with just one key can open it
alone, and losing one spare key doesn't lock everyone else out either.
