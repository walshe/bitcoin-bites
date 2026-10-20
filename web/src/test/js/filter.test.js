import { test } from "node:test";
import assert from "node:assert/strict";
import { matchesFilter } from "../../main/site/js/filter.js";

test("no selection matches everything", () => {
  assert.ok(matchesFilter(["mining"], new Set()));
});

test("a single category filters to facts that have it", () => {
  assert.ok(matchesFilter(["keys", "privacy"], new Set(["keys"])));
  assert.ok(!matchesFilter(["money"], new Set(["keys"])));
});

test("multiple categories show the union", () => {
  const selected = new Set(["keys", "custody"]);
  assert.ok(matchesFilter(["keys"], selected));
  assert.ok(matchesFilter(["custody"], selected));
  assert.ok(matchesFilter(["keys", "custody"], selected));
  assert.ok(!matchesFilter(["money"], selected));
});
