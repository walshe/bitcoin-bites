// Web Share API where available, otherwise copy the link (design.md decision 10).
// The text matches the Android share text, but links to the fact's own page.

export function buildShareText(text, url) {
  return `${text}\n\n— Shared from Bitcoin Bites\n${url}`;
}

/**
 * [showToast] reports the clipboard fallback's outcome. The URL travels inside
 * the text rather than as `url`, since several share targets keep only one of the two.
 */
export async function shareFact({ title, text, url }, { nav = globalThis.navigator, showToast = () => {} } = {}) {
  if (nav?.share) {
    try {
      await nav.share({ title, text: buildShareText(text, url) });
    } catch (error) {
      if (error?.name !== "AbortError") showToast("Couldn't open sharing");
    }
    return;
  }
  try {
    await nav.clipboard.writeText(url);
    showToast("Link copied");
  } catch {
    showToast("Couldn't copy the link");
  }
}
