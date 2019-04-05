import kotlinx.serialization.*
import kotlinx.serialization.internal.EnumDescriptor
import java.util.Stack
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.random.nextLong

open class RandomDecoder : NamedValueDecoder() {
    private val stackOfAnnotationMap: Stack<Map<String, Annotation?>> = Stack()
    private val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ \t\n"
    private val mapOfAnnotations: Map<String, Annotation?>
        get() = stackOfAnnotationMap.peek() //get current map of annotation
    private val probability: Int get() = Random.nextInt(1..100)
    private val mapTags = arrayListOf<String>()
    private var keyOrValue: Boolean = false

    /**
     * create map with [property, annotation?] entities
     */
    private fun createMapOfAnnotations(desc: SerialDescriptor): Map<String, Annotation?> {
        val mapOfAnnotations = mutableMapOf<String, Annotation?>()
        for (i in 0 until desc.elementsCount) {
            val elementName = desc.getElementName(i)
            val annotationList = desc.getElementAnnotations(i)
            if (annotationList.size == 2) {
                mapTags.add(elementName)
                mapOfAnnotations["$elementName.k"] = annotationList[0]
                mapOfAnnotations["$elementName.v"] = annotationList[1]
            } else mapOfAnnotations[elementName] = annotationList.firstOrNull()
        }
        return mapOfAnnotations
    }

    /**
     * begin structure implementation
     */
    override fun beginStructure(desc: SerialDescriptor, vararg typeParams: KSerializer<*>): RandomDecoder {
        if (desc.kind == StructureKind.CLASS) {
            val mapOfAnnotations = createMapOfAnnotations(desc)
            stackOfAnnotationMap.push(mapOfAnnotations)
        }
        return this
    }

    override fun endStructure(desc: SerialDescriptor) {
        if (desc.kind == StructureKind.CLASS) stackOfAnnotationMap.pop()
        super.endStructure(desc)
    }

    /**
     * For generate values with probability
     * where special cases have more probability then common
     */
    private fun getBooleanWithProbability(): Boolean = when (probability) {
        in 1..70 -> true
        else -> false
    }

    private fun getCharWithProbability(rangeChar: RangeChar?): Char = when (probability) {
        in 1..70 -> Random.nextInt(0..100).toChar()
        else -> {
            if (rangeChar != null) (rangeChar.min..rangeChar.max).random()
            else (Short.MIN_VALUE..Short.MAX_VALUE).random().toChar()
        }
    }

    private fun getByteWithProbability(rangeByte: RangeByte?): Byte = when (probability) {
        in 1..70 -> Random.nextInt(-1..1).toByte()
        else -> {
            if (rangeByte != null) (rangeByte.min..rangeByte.max).random().toByte()
            else (Short.MIN_VALUE..Short.MAX_VALUE).random().toByte()
        }
    }

    private fun getShortWithProbability(rangeShort: RangeShort?): Short = when (probability) {
        in 1..70 -> Random.nextInt(-1..1).toShort()
        else -> {
            if (rangeShort != null) (rangeShort.min..rangeShort.max).random().toShort()
            else (Short.MIN_VALUE..Short.MAX_VALUE).random().toShort()
        }
    }

    private fun getIntWithProbability(rangeInt: RangeInt?): Int = when (probability) {
        in 1..70 -> Random.nextInt(-1..1)
        else -> {
            if (rangeInt != null) Random.nextInt(rangeInt.min, rangeInt.max)
            else Random.nextInt()
        }
    }

    private fun getLongWithProbability(rangeLong: RangeLong?): Long = when (probability) {
        in 1..70 -> Random.nextLong(-1L..1L)
        else -> {
            if (rangeLong != null) Random.nextLong(rangeLong.min, rangeLong.max)
            else Random.nextLong()
        }
    }


    private fun getFloatWithProbability(rangeFloat: RangeFloat?): Float = when (probability) {
        in 1..30 -> Float.POSITIVE_INFINITY
        in 30..50 -> Float.NaN
        in 50..70 -> Float.NEGATIVE_INFINITY
        else -> {
            if (rangeFloat != null) Random.nextFloat()
            else Random.nextFloat()
        }
    }

    private fun getDoubleWithProbability(rangeDouble: RangeDouble?): Double = when (probability) {
        in 1..30 -> Double.POSITIVE_INFINITY
        in 30..50 -> Double.NaN
        in 50..70 -> Double.NEGATIVE_INFINITY
        else -> {
            if (rangeDouble != null) Random.nextDouble(rangeDouble.min, rangeDouble.max)
            else Random.nextDouble()
        }
    }

    override fun decodeTaggedNotNullMark(tag: String): Boolean = getBooleanWithProbability()

    override fun decodeCollectionSize(desc: SerialDescriptor): Int = Random.nextInt(0, 1000)

    /**
     * Get random values by decoding
     */
    override fun decodeTaggedBoolean(tag: String): Boolean = Random.nextBoolean()

    override fun decodeTaggedByte(tag: String): Byte = getByteWithProbability(mapOfAnnotations[tag] as RangeByte?)

    override fun decodeTaggedShort(tag: String): Short = getShortWithProbability(mapOfAnnotations[tag] as RangeShort?)

    override fun decodeTaggedInt(tag: String): Int = getIntWithProbability(mapOfAnnotations[tag] as RangeInt?)

    override fun decodeTaggedLong(tag: String): Long = getLongWithProbability(mapOfAnnotations[tag] as RangeLong?)

    override fun decodeTaggedFloat(tag: String): Float =
        getFloatWithProbability(mapOfAnnotations[tag] as RangeFloat?)

    override fun decodeTaggedDouble(tag: String): Double =
        getDoubleWithProbability(mapOfAnnotations[tag] as RangeDouble?)

    override fun decodeTaggedChar(tag: String): Char = getCharWithProbability(mapOfAnnotations[tag] as RangeChar?)

    override fun decodeTaggedString(tag: String): String = (0..500).map { Random.nextInt(0, source.length) }
        .map { source[it] }.joinToString("")

    override fun decodeTaggedEnum(tag: String, enumDescription: EnumDescriptor): Int =
        Random.nextInt(0, enumDescription.elementsCount)

    override fun composeName(parentName: String, childName: String): String {
        return if (parentName.isEmpty()) childName else {
            if (mapTags.contains(parentName)) getTagWithModifier(parentName)
            else parentName
        }
    }

    private fun getTagWithModifier(tag: String): String = when {
        keyOrValue -> {
            keyOrValue = false
            "$tag.v"
        }
        else -> {
            keyOrValue = true
            "$tag.k"
        }
    }
}
