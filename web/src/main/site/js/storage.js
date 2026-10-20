// Per-visitor state in localStorage under a versioned "bb.v1." prefix
// (design.md decision 6). Every access is wrapped so blocked or full storage
// (private browsing, disabled site data) degrades to in-memory state for the
// current page instead of breaking anything.

const PREFIX = "bb.v1.";

/** The opens log only needs enough history for the streak; older days are dropped. */
export const MAX_OPENS = 400;

/** localStorage, or null if merely touching it throws (some browsers do in private mode). */
export function defaultBackend() {
  try {
    const storage = globalThis.localStorage;
    if (!storage) return null;
    storage.getItem(PREFIX + "probe");
    return storage;
  } catch {
    return null;
  }
}

export function createStore(backend = defaultBackend()) {
  // Writes always land here too, so this session keeps working even when the
  // backend rejects them; reads prefer it since it's never older than the backend.
  const memory = new Map();

  function read(key, fallback) {
    const fullKey = PREFIX + key;
    if (memory.has(fullKey)) return memory.get(fullKey);
    try {
      const raw = backend?.getItem(fullKey);
      if (raw != null) return JSON.parse(raw);
    } catch {
      // unreadable or corrupt - fall through to the default
    }
    return fallback;
  }

  function write(key, value) {
    const fullKey = PREFIX + key;
    memory.set(fullKey, value);
    try {
      backend?.setItem(fullKey, JSON.stringify(value));
    } catch {
      // storage blocked or full - the in-memory copy still serves this page
    }
  }

  return {
    favorites() {
      const ids = read("favorites", []);
      return Array.isArray(ids) ? ids : [];
    },
    isFavorite(id) {
      return this.favorites().includes(id);
    },
    /** Returns the new favorited state. */
    toggleFavorite(id) {
      const ids = this.favorites();
      const favorited = !ids.includes(id);
      write("favorites", favorited ? [...ids, id] : ids.filter((other) => other !== id));
      return favorited;
    },

    /** Last fact viewed in a journey mode ("guided" or "random"), or null. */
    progress(mode) {
      const id = read("progress." + mode, null);
      return typeof id === "string" ? id : null;
    },
    setProgress(mode, id) {
      write("progress." + mode, id);
    },

    /** Distinct epoch days the site was opened on, oldest first. */
    opens() {
      const days = read("opens", []);
      return Array.isArray(days) ? days.filter(Number.isInteger) : [];
    },
    recordOpen(epochDay) {
      const days = this.opens();
      if (days.includes(epochDay)) return;
      write("opens", [...days, epochDay].sort((a, b) => a - b).slice(-MAX_OPENS));
    },
  };
}
