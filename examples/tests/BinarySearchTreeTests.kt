import io.kotlintest.matchers.collections.shouldContain
import io.kotlintest.matchers.collections.shouldContainAll
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.matchers.numerics.shouldBeLessThan
import io.kotlintest.matchers.numerics.shouldNotBeGreaterThan
import io.kotlintest.matchers.numerics.shouldNotBeLessThan
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.*
import kotlin.random.Random
import java.util.*

class BinaryTreeTests : AnnotationSpec() {

    private val dataSerializer = BinaryTree.serializer()
    private var newBinaryTree = dataSerializer.deserialize(randomDecoder).binaryTree

    @BeforeEach
    fun beforeTest() {
        newBinaryTree = dataSerializer.deserialize(randomDecoder).binaryTree
    }

    @Test
    fun checkSize() {
        var elementCount = 0
        newBinaryTree.forEach { _ -> elementCount++ }
        newBinaryTree.size shouldBe elementCount
    }

    @Test
    fun checkSizeAfterRemove() {
        val size = newBinaryTree.size
        val isRemoved = newBinaryTree.remove(Random.nextInt(newBinaryTree.first(), newBinaryTree.last() + 1))
        if (isRemoved) newBinaryTree.size shouldBeLessThan size
        else {
            newBinaryTree.size shouldNotBeGreaterThan size
            newBinaryTree.size shouldNotBeLessThan size
        }
    }

    @Test
    fun checkSubSet() {
        if (newBinaryTree.isEmpty()) {
            newBinaryTree shouldHaveSize 0
            return
        }
        val from = newBinaryTree.random()
        val to = newBinaryTree.random()
        val subSet = newBinaryTree.subSet(from, to)
        if (from >= to) subSet shouldHaveSize 0
        else {
            val (first, last) = if (subSet.isEmpty()) {
                shouldThrow<NoSuchElementException> { subSet.first() }
                shouldThrow<NoSuchElementException> { subSet.last() }
                return
            } else Pair(subSet.first(), subSet.last())
            val element = newBinaryTree.random()
            if (element in first..last) subSet shouldContain element
            newBinaryTree shouldContainAll subSet
        }
    }

    @Test
    fun checkHeadSet() {
        val first = if (newBinaryTree.isEmpty()) {
            shouldThrow<NoSuchElementException> { newBinaryTree.first() }
            return
        } else newBinaryTree.first()
        val to = newBinaryTree.random()
        val headSet = newBinaryTree.headSet(to)
        if (first >= to) headSet shouldHaveSize 0
        else {
            val last = if (headSet.isEmpty()) {
                shouldThrow<NoSuchElementException> { headSet.last() }
                return
            } else headSet.last()
            val element = newBinaryTree.random()
            if (element in first..last) headSet shouldContain element
            newBinaryTree shouldContainAll headSet
        }
    }

    @Test
    fun checkTailSet() {
        val last = if (newBinaryTree.isEmpty()) {
            shouldThrow<NoSuchElementException> { newBinaryTree.last() }
            return
        } else newBinaryTree.last()
        val from = newBinaryTree.random()
        val tailSet = newBinaryTree.tailSet(from)
        if (from > last) tailSet shouldHaveSize 0
        else {
            val first = if (tailSet.isEmpty()) {
                shouldThrow<NoSuchElementException> { tailSet.first() }
                return
            } else tailSet.first()
            val element = newBinaryTree.random()
            if (element in first..last) tailSet shouldContain element
            newBinaryTree shouldContainAll tailSet
        }
    }

    @Test
    fun repeatAllTests() {
        for (i in 1..1000) {
            beforeTest()
            checkSize()
            checkSizeAfterRemove()
            checkSubSet()
            checkHeadSet()
            checkTailSet()
        }
    }
}