# Random generator by deserialization using Kotlinx-Serialization library
If you need a random generator for your propery - based tests, use this decoder.
It's a random decoder, that you can give to deserialize method of your class serializer.
It will generate object with random values in it. 
To use it in your project write this code
```
    val randomDecoder = RandomDecoder()
    val myObject = randomDecoder.decode(DataTransient.serializer())
```
Generator will give you these values in common to test specific cases.
- empty collections;
- -1, 0, 1 in Int, Short, Long; 
- infinity, NaN in Double, Float;
- english alphabet characters in chars and strings.

Also you can generate random values in specific range by setting an annotation to your properties in class like this.
```
@Serializable
data class BinaryTree(@RangeInt(55,155) val binaryTree: BinaryTree<Int>)
```
You can use any of this annotations the same way.
```
annotation class RangeByte
annotation class RangeShort
annotation class RangeInt
annotation class RangeLong
annotation class RangeFloat
annotation class RangeDouble
annotation class RangeChar
annotation class RangeEnum
annotation class RangeString
```
