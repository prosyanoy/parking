package sbs.pros.parking.utils

import android.content.Context
import android.graphics.*
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.Map
import kotlin.math.abs


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

fun drawLocationPoint(color: Int = Color.rgb(13, 174, 252)): Bitmap {

    val bitmap = Bitmap.createBitmap(100, 120, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val shadowPaint = Paint()
    shadowPaint.shader = RadialGradient (
        50F,
        70F,
        50F,
        intArrayOf(Color.argb(100, 0,0,0), Color.argb(0,0,0,0)),
        floatArrayOf(0F, 1F),
        Shader.TileMode.REPEAT)

    canvas.drawCircle(50F,70F,50F, shadowPaint)

    val backgroundPaint = Paint()
    backgroundPaint.color = Color.WHITE
    canvas.drawCircle(50F,50F,50F, backgroundPaint)

    val paint = Paint()
    backgroundPaint.color = color
    canvas.drawCircle(50F,50F,40F, backgroundPaint)
    return bitmap
}

 fun drawSimpleBitmap(
    number: String,
    context: Context,
    backgroundColor: Int = Color.rgb(13, 174, 252)
): Bitmap {

    val FONT_SIZE = 22f
    val MARGIN_SIZE = 3f
    val STROKE_SIZE = 3f
    val POINTS_ZOOM = 13 //9-12.99 (точки)


    val textPaint = Paint()
    textPaint.textSize = FONT_SIZE * context.resources.displayMetrics.density
    textPaint.textAlign = Paint.Align.CENTER
    textPaint.style = Paint.Style.FILL
    textPaint.isAntiAlias = true
    textPaint.color = Color.WHITE
    val widthF = textPaint.measureText(number)
    val textMetrics = textPaint.fontMetrics
    val heightF = abs(textMetrics.bottom) + abs(textMetrics.top)

    val height = heightF + 0.5F
    val width = widthF-2F+ height

    val ss = STROKE_SIZE * context.resources.displayMetrics.density
    val externalHeight = height + 2*ss

    val tt = ss*2

    val externalShape = Path()
    externalShape.moveTo(tt, tt)
    val leftExternalCircle = RectF(tt, tt, externalHeight+tt, externalHeight+tt)
    externalShape.arcTo(leftExternalCircle, 90F, 180F)
    val x1 = externalHeight/2+widthF-2F
    externalShape.lineTo(x1+tt, tt)
    val rightExternalCircle = RectF(x1-externalHeight/2+tt, tt, x1+externalHeight/2+tt, externalHeight+tt)
    externalShape.arcTo(rightExternalCircle, 270F, 180F)
    externalShape.lineTo(externalHeight/2+tt, externalHeight+tt)
    externalShape.close()

    val internalShape = Path()
    internalShape.moveTo(ss+tt, ss+tt)
    val leftInternalCircle = RectF(ss+tt, ss+tt, height+ss+tt, height+ss+tt)
    internalShape.arcTo(leftInternalCircle, 90F, 180F)
    val x2 = height/2+widthF-2F
    internalShape.lineTo(x2+ss+tt, ss+tt)
    val rightInternalCircle = RectF(x2-height/2+ss+tt, ss+tt, x2+height/2+ss+tt, height+ss+tt)
    internalShape.arcTo(rightInternalCircle, 270F, 180F)
    internalShape.lineTo(height/2+ss+tt, height+ss+tt)
    internalShape.close()

    val bitmap = Bitmap.createBitmap((width+2*ss+2*tt).toInt(), (externalHeight+2*tt).toInt(), Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val backgroundPaint = Paint()
    backgroundPaint.isAntiAlias = true

    val shaderPaint = Paint()
    shaderPaint.shader = LinearGradient(
        0F,
        tt,
        0F,
        0F,
        Color.argb(100, 0,0,0),
        Color.argb(0,0,0,0),
        Shader.TileMode.REPEAT
    )
    val topRect = RectF(tt+externalHeight/2, 0F, x1+tt,tt)
    canvas.drawRect(topRect, shaderPaint)

    shaderPaint.shader = LinearGradient(
        0F,
        tt+externalHeight,
        0F,
        2*tt+externalHeight,
        Color.argb(100, 0,0,0),
        Color.argb(0, 0,0,0),
        Shader.TileMode.REPEAT
    )
    val bottomRect = RectF(tt+externalHeight/2, tt+externalHeight, x1+tt,2*tt+externalHeight)
    canvas.drawRect(bottomRect, shaderPaint)

    shaderPaint.shader = RadialGradient (
        tt+externalHeight/2,
        tt+externalHeight/2,
        tt+externalHeight/2,
        intArrayOf(Color.argb(100, 0,0,0), Color.argb(0,0,0,0)),
        floatArrayOf(1-tt/(tt+externalHeight), 1F),
        Shader.TileMode.REPEAT)
    canvas.drawCircle(tt+externalHeight/2, tt+externalHeight/2, tt+externalHeight/2, shaderPaint)

    shaderPaint.shader = RadialGradient (
        tt+externalHeight/2+widthF-2F,
        tt+externalHeight/2,
        tt+externalHeight/2,
        intArrayOf(Color.argb(100, 0,0,0), Color.argb(0,0,0,0)),
        floatArrayOf(1-tt/(tt+externalHeight), 1F),
        Shader.TileMode.REPEAT)
    canvas.drawCircle(tt+externalHeight/2+widthF-2F, tt+externalHeight/2, tt+externalHeight/2, shaderPaint)


    backgroundPaint.color = Color.WHITE
    canvas.drawPath(externalShape, backgroundPaint)

    backgroundPaint.color = backgroundColor
    canvas.drawPath(internalShape, backgroundPaint)

    canvas.drawText(
        number, (width / 2+ss+tt),
        externalHeight / 2 +tt - (textMetrics.ascent + textMetrics.descent) / 2,
        textPaint
    )
    return bitmap
}