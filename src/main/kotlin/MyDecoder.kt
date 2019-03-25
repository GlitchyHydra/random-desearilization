import kotlinx.serialization.*
import kotlinx.serialization.internal.EnumDescriptor
import kotlinx.serialization.json.*
import kotlin.random.Random

class RandomDecoder : NamedValueDecoder() {

    private val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ \t\n"

    override fun beginStructure(desc: SerialDescriptor, vararg typeParams: KSerializer<*>): RandomDecoder {
        return when (desc.kind) {
            StructureKind.LIST -> this//JsonTreeListInput(json, cast(currentObject))
            StructureKind.MAP -> this//JsonTreeMapInput(json, cast(currentObject))
            else -> this//JsonTreeInput(json, cast(currentObject))
        }
    }

    final override fun decodeCollectionSize(desc: SerialDescriptor): Int = Random.nextInt(1,1000)
    /**
     * Get random values by decoding
     */
    final override fun decodeTaggedBoolean(tag: String): Boolean = Random.nextBoolean()
    final override fun decodeTaggedByte(tag: String): Byte = (Byte.MIN_VALUE..Byte.MAX_VALUE).random().toByte()
    final override fun decodeTaggedShort(tag: String): Short = (Short.MIN_VALUE..Short.MAX_VALUE).random().toShort()
    final override fun decodeTaggedInt(tag: String): Int = Random.nextInt()
    final override fun decodeTaggedLong(tag: String): Long = Random.nextLong()
    final override fun decodeTaggedFloat(tag: String): Float = Random.nextFloat()
    final override fun decodeTaggedDouble(tag: String): Double = Random.nextDouble()
    final override fun decodeTaggedChar(tag: String): Char = (Char.MIN_VALUE..Char.MAX_VALUE).random()
    final override fun decodeTaggedString(tag: String): String = (1..100).map { Random.nextInt(0, source.length) }
        .map { source[it] }.joinToString("")
    final override fun decodeTaggedEnum(tag: String, enumDescription: EnumDescriptor): Int =
        Random.nextInt(0,enumDescription.elementsCount - 1)


}
