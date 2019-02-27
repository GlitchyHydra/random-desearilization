import kotlinx.serialization.Decoder
import kotlin.random.Random

class CharSerializer : PrimitiveSerializer<Char>() {
    override fun deserialize(decoder: Decoder): Data<Char> {
        return Data((Char.MIN_VALUE..Char.MAX_VALUE).random())
    }
}

class ByteSerializer : PrimitiveSerializer<Byte>() {
    override fun deserialize(decoder: Decoder): Data<Byte> {
        return Data(Random.nextBytes(1)[0])
    }
}

/*
* short not work
* */
class ShortSerializer : PrimitiveSerializer<Short>() {
    override fun deserialize(decoder: Decoder): Data<Short> {
        return Data(4)
    }
}

class IntSerializer : PrimitiveSerializer<Int>() {
    override fun deserialize(decoder: Decoder): Data<Int> {
        return Data((Int.MIN_VALUE..Int.MAX_VALUE).random())
    }
}

class LongSerializer : PrimitiveSerializer<Long>() {
    override fun deserialize(decoder: Decoder): Data<Long> {
        return Data((Long.MIN_VALUE..Long.MAX_VALUE).random())
    }
}

class FloatSerializer : PrimitiveSerializer<Float>() {
    override fun deserialize(decoder: Decoder): Data<Float> {
        return Data(Random.nextFloat())
    }
}

class DoubleSerializer : PrimitiveSerializer<Double>() {
    override fun deserialize(decoder: Decoder): Data<Double> {
        return Data(Random.nextDouble())
    }
}

class BooleanSerializer : PrimitiveSerializer<Boolean>() {
    override fun deserialize(decoder: Decoder): Data<Boolean> {
        return Data(Random.nextBoolean())
    }
}

class NullSerializer : PrimitiveSerializer<Double>() {
    override fun deserialize(decoder: Decoder): Data<Double> {
        return Data(Random.nextDouble())
    }
}

