---
id: mining-bundles-many-transactions-into-a-block
title: "Miners don't race over one payment at a time — they race over a whole batch"
categories: [proof-of-work]
sources:
  - title: "Inventing Bitcoin, Yan Pritzker, ch. 5"
    url: "https://inventingbitcoin.com"
notify_eligible: true
order: 26000
difficulty: intermediate
related: [proof-of-work-lottery, double-spending]
---

Every payment announced on the network gets gossiped from computer to
computer until everyone's heard about it. Miners scoop up a whole
batch of these pending payments at once and race to hash that entire
batch (plus their nonce) below the Target. Whoever wins doesn't just
announce a number — they announce the whole package: which
transactions they included, the nonce they used, and the winning hash.
That package is called a block.

Once a block is announced, everyone else on the network re-hashes it
themselves to confirm the winning number is real, checks that none of
the included payments conflict with anything already written earlier,
and then appends it to their own copy of the ledger. Do that over and
over, and you get a chain of these blocks — a blockchain.

**What Bitcoin fixes here:** Agreeing on one payment at a time,
worldwide, would never keep up. Bundling hundreds of them into a
single race means one contest settles them all at once — and pins down
their order in the process, which is exactly what stops the same coin
being spent twice.

**Analogy:** Like a courier who waits for the mailbag to fill before
sealing it and racing off — the whole bag gets stamped and delivered
as one, rather than making a separate trip for every letter.
