package BinarySearchTree

import java.util.*

interface CheckableSortedSet<T> : SortedSet<T> {
    fun checkInvariant(): Boolean
}