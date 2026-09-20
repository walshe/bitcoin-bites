---
id: 256-bits-is-a-universe-of-numbers
title: "Why 256 bits is basically an infinite lottery"
categories: [hashing]
sources:
  - title: "Inventing Bitcoin, Yan Pritzker, ch. 4"
    url: "https://inventingbitcoin.com"
notify_eligible: true
order: 23000
difficulty: intermediate
related: [what-a-hash-function-does]
---

Computers count in binary, where each digit (bit) is only ever a 0 or
a 1. Every extra bit you add doubles how many different values you
can represent — 1 bit gives you 2 values, 2 bits give you 4, 3 bits
give you 8, and so on. Sha256's output is 256 bits long, so keep
doubling all the way to 256 and you land on a 78-digit number —
roughly the same size as the estimated number of atoms in the entire
known universe. Put another way: landing on one specific output is
like calling 256 coin tosses in a row, correctly.

That's the space someone would have to search to reverse a hash, or
to land on a specific one on purpose. It's not "very hard" the way a
tough math problem is hard — it's a search space so vast that working
through even a meaningful fraction of it is effectively impossible,
even with every computer on Earth running forever.

**Analogy:** Like being asked to guess one specific atom I've silently
pointed at, somewhere in the universe. Technically a fair guessing
game — practically never happening by chance.
