---
id: small-change-scrambles-the-hash
title: "Change one letter, get a totally different hash"
categories: [hashing]
sources:
  - title: "Inventing Bitcoin, Yan Pritzker, ch. 4"
    url: "https://inventingbitcoin.com"
notify_eligible: true
order: 22000
difficulty: beginner
related: [what-a-hash-function-does]
---

Hashing "Hello world!" and "Hello world!!" — a one-character
difference — produces two completely unrelated giant numbers, with no
overlap or pattern connecting them. Nudge the input even slightly and
the output doesn't shift a little, it lands somewhere else entirely.

That's what makes hashes impossible to predict: there's no way to
look at an input, or even a slightly different version of an input,
and guess what its hash will look like. The only way to find a hash
with a particular property is to actually try inputs and see what
comes out — you can't shortcut it by reasoning your way there. That
one fact is what makes Bitcoin's mining lottery fair: nobody can think
their way to a winning number faster than anyone else.

**Analogy:** Like shaking a kaleidoscope — nudging it even a
millimeter completely rearranges the pattern, and there's no way to
predict the new pattern from the old one, let alone nudge it toward a
specific pattern on purpose.
