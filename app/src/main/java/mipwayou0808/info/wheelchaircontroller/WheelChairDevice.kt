package mipwayou0808.info.wheelchaircontroller

import java.util.*

/**
 * Device information
 */
data class WheelChairDevice(
        val macAddress: String = "AC:2B:6E:33:EA:AE",
        val notificationUUID: UUID = UUID.fromString("26bd0805-3c04-4327-8441-a1e3e7c9e691"),
        val writeUUID: UUID = UUID.fromString("4eea7a79-65f3-4056-9155-4aa0ce4a53d9"))