package mipwayou0808.info.wheelchaircontroller

import io.reactivex.Observable
import retrofit2.http.GET

/**
 * Api information
 */
interface ApiStanderd {

    @GET("command")
    fun postWheelChairCommand(): Observable<Void>

}