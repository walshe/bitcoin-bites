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
the **next** block on top of one of the two versions, that version pulls
ahead. The rule, hardcoded into every node, is simple — whichever
chain represents the most accumulated Proof of Work wins. Everyone
still building on the losing version switches over, and the block that
lost is discarded (an "orphan"), along with its reward.

**What Bitcoin fixes here:** Two conflicting versions of the truth
would normally need an authority to settle them. Bitcoin settles it
with no vote, no arbitrator and no discussion at all — whichever
version people keep building on simply wins.

**Analogy:** Like a walking group hitting a fork where two people each
insist their path is the real trail. Nobody argues it out — everyone
just keeps walking, and whichever path someone pushes furthest ahead
quietly becomes the trail everyone follows.
