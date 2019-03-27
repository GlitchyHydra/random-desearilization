import kotlinx.serialization.SerialInfo

@SerialInfo
@Target(AnnotationTarget.PROPERTY)
annotation class RangeByte(val min: Byte = Byte.MIN_VALUE, val max: Byte = Byte.MAX_VALUE)

@SerialInfo
@Target(AnnotationTarget.PROPERTY)
annotation class RangeShort(val min: Short = Short.MIN_VALUE, val max: Short = Short.MAX_VALUE)

@SerialInfo
@Target(AnnotationTarget.PROPERTY)
annotation class RangeInt(val min: Int = Int.MIN_VALUE, val max: Int = Int.MAX_VALUE)

/*@SerialInfo
@Target(AnnotationTarget.PROPERTY)
annotation class RangeFloat(val min: Float = Float.MIN_VALUE, val max: Float = Float.MAX_VALUE)

@SerialInfo
@Target(AnnotationTarget.PROPERTY)
annotation class RangeDouble(val min: Double = Double.MIN_VALUE, val max: Int = Double.MAX_VALUE)*/

@SerialInfo
@Target(AnnotationTarget.PROPERTY)
annotation class Range(val min: Int , val max: Int)

@SerialInfo
@Target(AnnotationTarget.PROPERTY)
annotation class RangeChar(val min: Char = Char.MIN_VALUE, val max: Char = Char.MAX_VALUE)