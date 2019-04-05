import kotlinx.serialization.*

@Serializable
data class Data(@RangeInt(0,100) val l: List<Int?>, val a: Int?, @RangeInt(-11, 10) @RangeDouble(4.3, 15.3) val map: Map<Int, Double>,
                @RangeChar(0.toChar(),100.toChar()) val b: Char, val c: Short?, val d: Byte, val e: A, val g: ProtocolState)

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