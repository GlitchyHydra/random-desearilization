import kotlinx.serialization.*

@Serializable()
data class Data(val l: Set<Int?>, val a: Int?, val b: Char, val c: Short, val d: Byte, val e: A, val g: ProtocolState)

@Serializable
data class A(val b: Int, val n: Color)

enum class Color(val rgb: Int) {
    RED(0xFF0000),
    GREEN(0x00FF00),
    BLUE(0x0000FF)
}

enum class ProtocolState {
    WAITING {
        override fun signal() = TALKING
    },

    TALKING {
        override fun signal() = WAITING
    };

    abstract fun signal(): ProtocolState
}