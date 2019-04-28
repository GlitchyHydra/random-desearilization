import BinarySearchTree.BinaryTree
import kotlinx.serialization.*

@ImplicitReflectionSerializer
@Serializable
data class Data(@Optional val c: Short? = 3)//, val g: B)

@Serializable
data class DataTransient(@Transient val c: Short = 3, val b: Int)

@Serializable
data class OptionalData(val c: Short = 3)

@Serializable
data class B(@Transient val c: Short? = 3, val b: Int)

@Serializable
data class A(@RangeEnum(2,6) val n: Color)

@Serializable
data class BinaryTree(@RangeInt(55,155) val binaryTree: BinaryTree<Int>)

enum class Color(val rgb: Int) {
    RED(0xFF0000),
    GREEN(0x00FF00),
    BLUE(0x0000FF),
    WHITE(0x0000FF),
    BLACK(0x0000FF),
    GREY(0x0000FF)
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