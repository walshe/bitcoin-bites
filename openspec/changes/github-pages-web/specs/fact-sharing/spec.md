## MODIFIED Requirements

### Requirement: Shared text includes a promotional footer
The system SHALL append an attribution line and the shared fact's web page URL to
the end of every shared fact's text. The URL is the site's base URL followed by
`facts/<fact id>/`.

#### Scenario: Footer is present on every share
- **WHEN** the user shares any fact
- **THEN** the shared text ends with an attribution line ("— Shared from
  Bitcoin Bites") followed by that fact's web page URL, e.g.
  `https://walshe.github.io/bitcoin-bites/facts/what-is-bitcoin/` for the fact
  with ID `what-is-bitcoin`

#### Scenario: Sharing from a journey uses the same footer
- **WHEN** the user shares the current fact from Guided Bites or Random Bites
- **THEN** the shared text ends with the same attribution line and that fact's web
  page URL

#### Scenario: Recipient without the app can read the fact
- **WHEN** someone without the app opens the URL in a shared fact's footer
- **THEN** the fact's web page loads in their browser and shows the shared fact
