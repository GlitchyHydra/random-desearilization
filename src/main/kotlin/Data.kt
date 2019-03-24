import kotlinx.serialization.*

@Serializable()
data class Data(val a: Int?, val b: Char, val c: Short, val d: Byte, val e: A)

@Serializable
data class A(val b: Int)
