---
id: fees-are-a-market-for-block-space
title: "Bitcoin fees aren't a cut of your payment — they're rent on space"
categories: [proof-of-work]
sources:
  - title: "Inventing Bitcoin, Yan Pritzker, ch. 9"
    url: "https://inventingbitcoin.com"
notify_eligible: true
order: 53000
difficulty: intermediate
related: [bitcoin-halving-schedule, mining-creates-new-bitcoin]
---

A bank or card network typically takes a percentage of however much
money you're moving. Bitcoin doesn't work that way at all: fees are
priced by how much space your transaction takes up in a block (its
size in bytes), completely independent of how much value it's
sending. A single transaction moving a million bitcoin can cost less
than one that consolidates scattered pocket change from ten
addresses, simply because the second one needs more data to
describe.

Every block has limited space, so when many people want in at once,
users bid higher fees to jump the line, and fees drop back down when
demand is light. This fee market is also Bitcoin's long-term plan for
paying miners: as the block reward keeps halving toward zero, fees are
what will keep incentivizing people to secure the network. Real
engineering has already eased fee pressure too — batching lets one
transaction pay hundreds of people at once, and an upgrade called
Segregated Witness freed up extra room within the existing block
size.

**Analogy:** Like a toll road that prices by how much room your
vehicle takes up, not by how valuable your cargo is — a truck full of
pennies pays more than a tiny envelope stuffed with gold.
