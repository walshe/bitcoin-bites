---
id: 51-percent-attack-mechanics-and-limits
title: "What controlling most of the network's hash power actually gets you"
categories: [proof-of-work, decentralization]
sources:
  - title: "Inventing Bitcoin, Yan Pritzker, ch. 9"
    url: "https://inventingbitcoin.com"
notify_eligible: true
order: 49000
difficulty: advanced
related: [longest-chain-wins, nodes-not-miners-hold-the-power]
---

Since the network always accepts whichever chain has the most
accumulated Proof of Work, someone controlling more than half the
total hash rate could quietly mine their own chain in secret, then
broadcast it once it's longer than everyone else's — forcing a mass
reorg. In theory that lets them erase recent history: send bitcoin to
an exchange, cash it out, then reveal a secret chain where that send
never happened, effectively spending the same coins twice.

But majority hash rate is not majority control. An attacker still can't
create coins out of thin air (every node checks the reward against the
rules, no matter how much Proof of Work backs the block), can't spend
coins that aren't theirs (they still can't forge someone else's digital
signature), and can't speed up issuance (difficulty adjusts every 2016
blocks regardless of who's mining). And doing this at all means
burning as much energy as a mid-sized country and buying hardware
worth millions — a cost that scales with exactly how much honest
hash rate you're trying to overpower.

**Analogy:** Like bribing the majority of referees at a match — you
might get bad calls made in your favor, but you still can't change the
final score displayed on everyone else's independently-kept
scoreboard, or hand yourself points that were never scored.
