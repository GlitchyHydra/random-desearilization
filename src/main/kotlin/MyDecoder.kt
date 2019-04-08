import kotlinx.serialization.*
import kotlinx.serialization.internal.EnumDescriptor
import java.util.Stack
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.random.nextLong

class RandomDecoder(
    private val stackOfAnnotationMap: Stack<Map<String, List<Annotation>>> = Stack()
) : NamedValueDecoder() {
    private val mapOfAnnotations: Map<String, List<Annotation>>
        get() = stackOfAnnotationMap.peek() //get current map of annotation
    private val probability: Int get() = Random.nextInt(1..100)

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
            stackOfAnnotationMap.push(mapOfAnnotations)
            return RandomDecoder(stackOfAnnotationMap)
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
            if (rangeChar == null) (Short.MIN_VALUE..Short.MAX_VALUE).random().toChar()
            else (rangeChar.min..rangeChar.max).random()
        }
    }

    private fun getByteWithProbability(rangeByte: RangeByte?): Byte = when (probability) {
        in 1..70 -> Random.nextInt(-1..1).toByte()
        else -> {
            if (rangeByte == null) (Short.MIN_VALUE..Short.MAX_VALUE).random().toByte()
            else (rangeByte.min..rangeByte.max).random().toByte()
        }
    }

    private fun getShortWithProbability(rangeShort: RangeShort?): Short = when (probability) {
        in 1..70 -> Random.nextInt(-1..1).toShort()
        else -> {
            if (rangeShort == null) (Short.MIN_VALUE..Short.MAX_VALUE).random().toShort()
            else (rangeShort.min..rangeShort.max).random().toShort()
        }
    }

    private fun getIntWithProbability(rangeInt: RangeInt?): Int = when (probability) {
        in 1..70 -> Random.nextInt(-1..1)
        else -> {
            if (rangeInt == null) Random.nextInt()
            else Random.nextInt(rangeInt.min, rangeInt.max)
        }
    }

    private fun getLongWithProbability(rangeLong: RangeLong?): Long = when (probability) {
        in 1..70 -> Random.nextLong(-1L..1L)
        else -> {
            if (rangeLong == null) Random.nextLong()
            else Random.nextLong(rangeLong.min, rangeLong.max)
        }
    }


    private fun getFloatWithProbability(rangeFloat: RangeFloat?): Float = when (probability) {
        in 1..30 -> Float.POSITIVE_INFINITY
        in 30..50 -> Float.NaN
        in 50..70 -> Float.NEGATIVE_INFINITY
        else -> {
            if (rangeFloat == null) Random.nextFloat()
            else Random.nextDouble(rangeFloat.min, rangeFloat.max).toFloat()
        }
    }

    private fun getDoubleWithProbability(rangeDouble: RangeDouble?): Double = when (probability) {
        in 1..30 -> Double.POSITIVE_INFINITY
        in 30..50 -> Double.NaN
        in 50..70 -> Double.NEGATIVE_INFINITY
        else -> {
            if (rangeDouble == null) Random.nextDouble()
            else Random.nextDouble(rangeDouble.min, rangeDouble.max)
        }
    }


    private fun getStringWithProbability(rangeString: RangeString?): String = when (probability) {
        in 1..60 -> List(500) { Random.nextInt(0, 100).toChar() }
            .joinToString(separator = "", postfix = "", prefix = "")
        else -> {
            if (rangeString == null) List(500)
            { Random.nextInt(0, 65535).toChar() }.joinToString(separator = "", postfix = "", prefix = "")
            else List(500) { Random.nextInt(rangeString.min, rangeString.max).toChar() }
                .joinToString(separator = "", postfix = "", prefix = "")
        }
    }

    override fun decodeTaggedNotNullMark(tag: String): Boolean = getBooleanWithProbability()

    override fun decodeCollectionSize(desc: SerialDescriptor): Int = Random.nextInt(0, 1000)

    /**
     * Get random values by decoding
     */
    override fun decodeTaggedBoolean(tag: String): Boolean = Random.nextBoolean()

    override fun decodeTaggedByte(tag: String): Byte =
        getByteWithProbability(mapOfAnnotations[tag]?.find { it is RangeByte } as RangeByte?)

    override fun decodeTaggedShort(tag: String): Short =
        getShortWithProbability(mapOfAnnotations[tag]?.find { it is RangeShort } as RangeShort?)

    override fun decodeTaggedInt(tag: String): Int =
        getIntWithProbability(mapOfAnnotations[tag]?.find { it is RangeInt } as RangeInt?)

    override fun decodeTaggedLong(tag: String): Long =
        getLongWithProbability(mapOfAnnotations[tag]?.find { it is RangeLong } as RangeLong?)

    override fun decodeTaggedFloat(tag: String): Float =
        getFloatWithProbability(mapOfAnnotations[tag]?.find { it is RangeFloat } as RangeFloat?)

    override fun decodeTaggedDouble(tag: String): Double =
        getDoubleWithProbability(mapOfAnnotations[tag]?.find { it is RangeDouble } as RangeDouble?)

    override fun decodeTaggedChar(tag: String): Char =
        getCharWithProbability(mapOfAnnotations[tag]?.find { it is RangeChar } as RangeChar?)

    override fun decodeTaggedString(tag: String): String =
        getStringWithProbability(mapOfAnnotations[tag]?.find { it is RangeString } as RangeString?)

    override fun decodeTaggedEnum(tag: String, enumDescription: EnumDescriptor): Int {
        val checkRange = (0..enumDescription.elementsCount)
        val rangeEnum = mapOfAnnotations[tag]?.find { it is RangeEnum } as RangeEnum?
        return if (rangeEnum == null || rangeEnum.min !in checkRange || rangeEnum.max !in checkRange)
            Random.nextInt(0, enumDescription.elementsCount)
        else Random.nextInt(rangeEnum.min, rangeEnum.max)
    }

    override fun composeName(parentName: String, childName: String): String {
        return if (parentName.isEmpty()) childName else parentName
    }

}
