import kotlinx.serialization.*

@ImplicitReflectionSerializer
fun main(args: Array<String>) {
   val customSerializer = Data.serializer()
   for (i in 0..500) {
      customSerializer.deserialize(RandomDecoder())
   }
   println(customSerializer.deserialize(RandomDecoder()))
}
