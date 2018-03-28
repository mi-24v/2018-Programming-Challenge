package mipwayou0808.info.wheelchaircontroller

/**
 * Command type definition
 */
enum class WheelChairCommandType(payload: Byte) {
    STOP(0),
    FORWARD(1),
    LEFT(2),
    BACK(3),
    RIGHT(4)
}