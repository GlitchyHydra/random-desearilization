fun main(args: Array<String>) {
    val t = PrimitiveRandom<Char, Boolean>()
    println( t.getRandomList())
    println( t.getRandomSet())
    println( t.getRandomMap())
}

inline fun <reified T: Any, E:Any> PrimitiveRandom<T, E>.getRandomList(): List<T>? =
    getRandomList(T::class)

inline fun <reified T: Any, E: Any> PrimitiveRandom<T, E>.getRandomSet(): Set<T>? =
    getRandomSet(T::class)

inline fun <reified T: Any,reified E: Any> PrimitiveRandom<T, E>.getRandomMap(): Map<T, E>? =
    getRandomMap(T::class, E::class)