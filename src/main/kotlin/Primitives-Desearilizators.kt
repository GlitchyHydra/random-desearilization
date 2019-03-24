import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.internal.SerialClassDescImpl
import kotlin.random.Random

sealed class PrimitiveSerializer<T> : KSerializer<T> {
    override fun serialize(encoder: Encoder, obj: T) {
        TODO("not implemented")
    }

    override val descriptor: SerialDescriptor
        get() = SerialClassDescImpl("Data")
}

object MyStringSerializer : PrimitiveSerializer<String>() {
    private const val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ \t\n"
    override fun deserialize(decoder: Decoder): String{
        //decoder.beginStructure(descriptor).endStructure(descriptor)
        return (1..100).map { Random.nextInt(0, source.length)  }
            .map { source[it] }
            .joinToString("")
    }
}

object CharSerializer : PrimitiveSerializer<Char>() {
    override fun deserialize(decoder: Decoder): Char {
        //decoder.beginStructure(descriptor).endStructure(descriptor)
        return (Char.MIN_VALUE..Char.MAX_VALUE).random()
    }
}

object ByteSerializer : PrimitiveSerializer<Byte>() {
    override fun deserialize(decoder: Decoder): Byte {
        //decoder.beginStructure(descriptor).endStructure(descriptor)
        return Random.nextBytes(1)[0]
    }
}

object ShortSerializer : PrimitiveSerializer<Short>() {
    override fun deserialize(decoder: Decoder): Short {
        //decoder.beginStructure(descriptor).endStructure(descriptor)
        return Short.MIN_VALUE.rangeTo(Short.MAX_VALUE).random().toShort()
    }
}

object IntSerializer : PrimitiveSerializer<Int>() {
    override fun deserialize(decoder: Decoder): Int {
        //decoder.beginStructure(descriptor).endStructure(descriptor)
        return (Int.MIN_VALUE..Int.MAX_VALUE).random()
    }
}

object LongSerializer : PrimitiveSerializer<Long>() {
    override fun deserialize(decoder: Decoder): Long {
        //decoder.beginStructure(descriptor).endStructure(descriptor)
        return Random.nextLong()
    }
}

object FloatSerializer : PrimitiveSerializer<Float>() {
    override fun deserialize(decoder: Decoder): Float {
        //decoder.beginStructure(descriptor).endStructure(descriptor)
        return Random.nextFloat()
    }
}

object DoubleSerializer : PrimitiveSerializer<Double>() {
    override fun deserialize(decoder: Decoder): Double {
        //decoder.beginStructure(descriptor).endStructure(descriptor)
        return Random.nextDouble()
    }
}

object MyBooleanSerializer : PrimitiveSerializer<Boolean>() {
    override fun deserialize(decoder: Decoder): Boolean {
        //decoder.beginStructure(descriptor).endStructure(descriptor)
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

