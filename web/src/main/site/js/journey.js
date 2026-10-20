// Guided and Random Bites, matching androidApp's JourneyScreen (design.md decision 7):
// guided follows canonical order and loops back to the start; random puts the
// resumed fact first, shuffles the rest, and reshuffles each time a full pass ends.

export function shuffle(items, random = Math.random) {
  const result = [...items];
  for (let i = result.length - 1; i > 0; i--) {
    const j = Math.floor(random() * (i + 1));
    [result[i], result[j]] = [result[j], result[i]];
  }
  return result;
}

/**
 * Pure journey state over [facts] (anything with `id` and `order`). [resumeId]
 * is where to start; an unknown id is ignored.
 */
export function createJourney(facts, mode, { resumeId = null, random = Math.random } = {}) {
  const guided = mode === "guided";
  const resume = facts.find((fact) => fact.id === resumeId) ?? null;
  let order;
  if (guided) order = [...facts].sort((a, b) => a.order - b.order || a.id.localeCompare(b.id));
  else if (resume) order = [resume, ...shuffle(facts.filter((fact) => fact !== resume), random)];
  else order = shuffle(facts, random);

  const size = order.length;
  // A forever-increasing page counter, like the app's virtual pager: the fact
  // shown is page % size, and each new pass (cycle) reshuffles in random mode.
  let page = guided && resume ? order.indexOf(resume) : 0;
  let lastCycle = Math.floor(page / Math.max(size, 1));

  return {
    size,
    get index() {
      return page % size;
    },
    get current() {
      return order[page % size];
    },
    get canGoBack() {
      return page > 0;
    },
    next() {
      page++;
      const cycle = Math.floor(page / size);
      if (!guided && cycle > lastCycle) {
        lastCycle = cycle;
        order = shuffle(order, random);
      }
      return this.current;
    },
    prev() {
      if (page > 0) page--;
      return this.current;
    },
  };
}

/**
 * Where a journey starts: a fact id in the URL hash (from a reload or a shared
 * journey link) wins over [storedId], but only if it names a fact that exists -
 * a mangled or stale hash must not throw or wipe out saved progress.
 */
export function resumeIdFor(facts, hash, storedId) {
  let hashId = "";
  try {
    hashId = decodeURIComponent(hash.replace(/^#/, ""));
  } catch {
    // malformed percent-encoding - ignore the hash
  }
  return facts.some((fact) => fact.id === hashId) ? hashId : storedId;
}

const SWIPE_DISTANCE = 50;

/** Wires a journey shell page to [facts]. [share] takes `{ title, text, url }`, as share.js's shareFact. */
export function initJourney(doc, facts, { store, root, share }) {
  const section = doc.querySelector("[data-journey]");
  const mode = section.dataset.journey;
  const card = doc.querySelector("[data-journey-card]");
  const position = doc.querySelector("[data-journey-position]");
  const controls = doc.querySelector("[data-journey-controls]");
  const prevButton = doc.querySelector("[data-journey-prev]");
  const nextButton = doc.querySelector("[data-journey-next]");
  const favButton = doc.querySelector("[data-journey-fav]");
  const shareButton = doc.querySelector("[data-journey-share]");

  if (facts.length === 0) {
    card.innerHTML = '<p class="empty">No bites to show yet.</p>';
    return;
  }

  const journey = createJourney(facts, mode, { resumeId: resumeIdFor(facts, location.hash, store.progress(mode)) });

  function render(direction) {
    const fact = journey.current;
    const title = doc.createElement("h2");
    const link = doc.createElement("a");
    link.href = root + fact.path;
    link.textContent = fact.title;
    title.append(link);

    const content = doc.createElement("div");
    content.innerHTML = fact.html; // pre-rendered and escaped by the site generator

    card.replaceChildren(title, content);
    card.classList.remove("enter-next", "enter-prev");
    if (direction) {
      void card.offsetWidth; // restart the entry animation
      card.classList.add(direction === "next" ? "enter-next" : "enter-prev");
    }

    position.textContent = `${journey.index + 1}/${journey.size}`;
    prevButton.disabled = !journey.canGoBack;
    favButton.dataset.fav = fact.id;
    const favorited = store.isFavorite(fact.id);
    favButton.setAttribute("aria-pressed", String(favorited));
    favButton.setAttribute("aria-label", favorited ? "Unfavorite" : "Favorite");
    store.setProgress(mode, fact.id);
    history.replaceState(null, "", "#" + encodeURIComponent(fact.id));
  }

  function go(direction) {
    if (direction === "prev" && !journey.canGoBack) return;
    if (direction === "next") journey.next();
    else journey.prev();
    render(direction);
    if (doc.documentElement.scrollTop > section.offsetTop) section.scrollIntoView();
  }

  prevButton.addEventListener("click", () => go("prev"));
  nextButton.addEventListener("click", () => go("next"));
  // favButton carries data-fav, so main.js's delegated favorite handler toggles it.
  shareButton.addEventListener("click", () => {
    const fact = journey.current;
    share({ title: fact.title, text: fact.shareText, url: doc.body.dataset.siteUrl + fact.path });
  });

  doc.addEventListener("keydown", (event) => {
    if (event.altKey || event.ctrlKey || event.metaKey || event.target.closest("input, textarea")) return;
    if (event.key === "ArrowRight") go("next");
    else if (event.key === "ArrowLeft") go("prev");
  });

  let start = null;
  card.addEventListener("pointerdown", (event) => {
    start = { x: event.clientX, y: event.clientY };
  });
  card.addEventListener("pointerup", (event) => {
    if (!start) return;
    const dx = event.clientX - start.x;
    const dy = event.clientY - start.y;
    start = null;
    if (Math.abs(dx) < SWIPE_DISTANCE || Math.abs(dx) < Math.abs(dy)) return;
    go(dx < 0 ? "next" : "prev"); // swipe left = next, as in the app
  });
  card.addEventListener("pointercancel", () => {
    start = null;
  });

  controls.hidden = false;
  render(null);
}
