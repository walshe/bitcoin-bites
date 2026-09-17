---
id: bitcoin-accounts-need-no-registrar
title: "Opening a Bitcoin account means generating a number, not asking permission"
categories: [keys]
sources:
  - title: "Inventing Bitcoin, Yan Pritzker, ch. 7"
    url: "https://inventingbitcoin.com"
notify_eligible: true
order: 37000
difficulty: intermediate
related: [256-bits-is-a-universe-of-numbers]
---

A bank checks that no two customers share a username. Bitcoin has no
one to do that job, so it solves the problem a different way: your
account is a mathematically linked pair of giant 256-bit random
numbers — a private key you keep secret, and a public key (used to
build your address) that you can hand out freely. Nobody needs to
register anything, because the numbers are so large that two people
ever generating the same pair by accident is effectively impossible.

That same size is what keeps your coins safe from guessing, not a
password policy. Line up the odds: guessing an 8-digit coin-flip
sequence is 1-in-256. Guessing a 6-character license plate is 1-in-2
billion. Guessing someone's 16-digit credit card number is 1-in-10
quintillion. A 256-bit private key sits at roughly 1-in-10^77 — a scale
where even a planet turned into a hard drive, powered by every star in
reach, running at a trillion guesses a second, wouldn't get through a
meaningful fraction of the possibilities before the universe grew old.

**Analogy:** Like a lock with more possible keys than there are atoms
in the universe — nobody's picking it by trying combinations, no
matter how fast they try.
