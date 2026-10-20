## ADDED Requirements

### Requirement: Installable as an app
The system SHALL provide a web app manifest (name "Bitcoin Bites", icons including at least 192px and 512px sizes, dark theme and background colors, standalone display, start URL and scope within the site's base path) so supporting browsers offer to install the site.

#### Scenario: Install prompt is available
- **WHEN** the site is opened over HTTPS in a browser that supports installing web apps
- **THEN** the browser recognises the site as installable and the installed app opens to the home page within the site's base path

### Requirement: Fully usable offline after the first visit
The system SHALL register a service worker that, on install, precaches every generated page, `facts.json`, and all static assets, so the whole site works offline after one successful online visit.

#### Scenario: Opening a never-visited fact while offline
- **WHEN** a visitor has loaded the site once online, goes offline, and then opens a fact page they have never visited
- **THEN** the fact page loads from the cache

#### Scenario: Journeys work offline
- **WHEN** the visitor is offline after a prior online visit and opens Random Bites
- **THEN** the journey loads and shuffles through every fact

### Requirement: New deploys replace cached content
The system SHALL give each build a cache version derived from the site's contents, SHALL fetch pages from the network first and fall back to the cache, and SHALL delete caches from older versions once the new service worker activates.

#### Scenario: A new fact is published
- **WHEN** a new fact is deployed and a returning visitor opens the site while online
- **THEN** the visitor sees the new fact without needing to clear browser data, and the old version's cache is removed
