## ADDED Requirements

### Requirement: Sharing a fact shares its complete text
The system SHALL share a fact's title and complete body text (including the
Analogy paragraph) when the user taps the Share action on the fact detail
screen, via the Android system share sheet.

#### Scenario: Sharing a fact with an analogy
- **WHEN** the user taps Share on a fact detail screen
- **THEN** the system share sheet opens with plain text containing the fact's
  title followed by its full body text, including the Analogy paragraph

#### Scenario: Shared text has no literal markdown markers
- **WHEN** a fact's body contains `**bold**` markdown emphasis
- **THEN** the shared plain text shows the emphasized words with the `**`
  markers removed, not literal asterisks

### Requirement: Shared text includes a promotional footer
The system SHALL append an attribution line and the app's Play Store URL to
the end of every shared fact's text.

#### Scenario: Footer is present on every share
- **WHEN** the user shares any fact
- **THEN** the shared text ends with an attribution line ("— Shared from
  Bitcoin Bites") followed by the URL
  `https://play.google.com/store/apps/details?id=dev.emmett.bitcoinfacts.android`

### Requirement: No dedicated copy-to-clipboard action
The system SHALL NOT provide a separate copy-to-clipboard button distinct
from the Share action - clipboard access is provided by the Android system
share sheet's own built-in copy shortcut.

#### Scenario: No extra icon added
- **WHEN** viewing the fact detail screen's top bar
- **THEN** it shows only the existing back, favorite, and share actions, with
  no additional copy-specific icon
