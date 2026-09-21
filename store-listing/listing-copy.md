# Play store listing copy

Paste-ready text for the Play Console listing, plus prepared answers for the
Data safety and Content rating forms.

Character counts are against Play's limits. **The fact count (55) appears in the
short and full descriptions — update both when content is added.**

---

## App name

Play limit: 30 characters.

**Bitcoin Bites** *(13)*

If you'd rather carry a keyword for search, this still fits:

**Bitcoin Bites — Learn Bitcoin** *(29)*

---

## Short description

Play limit: 80 characters. Shown under the app name; this is the line that
actually sells the install.

**Learn Bitcoin in plain English. 55 bite-sized facts, each with an analogy.** *(74)*

Alternatives:

- `Bitcoin explained in plain English — 55 short facts, offline and ad-free.` *(73)*
- `Bitcoin Knowledge — one bite at a time. 55 short, plainly-worded facts.` *(71)*

---

## Full description

Play limit: 4000 characters. The text below is ~1750.

```
Bitcoin, explained one bite at a time.

Bitcoin Bites is for the genuinely curious — people who keep hearing about
Bitcoin and want to actually understand it, not just nod along. No jargon, no
hype, no price charts. Just short, plainly-worded explanations you can read in
a minute.

WHAT'S INSIDE

55 bite-sized facts covering how Bitcoin really works: money and fiat, mining
and proof of work, keys and wallets, the network, scarcity, and the history
behind it all.

Every fact is built the same way:
• A short explanation in everyday language
• "What Bitcoin fixes here" — the specific problem being solved, so you can see
  the point rather than having to infer it
• An analogy that makes it stick, from bar tabs and coat checks to wet concrete
  and limbo bars

THREE WAYS TO READ

• Guided Bites — work through everything in order, building up from first
  principles, with your place saved
• Random Bites — shuffle and learn something new
• All Bites — browse the full list, filter by topic, jump to whatever you're
  curious about

Facts are tagged beginner, intermediate or advanced, and linked to related ones
so you can follow a thread as far as you like.

ALSO

• A daily fact notification, if you want one
• Save favorites to come back to
• A streak that counts the days you show up
• Share any fact with a friend

BUILT TO RESPECT YOU

• Works completely offline — there is no server, and the app makes no network
  calls at all
• No account, no sign-up, no email required
• No ads, no tracking, no analytics
• Nothing is collected about you. Favorites, streak and progress stay on your
  device

Bitcoin Bites is an educational app. It does not buy, sell, store or transfer
bitcoin, it is not a wallet or an exchange, and nothing in it is financial
advice.

Content is drawn from published Bitcoin books, including "Inventing Bitcoin" by
Yan Pritzker.
```

---

## Other listing fields

| Field | Value |
|---|---|
| Category | Education |
| Tags | Education, Reference |
| Contact email | bitesbitcoin@gmail.com |
| Privacy policy | https://walshe.github.io/cv/bitcoin-bites/ |
| Website | optional — leave blank or use your own site |
| Phone | optional — leave blank |

**Target audience:** choose **13+**. The app has nothing unsuitable for younger
users, but selecting an under-13 audience pulls the listing into Play's Families
policy, which adds requirements this app has no reason to take on.

---

## Data safety form

The whole form is straightforward here, because the app genuinely collects
nothing. The single question that decides the rest:

> Does your app collect or share any of the required user data types?

**No.**

Everything else then disappears or is not applicable:

| Question | Answer |
|---|---|
| Data collected | None |
| Data shared with third parties | None |
| Encrypted in transit | N/A — no data leaves the device |
| Way to request deletion | N/A — nothing is collected |
| Uses advertising ID | No |
| Third-party SDKs collecting data | None |

**Supporting facts if you're asked to justify it:** the app has no server.
Favorites, reading streak, journey position and notification history are
written to a local SQLite database on the device and never leave it.
Uninstalling removes them.

The strongest evidence is the manifest: **the app does not request
`android.permission.INTERNET`**, so it is not merely that it doesn't make
network calls — Android will not permit it to. That is worth knowing if a
reviewer ever questions the "no data collected" declaration.

The only permission declared in source is `POST_NOTIFICATIONS`, and it is
optional — the app works fully if denied. Four more appear in the *merged*
manifest, pulled in by WorkManager (which schedules the daily fact) rather than
requested by this app's own code:

| Permission | Why it's there |
|---|---|
| `ACCESS_NETWORK_STATE` | WorkManager checks connectivity constraints. Reads state only; grants no network access. |
| `WAKE_LOCK` | WorkManager holds one while a job runs. |
| `RECEIVE_BOOT_COMPLETED` | Lets WorkManager restore the daily schedule after a reboot. |
| `FOREGROUND_SERVICE` | Declared by WorkManager; this app schedules no foreground work. |

None of them collects or transmits anything, and none changes the Data safety
answers above.

---

## Content rating questionnaire

Select **Reference, News, or Educational** as the category, then:

| Question | Answer |
|---|---|
| Violence | No |
| Sexuality | No |
| Profanity | No |
| Controlled substances | No |
| Gambling — real or simulated | **No** |
| User-generated content | No |
| Users can interact / share location | No |
| Shares user-provided personal info | No |
| Digital purchases | No |
| Displays ads | No |

**On the gambling question:** several facts describe mining as a "lottery" or
"raffle". Those are explanatory analogies in body text — the app contains no
gambling, simulated gambling, or wagering of any kind. Answer No.

---

## Pre-submission checklist

- [ ] Upload `androidApp/build/outputs/bundle/release/androidApp-release.aab`
- [ ] Screenshots from `screenshots/` (at least 2 required; 4 provided)
- [ ] `feature-graphic.png`
- [ ] 512×512 icon from `../bitcoin-bites-bunny-clean-assets/bitcoin-bites-bunny-512.png`
- [ ] Short + full description from above
- [ ] Privacy policy URL
- [ ] Data safety form
- [ ] Content rating questionnaire
- [ ] Consider an Internal Testing release first — it catches upload-time
      rejections without burning a production review
