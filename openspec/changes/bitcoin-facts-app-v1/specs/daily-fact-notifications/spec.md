## ADDED Requirements

### Requirement: One daily notification
The system SHALL schedule and deliver exactly one local notification per day containing a fact, drawn only from facts marked `notify_eligible: true`.

#### Scenario: A day passes with notifications enabled
- **WHEN** a full day elapses with notifications enabled
- **THEN** exactly one local notification is delivered, referencing one `notify_eligible` fact

### Requirement: Random selection without repeats until exhausted
The system SHALL select each day's notification fact uniformly at random from the set of `notify_eligible` facts not yet shown in the current cycle. Once every eligible fact has been shown once, the shown-set SHALL reset and a new cycle SHALL begin.

#### Scenario: Fact already shown this cycle is not repeated
- **WHEN** the daily selection runs and some, but not all, eligible facts have already been shown in the current cycle
- **THEN** the selected fact is chosen only from the not-yet-shown subset

#### Scenario: All eligible facts have been shown
- **WHEN** every `notify_eligible` fact has been shown once in the current cycle
- **THEN** the shown-set resets and the next day's selection may choose from the full eligible pool again

### Requirement: Notification tap opens fact detail
The system SHALL, when the user taps a delivered daily notification, open the app directly to that fact's detail view.

#### Scenario: User taps the daily notification
- **WHEN** the user taps a delivered daily fact notification
- **THEN** the app opens directly to the detail view of the fact referenced in that notification
