---
id: target-controls-mining-difficulty
title: "A smaller Target means more electricity burned to win"
categories: [proof-of-work]
sources:
  - title: "Inventing Bitcoin, Yan Pritzker, ch. 4"
    url: "https://inventingbitcoin.com"
notify_eligible: true
order: 25000
difficulty: intermediate
related: [proof-of-work-lottery, 256-bits-is-a-universe-of-numbers]
---

The Target is a cutoff everyone on the network agrees to: any hash
number that falls below it is a winner. Make that cutoff smaller, and
only a tiny sliver of all possible hashes qualify, so it takes far
more attempts — and far more real electricity — to stumble onto a
winning one. Make it bigger, and winners turn up faster.

This is the whole reason Bitcoin mining costs real energy: the Target
is deliberately kept small relative to the total space of possible
hashes, so producing a valid winning hash is genuinely expensive to
do, but trivial for everyone else to check. That gap — expensive to
find, cheap to verify — is what gives the network a way to measure
"someone truly did the work," without asking anyone to trust their
word for it.

**Analogy:** Like a limbo bar — lower it and fewer people can slip
underneath on any given try, so it takes many more attempts before
someone finally squeezes under.
