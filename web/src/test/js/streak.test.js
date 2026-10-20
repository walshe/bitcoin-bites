// Same cases as shared's StreakCalculatorTest, so the two implementations can't drift.
import { test } from "node:test";
import assert from "node:assert/strict";
import { currentStreak, epochDay } from "../../main/site/js/streak.js";

test("no recorded days means zero streak", () => {
  assert.equal(currentStreak([], 100), 0);
});

test("a single day open today is a streak of one", () => {
  assert.equal(currentStreak([100], 100), 1);
});

test("consecutive days ending yesterday still count (today not yet opened)", () => {
  assert.equal(currentStreak([97, 98, 99], 100), 3);
});

test("consecutive days ending today count fully", () => {
  assert.equal(currentStreak([96, 97, 98, 99, 100], 100), 5);
});

test("a gap before the most recent day breaks the streak to zero", () => {
  assert.equal(currentStreak([90, 91, 92, 98], 100), 0);
});

test("a gap in the middle of history does not extend past it", () => {
  assert.equal(currentStreak([90, 99, 100], 100), 2);
});

test("epochDay uses the local calendar day", () => {
  assert.equal(epochDay(new Date(1970, 0, 1, 23, 59)), 0);
  assert.equal(epochDay(new Date(1970, 0, 2, 0, 1)), 1);
});
