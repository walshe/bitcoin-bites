package dev.emmett.bitcoinfacts.shared.model

import kotlin.test.Test
import kotlin.test.assertEquals

class VideoEmbedTest {

    @Test
    fun `x status link resolves to the platform embed URL`() {
        val resolved = VideoEmbed.resolve("https://x.com/LightningNewsX/status/2102782289239589230/video/1")
        assertEquals("https://platform.twitter.com/embed/Tweet.html?id=2102782289239589230", resolved)
    }

    @Test
    fun `legacy twitter-com status link also resolves`() {
        val resolved = VideoEmbed.resolve("https://twitter.com/someuser/status/12345")
        assertEquals("https://platform.twitter.com/embed/Tweet.html?id=12345", resolved)
    }

    @Test
    fun `youtube watch link resolves to the nocookie embed URL`() {
        val resolved = VideoEmbed.resolve("https://www.youtube.com/watch?v=dQw4w9WgXcQ")
        assertEquals("https://www.youtube-nocookie.com/embed/dQw4w9WgXcQ", resolved)
    }

    @Test
    fun `youtube short link resolves to the nocookie embed URL`() {
        val resolved = VideoEmbed.resolve("https://youtu.be/dQw4w9WgXcQ")
        assertEquals("https://www.youtube-nocookie.com/embed/dQw4w9WgXcQ", resolved)
    }

    @Test
    fun `unrecognized url is returned unchanged`() {
        val url = "https://example.com/some-video.mp4"
        assertEquals(url, VideoEmbed.resolve(url))
    }
}
