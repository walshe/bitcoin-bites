## ADDED Requirements

### Requirement: Fact authoring format
Each fact SHALL be authored as a single Markdown file with YAML frontmatter containing at minimum: `id` (unique string), `title`, `categories` (non-empty list), `sources` (non-empty list of `{title, url}`), and `notify_eligible` (boolean). Frontmatter MAY additionally include `difficulty` and `related` (list of other fact IDs). The Markdown body SHALL contain the fact's explanation text and an analogy.

#### Scenario: Fact missing required field fails validation
- **WHEN** the content compiler processes a fact file that omits `sources`, `categories`, or `id`
- **THEN** the build fails with an error identifying the file and the missing field

#### Scenario: Well-formed fact compiles successfully
- **WHEN** the content compiler processes a fact file containing `id`, `title`, non-empty `categories`, non-empty `sources`, and `notify_eligible`
- **THEN** the fact is included in the compiled runtime index

### Requirement: Canonical category taxonomy validation
The system SHALL maintain a single fixed, closed list of canonical category names. The content compiler SHALL validate that every category listed in every fact's frontmatter exists in this canonical list.

#### Scenario: Fact uses an unlisted category
- **WHEN** the content compiler processes a fact whose `categories` list includes a value not present in the canonical taxonomy
- **THEN** the build fails with an error naming the offending fact file and the unrecognized category

#### Scenario: Fact uses multiple valid categories
- **WHEN** a fact's frontmatter lists two or more categories that all exist in the canonical taxonomy
- **THEN** the fact is compiled and associated with all of those categories

### Requirement: Duplicate fact ID detection
The content compiler SHALL fail the build if two or more fact files declare the same `id`.

#### Scenario: Two facts share an ID
- **WHEN** the content compiler finds two fact files with identical `id` values
- **THEN** the build fails with an error listing both file paths and the conflicting ID

### Requirement: Related-fact symmetric linking
When a fact A declares another fact B in its `related` list, the content compiler SHALL ensure fact B's compiled `related` list also includes fact A, without requiring the author to declare both directions.

#### Scenario: One-directional authored link is mirrored
- **WHEN** fact A's frontmatter contains `related: [B]` and fact B's frontmatter does not list A
- **THEN** the compiled runtime index shows A related to B and B related to A

#### Scenario: Related ID does not exist
- **WHEN** a fact's `related` list references a fact ID that has no corresponding fact file
- **THEN** the build fails with an error naming the fact and the missing referenced ID

### Requirement: Build-time compilation into bundled runtime index
The content compiler SHALL run at build time and produce a single runtime-consumable index (containing all validated, cross-linked facts) that is bundled into the application binary. No network fetch of fact content SHALL occur at runtime.

#### Scenario: Successful build produces a bundled index
- **WHEN** all fact files pass validation
- **THEN** the compiler produces a runtime index file containing every fact, its resolved category list, its mirrored related-facts list, and its sources, ready to be embedded in the app build

### Requirement: Compiled index declares a schema version
The compiled runtime index SHALL include a schema version number identifying the shape of its own contents, separate from any individual fact's data.

#### Scenario: Compiled index includes a schema version
- **WHEN** the content compiler produces a runtime index
- **THEN** the index includes a schema version field alongside the compiled facts
