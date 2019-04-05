package com.hedvig.android.owldroid.util

import junit.framework.Assert.assertEquals
import org.junit.Test

class TextkeysTest {
    @Test
    fun `should replace placeholders with a regular text`() {
        val text = "{KEY1} bar {KEY2}"
        val replacement = "KEY1" to "Foo"
        val replacement2 ="KEY2" to "baz"

        val expected = "Foo bar baz"
        val actual = interpolateTextKey(text, replacement, replacement2)

        assertEquals(expected, actual)
    }

    @Test
    fun `should replace placeholders correctly without space next to replacement field`() {
        val text = "{KEY1}bar{KEY2}"
        val replacement = "KEY1" to "Foo"
        val replacement2 = "KEY2" to "baz"

        val expected = "Foobarbaz"
        val actual = interpolateTextKey(text, replacement, replacement2)

        assertEquals(expected, actual)
    }

    @Test
    fun `should ignore non-replaced keys`() {
        val text = "{KEY3} bar"
        val replacement = "KEY1" to "Foo"

        val actual = interpolateTextKey(text, replacement)

        assertEquals(text, actual)
    }
}
