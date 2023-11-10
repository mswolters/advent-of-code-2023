import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import kotlin.test.assertContentEquals

class SplitTest {

    @Test
    fun testSplit() {
        val expected = listOf(listOf("a"), listOf("c"))
        val actual = listOf("a", "b", "c").split { it == "b"}
        assertContentEquals(expected, actual)
    }

    @Test
    fun testSplitOn() {
        val expected = listOf(listOf("a"), listOf("c"))
        val actual = listOf("a", "", "c").split("")
        assertContentEquals(expected, actual)
    }

    @Test
    fun testSplitOnAny() {
        val expected = listOf(listOf(1),listOf(2),listOf(2, 3))
        val actual = listOf(1, 4, 2, 5, 2, 3).split(listOf(4, 5))
        assertContentEquals(expected, actual)
    }
}