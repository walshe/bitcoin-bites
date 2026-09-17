---
id: miners-cant-pay-themselves-extra
title: "A miner who tries to pay themselves extra just wastes their own electricity"
categories: [open-source, monetary-policy]
sources:
  - title: "Inventing Bitcoin, Yan Pritzker, ch. 5"
    url: "https://inventingbitcoin.com"
notify_eligible: true
order: 28000
difficulty: intermediate
related: [open-source-dont-trust-verify, mining-creates-new-bitcoin]
---

The exact reward a miner is allowed to claim is hardcoded into the
same open-source rules every computer on the network runs. If a miner
tries to sneak themselves extra bitcoin in their coinbase transaction,
every other computer checking that block notices the reward doesn't
match the rules and simply rejects the whole block — including the
miner's payment. They still burned the electricity to win the lottery;
they just get nothing for it.

No central authority has to catch the cheater or punish them. The
punishment is built directly into the math: everyone else independently
enforces the same rule, so breaking it is economically pointless.

**Analogy:** Like trying to cash a check you wrote yourself for more
than the agreed amount — the bank (everyone else on the network)
just bounces the whole thing, and you're out the cost of the paper and
the stamp.
