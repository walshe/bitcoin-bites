---
id: confirmations-arent-true-finality
title: "Bitcoin has no built-in \"final\" — you decide how safe is safe enough"
categories: [proof-of-work]
sources:
  - title: "Inventing Bitcoin, Yan Pritzker, ch. 6"
    url: "https://inventingbitcoin.com"
notify_eligible: true
order: 33000
difficulty: intermediate
related: [blocks-are-chained-by-hash]
---

Every block mined on top of the one holding your transaction makes
that transaction harder to reverse, since undoing it would mean
redoing the Proof of Work for every block stacked above it too. But
Bitcoin itself never declares a payment "final" — that's a threshold
each recipient sets for themselves. Six confirmations (six blocks deep,
roughly an hour) is a common convention, not a rule: a shop selling a
cheap digital download might accept zero confirmations, while someone
closing on a house might wait for twelve.

It's worth comparing this to what it's replacing: a typical card
payment can still be reversed up to 120 days after the fact. Bitcoin's
finality is a judgment call rather than a fixed guarantee, but it's a
call you get to make yourself — and it only gets more certain with
time, never less.

**Analogy:** Like wet concrete that keeps hardening the longer it
sits — nobody declares the exact moment it's "done," but everyone can
tell that concrete poured an hour ago is far more set than concrete
poured a minute ago.
