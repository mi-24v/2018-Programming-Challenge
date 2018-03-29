package mipwayou0808.info.wheelchaircontroller

import android.content.Context
import java.util.*

/**
 * Common interface to control wheelchair
 */
class WheelChairController(context: Context) {

    fun getTargetDevice(): String {
        return "AA:BB:CC:DD:FF"//TODO replace actually using
    }

    fun getTargetUUID(): UUID{
        return UUID.fromString("26bd0805-3c04-4327-8441-a1e3e7c9e691")//TODO replace actually using
    }

}