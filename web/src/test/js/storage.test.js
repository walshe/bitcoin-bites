import { test } from "node:test";
import assert from "node:assert/strict";
import { createStore, MAX_OPENS } from "../../main/site/js/storage.js";

function memoryBackend() {
  const data = new Map();
  return {
    data,
    getItem: (key) => (data.has(key) ? data.get(key) : null),
    setItem: (key, value) => data.set(key, String(value)),
  };
}

const throwingBackend = {
  getItem() {
    throw new Error("SecurityError");
  },
  setItem() {
    throw new Error("SecurityError");
  },
};

test("favorites persist through the backend", () => {
  const backend = memoryBackend();
  assert.equal(createStore(backend).toggleFavorite("a"), true);
  const reopened = createStore(backend);
  assert.deepEqual(reopened.favorites(), ["a"]);
  assert.equal(reopened.toggleFavorite("a"), false);
  assert.deepEqual(createStore(backend).favorites(), []);
});

test("progress is stored per mode", () => {
  const store = createStore(memoryBackend());
  store.setProgress("guided", "x");
  assert.equal(store.progress("guided"), "x");
  assert.equal(store.progress("random"), null);
});

test("opens are de-duplicated, sorted and capped", () => {
  const store = createStore(memoryBackend());
  store.recordOpen(5);
  store.recordOpen(3);
  store.recordOpen(5);
  assert.deepEqual(store.opens(), [3, 5]);
  for (let day = 10; day < 10 + MAX_OPENS + 5; day++) store.recordOpen(day);
  assert.equal(store.opens().length, MAX_OPENS);
  assert.equal(store.opens().at(-1), 10 + MAX_OPENS + 4);
});

test("a throwing backend falls back to in-memory state for the session", () => {
  const store = createStore(throwingBackend);
  assert.deepEqual(store.favorites(), []);
  assert.equal(store.toggleFavorite("a"), true);
  assert.ok(store.isFavorite("a"));
  store.setProgress("guided", "a");
  assert.equal(store.progress("guided"), "a");
});

test("no backend at all still works", () => {
  const store = createStore(null);
  store.recordOpen(1);
  assert.deepEqual(store.opens(), [1]);
});

test("corrupt stored JSON is treated as empty", () => {
  const backend = memoryBackend();
  backend.data.set("bb.v1.favorites", "{not json");
  assert.deepEqual(createStore(backend).favorites(), []);
});
