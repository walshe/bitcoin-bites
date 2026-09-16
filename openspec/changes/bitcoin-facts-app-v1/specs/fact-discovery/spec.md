## ADDED Requirements

### Requirement: Browse all facts
The system SHALL provide a view listing all facts from the bundled runtime index, showing at minimum each fact's title and categories.

#### Scenario: Opening the app shows the fact list
- **WHEN** the user opens the app
- **THEN** a list of facts (title and categories visible) is displayed, sourced entirely from the bundled index with no network request

### Requirement: Filter facts by category
The system SHALL allow the user to select one or more canonical categories and see only facts that have at least one selected category.

#### Scenario: Selecting a single category filters the list
- **WHEN** the user selects the "mining" category filter
- **THEN** only facts whose `categories` include "mining" are shown

#### Scenario: Selecting multiple categories shows the union
- **WHEN** the user selects both "keys" and "custody" category filters
- **THEN** facts that have "keys", "custody", or both are shown

#### Scenario: Clearing all filters shows every fact
- **WHEN** the user deselects all category filters
- **THEN** the full fact list is shown again

### Requirement: View fact detail
The system SHALL provide a detail view for a selected fact showing its full explanation text, its analogy, its list of sources (each with a title and a link the user can open), and its related facts (if any).

#### Scenario: Viewing a fact's detail
- **WHEN** the user taps a fact in the list
- **THEN** the detail view shows the fact's full text, analogy, all of its sources as tappable links, and a "related facts" section if the fact has any related facts

#### Scenario: Fact with no related facts
- **WHEN** the user views the detail of a fact whose compiled `related` list is empty
- **THEN** the detail view omits the "related facts" section entirely

### Requirement: Navigate to a related fact
The system SHALL allow the user to tap a related fact shown in a fact's detail view and be taken directly to that fact's own detail view.

#### Scenario: Tapping a related fact navigates to it
- **WHEN** the user taps a related fact entry in fact A's detail view
- **THEN** the app navigates to fact B's detail view, showing B's own text, analogy, sources, and related facts

### Requirement: Share a fact
The system SHALL provide a share action on the fact detail view that invokes the platform's native share sheet with the fact's title and a short excerpt of its text.

#### Scenario: Sharing a fact from detail view
- **WHEN** the user taps the share action on a fact's detail view
- **THEN** the platform's native share sheet opens, pre-populated with that fact's title and a short excerpt

#### Scenario: Sharing does not require any account or network call
- **WHEN** the user shares a fact
- **THEN** no app account, sign-in, or backend network call is required to complete the share
