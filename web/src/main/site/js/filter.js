// Category filter on the All Bites page - union semantics, as in the app's
// FactRepository.filterByCategories: a fact shows if it has ANY selected category.

/** Empty [selected] means no filter, so everything matches. */
export function matchesFilter(categories, selected) {
  return selected.size === 0 || categories.some((category) => selected.has(category));
}

export function initFilter(doc) {
  const bar = doc.querySelector("[data-filter]");
  if (!bar) return;
  const items = [...doc.querySelectorAll(".fact-list li[data-categories]")];
  const empty = doc.querySelector("[data-filter-empty]");
  const selected = new Set();

  bar.hidden = false;
  bar.addEventListener("click", (event) => {
    const chip = event.target.closest("[data-category]");
    if (!chip) return;
    const category = chip.dataset.category;
    const on = !selected.has(category);
    if (on) selected.add(category);
    else selected.delete(category);
    chip.setAttribute("aria-pressed", String(on));

    let shown = 0;
    for (const item of items) {
      const visible = matchesFilter(item.dataset.categories.split(" "), selected);
      item.hidden = !visible;
      if (visible) shown++;
    }
    if (empty) empty.hidden = shown > 0;
  });
}
