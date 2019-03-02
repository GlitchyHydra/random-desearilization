import kotlinx.serialization.*
import kotlinx.serialization.internal.SerialClassDescImpl
import kotlinx.serialization.json.Json
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import javax.activation.UnsupportedDataTypeException

@Serializable
data class Data<T>(val primitive: T) {

    @Deprecated("Not using")
    @Serializer(forClass = Data::class)
    open class DataSerializer<T>(val t: KSerializer<T>) : KSerializer<Data<T>> {
        override val descriptor: SerialDescriptor = SerialClassDescImpl("Data")

        override fun serialize(encoder: Encoder, obj: Data<T>) {
            TODO("not implemented")
        }

        override fun deserialize(decoder: Decoder): Data<T> {
            val dec = decoder.beginStructure(descriptor, t)
            val index = dec.decodeElementIndex(descriptor)
            val element = dec.decodeSerializableElement(descriptor, index, t)
            return Data(element)
        }

    }
}


@Suppress("IMPLICIT_CAST_TO_ANY", "JAVA_CLASS_ON_COMPANION")
open class PrimitiveRandom<T : Any> {

    companion object {
        fun getRandomString() =
            Json.unquoted.parse(Data.serializer(MyStringSerializer), """{primitive:}""")

        fun getRandomChar() =
            Json.unquoted.parse(Data.serializer(CharSerializer), """{primitive:}""")

        fun getRandomDouble() =
            Json.unquoted.parse(Data.serializer(DoubleSerializer), """{primitive:}""")

        fun getRandomFloat() =
            Json.unquoted.parse(Data.serializer(FloatSerializer), """{primitive:}""")

        fun getRandomLong() =
            Json.unquoted.parse(Data.serializer(LongSerializer), """{primitive:}""")

        fun getRandomInt() =
            Json.unquoted.parse(Data.serializer(IntSerializer), """{primitive:}""")

        fun getRandomShort() =
            Json.unquoted.parse(Data.serializer(ShortSerializer), """{primitive:}""")

        fun getRandomByte() =
            Json.unquoted.parse(Data.serializer(ByteSerializer), """{primitive:}""")

        fun getRandomBoolean() =
            Json.unquoted.parse(Data.serializer(MyBooleanSerializer), """{primitive:}""")
        //fun getRandomNull()
    }

    @Deprecated("This dont working properly")
    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Any> generateByDeserialize(): Data<T> = when (T::class) {
        String::class -> Json.unquoted.parse(Data.serializer(MyStringSerializer), """{primitive:}""")
        Char::class -> Json.unquoted.parse(Data.serializer(CharSerializer), """{primitive:}""")
        Double::class -> Json.unquoted.parse(Data.serializer(DoubleSerializer), """{primitive:}""")
        Float::class -> Json.unquoted.parse(Data.serializer(FloatSerializer), """{primitive:}""")
        Long::class -> Json.unquoted.parse(Data.serializer(LongSerializer), """{primitive:}""")
        Int::class -> Json.unquoted.parse(Data.serializer(IntSerializer), """{primitive:}""")
        Short::class -> Json.unquoted.parse(Data.serializer(ShortSerializer), """{primitive:}""")
        Byte::class -> Json.unquoted.parse(Data.serializer(ByteSerializer), """{primitive:}""")
        Boolean::class -> Json.unquoted.parse(Data.serializer(MyBooleanSerializer), """{primitive:}""")
        //Unit::class -> UnitSerializer
        else -> null
    } as Data<T>

}

abstract class TypeReference<T> : Comparable<TypeReference<T>> {
    val type: Type =
        (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0]

    override fun compareTo(other: TypeReference<T>) = 0
}

inline fun <reified T : Any> printGenerics(): String {
    val type = object : TypeReference<T>() {}.type
    if (type is ParameterizedType)
        return type.actualTypeArguments[0].typeName
    throw IllegalAccessError()
}