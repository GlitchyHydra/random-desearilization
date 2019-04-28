package BinarySearchTree

import kotlinx.serialization.*
import kotlinx.serialization.internal.LinkedHashSetClassDesc
import java.util.SortedSet
import RangeInt

@Serializer(forClass = BinaryTree::class)
class BinaryTreeSerializer<E : Comparable<E>>(private val eSerializer: KSerializer<E>) :
    KSerializer<BinaryTree<E>> {
    override val descriptor: SerialDescriptor
        get() = LinkedHashSetClassDesc(eSerializer.descriptor)

    override fun serialize(encoder: Encoder, obj: BinaryTree<E>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deserialize(decoder: Decoder): BinaryTree<E> {
        val a = decoder.beginStructure(descriptor, eSerializer)
        val size = a.decodeCollectionSize(descriptor)
        val binaryTree = BinaryTree<E>()
        for (i in 0..size) {
            val index = a.decodeElementIndex(descriptor)
            binaryTree.add(a.decodeSerializableElement(descriptor, index, eSerializer))
        }
        return binaryTree
    }
}

@Serializable(BinaryTreeSerializer::class)
class BinaryTree<T : Comparable<T>> : AbstractMutableSet<T>(), CheckableSortedSet<T> {

    private var root: Node<T>? = null

    override var size = 0
        private set

    @Serializable
    private class Node<T>(@RangeInt(0, 153) val value: T) {

        var left: Node<T>? = null

        var right: Node<T>? = null
    }

    override fun add(element: T): Boolean {
        val closest = find(element)
        val comparison = if (closest == null) -1 else element.compareTo(closest.value)
        if (comparison == 0) {
            return false
        }
        val newNode = Node(element)
        when {
            closest == null -> root = newNode
            comparison < 0 -> {
                assert(closest.left == null)
                closest.left = newNode
            }
            else -> {
                assert(closest.right == null)
                closest.right = newNode
            }
        }
        size++
        return true
    }

    override fun checkInvariant(): Boolean =
        root?.let { checkInvariant(it) } ?: true

    private fun checkInvariant(node: Node<T>): Boolean {
        val left = node.left
        if (left != null && (left.value >= node.value || !checkInvariant(left))) return false
        val right = node.right
        return right == null || right.value > node.value && checkInvariant(right)
    }

    override fun remove(element: T): Boolean {
        if (!this.contains(element)) return false
        var currentNode = root ?: return false
        var parentNode = root ?: return false
        var onRight = true
        while (currentNode.value != element) {
            parentNode = currentNode
            if (element > currentNode.value) {
                currentNode = currentNode.right ?: return false
                onRight = true
            } else if (element < currentNode.value) {
                currentNode = currentNode.left ?: return false
                onRight = false
            }
        }
        if (currentNode.left == null && currentNode.right == null) {
            //if removal point is leaf
            when {
                currentNode == root -> root = null
                onRight -> parentNode.right = null
                else -> parentNode.left = null
            }
        } else if (currentNode.left == null) {
            //if removal point have only right child
            if (currentNode == root) root = currentNode.right
            else {
                val right = currentNode.right ?: return false
                setNode(onRight, parentNode, right)
            }
        } else if (currentNode.right == null) {
            //if removal point have only left child
            if (currentNode == root) root = currentNode.left
            else {
                val left = currentNode.left ?: return false
                setNode(onRight, parentNode, left)
            }
        } else {
            //worst case - if removal point have both children
            var minNode = currentNode.right ?: return false
            var parentMinNode = currentNode.right ?: return false
            while (minNode.left != null) {
                parentMinNode = minNode
                val left = minNode.left ?: return false
                minNode = left
            }
            when {
                currentNode == root && parentMinNode == minNode -> {
                    val rootLeft = root!!.left
                    root = minNode
                    minNode.left = rootLeft
                }
                currentNode == root && parentMinNode != minNode -> {
                    parentMinNode.left = minNode.right
                    root = minNode
                    minNode.left = currentNode.left
                    minNode.right = currentNode.right
                }
                parentMinNode == minNode -> setNode(onRight, parentNode, minNode)
                else -> {
                    parentMinNode.left = minNode.right
                    minNode.right = currentNode.right
                    minNode.left = currentNode.left
                    setNode(onRight, parentNode, minNode)
                }
            }
            minNode.left = currentNode.left
        }

        size--
        return true
    }

    private fun setNode(onRight: Boolean, parentNode: Node<T>, currentNode: Node<T>) {
        if (onRight)
            parentNode.right = currentNode
        else parentNode.left = currentNode
    }

    override operator fun contains(element: T): Boolean {
        val closest = find(element)
        return closest != null && element.compareTo(closest.value) == 0
    }

    private fun find(value: T): Node<T>? =
        root?.let { find(it, value) }

    private fun find(start: Node<T>, value: T): Node<T> {
        val comparison = value.compareTo(start.value)
        return when {
            comparison == 0 -> start
            comparison < 0 -> start.left?.let { find(it, value) } ?: start
            else -> start.right?.let { find(it, value) } ?: start
        }
    }

    inner class BinaryTreeIterator : MutableIterator<T> {

        private var current: Node<T>? = null

        private fun findNext(): Node<T>? {
            if (size == 0) return null
            val currentNode = current ?: return find(first())
            if (currentNode.value == last()) return null
            if (currentNode.right != null) {
                var successor = currentNode.right ?: throw IllegalArgumentException()
                while (successor.left != null) {
                    successor = successor.left ?: return successor
                }
                return successor
            } else {
                var successor = root ?: throw IllegalArgumentException()
                var ancestor = root ?: throw IllegalArgumentException()
                while (ancestor != currentNode) {
                    if (currentNode.value < ancestor.value) {
                        successor = ancestor
                        ancestor = ancestor.left ?: return null
                    } else ancestor = ancestor.right ?: return null
                }
                return successor
            }
        }

        override fun hasNext(): Boolean = findNext() != null

        override fun next(): T {
            current = findNext()
            return (current ?: throw NoSuchElementException()).value
        }

        override fun remove() {
            val cur = current ?: throw IllegalArgumentException()
            var parent = root ?: throw IllegalArgumentException()
            var child = root ?: throw IllegalArgumentException()
            var onLeft = false
            while (child != current) {
                parent = child
                if (child.value < cur.value) {
                    child = child.right ?: throw IllegalArgumentException()
                    onLeft = false
                } else {
                    child = child.left ?: throw IllegalArgumentException()
                    onLeft = true
                }
            }
            when {
                cur.left == null && cur.right == null -> {
                    when {
                        current == root -> root = null
                        onLeft -> parent.left = null
                        else -> parent.right = null
                    }
                }
                cur.left == null -> {
                    when {
                        current == root -> root = cur.right
                        onLeft -> parent.left = cur.right
                        else -> parent.right = cur.right
                    }
                }
                cur.right == null -> {
                    when {
                        current == root -> root = cur.left
                        onLeft -> parent.left = cur.left
                        else -> parent.right = cur.left
                    }
                }
                else -> {
                    var minChild = cur.right ?: return
                    var parentMinChild = minChild
                    while (minChild.left != null) {
                        parentMinChild = minChild
                        minChild = minChild.left ?: return
                    }
                    when {
                        cur == root && parentMinChild == minChild -> {
                            val rootLeft = root!!.left
                            root = minChild
                            minChild.left = rootLeft
                        }
                        cur == root && parentMinChild != minChild -> {
                            parentMinChild.left = minChild.right
                            root = minChild
                            minChild.left = cur.left
                            minChild.right = cur.right
                        }
                        parentMinChild == minChild -> setNode(!onLeft, parent, minChild)
                        else -> {
                            parentMinChild.left = minChild.right
                            minChild.right = cur.right
                            minChild.left = cur.left
                            setNode(!onLeft, parent, minChild)
                        }
                    }
                    minChild.left = cur.left
                }
            }
            size--
            current = findNext()
        }
    }

    override fun iterator(): MutableIterator<T> = BinaryTreeIterator()

    override fun comparator(): Comparator<in T>? = null

    override fun subSet(fromElement: T, toElement: T): SortedSet<T> =
        SubSet(this, fromElement, toElement)

    override fun headSet(toElement: T): SortedSet<T> =
        SubSet(this, null, toElement)

    override fun tailSet(fromElement: T): SortedSet<T> =
        SubSet(this, fromElement, null)


    override fun first(): T {
        var current: Node<T> = root ?: throw NoSuchElementException("root is null")
        while (current.left != null) {
            current = current.left!!
        }
        return current.value
    }

    override fun last(): T {
        var current: Node<T> = root ?: throw NoSuchElementException("root is null")
        while (current.right != null) {
            current = current.right!!
        }
        return current.value
    }
}