package mipwayou0808.info.wheelchaircontroller

/**
 * Command type definition
 */
enum class WheelChairCommandType(val payload: Byte) {
    STOP(0),
    FORWARD(1),
    LEFT(2),
    BACK(3),
    RIGHT(4);

    override fun toString(): String {
        return when(this){
            STOP -> {
                "STOP command"
            }
            FORWARD -> {
                "FORWARD command"
            }
            LEFT -> {
                "LEFT command"
            }
            BACK -> {
                "BACK command"
            }
            RIGHT -> {
                "RIGHT command"
            }
        }
    }
}
    fun getCommandTypeFromInt(int: Int):WheelChairCommandType{
        return when(int){
            0 -> {
                WheelChairCommandType.STOP
            }
            1 -> {
                WheelChairCommandType.FORWARD
            }
            2 -> {
                WheelChairCommandType.LEFT
            }
            3 -> {
                WheelChairCommandType.BACK
            }
            4 -> {
                WheelChairCommandType.RIGHT
            }
            else -> {
                WheelChairCommandType.STOP
            }
        }
    }
