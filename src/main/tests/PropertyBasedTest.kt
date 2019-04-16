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
import DataForTesting.*
import io.kotlintest.matchers.collections.shouldBeOneOf
import io.kotlintest.matchers.doubles.shouldBeGreaterThan
import io.kotlintest.matchers.doubles.shouldBeGreaterThanOrEqual
import io.kotlintest.matchers.doubles.shouldBeLessThan
import kotlinx.serialization.decode


val randomDecoder = RandomDecoder()

class GeometryTests : AnnotationSpec() {

    private val pointSerializer = Point.serializer()
    private val triangleSerializer = Triangle.serializer()
    private val circleSerializer = Circle.serializer()
    private val segmentSerializer = Segment.serializer()
    private val point: Point get() = randomDecoder.decode(pointSerializer)
    private val triangle: Triangle get() = randomDecoder.decode(triangleSerializer)
    private val circle: Circle get() = randomDecoder.decode(circleSerializer)
    private val segment: Segment get() = randomDecoder.decode(segmentSerializer)


    @Test
    fun circleByDiameter() {
        for (i in 0..100) {
            val (segment1, segment2) = Pair(segment, segment)
            val circle1 = DataForTesting.circleByDiameter(segment1)
            val circle2 = DataForTesting.circleByDiameter(segment2)
            val result1 = segment1.begin.distance(segment1.end)
            val result2 = segment2.begin.distance(segment2.end)
            if (segment1 == segment2) circle1 shouldBe circle2
            val checkSpecial1 = circle1.radius == Double.NaN || circle1.radius == Double.POSITIVE_INFINITY
                    || circle1.radius == Double.NEGATIVE_INFINITY
            val checkSpecial2 = circle2.radius == Double.NaN || circle2.radius == Double.POSITIVE_INFINITY
                    || circle2.radius == Double.NEGATIVE_INFINITY
            if (checkSpecial1 && checkSpecial2)  circle1.radius shouldBe circle2.radius
            else if (checkSpecial1 || checkSpecial2) 3 shouldBe 3
            else if (result1 >= result2) circle1.radius  shouldBeGreaterThanOrEqual circle2.radius
            else circle1.radius  shouldBeLessThan circle2.radius
                /*
            val randomX = Random.nextDouble(segment1.begin.x - 11.2, segment1.end.x + 10.0)
            val randomY = Random.nextDouble(segment1.begin.y - 11.2, segment1.end.y + 10.0)
            val randomPoint = Point(randomX, randomY)
            if (randomX in segment1.begin.x..segment1.end.x && randomY in segment1.begin.y..segment1.end.y)
                circle1.contains(randomPoint) shouldBe true
            else circle1.contains(randomPoint) shouldBe false*/
        }
    }
}

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
                shouldThrow<NoSuchElementException> {subSet.first()}
                shouldThrow<NoSuchElementException> {subSet.last()}
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
            shouldThrow<NoSuchElementException> {newBinaryTree.first()}
            return
        } else newBinaryTree.first()
        val to = newBinaryTree.random()
        val headSet = newBinaryTree.headSet(to)
        if (first >= to) headSet shouldHaveSize 0
        else {
            val last = if (headSet.isEmpty()) {
                shouldThrow<NoSuchElementException> {headSet.last()}
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
            shouldThrow<NoSuchElementException> {newBinaryTree.last()}
            return
        } else newBinaryTree.last()
        val from = newBinaryTree.random()
        val tailSet = newBinaryTree.tailSet(from)
        if (from > last) tailSet shouldHaveSize 0
        else {
            val first = if (tailSet.isEmpty()) {
                shouldThrow<NoSuchElementException> {tailSet.first()}
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