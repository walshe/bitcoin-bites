// Port of shared's StreakCalculator (design.md decision 5) - keep the two in
// step; streak.test.js runs the same cases as StreakCalculatorTest.

/** Days since 1970-01-01 for the given date's *local* calendar day. */
export function epochDay(date = new Date()) {
  return Math.floor(Date.UTC(date.getFullYear(), date.getMonth(), date.getDate()) / 86_400_000);
}

/**
 * Length of the consecutive run of days ending at the most recent recorded day,
 * but only if that day is today or yesterday - any older gap means a full day
 * passed with no visit, so the streak is broken (0).
 */
export function currentStreak(openEpochDays, todayEpochDay) {
  if (openEpochDays.length === 0) return 0;
  const days = new Set(openEpochDays);
  const mostRecent = Math.max(...days);
  if (todayEpochDay - mostRecent > 1) return 0;

  let streak = 0;
  let day = mostRecent;
  while (days.has(day)) {
    streak++;
    day--;
  }
  return streak;
}
