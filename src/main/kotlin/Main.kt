import java.util.*

fun main() {
    val reader = object : BedReader {}
    val index = reader.emptyIndex()

    assert(index.bedIndex == hashMapOf<String, SegmentTree>())
    assert(index.indices == hashMapOf<Triple<String, Int, Int>, Int>())
}