## ADDED Requirements

### Requirement: Filter All Bites by category
The system SHALL let the visitor select one or more categories on the All Bites page and show only facts that have at least one selected category (union semantics, as in the app). Clearing every selection SHALL show all facts again.

#### Scenario: Selecting multiple categories shows the union
- **WHEN** the visitor selects the "keys" and "custody" category filters
- **THEN** facts with "keys", "custody", or both are shown, and other facts are hidden

#### Scenario: Clearing filters shows every fact
- **WHEN** the visitor deselects all category filters
- **THEN** every fact is shown again

### Requirement: Guided Bites follows canonical order and resumes
The system SHALL present Guided Bites as a one-fact-at-a-time journey through every fact in canonical `order`, and SHALL resume at the last fact the visitor viewed in Guided Bites on a later visit.

#### Scenario: Starting Guided Bites for the first time
- **WHEN** a visitor with no stored progress opens Guided Bites
- **THEN** the first fact in canonical order is shown

#### Scenario: Resuming Guided Bites
- **WHEN** a visitor who last viewed the fifth fact in Guided Bites returns to Guided Bites later
- **THEN** the fifth fact is shown

### Requirement: Random Bites shuffles and loops
The system SHALL present Random Bites as a one-fact-at-a-time journey through every fact in a shuffled order, and on moving past the last fact SHALL reshuffle the same facts and continue instead of ending.

#### Scenario: Reaching the end of Random Bites
- **WHEN** the visitor moves past the last fact in the current shuffled order
- **THEN** the facts are reshuffled and the journey continues with the first fact of the new order

### Requirement: Journey navigation by touch, keyboard and buttons
The system SHALL let the visitor move to the next or previous fact in a journey by horizontal swipe, by the left and right arrow keys, and by visible Previous and Next buttons. Each journey card SHALL link to that fact's own page.

#### Scenario: Keyboard navigation
- **WHEN** the visitor presses the right arrow key in a journey
- **THEN** the next fact is shown

#### Scenario: Swipe navigation
- **WHEN** the visitor swipes left on the current fact on a touch screen
- **THEN** the next fact is shown

### Requirement: Favorites stored in the browser
The system SHALL let the visitor favorite and unfavorite any fact from its fact page, from journey cards, and from the All Bites list, SHALL list all favorited facts on the Favorites page, and SHALL keep favorites across visits in browser storage.

#### Scenario: Favorite persists across visits
- **WHEN** the visitor favorites a fact, closes the browser, and later opens the Favorites page
- **THEN** that fact is listed

#### Scenario: Unfavoriting removes it from the list
- **WHEN** the visitor unfavorites a fact that is on the Favorites page
- **THEN** that fact is no longer listed

#### Scenario: No favorites yet
- **WHEN** the visitor opens the Favorites page with nothing favorited
- **THEN** a message explains how to favorite facts, instead of an empty page

### Requirement: Daily-visit streak
The system SHALL record each calendar day (in the visitor's local time zone) on which the visitor opens any page of the site, and SHALL show on the home page the current streak: the number of consecutive days ending today or yesterday on which the site was opened, or 0 if the most recent visit was earlier than yesterday.

#### Scenario: Consecutive days increase the streak
- **WHEN** the visitor has opened the site on each of the last 3 consecutive days, including today
- **THEN** the home page shows a streak of 3

#### Scenario: A missed day resets the streak
- **WHEN** the visitor's most recent visit before today was three days ago
- **THEN** the streak shown today is 1

### Requirement: Continue reading from the home page
The system SHALL show a "continue reading" entry on the home page naming the fact the visitor last viewed in Guided Bites, which opens Guided Bites at that fact. The entry SHALL be absent when there is no stored Guided Bites progress.

#### Scenario: Returning visitor sees where they left off
- **WHEN** a visitor with stored Guided Bites progress opens the home page
- **THEN** a continue-reading entry shows that fact's title and opens Guided Bites at it

### Requirement: Share a fact from the web
The system SHALL provide a share action on fact pages and journey cards. Where the browser supports the Web Share API, it SHALL open the native share sheet with the fact's title, its body with formatting markers removed, the attribution "— Shared from Bitcoin Bites", and the fact's absolute page URL. Otherwise it SHALL copy the fact's URL to the clipboard and confirm that it was copied.

#### Scenario: Sharing on a phone
- **WHEN** the visitor taps Share on a fact page in a browser that supports the Web Share API
- **THEN** the native share sheet opens with the fact's text and its page URL

#### Scenario: Sharing on a desktop browser without Web Share
- **WHEN** the visitor clicks Share in a browser without the Web Share API
- **THEN** the fact's URL is copied to the clipboard and a short "Link copied" confirmation is shown

### Requirement: Get the Android app link
The system SHALL show a "Get the Android app" link in the site header and on the home page. Its target SHALL be the app download URL configured at build time, so it can switch from the GitHub Release APK to the Play Store listing by redeploying the site.

#### Scenario: Link uses the configured target
- **WHEN** the site is built with the app download URL set to the latest GitHub Release APK
- **THEN** the "Get the Android app" link on every page points to that URL

#### Scenario: Switching to the Play Store
- **WHEN** the site is rebuilt with the app download URL set to the Play Store listing
- **THEN** the link points to the Play Store listing, and no Android app release is needed

### Requirement: Works without an account or network backend
The system SHALL deliver every feature using only static files and browser storage, with no sign-in and no requests to any backend service. Unavailable browser storage (for example, blocked in private browsing) SHALL NOT stop facts from being read; the visitor's state then lasts only for the current page session.

#### Scenario: Storage is blocked
- **WHEN** browser storage throws on access
- **THEN** every fact page, the list and both journeys still work, and favorites and progress last only until the page is closed
