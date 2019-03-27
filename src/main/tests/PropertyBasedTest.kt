import io.kotlintest.matchers.between
import io.kotlintest.matchers.collections.shouldNotHaveSize
import io.kotlintest.matchers.haveLength
import io.kotlintest.matchers.numerics.shouldBeGreaterThanOrEqual
import io.kotlintest.properties.assertAll
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.*

val dataSerializer = Data.serializer()
val randomDecoder = RandomDecoder()

class TestSpec : StringSpec( {

    val myData = dataSerializer.deserialize(randomDecoder)
    val sizeOfList = myData.l.size

})

class AnnotationSpecExample : AnnotationSpec() {
    var newData: Data = dataSerializer.deserialize(randomDecoder)
    @BeforeEach
    fun beforeTest() {
        newData = dataSerializer.deserialize(randomDecoder)
        println(newData.l)
    }

    @Test
    fun test1() {
        var countOfNull = 0
        var countOfNotNull = 0
        for (i in 0 until newData.l.size) {
            if (newData.l[i] != null) countOfNotNull++
            else countOfNull++
        }
        println(countOfNotNull)
        println(countOfNull)
        countOfNotNull shouldBeGreaterThanOrEqual countOfNull
    }

    @Test
    fun test2() {
        for (i in 1..100) {
            newData = dataSerializer.deserialize(randomDecoder)
            test1()
        }
    }
}