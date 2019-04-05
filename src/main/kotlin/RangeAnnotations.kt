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

@SerialInfo
@Target(AnnotationTarget.PROPERTY)
annotation class RangeLong(val min: Long = Long.MIN_VALUE, val max: Long = Long.MAX_VALUE)

@SerialInfo
@Target(AnnotationTarget.PROPERTY)
annotation class RangeFloat(val min: Float = 1.175494351e-38F, val max: Float = 3.402823466e+38F)

@Suppress("FLOAT_LITERAL_CONFORMS_INFINITY")
@SerialInfo
@Target(AnnotationTarget.PROPERTY)
annotation class RangeDouble(val min: Double = -1.79769313486232E+308, val max: Double = 1.79769313486232E+308)

@SerialInfo
@Target(AnnotationTarget.PROPERTY)
annotation class RangeChar(val min: Char = Char.MIN_VALUE, val max: Char = Char.MAX_VALUE)