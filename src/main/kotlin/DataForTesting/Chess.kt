package DataForTesting

import java.lang.Math.abs

/**
 * Клетка шахматной доски. Шахматная доска квадратная и имеет 8 х 8 клеток.
 * Поэтому, обе координаты клетки (горизонталь row, вертикаль column) могут находиться в пределах от 1 до 8.
 * Горизонтали нумеруются снизу вверх, вертикали слева направо.
 */
data class Square(val column: Int, val row: Int) {
    /**
     * Пример
     *
     * Возвращает true, если клетка находится в пределах доски
     */
    fun inside(): Boolean = column in 1..8 && row in 1..8

    /**
     * Простая
     *
     * Возвращает строковую нотацию для клетки.
     * В нотации, колонки обозначаются латинскими буквами от a до h, а ряды -- цифрами от 1 до 8.
     * Для клетки не в пределах доски вернуть пустую строку
     */
    fun notation(): String {
        if (!this.inside()) return ""
        return (this.column + 96).toChar() + "${this.row}"
    }
}

/**
 * Простая
 *
 * Создаёт клетку по строковой нотации.
 * В нотации, колонки обозначаются латинскими буквами от a до h, а ряды -- цифрами от 1 до 8.
 * Если нотация некорректна, бросить IllegalArgumentException
 */
fun square(notation: String): Square {
    if (!notation.matches(Regex("""([a-h][1-8])""")))
        throw IllegalArgumentException()
    val (row, column) = notation.toCharArray()
    return Square((row - 'a' + 1), (column - '0'))
}

/**
 * Простая
 *
 * Определить число ходов, за которое шахматная ладья пройдёт из клетки start в клетку end.
 * Шахматная ладья может за один ход переместиться на любую другую клетку
 * по вертикали или горизонтали.
 * Ниже точками выделены возможные ходы ладьи, а крестиками -- невозможные:
 *
 * xx.xxххх
 * xх.хxххх
 * ..Л.....
 * xх.хxххх
 * xx.xxххх
 * xx.xxххх
 * xx.xxххх
 * xx.xxххх
 *
 * Если клетки start и end совпадают, вернуть 0.
 * Если любая из клеток некорректна, бросить IllegalArgumentException().
 *
 * Пример: rookMoveNumber(Square(3, 1), Square(6, 3)) = 2
 * Ладья может пройти через клетку (3, 3) или через клетку (6, 1) к клетке (6, 3).
 */
fun rookMoveNumber(start: Square, end: Square): Int =
    when {
        !start.inside() || !end.inside() -> throw IllegalArgumentException()
        start.row != end.row && start.column != end.column -> 2
        start.row == end.row && start.column == end.column -> 0
        else -> 1
    }

/**
 * Средняя
 *
 * Вернуть список из клеток, по которым шахматная ладья может быстрее всего попасть из клетки start в клетку end.
 * Описание ходов ладьи см. предыдущую задачу.
 * Список всегда включает в себя клетку start. Клетка end включается, если она не совпадает со start.
 * Между ними должны находиться промежуточные клетки, по порядку от start до end.
 * Примеры: rookTrajectory(Square(3, 3), Square(3, 3)) = listOf(Square(3, 3))
 *          (здесь возможен ещё один вариант)
 *          rookTrajectory(Square(3, 1), Square(6, 3)) = listOf(Square(3, 1), Square(3, 3), Square(6, 3))
 *          (здесь возможен единственный вариант)
 *          rookTrajectory(Square(3, 5), Square(8, 5)) = listOf(Square(3, 5), Square(8, 5))
 * Если возможно несколько вариантов самой быстрой траектории, вернуть любой из них.
 */
fun rookTrajectory(start: Square, end: Square): List<Square> =
    when (rookMoveNumber(start, end)) {
        2 -> listOf(start, Square(start.column, end.row), end)
        1 -> listOf(start, end)
        else -> listOf(start)
    }


/**
 * Простая
 *
 * Определить число ходов, за которое шахматный слон пройдёт из клетки start в клетку end.
 * Шахматный слон может за один ход переместиться на любую другую клетку по диагонали.
 * Ниже точками выделены возможные ходы слона, а крестиками -- невозможные:
 *
 * .xxx.ххх
 * x.x.xххх
 * xxСxxxxx
 * x.x.xххх
 * .xxx.ххх
 * xxxxx.хх
 * xxxxxх.х
 * xxxxxхх.
 *
 * Если клетки start и end совпадают, вернуть 0.
 * Если клетка end недостижима для слона, вернуть -1.
 * Если любая из клеток некорректна, бросить IllegalArgumentException().
 *
 * Примеры: bishopMoveNumber(Square(3, 1), Square(6, 3)) = -1; bishopMoveNumber(Square(3, 1), Square(3, 7)) = 2.
 * Слон может пройти через клетку (6, 4) к клетке (3, 7).
 */
fun bishopMoveNumber(start: Square, end: Square): Int {
    val evenStart = abs(start.column - start.row) % 2 == 0
    val evenEnd = abs(end.column - end.row) % 2 == 0
    return when {
        !start.inside() || !end.inside() -> throw IllegalArgumentException()
        start == end -> 0
        abs(end.column - start.column) == abs(end.row - start.row) -> 1
        evenStart && evenEnd || !evenStart && !evenEnd -> 2
        else -> -1
    }
}


/**
 * Сложная
 *
 * Вернуть список из клеток, по которым шахматный слон может быстрее всего попасть из клетки start в клетку end.
 * Описание ходов слона см. предыдущую задачу.
 *
 * Если клетка end недостижима для слона, вернуть пустой список.
 *
 * Если клетка достижима:
 * - список всегда включает в себя клетку start
 * - клетка end включается, если она не совпадает со start.
 * - между ними должны находиться промежуточные клетки, по порядку от start до end.
 *
 * Примеры: bishopTrajectory(Square(3, 3), Square(3, 3)) = listOf(Square(3, 3))
 *          bishopTrajectory(Square(3, 1), Square(3, 7)) = listOf(Square(3, 1), Square(6, 4), Square(3, 7))
 *          bishopTrajectory(Square(1, 3), Square(6, 8)) = listOf(Square(1, 3), Square(6, 8))
 * Если возможно несколько вариантов самой быстрой траектории, вернуть любой из них.
 */
fun bishopTrajectory(start: Square, end: Square): List<Square> =
    when (bishopMoveNumber(start, end)) {
        0 -> listOf(start)
        1 -> listOf(start, end)
        -1 -> listOf()
        else -> listOf(start, intermediateSquare(start, end), end)
    }


fun intermediateSquare(start: Square, end: Square): Square {
    val column: Int
    val row: Int
    val squareDifferenceStart = start.column - start.row
    val squareDifferenceEnd = end.column - end.row
    val squareSumEnd = end.column + end.row
    //Вычисляю на какой диаганоли, легче без инсайд
    if ((squareDifferenceStart + squareSumEnd) / 2 <= 8) {
        column = (squareDifferenceStart + squareSumEnd) / 2
        row = column - squareDifferenceStart
    } else {
        column = (start.column + start.row + squareDifferenceEnd) / 2
        row = column - squareDifferenceEnd
    }
    return Square(column, row)
}

/**
 * Средняя
 *
 * Определить число ходов, за которое шахматный король пройдёт из клетки start в клетку end.
 * Шахматный король одним ходом может переместиться из клетки, в которой стоит,
 * на любую соседнюю по вертикали, горизонтали или диагонали.
 * Ниже точками выделены возможные ходы короля, а крестиками -- невозможные:
 *
 * xxxxx
 * x...x
 * x.K.x
 * x...x
 * xxxxx
 *
 * Если клетки start и end совпадают, вернуть 0.
 * Если любая из клеток некорректна, бросить IllegalArgumentException().
 *
 * Пример: kingMoveNumber(Square(3, 1), Square(6, 3)) = 3.
 * Король может последовательно пройти через клетки (4, 2) и (5, 2) к клетке (6, 3).
 */
fun kingMoveNumber(start: Square, end: Square): Int {
    if (!start.inside() || !end.inside())
        throw IllegalArgumentException()
    var rowMove = start.row
    var columnMove = start.column
    var moveCounter = 0
    while (rowMove != end.row ||
        columnMove != end.column) {
        if (rowMove < end.row)
            rowMove++
        else if (rowMove > end.row)
            rowMove--
        if (columnMove < end.column)
            columnMove++
        else if (columnMove > end.column)
            columnMove--
        moveCounter++
    }
    return moveCounter
}

/**
 * Сложная
 *
 * Вернуть список из клеток, по которым шахматный король может быстрее всего попасть из клетки start в клетку end.
 * Описание ходов короля см. предыдущую задачу.
 * Список всегда включает в себя клетку start. Клетка end включается, если она не совпадает со start.
 * Между ними должны находиться промежуточные клетки, по порядку от start до end.
 * Примеры: kingTrajectory(Square(3, 3), Square(3, 3)) = listOf(Square(3, 3))
 *          (здесь возможны другие варианты)
 *          kingTrajectory(Square(3, 1), Square(6, 3)) = listOf(Square(3, 1), Square(4, 2), Square(5, 2), Square(6, 3))
 *          (здесь возможен единственный вариант)
 *          kingTrajectory(Square(3, 5), Square(6, 2)) = listOf(Square(3, 5), Square(4, 4), Square(5, 3), Square(6, 2))
 * Если возможно несколько вариантов самой быстрой траектории, вернуть любой из них.
 */
fun kingTrajectory(start: Square, end: Square): List<Square> {
    when (kingMoveNumber(start, end)) {
        0 -> return listOf(start)
        1 -> return listOf(start, end)
    }
    val kingTrajectory = mutableListOf(Square(start.column, start.row))
    var rowMove = start.row
    var columnMove = start.column
    while (columnMove != end.column ||
        rowMove != end.row) {
        if (rowMove < end.row)
            rowMove++
        else if (rowMove > end.row)
            rowMove--
        if (columnMove < end.column)
            columnMove++
        else if (columnMove > end.column)
            columnMove--
        kingTrajectory.add(Square(columnMove, rowMove))
    }
    return kingTrajectory
}


/**
 * Сложная
 *
 * Определить число ходов, за которое шахматный конь пройдёт из клетки start в клетку end.
 * Шахматный конь одним ходом вначале передвигается ровно на 2 клетки по горизонтали или вертикали,
 * а затем ещё на 1 клетку под прямым углом, образуя букву "Г".
 * Ниже точками выделены возможные ходы коня, а крестиками -- невозможные:
 *
 * .xxx.xxx
 * xxKxxxxx
 * .xxx.xxx
 * x.x.xxxx
 * xxxxxxxx
 * xxxxxxxx
 * xxxxxxxx
 * xxxxxxxx
 *
 * Если клетки start и end совпадают, вернуть 0.
 * Если любая из клеток некорректна, бросить IllegalArgumentException().
 *
 * Пример: knightMoveNumber(Square(3, 1), Square(6, 3)) = 3.
 * Конь может последовательно пройти через клетки (5, 2) и (4, 4) к клетке (6, 3).
 */
fun knightMoveNumber(start: Square, end: Square): Int = knightGraph().bfs(start.notation(), end.notation())

/**
 * Добавление каждой клетки как вершины
 */
fun vertexOnBoard(): Graph {
    val ktGraph = Graph()
    for (row in 1..8) {
        for (col in 1..8) {
            ktGraph.addVertex(Square(row, col).notation())
        }
    }
    return ktGraph
}

/**
 * Соединение всех возможных вершин
 */
fun knightGraph(): Graph {
    val ktGraph = vertexOnBoard()
    for (row in 1..8) {
        for (col in 1..8) {
            val nodeId = Square(row, col).notation()
            val newPositions = genLegalMoves(row, col)
            for (e in newPositions) {
                val nid = Square(e.first, e.second).notation()
                ktGraph.connect(nodeId, nid)
            }
        }
    }
    return ktGraph
}

/**
 * Генерирование всех возможных ходов для клетки
 */
fun genLegalMoves(x: Int, y: Int): List<Pair<Int, Int>> {
    val newMoves = mutableListOf<Pair<Int, Int>>()
    val moveOffsets = listOf(
        Pair(-1, -2),
        Pair(-1, 2),
        Pair(-2, -1),
        Pair(-2, 1),
        Pair(1, -2),
        Pair(1, 2),
        Pair(2, -1),
        Pair(2, 1)
    )
    for ((first, second) in moveOffsets) {
        val newX = x + first
        val newY = y + second
        if (Square(newX, newY).inside())
            newMoves.add(Pair(newX, newY))
    }
    return newMoves
}

/**
 * Очень сложная
 *
 * Вернуть список из клеток, по которым шахматный конь может быстрее всего попасть из клетки start в клетку end.
 * Описание ходов коня см. предыдущую задачу.
 * Список всегда включает в себя клетку start. Клетка end включается, если она не совпадает со start.
 * Между ними должны находиться промежуточные клетки, по порядку от start до end.
 * Примеры:
 *
 * knightTrajectory(Square(3, 3), Square(3, 3)) = listOf(Square(3, 3))
 * здесь возможны другие варианты)
 * knightTrajectory(Square(3, 1), Square(6, 3)) = listOf(Square(3, 1), Square(5, 2), Square(4, 4), Square(6, 3))
 * (здесь возможен единственный вариант)
 * knightTrajectory(Square(3, 5), Square(5, 6)) = listOf(Square(3, 5), Square(5, 6))
 * (здесь опять возможны другие варианты)
 * knightTrajectory(Square(7, 7), Square(8, 8)) =
 *     listOf(Square(7, 7), Square(5, 8), Square(4, 6), Square(6, 7), Square(8, 8))
 *
 * Если возможно несколько вариантов самой быстрой траектории, вернуть любой из них.
 */
fun knightTrajectory(start: Square, end: Square): List<Square> {
    //Список ходов
    val listOfMoves = mutableListOf(start)
    //Если одинаковые возвращаю любой
    if (start.notation() == end.notation()) return listOfMoves
    //Смотрю за сколько ходов можно пройти
    val moves = knightMoveNumber(start, end)
    //Если дойдет за один ход то вывожу сразу
    if (moves == 1) return listOf(start, end)
    //Этим списком смотрю какие ходы можносделать из клетки
    var allMoves = genLegalMoves(start.column, start.row)
    //Нужно для нахождение клетки с кратчайшим количеством ходов до конечной
    var min = 9
    //Выбор клетки из списка клеток из которых можно дойти до конечной
    var choose = -1
    for (i in 1..moves) {
        val listV = mutableListOf<Square>()
        for ((first, second) in allMoves) {
            val square = Square(first, second)
            val s = knightMoveNumber(square, end)
            if (s != -1 && s < min) {
                listV.add(square)
                min = s
                choose++
            }
        }
        listOfMoves.add(listV[choose])
        choose = -1
        allMoves = genLegalMoves(listOfMoves.last().column, listOfMoves.last().row)
    }
    return listOfMoves
}