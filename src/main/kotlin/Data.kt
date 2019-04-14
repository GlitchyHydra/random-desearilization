import BinarySearchTree.BinaryTree
import kotlinx.serialization.*

@Serializable
data class Data(@RangeShort(-153, -1) val c: Short?, @Optional val d: Byte = 3,
                val e: A, val g: ProtocolState)

@Serializable
data class A(@RangeEnum(2,6) val n: Color, val binaryTree: BinarySearchTree.BinaryTree<Int>)

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