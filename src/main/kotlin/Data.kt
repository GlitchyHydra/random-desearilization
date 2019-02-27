import kotlinx.serialization.*
import kotlinx.serialization.json.Json

@Serializable
open class Data<T>(val primitive: T)

@Serializer(forClass = Data::class)
open class PrimitiveSerializer<T> : KSerializer<Data<T>> {
    override val descriptor: SerialDescriptor
        get() = TODO("not implemented")

    override fun serialize(encoder: Encoder, obj: Data<T>) {
        TODO("not implemented")
    }

    override fun deserialize(decoder: Decoder): Data<T> {
        TODO("not implemented")
    }

}


public fun getBoolean(): Boolean =
    Json.parse(BooleanSerializer(), "").primitive

public fun getByte(): Byte =
    Json.parse(ByteSerializer(), "").primitive

public fun getChar(): Char =
    Json.parse(CharSerializer(), "").primitive

public fun getShort(): Short =
    Json.parse(ShortSerializer(), "").primitive

public fun getInt(): Int =
    Json.parse(IntSerializer(), "").primitive

public fun getLong(): Long =
    Json.parse(LongSerializer(), "").primitive

public fun getFloat(): Float =
    Json.parse(FloatSerializer(), "").primitive

public fun getDouble(): Double =
    Json.parse(DoubleSerializer(), "").primitive
