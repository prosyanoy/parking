package sbs.pros.parking


import android.content.Context
import android.graphics.*
import android.graphics.Color.rgb
import android.os.Bundle
import android.util.Log

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.LinearRing
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polygon
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.map.*
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import sbs.pros.parking.bottom_sheet.BottomSheetDialog
import sbs.pros.parking.model.PinData
import sbs.pros.parking.utils.moveWithBottomPadding
import kotlin.math.abs


class MainActivity : AppCompatActivity(), ClusterListener, ClusterTapListener {

    private val MAPKIT_API_KEY = "024ae79a-58dc-4626-ac7e-1ba6ba83121e"

    private val url = "https://pros.sbs/parking/getting.php"

    private val TARGET_LOCATION = Point(43.590097, 39.721887)

    private var mapView: MapView? = null

    private var selectedObject: MapObject? = null

    private var clusterizedCollection: ClusterizedPlacemarkCollection? = null


    object MapKitInitializer {
        private var initialized = false
        fun initialize(apiKey: String, context: Context) {
            if (initialized) {
                return
            }
            MapKitFactory.setApiKey(apiKey)
            MapKitFactory.initialize(context)
            initialized = true
        }
    }

    private fun drawSimpleBitmap(
        number: String,
        context: Context,
        backgroundColor: Int = rgb(13, 174, 252)
    ): Bitmap {
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
        val leftExternalCircle = RectF(tt, tt, externalHeight, externalHeight)
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
        shaderPaint.shader = LinearGradient(tt, 0F, 0F, 0F, Color.BLACK, Color.WHITE, Shader.TileMode.REPEAT)
        val topRect = RectF(tt+externalHeight, 0F, x1+tt,tt)

        canvas.drawRect(topRect, shaderPaint)


        backgroundPaint.color = Color.WHITE
        canvas.drawPath(externalShape, backgroundPaint)

        Log.d("klke",R.color.lightGreen.toString())
        backgroundPaint.color = backgroundColor
        canvas.drawPath(internalShape, backgroundPaint)

        canvas.drawText(
            number, (
                    width / 2+ss+tt).toFloat(),
            externalHeight / 2 +tt - (textMetrics.ascent + textMetrics.descent) / 2,
            textPaint
        )
        return bitmap
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        MapKitInitializer.initialize(MAPKIT_API_KEY, applicationContext)
        setContentView(R.layout.activity_main)

        mapView = findViewById(R.id.mapview)


        mapView!!.map.move(
            CameraPosition(TARGET_LOCATION, 13.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 0F),
            null
        )
        val mapObjects = mapView!!.map.mapObjects.addCollection()
        clusterizedCollection = mapView!!.map.mapObjects.addClusterizedPlacemarkCollection(this)

        getParkings(applicationContext, url, mapObjects, clusterizedCollection!!)
    }

    private fun getParkings(context : Context, url : String, mapObjects : MapObjectCollection, clusterizedCollection: ClusterizedPlacemarkCollection) {
        val queue = Volley.newRequestQueue(context)
        val stringRequest = object : StringRequest(
            Method.GET, "$url?apicall=get_parkings",

            com.android.volley.Response.Listener { response ->

                val jsonArray = org.json.JSONTokener(response).nextValue() as org.json.JSONArray
                for (i in 0 until jsonArray.length()) {
                    val coordinatesObject = jsonArray.getJSONObject(i).getString("coordinates")
                    val coordinates = org.json.JSONTokener(coordinatesObject).nextValue() as org.json.JSONObject

                    val point1 = coordinates.getString("point")
                    val point2 = org.json.JSONTokener(point1).nextValue() as org.json.JSONArray

                    val lat = point2.getDouble(0)
                    val lon = point2.getDouble(1)
                    val point = Point(lat, lon)

                    val list = org.json.JSONTokener(coordinates.getString("list")).nextValue() as org.json.JSONArray
                    //Toast.makeText(context, coordinates.getString("list"), Toast.LENGTH_SHORT)

                    var myList = mutableListOf<Point>()
                    for (i in 0 until list.length()) {
                        val coord1 = list.getJSONArray(i).toString()
                        val coord2 = org.json.JSONTokener(coord1).nextValue() as org.json.JSONArray
                        val latitude = coord2.getDouble(0)
                        val longitude = coord2.getDouble(1)
                        myList += Point(latitude, longitude)
                    }

                    val type = coordinates.getString("type")

                    val address = jsonArray.getJSONObject(i).getString("address")
                    val hour_cost = jsonArray.getJSONObject(i).getInt("hour_cost")
                    val id = jsonArray.getJSONObject(i).getInt("id")

                    if (type == "l") {
                        parking(context, mapObjects, clusterizedCollection, point, myList, address, hour_cost, id)
                    } else if (type == "g") {
                        parkingG(context, mapObjects, clusterizedCollection, point, myList, address, hour_cost, id)
                    }
                }
            },
            com.android.volley.Response.ErrorListener {
                Toast.makeText(
                    context,
                    R.string.server_error,
                    Toast.LENGTH_SHORT
                ).show()
            }){}
            queue.add(stringRequest)
    }

    override fun onStop() {
        mapView?.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView?.onStart()
    }

    override fun onClusterAdded(cluster: Cluster) {
        // We setup cluster appearance and tap handler in this method
        cluster.appearance.setIcon(
            ImageProvider.fromAsset(this, "search_result.png"))
        cluster.addClusterTapListener(this)
    }

    override fun onClusterTap(cluster: Cluster): Boolean {
        mapView!!.map.move(
            CameraPosition(Point(cluster.appearance.geometry.latitude,cluster.appearance.geometry.longitude), mapView!!.map.cameraPosition.zoom * 1.08f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 0.25F),
            null
        )
        return true
    }



    private val parkingMapObjectTapListener =
           MapObjectTapListener { mapObject, point ->

               clearSelection()


               if (mapObject is PlacemarkMapObject) {
                    val parkingData = mapObject.userData as PinData

                   val style = IconStyle().apply { scale = 1.3f }
                   mapObject.setIcon(ImageProvider.fromBitmap(drawSimpleBitmap("${parkingData.hour_cost}\u2006₽", applicationContext, R.color.lightGreen)))

                   mapObject.setIconStyle(style)


                   when(parkingData.parking){
                       is PolylineMapObject -> setSelectedPolyline(parkingData.parking)
                       is PolygonMapObject -> setSelectedPolygon(parkingData.parking)
                   }


                   val bottomSheetDialog = BottomSheetDialog(parkingData){
                       mapObject.setIcon(ImageProvider.fromBitmap(drawSimpleBitmap("${parkingData.hour_cost}\u2006₽", applicationContext)))
                       clearSelection()
                   }

                    bottomSheetDialog.show(supportFragmentManager,"tag")
                }
                true
            }


    private fun setSelectedPolyline(polyline: PolylineMapObject){
        polyline.setStrokeColor(ContextCompat.getColor(applicationContext, R.color.lightGreen))
        selectedObject = polyline
    }

    private fun setSelectedPolygon(polygon: PolygonMapObject){
        polygon.fillColor = ContextCompat.getColor(applicationContext, R.color.lightGreen)
        polygon.strokeColor = ContextCompat.getColor(applicationContext, R.color.lightGreen)
        selectedObject = polygon
    }


    private fun clearSelection(){
        selectedObject?.let {
            when(it){
                is PolylineMapObject -> {
                    it.setStrokeColor(rgb(13, 174, 252))
                }

                is PolygonMapObject -> {
                    it.fillColor = rgb(91, 200, 252)
                    it.strokeColor = rgb(9, 133, 192)
                }
            }
        }
    }

    private fun parking(context: Context, mapObjects: MapObjectCollection, clusterizedCollection: ClusterizedPlacemarkCollection, point : Point, list : List<Point>, address : String, hour_cost : Int, id : Int) {
        val polyline = mapObjects
            .addPolyline(Polyline(list))
            .apply { setStrokeColor(rgb(13, 174, 252)) }

        val icon = clusterizedCollection.addPlacemark(
            point,
            ImageProvider.fromBitmap(drawSimpleBitmap("$hour_cost\u2006₽", context))
        )

        icon.addTapListener(parkingMapObjectTapListener)
        icon.setScaleFunction(listOf(PointF(2F, 1F)))
        icon.zIndex = 100.0f
        icon.userData = PinData(polyline, hour_cost, address, point)

        clusterizedCollection.clusterPlacemarks(60.0, 15)
    }



    private fun parkingG(context: Context, mapObjects: MapObjectCollection, clusterizedCollection: ClusterizedPlacemarkCollection, point : Point, list : List<Point>, address : String, hour_cost : Int, id : Int) {
        val polygon = mapObjects
            .addPolygon(Polygon(LinearRing(list), ArrayList()))
            .apply {
                fillColor = rgb(91, 200, 252)
                strokeColor = rgb(9, 133, 192)
                strokeWidth = 1.0f
                zIndex = 100.0f
            }


        val icon = clusterizedCollection.addPlacemark(
            point,
            ImageProvider.fromBitmap(drawSimpleBitmap("$hour_cost₽", context))
        )

        icon.addTapListener(parkingMapObjectTapListener)
        icon.setScaleFunction(listOf(PointF(1F, 0.5F)))
        icon.zIndex = 100.0f
        icon.userData = PinData(polygon, hour_cost, address, point)

        clusterizedCollection.clusterPlacemarks(60.0, 15)
    }

    fun changeCameraPosition(point : Point, cameraPosition : CameraPosition): CameraPosition {
        return CameraPosition(point, cameraPosition.zoom, cameraPosition.azimuth, cameraPosition.tilt)
    }

    companion object {
        private const val FONT_SIZE: Float = 15f
        private const val STROKE_SIZE = 3f
        private const val ZOOM_DURATION = 0.5f
    }


    private fun moveTo(target: Point, zoom: Float?) {
        mapView?.let {
            val cameraPosition = mapView!!.mapWindow.map.cameraPosition

            mapView!!.map.moveWithBottomPadding(
                CameraPosition(
                    target,
                    zoom ?: cameraPosition.zoom,
                    cameraPosition.azimuth,
                    cameraPosition.tilt
                ),
                Animation(Animation.Type.SMOOTH, ZOOM_DURATION), null,
                resources.getDimensionPixelSize(R.dimen.bottom_sheet_height),
                mapView!!.height()
            )
        }
    }
}