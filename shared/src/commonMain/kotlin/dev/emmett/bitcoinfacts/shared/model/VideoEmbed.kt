package dev.emmett.bitcoinfacts.shared.model

/**
 * Fact authors write a plain link to wherever the video actually lives (an X/Twitter
 * status, a YouTube watch/share link). This resolves that into the URL an embeddable
 * player iframe should load - kept as pure string logic here (no Android WebView
 * dependency) so it's unit-testable on any target.
 */
object VideoEmbed {
    private val TWITTER_STATUS = Regex("""(?:twitter\.com|x\.com)/[^/]+/status/(\d+)""")
    private val YOUTUBE_WATCH = Regex("""youtube\.com/watch\?v=([\w-]+)""")
    private val YOUTUBE_SHORT = Regex("""youtu\.be/([\w-]+)""")

    /**
     * Returns an embeddable iframe-player URL for [rawUrl], or [rawUrl] unchanged if
     * it doesn't match a known video source - the WebView still loads it directly in
     * that case, it just won't be the chromeless embed form.
     */
    fun resolve(rawUrl: String): String {
        TWITTER_STATUS.find(rawUrl)?.let { match ->
            return "https://platform.twitter.com/embed/Tweet.html?id=${match.groupValues[1]}"
        }
        YOUTUBE_WATCH.find(rawUrl)?.let { match ->
            return "https://www.youtube-nocookie.com/embed/${match.groupValues[1]}"
        }
        YOUTUBE_SHORT.find(rawUrl)?.let { match ->
            return "https://www.youtube-nocookie.com/embed/${match.groupValues[1]}"
        }
        return rawUrl
    }
}
