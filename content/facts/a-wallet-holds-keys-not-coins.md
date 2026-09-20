---
id: a-wallet-holds-keys-not-coins
title: "Your Bitcoin wallet doesn't actually hold any Bitcoin"
categories: [custody]
sources:
  - title: "Inventing Bitcoin, Yan Pritzker, ch. 7"
    url: "https://inventingbitcoin.com"
notify_eligible: true
order: 40000
difficulty: beginner
related: [bitcoin-accounts-need-no-registrar, open-source-dont-trust-verify]
---

A "wallet" in Bitcoin doesn't store coins at all — the coins only ever
exist as entries on the shared ledger. What a wallet actually stores is
your private keys: the proof needed to spend whatever's addressed to
you. That can be a piece of paper with a number written on it, a phone
app, or a dedicated hardware device — anyone with the programming
know-how can build one, and most existing wallets are free and open
source.

That design has a sharp edge to it, though. There's no bank behind a
Bitcoin wallet, which means there's also no fraud department to call.
Lose your private key and the coins tied to it are gone for good;
someone else who gets a copy of it can spend those coins as easily as
you could. Full control over your money comes bundled with full
responsibility for guarding the one thing that proves it's yours.

**What Bitcoin fixes here:** Keeping money anywhere else means
somebody is holding it on your behalf — and they can freeze it, lose
it, or go under with it still inside. A wallet removes that party
completely. The honest trade-off is that their safety net leaves with
them, and guarding the key becomes your job instead.

**Analogy:** A wallet is less like a safe holding cash, and more like
the only copy of a key to a safety deposit box that has no lost-key
counter — whoever holds the key, holds the contents.
