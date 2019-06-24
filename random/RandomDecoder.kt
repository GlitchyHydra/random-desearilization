import kotlinx.serialization.*
import kotlinx.serialization.internal.EnumDescriptor
import java.lang.StringBuilder
import kotlin.random.Random

class RandomDecoder private constructor(
    private val stackOfAnnotationMap: MutableSet<Map<String, List<Annotation>>> = mutableSetOf()
) : NamedValueDecoder() {
    private val mapOfAnnotations: Map<String, List<Annotation>>?
        get() = stackOfAnnotationMap.lastOrNull() //get current map of annotation
    private val probability: Int get() = Random.nextInt(1, 101)
    private val size: Int get() = Random.nextInt(0, 1001)

    constructor() : this(mutableSetOf<Map<String, List<Annotation>>>())

    /**
     * create map with [property, annotation?] entities
     */
    private fun createMapOfAnnotations(desc: SerialDescriptor): Map<String, List<Annotation>> {
        val mapOfAnnotations = mutableMapOf<String, List<Annotation>>()
        for (i in 0 until desc.elementsCount) {
            val elementName = desc.getElementName(i)
            val annotationList = desc.getElementAnnotations(i)
            mapOfAnnotations[elementName] = annotationList
        }
        return mapOfAnnotations
    }

    /**
     * begin structure implementation
     */
    override fun beginStructure(desc: SerialDescriptor, vararg typeParams: KSerializer<*>): RandomDecoder {
        if (desc.kind == StructureKind.CLASS) {
            val mapOfAnnotations = createMapOfAnnotations(desc)
            stackOfAnnotationMap.add(mapOfAnnotations)
            return RandomDecoder(stackOfAnnotationMap)
        }
        return this
    }

    override fun endStructure(desc: SerialDescriptor) {
        if (desc.kind == StructureKind.CLASS) stackOfAnnotationMap.drop(stackOfAnnotationMap.size - 1)
        super.endStructure(desc)
    }

    /**
     * Generate values with probability
     * where special cases have more probability then common
     */
    private fun getBooleanWithProbability(): Boolean = when (probability) {
        in 1..70 -> true
        else -> false
    }

    private fun getCharWithProbability(rangeChar: RangeChar?): Char = when {
        probability in 1..70 -> Random.nextInt(0, 100).toChar()
        rangeChar == null -> Random.nextInt(0, 65535).toChar()
        else -> (rangeChar.min..rangeChar.max).random()
    }

    private fun getByteWithProbability(rangeByte: RangeByte?): Byte = when {
        probability in 1..70 -> Random.nextInt(-1, 2).toByte()
        rangeByte == null -> (Short.MIN_VALUE..Short.MAX_VALUE).random().toByte()
        else -> (rangeByte.min..rangeByte.max).random().toByte()
    }

    private fun getShortWithProbability(rangeShort: RangeShort?): Short = when {
        probability in 1..70 -> Random.nextInt(-1, 2).toShort()
        rangeShort == null -> (Short.MIN_VALUE..Short.MAX_VALUE).random().toShort()
        else -> (rangeShort.min..rangeShort.max).random().toShort()
    }


    private fun getIntWithProbability(rangeInt: RangeInt?): Int = when {
        probability in 1..70 -> Random.nextInt(-1, 2)
        rangeInt == null -> Random.nextInt()
        else -> Random.nextInt(rangeInt.min, rangeInt.max)
    }


    private fun getLongWithProbability(rangeLong: RangeLong?): Long = when {
        probability in 1..70 -> Random.nextLong(-1L, 2L)
        rangeLong == null -> Random.nextLong()
        else -> Random.nextLong(rangeLong.min, rangeLong.max)
    }


    private fun getFloatWithProbability(rangeFloat: RangeFloat?): Float = when (probability) {
        in 1..20 -> Float.POSITIVE_INFINITY
        in 21..40 -> Float.NaN
        in 41..60 -> Float.NEGATIVE_INFINITY
        else -> {
            if (rangeFloat == null) Random.nextFloat()
            else Random.nextDouble(rangeFloat.min, rangeFloat.max).toFloat()
        }
    }

    private fun getDoubleWithProbability(rangeDouble: RangeDouble?): Double = when (probability) {
        in 1..20 -> Double.POSITIVE_INFINITY
        in 21..40 -> Double.NaN
        in 41..60 -> Double.NEGATIVE_INFINITY
        else -> {
            if (rangeDouble == null) Random.nextDouble()
            else Random.nextDouble(rangeDouble.min, rangeDouble.max)
        }
    }

    private fun stringRandomly(from: Int, to: Int): String {
        val line = StringBuilder()
        (0..size).map { line.append(Random.nextInt(from, to).toChar()) }
        return line.toString()
    }

    private fun getStringWithProbability(rangeString: RangeString?): String =
        when {
            probability in 1..60 -> stringRandomly(0, 100)
            rangeString == null -> stringRandomly(0, 65535)
            else -> stringRandomly(rangeString.min, rangeString.max)
        }


    override fun decodeTaggedNotNullMark(tag: String): Boolean = getBooleanWithProbability()

    override fun decodeCollectionSize(desc: SerialDescriptor): Int = size

    /**
     * Get random values by decoding
     */
    override fun decodeTaggedBoolean(tag: String): Boolean = Random.nextBoolean()

    override fun decodeTaggedByte(tag: String): Byte =
        getByteWithProbability(mapOfAnnotations?.get(tag)?.find { it is RangeByte } as RangeByte?)

    override fun decodeTaggedShort(tag: String): Short =
        getShortWithProbability(mapOfAnnotations?.get(tag)?.find { it is RangeShort } as RangeShort?)

    override fun decodeTaggedInt(tag: String): Int =
        getIntWithProbability(mapOfAnnotations?.get(tag)?.find { it is RangeInt } as RangeInt?)

    override fun decodeTaggedLong(tag: String): Long =
        getLongWithProbability(mapOfAnnotations?.get(tag)?.find { it is RangeLong } as RangeLong?)

    override fun decodeTaggedFloat(tag: String): Float =
        getFloatWithProbability(mapOfAnnotations?.get(tag)?.find { it is RangeFloat } as RangeFloat?)

    override fun decodeTaggedDouble(tag: String): Double =
        getDoubleWithProbability(mapOfAnnotations?.get(tag)?.find { it is RangeDouble } as RangeDouble?)

    override fun decodeTaggedChar(tag: String): Char =
        getCharWithProbability(mapOfAnnotations?.get(tag)?.find { it is RangeChar } as RangeChar?)

    override fun decodeTaggedString(tag: String): String =
        getStringWithProbability(mapOfAnnotations?.get(tag)?.find { it is RangeString } as RangeString?)

    override fun decodeTaggedEnum(tag: String, enumDescription: EnumDescriptor): Int {
        val checkRange = (0..enumDescription.elementsCount)
        val rangeEnum = mapOfAnnotations?.get(tag)?.find { it is RangeEnum } as RangeEnum?
        return if (rangeEnum == null || rangeEnum.min !in checkRange || rangeEnum.max !in checkRange)
            Random.nextInt(0, enumDescription.elementsCount)
        else Random.nextInt(rangeEnum.min, rangeEnum.max)
    }

    override fun composeName(parentName: String, childName: String): String {
        return if (parentName.isEmpty()) childName else parentName
    }

}
