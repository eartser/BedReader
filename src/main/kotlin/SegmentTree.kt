import java.util.SortedMap
import java.util.SortedSet

class SegmentTree(lBounds: SortedMap<Int, SortedSet<Int>>) {

    /**
     * Maximum value of left bound in [lBounds]
     */
    private val maxL: Int = lBounds.lastKey()

    /**
     * [Node] stores its left bound [l], round bound [r], pointers to children nodes
     * [leftNode] and [rightNode] and all the pairs (end, start) [rBounds] associated with
     * start in range from [l] to [r] exclusively
     */
    private data class Node(
        val l: Int, val r: Int,
        val rBounds: SortedSet<Pair<Int, Int>> =
            sortedSetOf(compareBy({ it.first }, { it.second }))
    ) {
        var leftNode: Node? = null
        var rightNode: Node? = null

        fun setNodes(nodeL: Node, nodeR: Node) {
            leftNode = nodeL
            rightNode = nodeR
        }

        fun isLeaf(): Boolean = l + 1 == r

        /**
         * Updates current node's [rBounds] from its children [rBounds]
         */
        fun mergeBounds() {
            when {
                isLeaf() -> {
                }
                else -> rBounds.addAll(leftNode!!.rBounds.union(rightNode!!.rBounds))
            }
        }

        /**
         * Return all pairs (end, start) from [rBounds] that end's value is no more than [bound]
         */
        fun findRBounds(bound: Int): SortedSet<Pair<Int, Int>> =
            rBounds.firstOrNull()?.let {
                if (it.first >= bound + 1)
                    null
                else
                    rBounds.subSet(it, Pair(bound + 1, -1))
            } ?: sortedSetOf()
    }

    private val head = Node(0, maxL + 1)

    init {
        fun initNodes(node: Node = head, lBounds: SortedMap<Int, SortedSet<Int>>) {
            if (lBounds.subMap(node.l, node.r).isEmpty()) return
            when {
                node.isLeaf() -> lBounds[node.l]?.let { node.rBounds.addAll(it.map { r -> Pair(r, node.l) }) }
                else -> {
                    val nm = (node.l + node.r) / 2
                    node.setNodes(Node(node.l, nm), Node(nm, node.r))
                    node.leftNode?.let { initNodes(it, lBounds) }
                    node.rightNode?.let { initNodes(it, lBounds) }
                    node.mergeBounds()
                }
            }
        }
        initNodes(lBounds = lBounds)
    }

    /**
     * Finds all pairs (end, start) from rBounds of [node] that satisfy following:
     * start is no less than [l] and no more than [r],
     * end is no more than [rBound]
     */
    private fun findBounds(
        node: Node,
        l: Int,
        r: Int,
        rBound: Int
    ): Set<Pair<Int, Int>> {
        return when {
            node.l >= r || node.r < l || l >= r -> emptySet()
            node.l >= l && node.r <= r -> node.findRBounds(rBound)
            else -> {
                val boundsL = node.leftNode?.let { findBounds(it, l, r, rBound) } ?: emptySet()
                val boundsR = node.rightNode?.let { findBounds(it, l, r, rBound) } ?: emptySet()
                boundsL.union(boundsR)
            }
        }
    }

    /**
     * Finds all pairs (start, end) that satisfy following
     * [start, end) is inside the range from [l] to [r]
     */
    fun find(l: Int, r: Int): List<Pair<Int, Int>> = findBounds(head, l, r, r).map { (r, l) -> Pair(l, r) }

}