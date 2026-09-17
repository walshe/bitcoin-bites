---
id: fixture-non-map-source
title: "Fixture: non-map source element"
categories: [history]
sources:
  - "https://bitcoin.org/bitcoin.pdf"
notify_eligible: false
order: 1
---

This fixture exists only to verify the compiler rejects a `sources` list
containing a bare string instead of a {title, url} object, instead of
crashing when it tries to read source["title"].
