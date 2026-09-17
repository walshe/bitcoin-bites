---
id: digital-signatures-prove-without-revealing
title: "Proving you own a Bitcoin address without ever showing your key"
categories: [keys, cryptography]
sources:
  - title: "Inventing Bitcoin, Yan Pritzker, ch. 7"
    url: "https://inventingbitcoin.com"
notify_eligible: true
order: 38000
difficulty: intermediate
related: [crypto-proof-vs-trust, bitcoin-accounts-need-no-registrar]
---

To spend coins, you have to prove you hold the private key for that
address — without handing that key to anyone. The trick: you take the
transaction itself ("send 2 bitcoin from address A to address B"),
hash it, and encrypt that hash with your private key. That's your
digital signature. Anyone can then use your *public* key — which
you've already shared openly — to decrypt the signature and confirm
it lines up, which is only possible if it was really encrypted with the
matching private key. Your private key itself never has to leave your
device.

Unlike a bank password or a signature on a paper check, this signature
is tied to the exact transaction data being signed. Sign one payment,
and that signature only proves authorization for that specific
payment — it can't be copied and reused to authorize a different one.

**Analogy:** Like a wax seal stamped onto one specific letter — anyone
can check the seal matches your signet ring without you ever handing
the ring over, and the seal on this letter says nothing about any other
letter you might write.
