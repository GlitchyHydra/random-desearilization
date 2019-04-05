import kotlinx.serialization.*
import kotlin.random.Random

@ImplicitReflectionSerializer
fun main(args: Array<String>) {
   val customSerializer = Data.serializer()
   val randomDecoder = RandomDecoder()
   //println(Random.nextInt(0, 1000))
   for (i in 0..500) println(customSerializer.deserialize(randomDecoder))
}

