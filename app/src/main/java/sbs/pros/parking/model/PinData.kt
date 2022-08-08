package sbs.pros.parking.model

import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.MapObject

class PinData (
    val parking: MapObject,
    val hour_cost: Int,
    val address: String,
    val point: Point,
    var isSelected: Boolean = false
)
