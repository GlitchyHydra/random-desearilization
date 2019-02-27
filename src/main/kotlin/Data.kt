import kotlinx.serialization.*

@Serializable
open class Data<T>(val primitive: T)

@Serializer(forClass = Data::class)
open class PrimitiveSerializer<T> : KSerializer<Data<T>> {
    override val descriptor: SerialDescriptor
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override fun serialize(encoder: Encoder, obj: Data<T>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deserialize(decoder: Decoder): Data<T> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}