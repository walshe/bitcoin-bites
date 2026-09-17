---
id: bitcoin-is-a-protocol-not-a-program
title: "\"Bitcoin\" is a set of rules, not one piece of software"
categories: [open-source, network]
sources:
  - title: "Inventing Bitcoin, Yan Pritzker, ch. 8"
    url: "https://inventingbitcoin.com"
notify_eligible: true
order: 43000
difficulty: intermediate
related: [open-source-dont-trust-verify, three-pillars-of-bitcoin]
---

Bitcoin is a protocol: a shared set of rules for what counts as a valid
transaction, a valid block, and a valid chain. Any software that
follows those rules and can talk to other Bitcoin software counts as
a Bitcoin node — there's no single official program you're required
to run. Bitcoin Core, the most widely used implementation and a
direct descendant of Satoshi's original code, just happens to be the
one almost everyone chooses, but other independent implementations
exist too.

Because agreement across the network matters so much, running
something other than the dominant implementation is risky — a stray
bug could make your software disagree with everyone else about
what's valid. But nothing stops anyone from writing their own client
from scratch, in any language, as long as it follows the same rules
everyone else is enforcing.

**Analogy:** Like a card game with a fixed rulebook that anyone can
print their own deck for — as long as your deck follows the printed
rules, the table doesn't care whose printer made it.
