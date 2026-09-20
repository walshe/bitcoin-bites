---
id: proof-of-work-lottery
title: "Bitcoin mining is a guess-and-check lottery, not a puzzle to solve"
categories: [proof-of-work]
sources:
  - title: "Inventing Bitcoin, Yan Pritzker, ch. 4"
    url: "https://inventingbitcoin.com"
notify_eligible: true
order: 24000
difficulty: intermediate
related: [what-a-hash-function-does, double-spending, how-banks-stop-double-spending]
---

Everyone racing to add the next batch of transactions to Bitcoin's
ledger takes that data, tacks on a random number called a nonce, and
hashes the whole thing. If the resulting number happens to fall below
an agreed-upon Target, they win and get to announce it. If not, they
try a new nonce and hash again — and again, often billions of times —
because there's no way to predict which nonce will work; you just
have to keep guessing.

The clever part is what happens after someone wins. Hashing forward
(input to output) is fast; reversing it is effectively impossible. So
while finding a winning nonce might take billions of attempts, anyone
else can check the answer in a single try — just hash the same
transaction data with the reported nonce and see if it matches.

**What Bitcoin fixes here:** This is exactly the referee-free trick
needed to stop double-spending. No one company decides whose version
of the ledger is real — everyone can independently verify who won, in
a single check.

**Analogy:** Like a raffle where everyone scribbles random ticket
numbers as fast as they can, hoping to land under a cutoff everyone
agreed on in advance. Nobody can shortcut finding a winning ticket —
but once someone wins, anyone can glance at it and confirm it
instantly.
