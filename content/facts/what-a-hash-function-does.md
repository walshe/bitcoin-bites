---
id: what-a-hash-function-does
title: "How a hash function turns anything into a fixed-size fingerprint"
categories: [hashing]
sources:
  - title: "Inventing Bitcoin, Yan Pritzker, ch. 4"
    url: "https://inventingbitcoin.com"
notify_eligible: true
order: 21000
difficulty: beginner
related: [crypto-proof-vs-trust]
---

A hash function takes any input — a word, a sentence, an entire book —
and squeezes it down into a giant, fixed-size, random-looking number.
Feed it the same input twice and you always get the exact same number
back (it's deterministic), but there's no way to run the process
backwards: you can't take the number and recover what was fed in (it's
a one-way function). Bitcoin's version of this is called sha256, and
no matter how big or small the input is, the output is always the
same size: 256 bits.

That combination — same input always gives the same output, but the
output never reveals the input — turns out to be exactly the kind of
math trick Bitcoin needs to let anyone prove they did real work,
without anyone being able to fake it.

**Analogy:** Like a fingerprint scanner: your fingerprint always
produces the same scan, and no two different fingers realistically
produce the same scan — but nobody can look at the scan alone and
reconstruct your actual finger from it.
