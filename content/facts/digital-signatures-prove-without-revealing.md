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
address — without handing that key to anyone. Here's the trick: you
take the transaction itself ("send 2 bitcoin from address A to address
B") and use your private key to produce a short scrap of data called a
digital signature. Anyone can then take three things — that signature,
the transaction, and your **public** key, which you've shared openly —
and run a quick check that confirms they all match. That check only
passes if the signature really was made using the matching private
key, so it proves you hold the key without ever showing it. Your
private key never has to leave your device.

Unlike a bank password or a signature on a paper check, this signature
is tied to the exact transaction data being signed. Sign one payment,
and that signature only proves authorization for that specific
payment — it can't be copied and reused to authorize a different one.

**Analogy:** Like a wax seal stamped onto one specific letter — anyone
can check the seal matches your signet ring without you ever handing
the ring over, and the seal on this letter says nothing about any other
letter you might write.
