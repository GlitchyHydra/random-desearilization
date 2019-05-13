import kotlinx.serialization.Serializable
import java.lang.Math.*


fun sqr(x: Double) = x * x

/**
 * Точка на плоскости
 */
@Serializable
data class Point(val x: Double, val y: Double) {
    /**
     * Пример
     *
     * Рассчитать (по известной формуле) расстояние между двумя точками
     */
    fun distance(other: Point): Double = Math.sqrt(
        sqr(x - other.x) + sqr(
            y - other.y
        )
    )

}

/**
 * Треугольник, заданный тремя точками (a, b, c, см. constructor ниже).
 * Эти три точки хранятся в множестве points, их порядок не имеет значения.
 */
@Serializable
class Triangle private constructor(private val points: Set<Point>) {

    private val pointList = points.toList()

    val a: Point get() = pointList[0]

    val b: Point get() = pointList[1]

    val c: Point get() = pointList[2]

    constructor(a: Point, b: Point, c: Point) : this(linkedSetOf(a, b, c))

    /**
     * Пример: полупериметр
     */
    fun halfPerimeter() = (a.distance(b) + b.distance(c) + c.distance(a)) / 2.0

    /**
     * Пример: площадь
     */
    fun area(): Double {
        val p = halfPerimeter()
        return Math.sqrt(p * (p - a.distance(b)) * (p - b.distance(c)) * (p - c.distance(a)))
    }

    /**
     * Пример: треугольник содержит точку
     */
    fun contains(p: Point): Boolean {
        val abp = Triangle(a, b, p)
        val bcp = Triangle(b, c, p)
        val cap = Triangle(c, a, p)
        return abp.area() + bcp.area() + cap.area() <= area()
    }

    override fun equals(other: Any?) = other is Triangle && points == other.points

    override fun hashCode() = points.hashCode()

    override fun toString() = "Triangle(a = $a, b = $b, c = $c)"
}

/**
 * Окружность с заданным центром и радиусом
 */
@Serializable
data class Circle(val center: Point, val radius: Double) {
    /**
     * Простая
     *
     * Рассчитать расстояние между двумя окружностями.
     * Расстояние между непересекающимися окружностями рассчитывается как
     * расстояние между их центрами минус сумма их радиусов.
     * Расстояние между пересекающимися окружностями считать равным 0.0.
     */
    fun distance(other: Circle): Double {
        val distanceBetweenCenter = Point(other.center.x, other.center.y).
            distance(Point(center.x, center.y))
        if (distanceBetweenCenter > radius + other.radius)
            return distanceBetweenCenter - (other.radius + radius)
        return 0.0
    }

    /**
     * Тривиальная
     *
     * Вернуть true, если и только если окружность содержит данную точку НА себе или ВНУТРИ себя
     */
    fun contains(p: Point): Boolean =
        pow((p.x - this.center.x), 2.0) + pow((p.y - this.center.y), 2.0) <= sqr(radius)
}

/**
 * Отрезок между двумя точками
 */
@Serializable
data class Segment(val begin: Point, val end: Point) {
    override fun equals(other: Any?) =
        other is Segment && (begin == other.begin && end == other.end || end == other.begin && begin == other.end)

    override fun hashCode() =
        begin.hashCode() + end.hashCode()
}

/**
 * Средняя
 *
 * Дано множество точек. Вернуть отрезок, соединяющий две наиболее удалённые из них.
 * Если в множестве менее двух точек, бросить IllegalArgumentException
 */

fun diameter(vararg points: Point): Segment {
    if (points.size < 2) throw IllegalArgumentException()
    var max = 0.0
    var maxSegment = Segment(points[0], points[1])
    for (i in 0..points.lastIndex) {
        for (j in 0..points.lastIndex) {
            val distance = points[i].distance(points[j])
            if (max < distance) {
                max = distance
                maxSegment = Segment(points[i], points[j])
            }
        }
    }
    return maxSegment
}

/**
 * Простая
 *
 * Построить окружность по её диаметру, заданному двумя точками
 * Центр её должен находиться посередине между точками, а радиус составлять половину расстояния между ними
 */
fun circleByDiameter(diameter: Segment): Circle {
    val center = Point(
        (diameter.end.x + diameter.begin.x) / 2,
        (diameter.end.y + diameter.begin.y) / 2
    )
    val radius = diameter.begin.distance(diameter.end) / 2
    return Circle(center, radius)
}

/**
 * Прямая, заданная точкой point и углом наклона angle (в радианах) по отношению к оси X.
 * Уравнение прямой: (y - point.y) * cos(angle) = (x - point.x) * sin(angle)
 * или: y * cos(angle) = x * sin(angle) + b, где b = point.y * cos(angle) - point.x * sin(angle).
 * Угол наклона обязан находиться в диапазоне от 0 (включительно) до PI (исключительно).
 */
class Line private constructor(val b: Double, val angle: Double) {
    init {
        assert(angle >= 0 && angle < Math.PI) { "Incorrect line angle: $angle" }
    }

    constructor(point: Point, angle: Double) : this(point.y * Math.cos(angle) - point.x * Math.sin(angle), angle)

    /**
     * Средняя
     *
     * Найти точку пересечения с другой линией.
     * Для этого необходимо составить и решить систему из двух уравнений (каждое для своей прямой)
     */
    fun crossPoint(other: Line): Point {
        val x: Double = (other.b / cos(other.angle) - b / cos(angle)) /
                (sin(angle) / cos(angle) - sin(other.angle) / cos(other.angle))
        val y: Double
        if (angle == PI / 2) {
            y = -b * sin(other.angle) / cos(other.angle) + other.b / cos(other.angle)
            return Point(-b, y)
        }
        if (other.angle == PI / 2) {
            y = -other.b * sin(angle) / cos(angle) + b / cos(angle)
            return Point(-other.b, y)
        }
        y = x * sin(angle) / cos(angle) + b / cos(angle)
        return Point(x, y)
    }

    override fun equals(other: Any?) = other is Line && angle == other.angle && b == other.b

    override fun hashCode(): Int {
        var result = b.hashCode()
        result = 31 * result + angle.hashCode()
        return result
    }

    override fun toString() = "Line(${cos(angle)} * y = ${sin(angle)} * x + $b)"
}

/**
 * Средняя
 *
 * Построить прямую по отрезку
 */
fun lineBySegment(s: Segment): Line =
    lineByPoints(s.begin, s.end)

/**
 * Средняя
 *
 * Построить прямую по двум точкам
 */
fun lineByPoints(a: Point, b: Point): Line {
    val tan = (a.y - b.y) / (a.x - b.x)
    val angle = angle(atan(tan))
    return Line(a, angle)
}

/**
 * Сложная
 *
 * Построить серединный перпендикуляр по отрезку или по двум точкам
 */
fun bisectorByPoints(a: Point, b: Point): Line {
    val point = middlePoint(a, b)
    if (a.x == b.x) return Line(point, 0.0)
    val line = lineByPoints(a, b)
    val angleOfLine = angle(PI / 2 + line.angle)
    return Line(point, angleOfLine)
}

fun middlePoint(begin: Point, end: Point): Point {
    val x = (begin.x + end.x) / 2
    val y = (begin.y + end.y) / 2
    return Point(x, y)
}

fun angle(angle: Double):Double = when {
    angle >= PI -> angle - PI
    angle < 0.0 -> angle + PI
    else -> angle
}

/**
 * Средняя
 *
 * Задан список из n окружностей на плоскости. Найти пару наименее удалённых из них.
 * Если в списке менее двух окружностей, бросить IllegalArgumentException
 */
fun findNearestCirclePair(vararg circles: Circle): Pair<Circle, Circle> {
    if (circles.size < 2) throw  IllegalArgumentException()
    var min = circles[0].distance(circles[1])
    var pairOfMin = Pair(circles[0], circles[1])
    for (i in 0..circles.lastIndex) {
        for (j in 0..circles.lastIndex) {
            val distance = circles[i].distance(circles[j])
            if (i != j) {
                if (distance < min) {
                    pairOfMin = Pair(circles[i], circles[j])
                    min = distance
                }
            }
        }
    }
    return pairOfMin
}

/**
 * Сложная
 *
 * Дано три различные точки. Построить окружность, проходящую через них
 * (все три точки должны лежать НА, а не ВНУТРИ, окружности).
 * Описание алгоритмов см. в Интернете
 * (построить окружность по трём точкам, или
 * построить окружность, описанную вокруг треугольника - эквивалентная задача).
 */
fun circleByThreePoints(a: Point, b: Point, c: Point): Circle {
    val perpendicularToChord1 = bisectorByPoints(a, b)
    val perpendicularToChord2 = bisectorByPoints(b, c)
    val center = perpendicularToChord1.crossPoint(perpendicularToChord2)
    return Circle(center, center.distance(b))
}

/**
 * Очень сложная
 *
 * Дано множество точек на плоскости. Найти круг минимального радиуса,
 * содержащий все эти точки. Если множество пустое, бросить IllegalArgumentException.
 * Если множество содержит одну точку, вернуть круг нулевого радиуса с центром в данной точке.
 *
 * Примечание: в зависимости от ситуации, такая окружность может либо проходить через какие-либо
 * три точки данного множества, либо иметь своим диаметром отрезок,
 * соединяющий две самые удалённые точки в данном множестве.
 */
fun minContainingCircle(vararg points: Point): Circle {
    if (points.isEmpty()) throw IllegalArgumentException()
    else if (points.size == 1) return Circle(points[0], 0.0)
    val diameter = diameter(*points)
    val circleByDiameter = circleByDiameter(diameter)
    var containingPoint = false
    for (i in points) {
        containingPoint = circleByDiameter.contains(i)
        if (!containingPoint) break
    }
    if (containingPoint) return Circle(circleByDiameter.center, circleByDiameter.radius)
    var farthestPoint = points[0]
    var maxDistance = farthestPoint.distance(circleByDiameter.center)
    for (i in 1 until points.size) {
        if (points[i] != diameter.begin && points[i] != diameter.end && !circleByDiameter.contains(points[i])) {
            val currentDistance = points[i].distance(circleByDiameter.center)
            if (currentDistance > maxDistance) {
                maxDistance = currentDistance
                farthestPoint = points[i]
            }
        }
    }
    return circleByThreePoints(diameter.begin, diameter.end, farthestPoint)
}