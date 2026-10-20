// Entry point on every page: records today's visit for the streak, wires
// favorites and share buttons, then runs the page-specific enhancement named
// by <body data-page>. Pre-rendered content never depends on any of this.
import { createStore } from "./storage.js";
import { currentStreak, epochDay } from "./streak.js";
import { initFilter } from "./filter.js";
import { initFavoriteButtons, initFavoritesPage } from "./favorites.js";
import { initJourney } from "./journey.js";
import { shareFact } from "./share.js";

const doc = document;
const { root, page } = doc.body.dataset;
const store = createStore();

let toastTimer;
function showToast(message) {
  const toast = doc.querySelector(".toast");
  toast.textContent = message;
  toast.hidden = false;
  clearTimeout(toastTimer);
  toastTimer = setTimeout(() => {
    toast.hidden = true;
  }, 2500);
}

const share = (fact) => shareFact(fact, { showToast });

async function loadFacts() {
  const response = await fetch(root + "facts.json");
  if (!response.ok) throw new Error(`facts.json: HTTP ${response.status}`);
  return (await response.json()).facts;
}

function initShareButtons() {
  for (const button of doc.querySelectorAll("[data-share]")) {
    button.addEventListener("click", () => {
      const { shareTitle: title, shareText: text, shareUrl: url } = button.dataset;
      share({ title, text, url });
    });
  }
}

async function initHome() {
  const streak = currentStreak(store.opens(), epochDay());
  const streakEl = doc.querySelector("[data-streak]");
  if (streak > 0) {
    streakEl.textContent = `🔥 ${streak}-day streak`;
    streakEl.hidden = false;
  }

  const continueId = store.progress("guided");
  if (!continueId) return;
  const fact = (await loadFacts()).find((f) => f.id === continueId);
  if (!fact) return;
  const card = doc.querySelector("[data-continue]");
  card.href = `${root}guided/#${encodeURIComponent(fact.id)}`;
  doc.querySelector("[data-continue-title]").textContent = fact.title;
  card.hidden = false;
}

async function initPage() {
  switch (page) {
    case "home":
      return initHome();
    case "list":
      return initFilter(doc);
    case "journey":
      return initJourney(doc, await loadFacts(), { store, root, share });
    case "favorites":
      return initFavoritesPage(doc, await loadFacts(), { store, root });
  }
}

store.recordOpen(epochDay());
initFavoriteButtons(doc, store);
initShareButtons();
initPage().catch((error) => {
  console.error(error);
  const card = doc.querySelector("[data-journey-card]");
  if (card) card.innerHTML = `<p class="empty">Couldn't load the bites. <a href="${root}facts/">Browse all bites</a> instead.</p>`;
});

if ("serviceWorker" in navigator) {
  navigator.serviceWorker.register(root + "sw.js", { scope: root }).catch((error) => console.warn("Service worker:", error));
}
