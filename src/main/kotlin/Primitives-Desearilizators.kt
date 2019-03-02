import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialDescriptor
import kotlin.random.Random

sealed class PrimitiveSerializer<T> : KSerializer<T> {
    override fun serialize(encoder: Encoder, obj: T) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override val descriptor: SerialDescriptor
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
}

object MyStringSerializer : PrimitiveSerializer<String>() {
    private const val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ \t\n"
    override fun deserialize(decoder: Decoder): String{
        return (1..100).map { Random.nextInt(0, source.length)  }
            .map { source[it] }
            .joinToString("")
    }
}

object CharSerializer : PrimitiveSerializer<Char>() {
    override fun deserialize(decoder: Decoder): Char {
        return (Char.MIN_VALUE..Char.MAX_VALUE).random()
    }
}

object ByteSerializer : PrimitiveSerializer<Byte>() {
    override fun deserialize(decoder: Decoder): Byte {
        return Random.nextBytes(1)[0]
    }
}

object ShortSerializer : PrimitiveSerializer<Short>() {
    override fun deserialize(decoder: Decoder): Short {
        return Short.MIN_VALUE.rangeTo(Short.MAX_VALUE).random().toShort()
    }
}

object IntSerializer : PrimitiveSerializer<Int>() {
    override fun deserialize(decoder: Decoder): Int {
        return (Int.MIN_VALUE..Int.MAX_VALUE).random()
    }
}

object LongSerializer : PrimitiveSerializer<Long>() {
    override fun deserialize(decoder: Decoder): Long {
        return Random.nextLong()
    }
}

object FloatSerializer : PrimitiveSerializer<Float>() {
    override fun deserialize(decoder: Decoder): Float {
        return Random.nextFloat()
    }
}

object DoubleSerializer : PrimitiveSerializer<Double>() {
    override fun deserialize(decoder: Decoder): Double {
        return Random.nextDouble()
    }
}

object MyBooleanSerializer : PrimitiveSerializer<Boolean>() {
    override fun deserialize(decoder: Decoder): Boolean {
        return Random.nextBoolean()
    }
}

/*
Need to find library for generate random with Null
 */
/*
object NullSerializer : PrimitiveSerializer<Double>() {
    override fun deserialize(decoder: Decoder): Data<Double> {
        return Data(Random.nextDouble())
    }*/

