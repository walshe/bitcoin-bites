---
id: longest-chain-wins
title: "When two miners win at once, the network just waits and sees"
categories: [proof-of-work, decentralization]
sources:
  - title: "Inventing Bitcoin, Yan Pritzker, ch. 6"
    url: "https://inventingbitcoin.com"
notify_eligible: true
order: 34000
difficulty: intermediate
related: [blocks-are-chained-by-hash, difficulty-adjustment-keeps-blocks-steady]
---

On a worldwide network, two miners can occasionally find a valid
block within seconds of each other in different parts of the world.
For a short while, different corners of the network end up looking at
two different, equally valid versions of the ledger, and there's no
central referee to call which one is "correct."

Bitcoin's answer: don't decide, just keep mining. Everyone keeps
building on the version they heard first, and as soon as anyone mines
the *next* block on top of one of the two versions, that version pulls
ahead. The rule, hardcoded into every node, is simple — whichever
chain represents the most accumulated Proof of Work wins. Everyone
still building on the losing version switches over, and the block that
lost is discarded (an "orphan"), along with its reward.

**Analogy:** Like two relay teams that both cross a finish line at
nearly the same instant — instead of arguing over a photo, the race
organizers just check which team's baton makes it furthest down the
*next* leg of the track.
