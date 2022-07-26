package sbs.pros.parking


import android.content.Context
import android.graphics.*
import android.graphics.Color.rgb
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.LinearRing
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polygon
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.map.*
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.runtime.image.ImageProvider
import sbs.pros.parking.model.PinData
import sbs.pros.parking.utils.moveWithBottomPadding
import kotlin.math.abs
import kotlin.math.sqrt


class MainActivity : AppCompatActivity(), ClusterListener, ClusterTapListener {

    private val MAPKIT_API_KEY = "024ae79a-58dc-4626-ac7e-1ba6ba83121e"

    private val url = "https://pros.sbs/parking/getting.php"

    private var userLocationLayer: UserLocationLayer? = null

    private var myLocation: Point? = null

    private val TARGET_LOCATION = Point(43.590097, 39.721887)

    private val mapView: MapView? = null

    private var selectedPin: PinData? = null



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

   //@SuppressLint("ResourceAsColor")
    fun drawSimpleBitmap(number: String, context: Context): Bitmap {
        val textPaint = Paint()
        textPaint.textSize = FONT_SIZE * context.resources.displayMetrics.density
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.style = Paint.Style.FILL
        textPaint.isAntiAlias = true
        val widthF = textPaint.measureText(number)
        val textMetrics = textPaint.fontMetrics
        val heightF = abs(textMetrics.bottom) + abs(textMetrics.top)
        val textRadius = sqrt((widthF * widthF + heightF * heightF).toDouble())
            .toFloat() / 2
        val internalRadius = textRadius + MARGIN_SIZE * context.resources.displayMetrics.density
        val externalRadius = internalRadius + STROKE_SIZE * context.resources.displayMetrics.density
        val width = (2 * externalRadius + 0.5).toInt()
        val bitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val backgroundPaint = Paint()
        backgroundPaint.isAntiAlias = true
        backgroundPaint.color = rgb(13, 174, 252)
        canvas.drawCircle(
            (width / 2).toFloat(),
            (width / 2).toFloat(),
            externalRadius,
            backgroundPaint
        )
        backgroundPaint.color = Color.WHITE
        canvas.drawCircle(
            (width / 2).toFloat(),
            (width / 2).toFloat(),
            internalRadius,
            backgroundPaint
        )
        canvas.drawText(
            number, (
                    width / 2).toFloat(),
            width / 2 - (textMetrics.ascent + textMetrics.descent) / 2,
            textPaint
        )
        return bitmap
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        MapKitInitializer.initialize(MAPKIT_API_KEY, applicationContext)
        setContentView(R.layout.activity_main)

        val llBottomSheet = findViewById<View>(R.id.bottom_sheet)
        val bottomSheetBehavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(llBottomSheet)

        val mapView = findViewById<MapView>(R.id.mapview)

        bottomSheetBehavior.isFitToContents = false
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN


        mapView!!.map.move(
            CameraPosition(TARGET_LOCATION, 11.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 0F),
            null
        )
        val mapObjects = mapView.map.mapObjects.addCollection()
        val clusterizedCollection = mapView.map.mapObjects.addClusterizedPlacemarkCollection(this)

        getParkings(applicationContext, url, mapObjects, clusterizedCollection)
    }



    class getParkings(context : Context, url : String, mapObjects : MapObjectCollection, clusterizedCollection: ClusterizedPlacemarkCollection) {
        private val queue = Volley.newRequestQueue(context)
        private val stringRequest = object : StringRequest(
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
                    val point = Point(lat.toDouble(), lon.toDouble())

                    val list = org.json.JSONTokener(coordinates.getString("list")).nextValue() as org.json.JSONArray
                    Toast.makeText(context, coordinates.getString("list"), Toast.LENGTH_SHORT)

                    var myList = mutableListOf<Point>()
                    for (i in 0 until list.length()) {
                        val coord1 = list.getJSONArray(i).toString()
                        val coord2 = org.json.JSONTokener(coord1).nextValue() as org.json.JSONArray
                        val latitude = coord2.getDouble(0)
                        val longitude = coord2.getDouble(1)
                        myList += Point(latitude.toDouble(), longitude.toDouble())
                    }

                    val type = coordinates.getString("type")

                    val address = jsonArray.getJSONObject(i).getString("address")
                    val hour_cost = jsonArray.getJSONObject(i).getInt("hour_cost")
                    val id = jsonArray.getJSONObject(i).getInt("id")

                    if (type == "l") {
                        MainActivity().parking(context, mapObjects, clusterizedCollection, point, myList, address, hour_cost, id)
                    } else if (type == "g") {
                        android.app.Activity()
                        MainActivity().parkingG(context, mapObjects, clusterizedCollection, point, myList, address, hour_cost, id)
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
        init {
            queue.add(stringRequest)
        }
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
        return true
    }

    private val parkingMapObjectTapListener =
            MapObjectTapListener { mapObject, point ->
                if (mapObject is PlacemarkMapObject) {
                    val parking = mapObject
                    val parkingData = parking.userData as PinData

                    val llBottomSheet = this.findViewById<LinearLayout>(R.id.bottom_sheet)
                    val bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet)
                    val parkingAddress = this.findViewById<TextView>(R.id.parking_address)
                    val parkingInfo = this.findViewById<TextView>(R.id.parking_info)

                    parkingAddress.text = parkingData.address
                    parkingInfo.text = parkingData.hour_cost.toString()
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                }
                true
            }




    private fun parking(context: Context, mapObjects: MapObjectCollection, clusterizedCollection: ClusterizedPlacemarkCollection, point : Point, list : List<Point>, address : String, hour_cost : Int, id : Int) {
        val polyline = mapObjects.addPolyline(
            Polyline(list))
        polyline.setStrokeColor(rgb(13, 174, 252))

        val icon = clusterizedCollection.addPlacemark(
            point,
            ImageProvider.fromBitmap(drawSimpleBitmap("$hour_cost₽", context))
        )
        icon.addTapListener(parkingMapObjectTapListener)
        icon.setScaleFunction(listOf(PointF(1F, 0.5F)))
        icon.zIndex = 100.0f

        icon.userData = PinData(polyline, hour_cost, address, point)


        clusterizedCollection.clusterPlacemarks(60.0, 15)
    }

    private fun parkingG(context: Context, mapObjects: MapObjectCollection, clusterizedCollection: ClusterizedPlacemarkCollection, point : Point, list : List<Point>, address : String, hour_cost : Int, id : Int) {
        val polygon = mapObjects.addPolygon(
            Polygon(LinearRing(list), ArrayList())
        )
        polygon.fillColor =
                //rgb(13, 174, 252)
            rgb(91, 200, 252)
        polygon.strokeColor = rgb(9, 133, 192)
        polygon.strokeWidth = 1.0f
        polygon.zIndex = 100.0f

        val icon = clusterizedCollection.addPlacemark(
            point,
            ImageProvider.fromBitmap(drawSimpleBitmap("$hour_cost₽", context))
        )
        icon.setScaleFunction(listOf(PointF(1F, 0.5F)))
        icon.zIndex = 100.0f

        icon.userData = PinData(polygon, hour_cost, address, point)

        clusterizedCollection.clusterPlacemarks(60.0, 15)
    }

    fun changeCameraPosition(point : Point, cameraPosition : CameraPosition): CameraPosition {
        return CameraPosition(point, cameraPosition.zoom, cameraPosition.azimuth, cameraPosition.tilt)
    }

    companion object {
        private var currentSelection : MapObject? = null
        private val node = currentSelection
        fun changeSelection(parking: MapObject?) {
            if (node is PolylineMapObject) {
                node.setStrokeColor(rgb(13, 174, 252))
            } else if (node is PolygonMapObject) {
                node.strokeColor = rgb(13, 174, 252)
            }
            var currentSelection = parking

            if (parking is PolylineMapObject) {
                parking.setStrokeColor(rgb(57, 180, 36))
            } else if (parking is PolygonMapObject) {
                parking.strokeColor = rgb(57, 180, 36)
            }
        }

        private const val FONT_SIZE = 15f
        private const val MARGIN_SIZE = 3f
        private const val STROKE_SIZE = 3f
        const val POINTS_ZOOM = 13 //9-12.99 (точки)
        private const val ZOOM_DURATION = 0.5f



        private const val PERMISSIONS_REQUEST_FINE_LOCATION = 1
    }



    private fun unSelectPrevMarker() {
        selectedPin?.let { //unselect prev
            it.isSelected = false
//            redrawMarkerItem(it)
            selectedPin = null
        }
    }




    private fun selectMarkerItem(item: PinData) {
        unSelectPrevMarker()

        selectedPin = item
        selectedPin?.isSelected = true
//        selectedPin?.let { redrawMarkerItem(it) }
    }

    private fun moveTo(target: Point, zoom: Float?) {
        mapView?.let {
            val cameraPosition = mapView.mapWindow.map.cameraPosition

            mapView.map.moveWithBottomPadding(
                CameraPosition(
                    target,
                    zoom ?: cameraPosition.zoom,
                    cameraPosition.azimuth,
                    cameraPosition.tilt
                ),
                Animation(Animation.Type.SMOOTH, ZOOM_DURATION), null,
                resources.getDimensionPixelSize(R.dimen.bottom_sheet_height),
                mapView.height()
            )
        }
    }
}