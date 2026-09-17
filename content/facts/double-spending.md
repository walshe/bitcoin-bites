---
id: double-spending
title: "Why can't you just copy-paste digital money?"
categories: [double-spending, network]
sources:
  - title: "Inventing Bitcoin, Yan Pritzker, ch. 1"
    url: "https://inventingbitcoin.com"
  - title: "Bitcoin: A Peer-to-Peer Electronic Cash System"
    url: "https://bitcoin.org/bitcoin.pdf"
notify_eligible: true
difficulty: intermediate
---

Digital files are trivial to copy — that's exactly the problem digital cash
had to solve. If a "coin" is just data, what stops you from spending the
same coin twice, the same way you'd copy-paste a photo? This is called
double-spending, and every attempt at digital cash before Bitcoin had to
solve it somehow, usually by trusting one central company to keep the only
official ledger.

Satoshi's fix: a peer-to-peer network that acts like a distributed
timestamp server, collectively agreeing on the order transactions happened
in. Once the network agrees a coin was spent first in transaction A, a
later attempt to spend it again in transaction B is simply rejected — no
central company required to referee it.

**Analogy:** Like a single shared sign-in sheet that thousands of witnesses
watch at once, so nobody can quietly erase an entry or claim they signed in
first when they didn't.
