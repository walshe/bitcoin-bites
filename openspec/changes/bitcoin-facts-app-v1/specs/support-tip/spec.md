## ADDED Requirements

### Requirement: Optional one-time tip purchase
The system SHALL offer a single optional in-app purchase ("tip"/"support") that the user may buy at most once. The purchase SHALL NOT be required for, and SHALL NOT gate access to, any fact content or any core feature (browsing, filtering, favorites, notifications).

#### Scenario: App is fully usable without purchasing
- **WHEN** the user never completes the tip purchase
- **THEN** every fact, category filter, favorite action, and daily notification remains fully available

#### Scenario: User completes the tip purchase
- **WHEN** the user completes the tip purchase through the platform's billing flow
- **THEN** the purchase is recorded and the cosmetic reward becomes available

### Requirement: Cosmetic reward unlock
Upon a successful tip purchase, the system SHALL unlock a cosmetic reward (an alternate app icon or theme) that has no effect on app functionality or content access.

#### Scenario: Selecting the unlocked cosmetic reward
- **WHEN** the user has completed the tip purchase and selects the unlocked cosmetic option
- **THEN** the app applies the cosmetic change (e.g. alternate icon/theme) without altering any other behavior

### Requirement: Purchase state persists across restarts
The system SHALL remember a completed tip purchase across app restarts so the user is not asked to purchase again and retains access to the unlocked cosmetic reward.

#### Scenario: Restarting after purchase
- **WHEN** the user has completed the tip purchase, then closes and reopens the app
- **THEN** the app still reflects the purchase as complete and the cosmetic reward remains selectable
