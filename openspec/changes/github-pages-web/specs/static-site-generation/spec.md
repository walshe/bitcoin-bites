## ADDED Requirements

### Requirement: Site is generated from the same validated content as the app
The system SHALL generate the static site from `content/facts/*.md` using the same compilation and validation as the app build, and SHALL fail the site build with the compiler's error messages if any fact is invalid.

#### Scenario: Valid content produces a site
- **WHEN** the site build runs and every fact file is valid
- **THEN** a complete static site is written to the site output directory

#### Scenario: Invalid content fails the site build
- **WHEN** the site build runs and a fact references a category that does not exist in the taxonomy
- **THEN** the build fails with the same validation error the app build would report, and no site is written

### Requirement: One pre-rendered page per fact at a stable URL
The system SHALL generate one HTML page per fact at `facts/<id>/index.html`, containing the fact's title, categories, difficulty, main body text, analogy (as a distinct block when present), and links to each related fact's page, all readable without JavaScript. Sources SHALL be shown as links only when source display is enabled, which mirrors the Android app's `SHOW_SOURCES` setting (currently off).

#### Scenario: Fact page contains the full fact
- **WHEN** the site is generated
- **THEN** `facts/what-is-bitcoin/index.html` exists and contains that fact's title, full body and analogy in its HTML source

#### Scenario: Sources follow the app's setting
- **WHEN** source display is disabled, as it is in the Android app
- **THEN** fact pages show no sources section

#### Scenario: Fact page links its related facts
- **WHEN** a fact has related facts in the compiled index
- **THEN** its page links to each related fact's own page, and a fact with no related facts has no related section

### Requirement: Body formatting matches the app
The system SHALL format fact bodies with the same rules the Android app uses: paragraphs split on blank lines, the `**Analogy:**` paragraph rendered as a separate analogy block, and `**bold**` spans rendered as bold text.

#### Scenario: Bold markers are rendered, not shown literally
- **WHEN** a fact body contains `**hash**`
- **THEN** the page renders "hash" in bold and the literal `**` characters do not appear in the visible text

### Requirement: Content is HTML-escaped
The system SHALL HTML-escape all fact content inserted into generated pages, so characters such as `<`, `>`, `&` and quotes in titles, bodies or sources appear as text and never as markup.

#### Scenario: Angle brackets in a fact body
- **WHEN** a fact body contains the text `a < b && c > d`
- **THEN** the generated page displays that text literally and contains no unintended HTML elements

### Requirement: Home, list, journey, favorites and not-found pages
The system SHALL generate a home page with Guided Bites, Random Bites and All Bites entry points; an All Bites page listing every fact with its title and categories in canonical order; shell pages for Guided Bites, Random Bites and Favorites; and a `404.html` page linking back to the home page.

#### Scenario: All Bites lists every fact without JavaScript
- **WHEN** the All Bites page is loaded with JavaScript disabled
- **THEN** every fact is listed with its title and categories, each linking to its fact page

#### Scenario: Unknown URL shows the not-found page
- **WHEN** a visitor requests a path that does not exist on the deployed site
- **THEN** the generated `404.html` is shown with a link back to the home page

### Requirement: Link-preview metadata on every page
The system SHALL include a `<title>`, a meta description, a canonical URL, and Open Graph and Twitter card tags (title, description, absolute URL, absolute image URL) on every generated page. On fact pages the title is the fact's title and the description is an excerpt of its body with formatting markers removed.

#### Scenario: Shared fact link unfurls with the fact
- **WHEN** a fact page's HTML is inspected
- **THEN** its `og:title` is the fact's title, its `og:description` is a plain-text excerpt of the fact, and its `og:url` is the fact's absolute URL

### Requirement: Base-path-aware URLs
The system SHALL use relative URLs for all internal links and assets, and SHALL build absolute URLs (canonical, Open Graph, sitemap) from a configurable site base URL, so the site works when served from a sub-path such as `/bitcoin-bites/`.

#### Scenario: Site works under a sub-path
- **WHEN** the generated site is served at `https://walshe.github.io/bitcoin-bites/`
- **THEN** every internal link and asset resolves within `/bitcoin-bites/` and none points to the domain root

#### Scenario: Base URL is configurable
- **WHEN** the site is built with a different site base URL
- **THEN** canonical, Open Graph and sitemap URLs use that base URL, and internal links are unchanged

### Requirement: Sitemap and fact index are published
The system SHALL generate a `sitemap.xml` listing the absolute URL of every page, and SHALL publish the compiled fact index as `facts.json` for use by the client-side features.

#### Scenario: Sitemap lists every fact
- **WHEN** the site is generated
- **THEN** `sitemap.xml` contains one entry for each fact page plus the home and All Bites pages

### Requirement: Video facts are not published
The system SHALL leave every fact with a `video_url` off the site: no fact page, no entry in All Bites, the journeys, `facts.json` or `sitemap.xml`, and no related-fact links pointing to it. This matches the Android app, where video facts are kept out of All Bites and the journeys and their Videos entry point is hidden.

#### Scenario: A video fact is skipped
- **WHEN** the site is generated from content that includes a fact with a `video_url`
- **THEN** no page exists for that fact, and no list, journey data, sitemap entry or related link refers to it

#### Scenario: Related links to a video fact are dropped
- **WHEN** a text fact lists a video fact among its related facts
- **THEN** the text fact's page shows its other related facts but no link to the video fact
