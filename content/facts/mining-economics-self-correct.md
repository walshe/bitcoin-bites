---
id: mining-economics-self-correct
title: "Mining profit and network security are the same feedback loop"
categories: [proof-of-work]
sources:
  - title: "Inventing Bitcoin, Yan Pritzker, ch. 6"
    url: "https://inventingbitcoin.com"
notify_eligible: true
order: 36000
difficulty: advanced
related: [longest-chain-wins, difficulty-adjustment-keeps-blocks-steady, mining-creates-new-bitcoin]
---

When Bitcoin's price rises, mining becomes more profitable, so more
miners join in, and the network's total hash rate climbs. About every
two weeks, the difficulty adjustment catches up to that new hash rate
and makes the puzzle harder — which squeezes out whichever miners
have the highest costs, since it now takes more energy to earn the
same reward. If price falls instead, the same loop runs in reverse:
miners drop out, hash rate falls, and difficulty eases back down until
it's profitable again for whoever's left. Rather than a "death spiral,"
this constant rebalancing settles the network into an equilibrium
between price and mining activity.

This loop is also what makes the network expensive to attack: since
overriding the "longest chain wins" rule requires out-mining more
than half of everyone else combined, the more real money and hash
rate the honest majority has already committed, the more an attacker
would need to spend just to catch up.

**Analogy:** Like a thermostat wired to a competitive gym membership —
more people showing up (miners joining) makes management raise
the price of admission (difficulty), which thins the crowd back down
to whoever still finds it worth it.
