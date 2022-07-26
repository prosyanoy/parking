package sbs.pros.parking.utils

import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.Map


    fun Map.moveWithBottomPadding(
        cameraPosition: CameraPosition,
        animation: Animation,
        callback: Map.CameraCallback?,
        bottomPadding: Int,
        mapHeight: Int
    ) {
        val visibleRegion = visibleRegion(cameraPosition)
        val lngDiff = visibleRegion.topLeft.latitude - visibleRegion.bottomLeft.latitude
        val ratio = lngDiff / mapHeight
        val newTarget = Point(
            cameraPosition.target.latitude - (bottomPadding / 2 * ratio),
            cameraPosition.target.longitude
        )

        move(
            CameraPosition(newTarget, cameraPosition.zoom, cameraPosition.azimuth, cameraPosition.tilt),
            animation, callback
        )
    }
