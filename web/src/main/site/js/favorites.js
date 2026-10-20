// Favorite toggles and the Favorites page. Every heart button carries
// data-fav="<fact id>", so one delegated handler covers pre-rendered lists,
// fact pages and journey cards alike.

function syncButtons(doc, id, favorited) {
  for (const button of doc.querySelectorAll("[data-fav]")) {
    if (button.dataset.fav !== id) continue;
    button.setAttribute("aria-pressed", String(favorited));
    button.setAttribute("aria-label", favorited ? "Unfavorite" : "Favorite");
  }
}

export function initFavoriteButtons(doc, store) {
  for (const button of doc.querySelectorAll("[data-fav]")) {
    if (button.dataset.fav) syncButtons(doc, button.dataset.fav, store.isFavorite(button.dataset.fav));
  }
  doc.addEventListener("click", (event) => {
    const button = event.target.closest("[data-fav]");
    if (!button || !button.dataset.fav) return;
    const id = button.dataset.fav;
    const favorited = store.toggleFavorite(id);
    syncButtons(doc, id, favorited);
    doc.dispatchEvent(new CustomEvent("favoritechange", { detail: { id, favorited } }));
  });
}

/** Lists favorited facts in canonical order; unfavoriting here removes the row. */
export function initFavoritesPage(doc, facts, { store, root }) {
  const list = doc.querySelector("[data-favorites-list]");
  const empty = doc.querySelector("[data-favorites-empty]");
  const favorites = new Set(store.favorites());

  for (const fact of facts.filter((f) => favorites.has(f.id)).sort((a, b) => a.order - b.order)) {
    const item = doc.createElement("li");
    const link = doc.createElement("a");
    link.href = root + fact.path;
    const title = doc.createElement("span");
    title.className = "title";
    title.textContent = fact.title;
    link.append(title);

    const button = doc.createElement("button");
    button.type = "button";
    button.className = "icon-button fav";
    button.dataset.fav = fact.id;
    button.setAttribute("aria-pressed", "true");
    button.setAttribute("aria-label", "Unfavorite");
    button.innerHTML = HEART_SVG;

    item.append(link, button);
    list.append(item);
  }
  empty.hidden = list.children.length > 0;

  doc.addEventListener("favoritechange", (event) => {
    if (event.detail.favorited) return;
    list.querySelector(`[data-fav="${CSS.escape(event.detail.id)}"]`)?.closest("li")?.remove();
    empty.hidden = list.children.length > 0;
  });
}

// Same path as Pages.kt's Icons.heart.
const HEART_SVG =
  '<svg viewBox="0 0 24 24" width="24" height="24" aria-hidden="true" focusable="false"><path class="heart" d="M12 21s-7.5-4.6-10-9.3C.4 8.4 2.3 4 6.3 4c2.3 0 3.9 1.3 5.7 3.4C13.8 5.3 15.4 4 17.7 4c4 0 5.9 4.4 4.3 7.7C19.5 16.4 12 21 12 21z"/></svg>';
