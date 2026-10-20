## ADDED Requirements

### Requirement: Automatic deploy on merge to main
The system SHALL provide a GitHub Actions workflow that builds the site and deploys it to GitHub Pages on every push to `main`, and SHALL also allow a manual run.

#### Scenario: Merging content updates the live site
- **WHEN** a commit that changes a fact is pushed to `main`
- **THEN** the workflow builds the site and the live GitHub Pages site shows the updated fact

#### Scenario: Manual redeploy
- **WHEN** a maintainer triggers the workflow manually
- **THEN** the site is rebuilt and redeployed from the selected ref

### Requirement: Broken builds never deploy
The workflow SHALL run the site generator's tests and the client-side JavaScript tests, and SHALL deploy only if the build and all tests succeed.

#### Scenario: Invalid content blocks deployment
- **WHEN** a commit pushed to `main` contains a fact with an invalid `related` ID
- **THEN** the workflow fails before the deploy step and the previously deployed site stays live

### Requirement: Built output is never committed
The system SHALL deploy the built site as a Pages artifact (Pages source "GitHub Actions"), SHALL NOT commit generated HTML to any branch, and SHALL leave the repository's default README view unchanged.

#### Scenario: Repository view after a deploy
- **WHEN** a deploy completes
- **THEN** no new commits or branches containing generated site files exist, and the repository home page still renders `README.md`

### Requirement: Deploys use the Pages base URL and don't overlap
The workflow SHALL build the site with the base URL GitHub Pages reports for the repository, and SHALL allow only one deployment at a time.

#### Scenario: Base URL comes from Pages configuration
- **WHEN** the workflow runs for `walshe/bitcoin-bites`
- **THEN** the site's canonical and Open Graph URLs begin with `https://walshe.github.io/bitcoin-bites/`

#### Scenario: Two pushes in quick succession
- **WHEN** two commits are pushed to `main` while a deploy is running
- **THEN** deployments do not run concurrently, and the site ends up reflecting the latest commit
