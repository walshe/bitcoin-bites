---
id: blocks-are-chained-by-hash
title: "Why it's called a blockchain: every block hashes in the one before it"
categories: [proof-of-work]
sources:
  - title: "Inventing Bitcoin, Yan Pritzker, ch. 6"
    url: "https://inventingbitcoin.com"
notify_eligible: true
order: 32000
difficulty: intermediate
related: [mining-bundles-many-transactions-into-a-block, small-change-scrambles-the-hash]
---

Winning a block doesn't just mean hashing transactions and a nonce —
it means hashing those together with one more thing: the hash of the
block that came right before it. That third ingredient links every
block to its predecessor, all the way back to the very first "Genesis"
block, which is what actually makes it a **chain**.

Because changing even one character scrambles a hash completely,
tampering with any old block changes its hash — which changes the
next block's hash, since that block includes the old one as an
ingredient — which changes the block after that, and so on through
the entire rest of the chain. Rewriting history isn't just against the
rules; it's mathematically visible to everyone the instant you try.

**Analogy:** Like a paper chain where each new link is stamped with an
imprint of the link before it — tug on a link from years ago and
every link made after it visibly doesn't match anymore.
