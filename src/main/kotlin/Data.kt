import kotlinx.serialization.*
import kotlinx.serialization.internal.SerialClassDescImpl
import kotlinx.serialization.json.Json
import kotlin.reflect.KClass

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
/*
class MyElementDescriptor() : SerialDescriptor  {
    override fun isElementOptional(index: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getElementName(index: Int): String = "primitive"

    override fun getElementIndex(name: String): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override val name: String
        get() = "Data"
    override val kind: SerialKind
        get() = PrimitiveKind.UNIT

}*/


@Suppress("IMPLICIT_CAST_TO_ANY")
class PrimitiveRandom<T : Any, E : Any> {
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

    }

    @PublishedApi
    internal fun <T : Any> getRandomList(
        clazz: KClass<T>
    ): List<T>? = when (clazz) {
        String::class -> Json.parse(ArrayListSerializer(MyStringSerializer), """[]""")
        Char::class -> Json.parse(ArrayListSerializer(CharSerializer), """[]""")
        Double::class -> Json.parse(ArrayListSerializer(DoubleSerializer), """[]""")
        Float::class -> Json.parse(ArrayListSerializer(FloatSerializer), """[]""")
        Long::class -> Json.parse(ArrayListSerializer(LongSerializer), "[]")
        Int::class -> Json.parse(ArrayListSerializer(IntSerializer), """[]""")
        Short::class ->  Json.parse(ArrayListSerializer(ShortSerializer), "[]")
        Byte::class ->  Json.parse(ArrayListSerializer(ByteSerializer), "[]")
        Boolean::class ->  Json.parse(ArrayListSerializer(MyBooleanSerializer), "[]")
        //Unit::class -> UnitSerializer
        else -> null
    } as List<T>?


    @PublishedApi
    internal fun <T : Any> getRandomSet(
        clazz: KClass<T>
    ): Set<T>? = when (clazz) {
        String::class -> Json.parse(SetSerializer(MyStringSerializer), """[]""")
        Char::class -> Json.parse(SetSerializer(CharSerializer), """[]""")
        Double::class -> Json.parse(SetSerializer(DoubleSerializer), """[]""")
        Float::class -> Json.parse(SetSerializer(FloatSerializer), """[]""")
        Long::class -> Json.parse(SetSerializer(LongSerializer), "[]")
        Int::class -> Json.parse(SetSerializer(IntSerializer), """[]""")
        Short::class ->  Json.parse(SetSerializer(ShortSerializer), "[]")
        Byte::class ->  Json.parse(SetSerializer(ByteSerializer), "[]")
        Boolean::class ->  Json.parse(SetSerializer(MyBooleanSerializer), "[]")
        //Unit::class -> UnitSerializer
        else -> null
    } as Set<T>?

    @PublishedApi
    internal fun <T : Any> defaultSerializer(
        clazz: KClass<T>
    ): KSerializer<T> = when (clazz) {
        String::class -> MyStringSerializer
        Char::class -> CharSerializer
        Double::class -> DoubleSerializer
        Float::class -> FloatSerializer
        Long::class -> LongSerializer
        Int::class -> IntSerializer
        Short::class -> ShortSerializer
        Byte::class -> ByteSerializer
        Boolean::class -> MyBooleanSerializer
        //Unit::class -> UnitSerializer
        else -> null
    } as KSerializer<T>

    @PublishedApi
    internal fun <T : Any, E: Any> getRandomMap(
        type1: KClass<T>, type2: KClass<E>
    ): Map<T, E>? = when(type2)  {
        String::class -> Json.parse(MapSerializer(defaultSerializer(type1), MyStringSerializer), """""")
        Char::class -> Json.parse(MapSerializer(defaultSerializer(type1), CharSerializer), """""")
        Double::class -> Json.parse(MapSerializer(defaultSerializer(type1), DoubleSerializer), """""")
        Float::class -> Json.parse(MapSerializer(defaultSerializer(type1), FloatSerializer), """""")
        Long::class -> Json.parse(MapSerializer(defaultSerializer(type1), LongSerializer), """""")
        Int::class -> Json.parse(MapSerializer(defaultSerializer(type1), IntSerializer), """""")
        Short::class ->  Json.parse(MapSerializer(defaultSerializer(type1), ShortSerializer), """""")
        Byte::class -> Json.parse(MapSerializer(defaultSerializer(type1), ByteSerializer), """""")
        Boolean::class ->  Json.parse(MapSerializer(defaultSerializer(type1), MyBooleanSerializer), """""")
        //Unit::class -> UnitSerializer
        else -> null
    } as Map<T, E>?

}



/*
@PublishedApi
internal fun <T : Any> PrimitiveRandom<T>.defaultSerializer(
    clazz: KClass<T>
): KSerializer<T>? = when (clazz) {
    String::class -> StringSerializer()
    Char::class -> CharSerializer
    Double::class -> DoubleSerializer
    Float::class -> FloatSerializer
    Long::class -> LongSerializer
    Int::class -> IntSerializer
    Short::class -> ShortSerializer
    Byte::class -> ByteSerializer
    Boolean::class -> BooleanSerializer
    Unit::class -> UnitSerializer
    else -> null
} as KSerializer<T>?

inline fun <reified T: Any> PrimitiveRandom<T>.defaultSerializer(): KSerializer<T>? =
    defaultSerializer(T::class)*/
/*

    @PublishedApi
    @Suppress("UNCHECKED_CAST")
    internal fun <T : Any> PrimitiveRandom<T>.generateByDeserialize(classInstance: KClass<T>): Data<T>? =
        when (classInstance) {
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
        } as Data<T>?

    inline fun <reified T : Any> PrimitiveRandom<T>.generateByDeserialize(): Data<T>? =
        generateByDeserialize(T::class)
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
}*/