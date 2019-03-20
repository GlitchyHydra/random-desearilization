import kotlinx.serialization.*
import kotlinx.serialization.internal.*
import kotlinx.serialization.json.Json
import java.lang.UnsupportedOperationException
import java.util.*
import kotlin.random.Random
import kotlin.reflect.KClass

sealed class AbstractCollectionSerializer<TElement, TCollection, TBuilder>: KSerializer<TCollection> {
    abstract fun TCollection.objSize(): Int
    abstract fun TCollection.objIterator(): Iterator<TElement>
    abstract fun builder(): TBuilder
    abstract fun TBuilder.builderSize(): Int
    abstract fun TBuilder.toResult(): TCollection
    abstract fun TCollection.toBuilder(): TBuilder
    abstract fun TBuilder.checkCapacity(size: Int)

    abstract val typeParams: Array<KSerializer<*>>

    abstract override fun serialize(encoder: Encoder, obj: TCollection)

    final override fun patch(decoder: Decoder, old: TCollection): TCollection {
        val builder = old.toBuilder()
        val startIndex = builder.builderSize()
        @Suppress("NAME_SHADOWING")
        val decoder = decoder.beginStructure(descriptor, *typeParams)
        val size = readSize(decoder, builder)
        mainLoop@ while (true) {
            val index = decoder.decodeElementIndex(descriptor)
            when (index) {
                CompositeDecoder.READ_ALL -> {
                    readAll(decoder, builder, startIndex, size)
                    break@mainLoop
                }
                CompositeDecoder.READ_DONE -> break@mainLoop
                else -> readItem(decoder, startIndex + index, builder)
            }

        }
        decoder.endStructure(descriptor)
        return builder.toResult()
    }

    private fun readSize(decoder: CompositeDecoder, builder: TBuilder): Int {
        val size = decoder.decodeCollectionSize(descriptor)
        builder.checkCapacity(size)
        return size
    }

    protected abstract fun readItem(decoder: CompositeDecoder, index: Int, builder: TBuilder, checkIndex: Boolean = true)

    private fun readAll(decoder: CompositeDecoder, builder: TBuilder, startIndex: Int, size: Int) {
        require(size >= 0) { "Size must be known in advance when using READ_ALL" }
        for (index in 0 until size)
            readItem(decoder, startIndex + index, builder, checkIndex = false)
    }
}

sealed class ListLikeSerializer<TElement, TCollection, TBuilder>(private val elementSerializer: KSerializer<TElement>) :
    AbstractCollectionSerializer<TElement, TCollection, TBuilder>() {

    abstract fun TBuilder.insert(index: Int, element: TElement)
    abstract override val descriptor: ListLikeDescriptor

    final override val typeParams: Array<KSerializer<*>> = arrayOf(elementSerializer)

    override fun serialize(encoder: Encoder, obj: TCollection) {
        val size = obj.objSize()
        @Suppress("NAME_SHADOWING")
        val encoder = encoder.beginCollection(descriptor, size, *typeParams)
        val iterator = obj.objIterator()
        for (index in 0 until size)
            encoder.encodeSerializableElement(descriptor, index, elementSerializer, iterator.next())
        encoder.endStructure(descriptor)
    }

    protected override fun readItem(decoder: CompositeDecoder, index: Int, builder: TBuilder, checkIndex: Boolean) {
        builder.insert(index, decoder.decodeSerializableElement(descriptor, index, elementSerializer))
    }
}

sealed class MapLikeSerializer<TKey, TVal, TCollection, TBuilder: MutableMap<TKey, TVal>>(
    val keySerializer: KSerializer<TKey>,
    val valueSerializer: KSerializer<TVal>
) : AbstractCollectionSerializer<Map.Entry<TKey, TVal>, TCollection, TBuilder>() {

    abstract fun TBuilder.insertKeyValuePair(index: Int, key: TKey, value: TVal)
    abstract override val descriptor: MapLikeDescriptor

    final override val typeParams = arrayOf(keySerializer, valueSerializer)

    final override fun readItem(decoder: CompositeDecoder, index: Int, builder: TBuilder, checkIndex: Boolean) {
        val key: TKey = decoder.decodeSerializableElement(descriptor, index, keySerializer)
        val vIndex = if (checkIndex) {
            decoder.decodeElementIndex(descriptor).also {
                require(it == index + 1) { "Value must follow key in a map, index for key: $index, returned index for value: $it" }
            }
        } else {
            index + 1
        }
        val value: TVal = if (builder.containsKey(key) && valueSerializer.descriptor.kind !is PrimitiveKind) {
            decoder.updateSerializableElement(descriptor, vIndex, valueSerializer, builder.getValue(key))
        } else {
            decoder.decodeSerializableElement(descriptor, vIndex, valueSerializer)
        }
        builder[key] = value
    }

    override fun serialize(encoder: Encoder, obj: TCollection) {
        val size = obj.objSize()
        @Suppress("NAME_SHADOWING")
        val encoder = encoder.beginCollection(descriptor, size, *typeParams)
        val iterator = obj.objIterator()
        var index = 0
        iterator.forEach { (k, v) ->
            encoder.encodeSerializableElement(descriptor, index++, keySerializer, k)
            encoder.encodeSerializableElement(descriptor, index++, valueSerializer, v)
        }
        encoder.endStructure(descriptor)
    }
}

class SetSerializer<E>(private val eSerializer: KSerializer<E>) : ListLikeSerializer<E, Set<E>, HashSet<E>>(eSerializer) {

    override fun deserialize(decoder: Decoder): Set<E> {
        decoder.beginStructure(descriptor).endStructure(descriptor)
        return List(10) { Json.parse(eSerializer, """""")}.toSet()
    }

    override val descriptor = HashSetClassDesc(eSerializer.descriptor)

    override fun Set<E>.objSize(): Int = size
    override fun Set<E>.objIterator(): Iterator<E> = iterator()
    override fun builder(): HashSet<E> = HashSet()
    override fun HashSet<E>.builderSize(): Int = size
    override fun HashSet<E>.toResult(): Set<E> = this
    override fun Set<E>.toBuilder(): HashSet<E> = this as? HashSet<E> ?: HashSet(this)
    override fun HashSet<E>.checkCapacity(size: Int) {}
    override fun HashSet<E>.insert(index: Int, element: E) { add(element) }
}

class ArrayListSerializer<E>(private val element: KSerializer<E>) : ListLikeSerializer<E, List<E>, ArrayList<E>>(element) {

    override fun deserialize(decoder: Decoder): List<E> {
        decoder.beginStructure(descriptor).endStructure(descriptor)
        return List(10) { Json.parse(element, """""")}
    }

    override val descriptor = ArrayListClassDesc(element.descriptor)

    override fun List<E>.objSize(): Int = size
    override fun List<E>.objIterator(): Iterator<E> = iterator()
    override fun builder(): ArrayList<E> = arrayListOf()
    override fun ArrayList<E>.builderSize(): Int = size
    override fun ArrayList<E>.toResult(): List<E> = this
    override fun List<E>.toBuilder(): ArrayList<E> = this as? ArrayList<E> ?: ArrayList(this)
    override fun ArrayList<E>.checkCapacity(size: Int) = ensureCapacity(size)
    override fun ArrayList<E>.insert(index: Int, element: E) { add(index, element) }

}

class MapSerializer<K, V>(private val kSerializer: KSerializer<K>,
                          private val vSerializer: KSerializer<V>) :
    MapLikeSerializer<K, V, Map<K, V>, HashMap<K, V>>(kSerializer, vSerializer) {
    override fun deserialize(decoder: Decoder): Map<K, V> {
        /*decoder.beginStructure(descriptor.keyDescriptor).endStructure(descriptor.keyDescriptor)
        decoder.beginStructure(descriptor.valueDescriptor).endStructure(descriptor.valueDescriptor)
        decoder.beginStructure(descriptor).endStructure(descriptor)*/
        return List(10) { Json.parse(kSerializer, """""")}
            .zip(List(10) { Json.parse(vSerializer, """""")}).toMap()
    }

    override val descriptor = HashMapClassDesc(kSerializer.descriptor, vSerializer.descriptor)

    override fun Map<K, V>.objSize(): Int = size
    override fun Map<K, V>.objIterator(): Iterator<Map.Entry<K, V>> = iterator()
    override fun builder(): HashMap<K, V> = HashMap()
    override fun HashMap<K, V>.builderSize(): Int = size
    override fun HashMap<K, V>.toResult(): Map<K, V> = this
    override fun Map<K, V>.toBuilder(): HashMap<K, V> = this as? HashMap<K, V> ?: HashMap(this)
    override fun HashMap<K, V>.checkCapacity(size: Int) {}
    override fun HashMap<K, V>.insertKeyValuePair(index: Int, key: K, value: V) = set(key, value)
}
