import kotlinx.serialization.*
import kotlinx.serialization.internal.EnumDescriptor
import kotlinx.serialization.internal.SerialClassDescImpl
import java.util.*
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.random.nextLong

open class RandomDecoder : NamedValueDecoder() {
    private val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ \t\n"
    private val stackOfDescriptors = Stack<SerialDescriptor>()

    override fun beginStructure(desc: SerialDescriptor, vararg typeParams: KSerializer<*>): RandomDecoder {
        if (desc.kind == StructureKind.CLASS) stackOfDescriptors.push(desc)
        return this
        /*return when (desc.kind) {
            StructureKind.LIST -> JsonTreeListInput(json, cast(currentObject))
            StructureKind.MAP -> JsonTreeMapInput(json, cast(currentObject))
            else -> JsonTreeInput(json, cast(currentObject))
        }*/
    }

    override fun endStructure(desc: SerialDescriptor) {
       // val k = getSerialAnnotation(desc
        super.endStructure(desc)
    }

    final private fun getBooleanWithProbability(probability: Int): Boolean = when(probability) {
        in 1..80 -> true
        else -> false
    }

    final private fun getCharWithProbability(probability: Int): Char = when(probability) {
        in 1..80 -> Random.nextInt(0..100).toChar()
        else -> (Char.MIN_VALUE..Char.MAX_VALUE).random()
    }

    final private fun getByteWithProbability(probability: Int) : Byte = when(probability) {
        in 1..80 -> Random.nextInt(-1..1).toByte()
        else -> (Byte.MIN_VALUE..Byte.MAX_VALUE).random().toByte()
    }

    final private fun getShortWithProbability(probability: Int) : Short = when(probability) {
        in 1..80 -> Random.nextInt(-1..1).toShort()
        else -> (Short.MIN_VALUE..Short.MAX_VALUE).random().toShort()
    }

    final private fun getIntWithProbability(probability: Int) : Int = when(probability) {
        in 1..80 -> Random.nextInt(-1..1)
        else -> Random.nextInt()
    }

    final private fun getLongWithProbability(probability: Int) : Long = when(probability) {
        in 1..80 -> Random.nextLong(-1L..1L)
        else -> Random.nextLong()
    }

    final private fun getCurrentRange(tag: String): Annotation? {
        val index = stackOfDescriptors.getElementIndex(tag)
        return stackOfDescriptors.getPropertyAnnotation(index)
    }
    private fun Stack<SerialDescriptor>.getElementIndex(tag: String): Int = this.peek().getElementIndex(tag)
    private fun Stack<SerialDescriptor>.getPropertyAnnotation(index: Int): Annotation? =
        this.peek().getElementAnnotations(index).firstOrNull()


    final override fun decodeTaggedNotNullMark(tag: String): Boolean = getBooleanWithProbability(Random.nextInt(1,100))
    final override fun decodeCollectionSize(desc: SerialDescriptor): Int = Random.nextInt(0,1000)
    /**
     * Get random values by decoding
     */
    final override fun decodeTaggedBoolean(tag: String): Boolean = Random.nextBoolean()
    final override fun decodeTaggedByte(tag: String): Byte = when(getCurrentRange(tag)) {
        null -> getByteWithProbability(Random.nextInt(1, 100))
        else -> getByteWithProbability(Random.nextInt(1, 100))
    }
    final override fun decodeTaggedShort(tag: String): Short =
        getShortWithProbability(Random.nextInt(1, 100))
    final override fun decodeTaggedInt(tag: String): Int = getIntWithProbability(Random.nextInt(1, 100))
    final override fun decodeTaggedLong(tag: String): Long = getLongWithProbability(Random.nextInt(1, 100))
    final override fun decodeTaggedFloat(tag: String): Float = Random.nextFloat()
    final override fun decodeTaggedDouble(tag: String): Double = Random.nextDouble()
    final override fun decodeTaggedChar(tag: String): Char = getCharWithProbability(Random.nextInt(1,100))
    final override fun decodeTaggedString(tag: String): String = (1..100).map { Random.nextInt(0, source.length) }
        .map { source[it] }.joinToString("")
    final override fun decodeTaggedEnum(tag: String, enumDescription: EnumDescriptor): Int =
        Random.nextInt(0,enumDescription.elementsCount - 1)

}
