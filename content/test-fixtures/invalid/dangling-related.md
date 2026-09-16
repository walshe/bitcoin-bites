---
id: fixture-dangling-related
title: "Fixture: dangling related ID"
categories: [history]
sources:
  - title: "n/a"
    url: "https://example.com"
notify_eligible: false
related: [fact-does-not-exist]
---

This fixture exists only to verify the compiler rejects a `related` entry that
does not correspond to any real fact file.
