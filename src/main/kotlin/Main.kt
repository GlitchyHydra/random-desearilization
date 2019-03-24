import kotlinx.serialization.*

@ImplicitReflectionSerializer
fun main(args: Array<String>) {
   val customSerializer = Data.serializer()
   println(customSerializer.deserialize(MyDecoder()))
}
