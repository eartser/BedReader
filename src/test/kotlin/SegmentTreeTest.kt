import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.util.*

internal class SegmentTreeTest {

    private val myMap: SortedMap<Int, SortedSet<Int>> = sortedMapOf(
        Pair(1, sortedSetOf(2, 5)),
        Pair(2, sortedSetOf(5)),
        Pair(4, sortedSetOf(7, 8)),
        Pair(6, sortedSetOf(7))
    )
    private val p: List<Pair<Int, Int>> = listOf(
        Pair(1, 2),
        Pair(1, 5),
        Pair(2, 5),
        Pair(4, 7),
        Pair(4, 8),
        Pair(6, 7)
    )
    private val tree = SegmentTree(myMap)

    @Test
    fun `invalid request`() {
        assert(tree.find(0, 0) == listOf<Pair<Int, Int>>())
        assert(tree.find(4, 2) == listOf<Pair<Int, Int>>())
    }

    @Test
    fun `all segments`() {
        assert(tree.find(0, 8).toSet() == p.toSet())
    }

    @Test
    fun `simple request`() {
        assert(tree.find(0, 1).toSet() == listOf<Pair<Int, Int>>().toSet())
        assert(tree.find(0, 2).toSet() == listOf(p[0]).toSet())
        assert(tree.find(1, 3).toSet() == listOf(p[0]).toSet())
        assert(tree.find(0, 6).toSet() == listOf(p[0], p[1], p[2]).toSet())
        assert(tree.find(3, 7).toSet() == listOf(p[3], p[5]).toSet())
        assert(tree.find(6, 7).toSet() == listOf(p[5]).toSet())
    }
}