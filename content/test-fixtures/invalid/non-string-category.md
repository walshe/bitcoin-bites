---
id: fixture-non-string-category
title: "Fixture: non-string category element"
categories: [money, no]
sources:
  - title: "n/a"
    url: "https://example.com"
notify_eligible: false
order: 1
---

This fixture exists only to verify the compiler rejects a `categories` list
containing a non-string element instead of crashing. `no` is unquoted YAML
1.1 and SnakeYAML parses it as a Boolean, not a string.
