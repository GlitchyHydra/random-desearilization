import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlin.random.Random

@ImplicitReflectionSerializer
fun main(args: Array<String>) {
    //Int.MIN_VALUE.rangeTo(Int.MAX_VALUE).random()
    //val randomVal = List(10) { Random.nextInt(0, 100) }
    for (i in 0..1150) {
        val d = Json.parse(BooleanSerializer(), "")
        println(d.primitive)
    }
}