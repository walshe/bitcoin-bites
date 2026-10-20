import { test } from "node:test";
import assert from "node:assert/strict";
import { buildShareText, shareFact } from "../../main/site/js/share.js";

const fact = { title: "T", text: "T\n\nBody.", url: "https://example.test/facts/t/" };

test("share text ends with the attribution and the fact's URL", () => {
  assert.equal(buildShareText("Body.", "https://u/"), "Body.\n\n— Shared from Bitcoin Bites\nhttps://u/");
});

test("uses the Web Share API when available", async () => {
  let shared;
  await shareFact(fact, { nav: { share: async (data) => (shared = data) } });
  assert.equal(shared.title, "T");
  assert.ok(shared.text.endsWith("— Shared from Bitcoin Bites\nhttps://example.test/facts/t/"));
});

test("falls back to copying the link", async () => {
  let copied;
  let toast;
  await shareFact(fact, {
    nav: { clipboard: { writeText: async (text) => (copied = text) } },
    showToast: (message) => (toast = message),
  });
  assert.equal(copied, fact.url);
  assert.equal(toast, "Link copied");
});

test("a cancelled share sheet shows nothing", async () => {
  let toast = null;
  const abort = Object.assign(new Error("cancelled"), { name: "AbortError" });
  await shareFact(fact, { nav: { share: async () => { throw abort; } }, showToast: (m) => (toast = m) });
  assert.equal(toast, null);
});
