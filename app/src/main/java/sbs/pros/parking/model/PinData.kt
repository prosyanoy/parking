package sbs.pros.parking.model

import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.MapObject

class PinData(
    val parking: MapObject,
    val hour_cost: Int,
    val address: String,
    val point: Point,
    val id: Int,
    val secure: Int,
    val around_the_clock: Int,
    val ev: Int,
    val disabled: Int,
    val places: Int,
    val free_places: Int,
    var isSelected: Boolean = false
)
