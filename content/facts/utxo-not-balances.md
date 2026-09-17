---
id: utxo-not-balances
title: "Bitcoin's ledger doesn't store balances at all"
categories: [double-spending]
sources:
  - title: "Inventing Bitcoin, Yan Pritzker, ch. 7"
    url: "https://inventingbitcoin.com"
notify_eligible: true
order: 39000
difficulty: advanced
related: [double-spending, how-banks-stop-double-spending, bitcoin-accounts-need-no-registrar]
---

There's no field anywhere in Bitcoin that says "Alice has 1 bitcoin."
Instead, the ledger just tracks unspent transaction outputs, or UTXOs
— individual chunks of bitcoin created by past transactions that
haven't been spent yet. Sending money means consuming one or more
existing UTXOs as input and producing brand new UTXOs as output. If
Alice has a single 1-bitcoin UTXO and wants to send Bob 0.3, her
transaction destroys that UTXO and creates two new ones: 0.3 to Bob,
and 0.7 back to herself as "change." Your balance is really just
whatever you get from adding up every UTXO that points to one of
your addresses.

This has a nice side effect: since nothing on the chain says who
controls which address, Alice can send her change to a brand new
address instead of reusing her old one, making it harder to link her
past and future transactions together. It also makes checking for
double-spends simple — a given UTXO can only ever be consumed once,
with no need to replay someone's entire transaction history to know
if a payment is valid.

**Analogy:** Like a cash register that never tracks "how much money
does this customer have" — it only tracks which individual bills and
coins exist and haven't been spent, and hands out exact change as new
bills each time.
