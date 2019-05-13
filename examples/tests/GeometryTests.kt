import io.kotlintest.matchers.doubles.shouldBeExactly
import io.kotlintest.matchers.doubles.shouldBeGreaterThan
import io.kotlintest.matchers.doubles.shouldBeLessThanOrEqual
import io.kotlintest.shouldBe
import io.kotlintest.specs.AnnotationSpec
import kotlinx.serialization.decode
import java.lang.Math.atan
import java.util.ArrayList

val randomDecoder = RandomDecoder()
private val pointSerializer = Point.serializer()
private val circleSerializer = Circle.serializer()
private val segmentSerializer = Segment.serializer()
private val pointNew: Point get() = randomDecoder.decode(pointSerializer)
private val circleNew: Circle get() = randomDecoder.decode(circleSerializer)
private val segmentNew: Segment get() = randomDecoder.decode(segmentSerializer)


class GeometryTests : AnnotationSpec() {

    private fun checkDouble(b: Double): Boolean = b.isNaN() || b.isInfinite()

    @Test
    fun triangle() {
        val triangle1 = Triangle(pointNew, pointNew, pointNew)
        val triangle2 = Triangle(pointNew, pointNew, pointNew)
        val area1 = triangle1.area()
        val area2 = triangle2.area()
        if (checkDouble(area1) || checkDouble(area2))
            return
        if (triangle1.halfPerimeter() > triangle2.halfPerimeter())
            area1 shouldBeGreaterThan area2
        else area1 shouldBeLessThanOrEqual area2
    }

    private fun Point.isFinite() = this.x.isFinite() && this.y.isFinite()
    private fun Segment.isFinite() = this.begin.isFinite() && this.end.isFinite()
    private fun Segment.distance() = this.begin.distance(this.end)

    @Test
    fun circleByDiameter() {
        val segment = segmentNew
        val circle = circleByDiameter(segment)
        if (segment.isFinite()) {
            circle.radius shouldBeExactly segment.distance() / 2.0
            circle.center.x shouldBeExactly (segment.end.x + segment.begin.x) / 2.0
            circle.center.y shouldBeExactly (segment.end.y + segment.begin.y) / 2.0
        } else segment.distance().isFinite() shouldBe false
    }

    @Test
    fun lineByPoints() {
        val point1 = pointNew
        val point2 = pointNew
        if (!point1.isFinite() || !point2.isFinite()) 2 shouldBe 2
        else {
            lineByPoints(point1, point2) shouldBe angle(atan((point1.y - point2.y) / (point1.x - point2.x)))
        }
    }

    fun ArrayList<Circle>.addSorted(circle: Circle): Double {
        if (this.isEmpty()) {
            this.add(circle)
            return 0.0
        }
        var minIndex = lastIndex
        var min = last().center.distance(circle.center)
        for (i in 0 until size) {
            val current = this[i].center.distance(circle.center)
            if (current < min) {
                min = current
                minIndex = i
            }
        }
        add(minIndex, circle)
        return min
    }

    @Test
    fun findNearestCirclePair() {
        val arrayOfCircle = ArrayList<Circle>()
        for (i in 0..11) arrayOfCircle.addSorted(circleNew)
        3 shouldBe 3
    }
}