package BinarySearchTree

import java.util.SortedSet

class SubSet<T : Comparable<T>>(private val delegate: SortedSet<T>,
                                private val fromElement: T?,
                                private val toElement: T?) : AbstractMutableSet<T>(), SortedSet<T> {
    override fun comparator(): Comparator<in T>? = delegate.comparator()

    override fun subSet(fromElement: T, toElement: T): SortedSet<T> {
        return SubSet(this, fromElement, toElement)
    }

    override fun headSet(toElement: T): SortedSet<T> {
        return SubSet(this, null, toElement)
    }

    override fun tailSet(fromElement: T): SortedSet<T> {
        return SubSet(this, fromElement, null)
    }

    override fun last(): T {
        val iter = iterator()
        var element = iter.next()
        while (iter.hasNext()) {
            element = iter.next()
        }
        return element
    }

    override fun first(): T {
        return iterator().next()
    }

    override fun add(element: T): Boolean {
        return when {
            fromElement == null && toElement == null -> false
            fromElement == null && element < toElement!! -> delegate.add(element)
            toElement == null && element >= fromElement!! -> delegate.add(element)
            element >= fromElement!! && element < toElement!! -> delegate.add(element)
            else -> false
        }
    }

    override fun remove(element: T): Boolean {
        return when {
            fromElement == null && toElement == null -> false
            fromElement == null && element < toElement!! -> delegate.remove(element)
            toElement == null && element >= fromElement!! -> delegate.remove(element)
            element >= fromElement!! && element < toElement!! -> delegate.remove(element)
            else -> false
        }
    }

    override fun iterator(): MutableIterator<T> = object : MutableIterator<T> {
        private val delegate = this@SubSet.delegate.iterator()

        private var next: T? = null

        init {
            while (delegate.hasNext()) {
                if (fromElement == null) {
                    this.next = delegate.next()
                    break
                }
                val next = delegate.next()
                if (next >= fromElement) {
                    this.next = next
                    break
                }
            }
        }

        override fun hasNext(): Boolean {
            val n = next ?: return false
            return n < toElement ?: return true
        }

        override fun next(): T {
            val result = next ?: throw NoSuchElementException()
            next = if (delegate.hasNext()) delegate.next() else null
            return result
        }

        override fun remove() {
            delegate.remove()
        }

    }

    override val size: Int
        get() = when {
            fromElement == null && toElement == null -> 0
            fromElement == null -> delegate.count { it < toElement!! }
            toElement == null -> delegate.count { it >= fromElement }
            else -> delegate.count { it >= fromElement && it < toElement }
        }
}