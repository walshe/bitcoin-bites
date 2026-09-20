---
id: mining-hardware-arms-race
title: "Mining went from laptops to chips that do nothing but hash"
categories: [proof-of-work]
sources:
  - title: "Inventing Bitcoin, Yan Pritzker, ch. 9"
    url: "https://inventingbitcoin.com"
notify_eligible: true
order: 47000
difficulty: intermediate
related: [proof-of-work-lottery, mining-economics-self-correct]
---

Satoshi mined the earliest blocks on an ordinary computer processor
(CPU). As more people joined, miners discovered that graphics cards
(GPUs) — built for rendering video games — happened to be
thousands of times more efficient at hashing, and CPU mining
instantly stopped being worth the electricity. GPUs then lost the same
race to ASICs: chips custom-built to do exactly one thing, run
Bitcoin's sha256 hash function, and nothing else. Being that
specialized made them thousands of times more efficient than GPUs,
and each new generation of ASIC hardware retires the previous one
every few years.

Every step of this arms race did the same thing: it raised the total
hash rate, which raised the difficulty, which pushed out whoever
couldn't keep up — the exact self-correcting loop that keeps mining
profitability in equilibrium.

**Analogy:** Like a race that began on foot, then allowed bicycles,
then motorcycles, then purpose-built race cars — every generation
makes the one before it pointless to enter, purely because it's built
for nothing except winning this one race.
