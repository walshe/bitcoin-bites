---
id: why-bitcoin-paces-its-blocks
title: "Bitcoin deliberately mines slowly — that's a feature, not a limitation"
categories: [decentralization, network]
sources:
  - title: "Inventing Bitcoin, Yan Pritzker, ch. 6"
    url: "https://inventingbitcoin.com"
notify_eligible: true
order: 35000
difficulty: intermediate
related: [longest-chain-wins]
---

Bitcoin could technically produce blocks every second instead of every
ten minutes. It doesn't, on purpose. Blocks take real time to travel
across a network spread over the whole planet, so if they were mined
too quickly, far-apart miners would constantly find valid blocks before
they'd even heard about each other's — turning simultaneous chain
splits from a rare event into a constant one, and making it much
harder for the network to agree on a single history.

Keeping blocks modestly sized helps too: it keeps the cost of running
a full node low enough that ordinary computers, not just data centers,
can keep a copy of the ledger and check the rules for themselves.

**What Bitcoin fixes here:** Both choices trade a little raw speed for
something Bitcoin values more: a network ordinary people can still
afford to help verify, rather than one only data centers can keep up
with.

**Analogy:** Like a large meeting run with a strict "one person speaks
at a time" rule — slower than everyone shouting at once, but it's the
only way the whole room can agree on what was actually said.
