---
id: lightning-network-moves-payments-off-chain
title: "Not every coffee needs its own entry in the world's ledger"
categories: [scaling]
sources:
  - title: "Inventing Bitcoin, Yan Pritzker, ch. 9"
    url: "https://inventingbitcoin.com"
notify_eligible: true
order: 54000
difficulty: advanced
related: [fees-are-a-market-for-block-space, why-bitcoin-paces-its-blocks]
---

Bitcoin's base ledger can only handle a handful of transactions per
second, since every single payment gets broadcast to, and stored by,
thousands of computers worldwide forever. Simply making blocks
bigger would ease that, but at a cost: heavier blocks mean fewer
ordinary computers can afford to keep running a full node, quietly
pulling the network toward centralization — which is why Bitcoin has
resisted that path.

The Lightning Network takes a different approach: two people lock up
some bitcoin together in an on-chain "payment channel," then trade
funds back and forth between themselves instantly, privately, and
essentially for free — with nothing touching the main blockchain
until they finally close the channel and settle the net result on-chain
once. Most of the running tab never needs the whole world to see or
store it.

**Analogy:** Like two neighbours who are forever covering each other's
coffees and keeping a running tally on the fridge — dozens of small
debts all month, but only one actual bank transfer to square it all
up at the end.
