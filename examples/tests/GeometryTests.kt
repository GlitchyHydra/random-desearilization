
import io.kotlintest.matchers.doubles.*
import io.kotlintest.matchers.numerics.*
import io.kotlintest.*
import io.kotlintest.specs.AnnotationSpec
import kotlinx.serialization.Serializable
import kotlinx.serialization.decode
import java.lang.IllegalArgumentException

class GeometryTests : AnnotationSpec() {
    private val randomDecoder = RandomDecoder()
    private val pointSerializer = Point.serializer()
    private val triangleSerializer = Triangle.serializer()
    private val circleSerializer = Circle.serializer()
    private val segmentSerializer = Segment.serializer()
    private val pointNew: Point get() = randomDecoder.decode(pointSerializer)
    private val triangleNew: Triangle get() = randomDecoder.decode(triangleSerializer)
    private val circleNew: Circle get() = randomDecoder.decode(circleSerializer)
    private val segmentNew: Segment get() = randomDecoder.decode(segmentSerializer)

    private fun Point.isNotFinite(): Boolean = !this.x.isFinite() || !this.y.isFinite()

    @Test
    fun pointDistance() {
        for (i in 1..100) {
            val a = pointNew
            val b = pointNew
            val c = pointNew
            if (a.isNotFinite() || b.isNotFinite() || c.isNotFinite())
                (a.distance(b).isFinite() && a.distance(c).isFinite()) shouldBe false
            else {
                a.distance(b).isNaN() shouldBe false
                a.distance(a) shouldBe 0.0
                b.distance(b) shouldBe 0.0
                c.distance(c) shouldBe 0.0
                val distAB = a.distance(b)
                val distBC = b.distance(c)
                val distAC = a.distance(c)
                if (distAB > distBC && distAC > distAB) distAC shouldBeGreaterThan distBC
            }
        }
    }

    private fun Triangle.isNotFinite() =
        this.a.isNotFinite() || this.b.isNotFinite() || this.c.isNotFinite()

    private fun checkTwoTriangles(triangle: Triangle, points: Triple<Point, Point, Point>) {
        val (a, b, c) = points
        if (triangle.contains(a) && triangle.contains(b) && triangle.contains(c)) {
            val innerTriangle = Triangle(a, b, c)
            triangle.area() shouldBeGreaterThan innerTriangle.area()
            triangle.halfPerimeter() shouldBeGreaterThan innerTriangle.area()
        } else {
            val triangleByPoints = Triangle(a, b, c)
            triangle.area() shouldNotBe triangleByPoints.area()
            triangle.halfPerimeter() shouldNotBe triangleByPoints.halfPerimeter()
        }
    }

    @Test
    fun triangle() {
        for (i in 0..100) {
            val (a, b, c) = Triple(pointNew, pointNew, pointNew)
            val triangle = triangleNew
            if (triangle.isNotFinite()) triangle.area().isFinite() shouldBe false
            else {
                triangle.area().isFinite() shouldBe true
                if (!a.isNotFinite() && !b.isNotFinite() && !c.isNotFinite()) {
                    checkTwoTriangles(triangle, Triple(a, b, c))
                }
            }
        }
    }

    private fun Circle.isFinite() = !this.center.isNotFinite() || this.radius.isFinite()


    @Test
    fun circle() {
        for (i in 0..100) {
            val circle = circleNew
            val (a, b, c) = Triple(pointNew, pointNew, pointNew)
            if (circle.isFinite()) {
                if (!a.isNotFinite() && !b.isNotFinite() && !c.isNotFinite()) {
                    val circleByDiameter1 = circleByDiameter(Segment(a, b))
                    val circleByDiameter2 = circleByDiameter(Segment(b, c))
                    val circleByDiameter3 = circleByDiameter(Segment(a, c))
                    val circleByThreePoints = circleByThreePoints(a, b, c)
                    if (circle.contains(a) && circle.contains(b) && circle.contains(c)) {
                        circle.radius shouldBeGreaterThanOrEqual circleByThreePoints.radius
                        circle.radius shouldBeGreaterThanOrEqual circleByDiameter1.radius
                        circle.radius shouldBeGreaterThanOrEqual circleByDiameter2.radius
                        circle.radius shouldBeGreaterThanOrEqual circleByDiameter3.radius
                    } else {
                        circle.radius shouldNotBe circleByThreePoints.radius
                        circle.radius shouldNotBe circleByDiameter1.radius
                        circle.radius shouldNotBe circleByDiameter2.radius
                        circle.radius shouldNotBe circleByDiameter3.radius
                    }
                }
            }
        }
    }

    @Serializable
    data class VarietyOfPoints(val listOfPoints: List<Point>)

    @Test
    fun diameterTest() {
        val serializerOfPoints = VarietyOfPoints.serializer()
        for (i in 0..100) {
            val varargPoints = randomDecoder.decode(serializerOfPoints).listOfPoints.toTypedArray()
            try {
                varargPoints.size shouldBeGreaterThanOrEqual 2
                val maxSegment = diameter(*varargPoints)
                maxSegment shouldBe maxSegment
            } catch (e: IllegalArgumentException) {
                varargPoints.size shouldBeLessThan 2
            }
        }
    }

}