package sbs.pros.parking

//import com.yandex.mapkit.map.CameraPosition

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
import kotlin.math.abs
import kotlin.math.sqrt


class MainActivity : AppCompatActivity(), ClusterListener, ClusterTapListener {

    private val MAPKIT_API_KEY = "024ae79a-58dc-4626-ac7e-1ba6ba83121e"

    private val url = "https://pros.sbs/parking/getting.php"

    private var userLocationLayer: UserLocationLayer? = null

    private var myLocation: Point? = null

    private val TARGET_LOCATION = Point(43.590097, 39.721887)

    private val mapView: MapView? = null

    /*private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                "android.permission.ACCESS_FINE_LOCATION"
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf("android.permission.ACCESS_FINE_LOCATION"),
                PERMISSIONS_REQUEST_FINE_LOCATION
            )
        }
    }*/

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

        /*val resultLauncher = registerForActivityResult(MyIntroActivityContract()) { _ ->
            val prefs = getSharedPreferences("data.xml", MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putBoolean("login", false)
            editor.apply()
        }

        val prefs = getSharedPreferences("data.xml", MODE_PRIVATE)

        if (prefs.getBoolean("login", true)) {
            resultLauncher.launch("Login?")
        }*/

        MapKitInitializer.initialize(MAPKIT_API_KEY, applicationContext)
        setContentView(R.layout.activity_main)

        val llBottomSheet = findViewById<View>(R.id.bottom_sheet) as LinearLayout

        //var parkingAddress = findViewById<View>(R.id.parking_address) as TextView
        //var parkingInfo = findViewById<View>(R.id.parking_info) as TextView

        val bottomSheetBehavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(llBottomSheet)

        val mapView = findViewById<MapView>(R.id.mapview)

        bottomSheetBehavior.isFitToContents = false
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        /*requestLocationPermission()
        val mapKit = MapKitFactory.getInstance()
        mapKit.resetLocationManagerToDefault()
        userLocationLayer = mapKit.createUserLocationLayer(mapView.mapWindow)
        userLocationLayer!!.isVisible = true
        userLocationLayer!!.isHeadingEnabled = true
        userLocationLayer!!.setObjectListener(this)*/

        mapView!!.map.move(
            CameraPosition(TARGET_LOCATION, 11.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 0F),
            null
        )
        val mapObjects = mapView.map.mapObjects.addCollection()
        val clusterizedCollection = mapView.map.mapObjects.addClusterizedPlacemarkCollection(this)

        //parking(applicationContext, mapObjects, clusterizedCollection, Point(43.595912, 39.715943), listOf(Point(43.596098, 39.716050), Point(43.595666, 39.715801)), "leve", "0")
        getParkings(applicationContext, url, mapObjects, clusterizedCollection)
    }

    /*fun onLocationUpdated(location: Location) {
        if (myLocation == null) {
            mapView!!.map.move(
                CameraPosition(location.position, 11.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 0F),
                null
            )
            //moveCamera(location.getPosition(), 11f);
        }
        //myLocation = location.position
        //Log.w(TAG, "my location - " + myLocation!!.latitude + "," + myLocation!!.longitude)
    }

    fun onLocationStatusUpdated(locationStatus: LocationStatus) {
        if (locationStatus == LocationStatus.NOT_AVAILABLE) {
            //Snackbar.make(rootCoordinatorLayout, "lkj", Snackbar.LENGTH_LONG).show();
            mapView!!.map.move(
                CameraPosition(TARGET_LOCATION, 11.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 0F),
                null
            )
        }
    }*/

    class getParkings(context : Context, url : String, mapObjects : MapObjectCollection, clusterizedCollection: ClusterizedPlacemarkCollection) {
        val queue = Volley.newRequestQueue(context)
        private val TAG = "MainActivity"
        val stringRequest = object : StringRequest(
            Method.GET,
            "$url?apicall=get_parkings",
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
        Toast.makeText(
            applicationContext,
            String.format(getString(R.string.cluster_tap_message), cluster.size),
            Toast.LENGTH_SHORT
        ).show()
        return true
    }

    /*override fun onObjectAdded(userLocationView: UserLocationView) {
        userLocationLayer!!.setAnchor(
            PointF((mapView!!.width * 0.5).toFloat(), (mapView!!.height * 0.5).toFloat()),
            PointF((mapView!!.width * 0.5).toFloat(), (mapView!!.height * 0.83).toFloat())
        )
        userLocationView.arrow.setIcon(
            ImageProvider.fromAsset(this, "user_arrow.png")
        )
        val pinIcon = userLocationView.pin.useCompositeIcon()
        pinIcon.setIcon(
            "icon",
            ImageProvider.fromAsset(this, "icon.png"),
            IconStyle().setAnchor(PointF(0f, 0f))
                .setRotationType(RotationType.ROTATE)
                .setZIndex(0f)
                .setScale(1f)
        )
        pinIcon.setIcon(
            "pin",
            ImageProvider.fromAsset(this, "search_result.png"),
            IconStyle().setAnchor(PointF(0.5f, 0.5f))
                .setRotationType(RotationType.ROTATE)
                .setZIndex(1f)
                .setScale(0.5f)
        )
        userLocationView.accuracyCircle.fillColor = Color.BLUE and -0x66000001
    }

    override fun onObjectRemoved(view: UserLocationView) {}
    override fun onObjectUpdated(view: UserLocationView, event: ObjectEvent) {}*/

    //private class ParkingMapObjectUserData constructor(val address : String, val hour_cost: String, val mapObject: MapObject?)
    private class ParkingMapObjectUserData(val id: Int, val mapObject: MapObject?)

    private class parkingListener (address: String, hour_cost: Int) {
        val parkingMapObjectTapListener =
            MapObjectTapListener { mapObject, point ->
                if (mapObject is PlacemarkMapObject) {
                    //Toast.makeText(applicationContext, "lol", Toast.LENGTH_SHORT).show()
                    //val parking = mapObject
                    //val parkingData = parking.userData

                    val llBottomSheet: LinearLayout by lazy { MainActivity().findViewById<View>(R.id.bottom_sheet) as LinearLayout }
                    val bottomSheetBehavior: BottomSheetBehavior<*> =
                        BottomSheetBehavior.from(llBottomSheet)
                    val parkingAddress = MainActivity().findViewById<View>(R.id.parking_address) as TextView
                    val parkingInfo = MainActivity().findViewById<View>(R.id.parking_info) as TextView

                    parkingAddress.text = address
                    parkingInfo.text = hour_cost.toString()
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                    /*mapView!!.map.move(
                        changeCameraPosition(point, mapView!!.map.cameraPosition),
                        Animation(Animation.Type.SMOOTH, 0F),
                        null
                    )*/
                    //MainActivity.changeSelection(parkingUserData?.mapObject!!)
                }
                true
            }
    }

    private fun parking(context: Context, mapObjects: MapObjectCollection, clusterizedCollection: ClusterizedPlacemarkCollection, point : Point, list : List<Point>, address : String, hour_cost : Int, id : Int) {
        val polyline = mapObjects.addPolyline(
            Polyline(list))
        polyline.setStrokeColor(rgb(13, 174, 252))

        val icon = clusterizedCollection.addPlacemark(
            point,
            ImageProvider.fromBitmap(drawSimpleBitmap("$hour_cost₽", context))
        )
        icon.setScaleFunction(listOf(PointF(1F, 0.5F)))
        icon.zIndex = 100.0f

       /* val m = SaveState()
        val result: Boolean = m.saveObject(myObject(parkingListener(address, hour_cost).parkingMapObjectTapListener, polyline), id, applicationContext)

        icon.addTapListener(SaveState().getObject(applicationContext, id)!!.listener)*/

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

        /*val m = SaveState()
        val result: Boolean = m.saveObject(myObject(parkingListener(address, hour_cost).parkingMapObjectTapListener, polygon), id, applicationContext)

        icon.addTapListener(SaveState().getObject(applicationContext, id)!!.listener)*/

        clusterizedCollection.clusterPlacemarks(60.0, 15)
    }
    /*object changeSelection(parking : MapObject) {
        var currentSelection : MapObject? = null
        private val node = currentSelection
        init {
            if (node is PolylineMapObject) {
                node.setStrokeColor(rgb(13, 174, 252))
            } else if (node is PolygonMapObject) {
                node.strokeColor = rgb(13, 174, 252)
            }
            currentSelection = parking

            if (parking is PolylineMapObject) {
                parking.setStrokeColor(rgb(57, 180, 36))
            } else if (parking is PolygonMapObject) {
                parking.strokeColor = rgb(57, 180, 36)
            }
        }
    }*/
    fun changeCameraPosition(point : Point, cameraPosition : CameraPosition): CameraPosition {
        return CameraPosition(point, cameraPosition.zoom, cameraPosition.azimuth, cameraPosition.tilt)
    }

    companion object {
        /*private var currentSelection : MapObject? = null
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
        }*/

        private const val FONT_SIZE = 15f
        private const val MARGIN_SIZE = 3f
        private const val STROKE_SIZE = 3f

        private const val PERMISSIONS_REQUEST_FINE_LOCATION = 1
    }
}