## ADDED Requirements

### Requirement: Home/menu screen with two entry points
The system SHALL provide a home/menu screen, shown on app launch, offering "Browse All" (the existing fact list) and "Random Journey" (an unfiltered swipeable journey through every fact) as distinct entry points.

#### Scenario: Choosing Browse All
- **WHEN** the user taps "Browse All" on the home screen
- **THEN** the app navigates to the existing fact list screen

#### Scenario: Choosing Random Journey
- **WHEN** the user taps "Random Journey" on the home screen
- **THEN** the app navigates directly to the Journey screen with no category filter applied (all facts eligible)

### Requirement: Start a journey scoped to the current category filter
The system SHALL provide a "Start Journey" action on the fact list screen that launches the Journey screen scoped to whatever categories are currently selected in the list screen's category filter (an empty selection means all facts).

#### Scenario: Starting a journey with categories selected
- **WHEN** the user has selected one or more categories in the list screen's filter and taps "Start Journey"
- **THEN** the Journey screen shows only facts matching at least one of the selected categories (the same union semantics as the list filter)

#### Scenario: Starting a journey with no categories selected
- **WHEN** the user has no categories selected in the list screen's filter and taps "Start Journey"
- **THEN** the Journey screen includes every fact

### Requirement: Swipe through facts one at a time
The system SHALL let the user move to the next or previous fact in a journey via a horizontal swipe gesture, showing one fact's full detail (title, body/analogy, sources, favorite control) per screen.

#### Scenario: Swiping to the next fact
- **WHEN** the user swipes left on the current fact card
- **THEN** the next fact in the journey's shuffled order is shown with its own full detail

#### Scenario: Favoriting from within a journey
- **WHEN** the user taps the favorite control while viewing a fact in a journey
- **THEN** that fact's favorited state is updated the same way as favoriting from the list or detail screen

### Requirement: Journeys reshuffle and loop rather than ending
The system SHALL, upon reaching the end of a journey's shuffled fact order, reshuffle that same set of facts and continue rather than presenting an end-of-journey state.

#### Scenario: Reaching the end of a journey
- **WHEN** the user swipes past the last fact in the current shuffled order
- **THEN** the same set of facts is reshuffled into a new order and the journey continues from the first fact of that new order

### Requirement: Empty journey scope is handled gracefully
The system SHALL show a clear message, rather than an error or blank screen, when a journey's category scope matches no facts.

#### Scenario: Starting a journey with an empty result set
- **WHEN** the user starts a journey scoped to categories that currently match no facts
- **THEN** the Journey screen shows a message indicating there are no facts for that selection, with a way to go back
