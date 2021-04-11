import java.nio.file.Path
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.toSortedSet

data class BedEntry(val chromosome: String, val start: Int, val end: Int, val other: List<Any>)

interface BedIndex {
    /**
     * [indices] of (chromosome, start, end) stores a line number
     * associated with corresponding [BedEntry]
     */
    val indices: HashMap<Triple<String, Int, Int>, Int>

    /**
     * Stores a [SegmentTree] associated with chromosome
     */
    val bedIndex: HashMap<String, SegmentTree>

    /**
     * Initializes [indices] and [bedIndex] with values from [entries]
     */
    fun init(entries: List<BedEntry>) {
        entries.forEachIndexed { ind, it -> indices[Triple(it.chromosome, it.start, it.end)] = ind }
        entries.groupBy { it.chromosome }.map { (k, it) ->
            bedIndex[k] = SegmentTree(it.groupBy { e -> e.start }.mapValues { (_, list) ->
                list.map { it.end }.toSortedSet()
            })
        }
    }

    /**
     * Finds all indices of [BedEntry] from [indices] that satisfy following:
     * bedEntry's chromosome equals [chromosome],
     * bedEntry's start equals [start],
     * bedEntry's end equals [end]
     */
    fun find(chromosome: String, start: Int, end: Int): List<Int> =
        bedIndex[chromosome]?.find(start, end)?.map {
                (l, r) -> indices[Triple(chromosome, l, r)]!!
        } ?: emptyList()

    fun write(): String = TODO("Not yet implemented")

    fun read(lines: List<String>): Unit = TODO("Not yet implemented")
}

interface BedReader {

    /**
     * Converts [line] to [BedEntry] instance
     */
    fun toBedEntry(line: String): BedEntry {
        val args: List<String> = line.split(" ")
        return BedEntry(args[0], args[1].toInt(), args[2].toInt(), args.drop(3))
    }

    /**
     * Loads all [BedEntry] instances from file [bedPath]
     */
    fun getEntries(bedPath: Path): List<BedEntry> =
        bedPath.toFile().readLines().map { toBedEntry(it) }

    /**
     * Loads [BedEntry] instance on line [ind] from file [bedPath]
     */
    fun getEntry(bedPath: Path, ind: Int): BedEntry {
        return toBedEntry(bedPath.toFile().readLines().elementAt(ind))
    }

    /**
     * Creates index for [bedPath] and saves it to [indexPath]
     */
    fun createIndex(bedPath: Path, indexPath: Path) {
        val index = object : BedIndex {
            override val bedIndex: HashMap<String, SegmentTree>
                get() = emptyMap<String, SegmentTree>() as HashMap<String, SegmentTree>
            override val indices: HashMap<Triple<String, Int, Int>, Int>
                get() = emptyMap<Triple<String, Int, Int>, Int>() as HashMap<Triple<String, Int, Int>, Int>
        }
        index.init(getEntries(bedPath))
        indexPath.toFile().writeText(index.write())
    }

    /**
     * Loads [BedIndex] instance from file [indexPath]
     */
    fun loadIndex(indexPath: Path): BedIndex {
        val index = object : BedIndex {
            override val bedIndex: HashMap<String, SegmentTree>
                get() = emptyMap<String, SegmentTree>() as HashMap<String, SegmentTree>
            override val indices: HashMap<Triple<String, Int, Int>, Int>
                get() = emptyMap<Triple<String, Int, Int>, Int>() as HashMap<Triple<String, Int, Int>, Int>
        }
        index.read(indexPath.toFile().readLines())
        return index
    }

    /**
     * Loads list of [BedEntry] from file [bedPath] using [index].
     * All the loaded entries should be located on the given [chromosome],
     * and be inside the range from [start] inclusive to [end] exclusive.
     * E.g. entry [1, 2) is inside [0, 2), but not inside [0, 1).
     */
    fun findWithIndex(
        index: BedIndex, bedPath: Path,
        chromosome: String, start: Int, end: Int
    ): List<BedEntry> {
        return index.find(chromosome, start, end).map { ind -> getEntry(bedPath, ind) }
    }

}