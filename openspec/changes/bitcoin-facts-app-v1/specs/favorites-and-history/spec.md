## ADDED Requirements

### Requirement: Mark and unmark favorite facts
The system SHALL allow the user to mark any fact as a favorite and later unmark it, from both the fact list and the fact detail view.

#### Scenario: Marking a fact as favorite
- **WHEN** the user taps the favorite control on a fact
- **THEN** the fact is recorded as a favorite and the control reflects the favorited state

#### Scenario: Unmarking a favorite
- **WHEN** the user taps the favorite control on a fact already marked as a favorite
- **THEN** the fact is no longer recorded as a favorite and the control reflects the unfavorited state

### Requirement: View favorited facts
The system SHALL provide a view listing all facts the user has marked as favorites.

#### Scenario: Viewing the favorites list
- **WHEN** the user opens the favorites view
- **THEN** every currently-favorited fact is shown, and unfavorited facts are not

### Requirement: Persist favorites and seen-history across restarts
The system SHALL persist favorite status and the daily-notification seen-history locally such that both survive an app restart.

#### Scenario: Restarting the app preserves favorites
- **WHEN** the user favorites a fact, then closes and reopens the app
- **THEN** the fact still appears in the favorites list

#### Scenario: Restarting the app preserves notification seen-history
- **WHEN** the daily notification has shown some facts in the current cycle, then the app is closed and reopened
- **THEN** the next day's random selection still excludes those already-shown facts (per the daily-fact-notifications no-repeat requirement)

### Requirement: Daily-open streak counter
The system SHALL compute a "current streak" value representing the number of consecutive days the user has opened the app or received a daily fact, derived from the persisted seen-fact-history timestamps rather than a separately stored counter.

#### Scenario: Consecutive days increase the streak
- **WHEN** the user has opened the app (or received a daily notification fact) on each of the last N consecutive calendar days
- **THEN** the displayed streak value is N

#### Scenario: A missed day resets the streak
- **WHEN** at least one calendar day passes with no app open and no daily notification recorded
- **THEN** the streak value resets to reflect only the current unbroken run of days, not the prior run
