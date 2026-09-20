---
id: difficulty-adjustment-keeps-blocks-steady
title: "Bitcoin re-tunes itself every two weeks — no committee required"
categories: [proof-of-work]
sources:
  - title: "Inventing Bitcoin, Yan Pritzker, ch. 5"
    url: "https://inventingbitcoin.com"
notify_eligible: true
order: 31000
difficulty: intermediate
related: [target-controls-mining-difficulty, more-mining-power-more-often-you-win]
---

Bitcoin aims for one new block roughly every 10 minutes. But the total
computing power pointed at mining changes constantly as people join
or leave, so the network has to keep adjusting how hard the puzzle is
to keep that pace steady. Every 2016 blocks (about two weeks), every
computer on the network independently looks back at how long that
batch actually took, then raises or lowers the Target proportionally —
tighter if blocks came too fast, looser if they came too slow.

**What Bitcoin fixes here:** No central party sets this. Everyone runs
the exact same rule against the exact same shared history, so everyone
lands on the identical new Target without needing to ask anyone or
trust an announcement.

**Analogy:** Like a thermostat that checks the last two weeks of
readings and nudges its own setting up or down — no repair technician
required, because the adjustment rule is baked into the thermostat
itself and every thermostat in the building runs the same rule.
