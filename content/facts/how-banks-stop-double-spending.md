---
id: how-banks-stop-double-spending
title: "Why your bank always catches you trying to spend the same dollar twice"
categories: [double-spending, money]
sources:
  - title: "Inventing Bitcoin, Yan Pritzker, ch. 2"
    url: "https://inventingbitcoin.com"
notify_eligible: true
order: 17000
difficulty: beginner
related: [banks-are-just-ledgers, double-spending]
---

If you already spent $2 and try to spend it again, your bank simply
refuses — it checks its own ledger, sees the money is already gone,
and rejects the second attempt. This works because the bank is the
**only** party allowed to write to the ledger, backed by audits,
backups, and internal checks to keep that one record correct.

That's easy to police with a single trusted referee in charge. The
much harder question — and the one Bitcoin actually had to solve —
is how you stop the same trick with no referee at all: no single
party keeping the one true ledger.

**What Bitcoin fixes here:** Bitcoin keeps the "you can't spend it
twice" guarantee but throws away the referee that used to provide it.
Thousands of independent computers each hold the ledger and check
every payment against it, so a second attempt gets rejected by
everyone at once rather than by one company with the authority to
say no.

**Analogy:** Like a single scorekeeper at a game — cheating is easy to
catch when there's only one scoreboard and one person allowed to
touch the marker.
