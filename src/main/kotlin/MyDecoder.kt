import kotlinx.serialization.*
import kotlinx.serialization.internal.EnumDescriptor
import kotlinx.serialization.json.*
import kotlin.random.Random

class MyDecoder : NamedValueDecoder() {

    private var position = 0

   // private fun currentObject() = currentTagOrNull?.let { currentElement(it) } ?: obj

    override fun beginStructure(desc: SerialDescriptor, vararg typeParams: KSerializer<*>): MyDecoder {
        return when (desc.kind) {
            StructureKind.LIST -> this//JsonTreeListInput(json, cast(currentObject))
            StructureKind.MAP -> this//JsonTreeMapInput(json, cast(currentObject))
            else -> this//JsonTreeInput(json, cast(currentObject))
        }
    }

    override fun decodeTaggedBoolean(tag: String): Boolean = Random.nextBoolean()
    override fun decodeTaggedByte(tag: String): Byte = (Byte.MIN_VALUE..Byte.MAX_VALUE).random().toByte()
    override fun decodeTaggedShort(tag: String): Short = (Short.MIN_VALUE..Short.MAX_VALUE).random().toShort()
    override fun decodeTaggedInt(tag: String): Int = Random.nextInt()
    override fun decodeTaggedLong(tag: String): Long = Random.nextLong()
    override fun decodeTaggedFloat(tag: String): Float = Random.nextFloat()
    override fun decodeTaggedDouble(tag: String): Double = Random.nextDouble()
    override fun decodeTaggedChar(tag: String): Char = (Char.MIN_VALUE..Char.MAX_VALUE).random()
    override fun decodeTaggedString(tag: String): String = decodeTaggedValue(tag) as String
    override fun decodeTaggedEnum(tag: String, enumDescription: EnumDescriptor): Int = decodeTaggedValue(tag) as Int

    //final fun <T : Any?> decodeSerializableElementRandomly(desc: SerialDescriptor, index: Int, deserializer: DeserializationStrategy<T>): T =
       // desc.getTag(index)
}

/**
 * This is not using
 */
private sealed class MyAbstractJsonTreeInput(override val json: Json, open val obj: JsonElement)
    : NamedValueDecoder(), JsonInput {

    init {
        context = json.context
    }

    private fun currentObject() = currentTagOrNull?.let { currentElement(it) } ?: obj

    override fun decodeJson(): JsonElement = currentObject()

    override val updateMode: UpdateMode
        get() = json.updateMode

    override fun composeName(parentName: String, childName: String): String = childName

    override fun beginStructure(desc: SerialDescriptor, vararg typeParams: KSerializer<*>): CompositeDecoder {
        val currentObject = currentObject()
        return when (desc.kind) {
            StructureKind.LIST -> JsonTreeListInput(json, cast(currentObject))
            StructureKind.MAP -> JsonTreeMapInput(json, cast(currentObject))
            else -> JsonTreeInput(json, cast(currentObject))
        }
    }

    protected open fun getValue(tag: String): JsonPrimitive {
        val currentElement = currentElement(tag)
        return currentElement as? JsonPrimitive ?: throw JsonElementTypeMismatchException("$currentElement at $tag", "JsonPrimitive")
    }

    protected abstract fun currentElement(tag: String): JsonElement

    override fun decodeTaggedChar(tag: String): Char {
        val o = getValue(tag)
        return if (o.content.length == 1) (Char.MIN_VALUE..Char.MAX_VALUE).random()
        else throw SerializationException("$o can't be represented as Char")
    }

    override fun decodeTaggedEnum(tag: String, enumDescription: EnumDescriptor): Int =
        enumDescription.getElementIndex(getValue(tag).content)

    override fun decodeTaggedNull(tag: String): Nothing? = null

    override fun decodeTaggedNotNullMark(tag: String): Boolean = currentElement(tag) !== JsonNull

    override fun decodeTaggedUnit(tag: String) {
        return
    }

    override fun decodeTaggedBoolean(tag: String): Boolean = Random.nextBoolean()
    override fun decodeTaggedByte(tag: String): Byte = (Byte.MIN_VALUE..Byte.MAX_VALUE).random().toByte()
    override fun decodeTaggedShort(tag: String) = (Short.MIN_VALUE..Short.MAX_VALUE).random().toShort()
    override fun decodeTaggedInt(tag: String): Int {
        println(tag)
        return Random.nextInt()
    }
    override fun decodeTaggedLong(tag: String) = Random.nextLong()
    override fun decodeTaggedFloat(tag: String) = Random.nextFloat()
    override fun decodeTaggedDouble(tag: String) = Random.nextDouble()
    override fun decodeTaggedString(tag: String) = getValue(tag).content
}

private class JsonPrimitiveInput(json: Json, override val obj: JsonPrimitive) : MyAbstractJsonTreeInput(json, obj) {

    companion object {
        const val primitive = "primitive"
    }

    init {
        pushTag(primitive)
    }

    override fun currentElement(tag: String): JsonElement {
        require(tag == primitive)
        return obj
    }
}

private open class JsonTreeInput(json: Json, override val obj: JsonObject) : MyAbstractJsonTreeInput(json, obj) {

    private var position = 0

    override fun decodeElementIndex(desc: SerialDescriptor): Int {
        while (position < desc.elementsCount) {
            val name = desc.getTag(position++)
            if (name in obj) {
                return position - 1
            }
        }
        return CompositeDecoder.READ_DONE
    }

    override fun currentElement(tag: String): JsonElement = obj.getValue(tag)

    override fun endStructure(desc: SerialDescriptor) {
        // This one can be optimized
        //if (json.strictMode) {
            val names = HashSet<String>(desc.elementsCount)
            for (i in 0 until desc.elementsCount) {
                names += desc.getElementName(i)
            }

            for (key in obj.keys) {
                if (key !in names) throw JsonUnknownKeyException("Encountered an unknown key '$key'")
            }
        //}
    }
}

private class JsonTreeMapInput(json: Json, override val obj: JsonObject) : JsonTreeInput(json, obj) {
    private val keys = obj.keys.toList()
    private val size: Int = keys.size * 2
    private var position = -1

    override fun elementName(desc: SerialDescriptor, index: Int): String {
        val i = index / 2
        return keys[i]
    }

    override fun decodeElementIndex(desc: SerialDescriptor): Int {
        while (position < size - 1) {
            position++
            return position
        }
        return CompositeDecoder.READ_DONE
    }

    override fun currentElement(tag: String): JsonElement {
        return if (position % 2 == 0) JsonLiteral(tag) else obj[tag]
    }

    override fun endStructure(desc: SerialDescriptor) {
        // do nothing
    }
}

private class JsonTreeListInput(json: Json, override val obj: JsonArray) : MyAbstractJsonTreeInput(json, obj) {
    private val size = obj.content.size
    private var currentIndex = -1

    override fun elementName(desc: SerialDescriptor, index: Int): String = (index).toString()

    override fun currentElement(tag: String): JsonElement {
        return obj[tag.toInt()]
    }

    override fun decodeElementIndex(desc: SerialDescriptor): Int {
        while (currentIndex < size - 1) {
            currentIndex++
            return currentIndex
        }
        return CompositeDecoder.READ_DONE
    }
}

@Suppress("USELESS_CAST") // Contracts does not work in K/N
internal inline fun <reified T : JsonElement> cast(obj: JsonElement): T {
    check(obj is T) { "Expected ${T::class} but found ${obj::class}" }
    return obj as T
}