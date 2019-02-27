import kotlinx.serialization.Decoder

object CharSerializer : PrimitiveSerializer<Char>() {
    override fun deserialize(decoder: Decoder): Data<Char> {
        return Data((Char.MIN_VALUE..Char.MAX_VALUE).random())
    }
}

object ByteSerializer : PrimitiveSerializer<Byte>() {
    override fun deserialize(decoder: Decoder): Data<Byte> {
        return Data(1)
    }
}

/*object ShortSerializer : PrimitiveSerializer<Short>() {
override fun deserialize(decoder: Decoder): Data<Short> {
return Data((Short.MIN_VALUE..Short.MAX_VALUE).random())
}
}*/

object IntSerializer : PrimitiveSerializer<Int>() {
    override fun deserialize(decoder: Decoder): Data<Int> {
        return Data((Int.MIN_VALUE..Int.MAX_VALUE).random())
    }
}

object LongSerializer : PrimitiveSerializer<Long>() {
    override fun deserialize(decoder: Decoder): Data<Long> {
        return Data((Long.MIN_VALUE..Long.MAX_VALUE).random())
    }
}

/*object FloatSerializer : PrimitiveSerializer<Int>() {
override fun deserialize(decoder: Decoder): Data<Int> {
return Data((Int.MIN_VALUE..Int.MAX_VALUE).random())
}
}*/

/*class IntSerializer : PrimitiveSerializer<Int>() {
override fun deserialize(decoder: Decoder): Data<Int> {
return Data((Int.MIN_VALUE..Int.MAX_VALUE).random())
}
}*/

