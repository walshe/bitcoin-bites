import { test } from "node:test";
import assert from "node:assert/strict";
import { createJourney, shuffle } from "../../main/site/js/journey.js";

const facts = [
  { id: "c", order: 3 },
  { id: "a", order: 1 },
  { id: "b", order: 2 },
];

/** Deterministic pseudo-random sequence, so shuffles are repeatable. */
function seeded(seed = 1) {
  return () => {
    seed = (seed * 16807) % 2147483647;
    return (seed - 1) / 2147483646;
  };
}

const ids = (list) => list.map((f) => f.id);

test("shuffle keeps every item exactly once", () => {
  const result = shuffle(facts, seeded(7));
  assert.deepEqual(ids(result).sort(), ["a", "b", "c"]);
});

test("guided starts at the first fact in canonical order", () => {
  const journey = createJourney(facts, "guided");
  assert.equal(journey.current.id, "a");
  assert.equal(journey.next().id, "b");
  assert.equal(journey.next().id, "c");
});

test("guided resumes at the stored fact", () => {
  const journey = createJourney(facts, "guided", { resumeId: "b" });
  assert.equal(journey.current.id, "b");
  assert.equal(journey.index, 1);
  assert.ok(journey.canGoBack);
});

test("guided loops back to the start after the last fact, in the same order", () => {
  const journey = createJourney(facts, "guided", { resumeId: "c" });
  assert.equal(journey.next().id, "a");
  assert.equal(journey.next().id, "b");
});

test("an unknown resume id is ignored", () => {
  assert.equal(createJourney(facts, "guided", { resumeId: "gone" }).current.id, "a");
});

test("random puts the resumed fact first", () => {
  assert.equal(createJourney(facts, "random", { resumeId: "c", random: seeded(3) }).current.id, "c");
});

test("random shows every fact once per pass, then reshuffles and keeps going", () => {
  const journey = createJourney(facts, "random", { random: seeded(5) });
  const firstPass = [journey.current.id, journey.next().id, journey.next().id];
  assert.deepEqual([...firstPass].sort(), ["a", "b", "c"]);
  const secondPass = [journey.next().id, journey.next().id, journey.next().id];
  assert.deepEqual([...secondPass].sort(), ["a", "b", "c"]);
  assert.equal(journey.index, 2);
});

test("cannot go back before the first fact", () => {
  const journey = createJourney(facts, "guided");
  assert.equal(journey.canGoBack, false);
  assert.equal(journey.prev().id, "a");
});
