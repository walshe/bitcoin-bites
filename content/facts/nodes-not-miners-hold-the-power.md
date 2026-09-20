---
id: nodes-not-miners-hold-the-power
title: "Miners don't write Bitcoin's rules — they just compete under them"
categories: [decentralization]
sources:
  - title: "Inventing Bitcoin, Yan Pritzker, ch. 8"
    url: "https://inventingbitcoin.com"
notify_eligible: true
order: 44000
difficulty: intermediate
related: [miners-cant-pay-themselves-extra, mining-bundles-many-transactions-into-a-block]
---

It's tempting to think whoever writes the next block is in charge,
since they're the one actually adding to the ledger. They're not.
Every ordinary node on the network — anyone running Bitcoin
software, not just miners — independently checks each new block
against the rules their own software enforces, and simply throws away
anything that breaks them. A miner who tries to bend a rule doesn't
get to overrule anyone; they just get ignored, and eat the wasted
electricity.

That flips the usual picture: the people actually enforcing Bitcoin's
rules are the everyday nodes doing validation — merchants, exchanges,
wallet providers, ordinary users — while miners are more like
contestants competing under whatever rules those nodes have already
agreed to accept.

**What Bitcoin fixes here:** Money usually ends up controlled by
whoever owns the most infrastructure. Here, buying more hardware buys
you more chances to win blocks and no say whatsoever over the rules —
those stay with the ordinary people running nodes.

**Analogy:** Like a marathon where runners can sprint however they
like, but judges standing at every mile marker simply refuse to count
anyone who cut the course — no runner, however fast, gets to redraw
the route.
