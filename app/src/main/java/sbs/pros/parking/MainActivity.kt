package sbs.pros.parking


import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.Color.rgb
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.LinearRing
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polygon
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.*
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sbs.pros.parking.model.PinData
import sbs.pros.parking.utils.moveWithBottomPadding
import kotlin.math.abs
import kotlin.math.sqrt


class MainActivity : AppCompatActivity(), ClusterListener, ClusterTapListener,
    UserLocationObjectListener{

    private val MAPKIT_API_KEY = "024ae79a-58dc-4626-ac7e-1ba6ba83121e"

    private val url = "https://pros.sbs/parking/getting.php"

    private val TARGET_LOCATION = Point(43.590097, 39.721887)

    private var mapView: MapView? = null

    private var userLocationLayer: UserLocationLayer? = null

    private var mapKit: MapKit? = null

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

    private fun drawSimpleBitmap(number: String, context: Context): Bitmap {
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

        backgroundPaint.color = rgb(13, 174, 252)
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

        mapView = findViewById<MapView>(R.id.mapview)

        val llBottomSheet = findViewById<View>(R.id.bottom_sheet)
        val bottomSheetBehavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(llBottomSheet)

        bottomSheetBehavior.isFitToContents = false
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        //location
        mapKit = MapKitFactory.getInstance()
        mapKit?.resetLocationManagerToDefault()

        requestLocationPermission(grant = true)
        setUserLocationLayer()
        checkUserLocation()

        val meFloatButton = findViewById<FloatingActionButton>(R.id.centeringRelativeUser)
        meFloatButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                if(userLocationLayer!!.cameraPosition()?.target != null){
                    centerCameraByUser()
                } else{
                    if(checkGEOStatus()){
                        requestLocationPermission(grant = true, denied = true)
                    } else {
                        geoStatusDialog()
                    }
                }
            }
        })

        mapView!!.map.move(
            CameraPosition(TARGET_LOCATION, 13.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 0F),
            null
        )
        val mapObjects = mapView!!.map.mapObjects.addCollection()
        val clusterizedCollection = mapView!!.map.mapObjects.addClusterizedPlacemarkCollection(this)

        getParkings(applicationContext, url, mapObjects, clusterizedCollection)
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
                    Toast.makeText(context, coordinates.getString("list"), Toast.LENGTH_SHORT)

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
                if (mapObject is PlacemarkMapObject) {
                    val parkingData = mapObject.userData as PinData

                    val llBottomSheet = findViewById<LinearLayout>(R.id.bottom_sheet)
                    val view = LayoutInflater.from(applicationContext).inflate(R.layout.bottom_sheet, llBottomSheet, false)
                    val bottomSheetDialog = BottomSheetDialog(this)
                    bottomSheetDialog.setContentView(view)

                    val parkingAddress = view.findViewById<TextView>(R.id.parking_address)
                    val parkingInfo = view.findViewById<TextView>(R.id.parking_info)
                    parkingAddress.text = parkingData.address
                    parkingInfo.text = parkingData.hour_cost.toString()

                    bottomSheetDialog.show()
                }
                true
            }




    private fun parking(context: Context, mapObjects: MapObjectCollection, clusterizedCollection: ClusterizedPlacemarkCollection, point : Point, list : List<Point>, address : String, hour_cost : Int, id : Int) {
        val polyline = mapObjects.addPolyline(
            Polyline(list))
        polyline.setStrokeColor(rgb(13, 174, 252))

        val icon = clusterizedCollection.addPlacemark(
            point,
            ImageProvider.fromBitmap(drawSimpleBitmap("$hour_cost\u2006₽", context))
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

    //user location
    private fun requestLocationPermission(grant : Boolean = false, denied : Boolean = false) {
        if(checkFineLocationDenied() && denied){
            fineLocationDialog()
        } else if (!checkFineLocationGrant() && grant)
        {
            ActivityCompat.requestPermissions(this, arrayOf("android.permission.ACCESS_FINE_LOCATION"),
                PERMISSIONS_REQUEST_FINE_LOCATION)
        }
    }

    private fun fineLocationDialog(){
        val dialogView = layoutInflater.inflate(R.layout.dialog_intent_settings, null)

        val customDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .show()

        val dialogText = dialogView.findViewById<TextView>(R.id.intentSettingsTextView)
        dialogText.text = getString(R.string.intent_settings_text_view_fine_location)

        val dialogButton = dialogView.findViewById<Button>(R.id.intentSettingsButton)
        dialogButton.setOnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:" + this.packageName)
            startActivity(intent)
            customDialog.dismiss()
        }
    }

    private fun geoStatusDialog(){
        val dialogView = layoutInflater.inflate(R.layout.dialog_intent_settings, null)

        val customDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .show()

        val dialogText = dialogView.findViewById<TextView>(R.id.intentSettingsTextView)
        dialogText.text = getString(R.string.intent_settings_text_view_geo_status)

        val dialogButton = dialogView.findViewById<Button>(R.id.intentSettingsButton)
        dialogButton.setOnClickListener {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
            customDialog.dismiss()
        }

        GlobalScope.launch() {
            while (true){
                if (checkGEOStatus()){
                    customDialog.dismiss()
                }
                delay(250L)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSIONS_REQUEST_FINE_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(Intent.makeRestartActivityTask(this.intent?.component))
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun setUserLocationLayer(){
        userLocationLayer = mapKit?.createUserLocationLayer(mapView!!.mapWindow)
        userLocationLayer!!.isVisible = true
        userLocationLayer!!.isHeadingEnabled = false
        userLocationLayer!!.setObjectListener(this)
    }

    private fun checkUserLocation(){
        GlobalScope.launch(Dispatchers.Main) {
            var t = 0
            while (t < 10000){
                if (userLocationLayer!!.cameraPosition()?.target != null){
                    centerCameraByUser()
                    break
                }
                delay(250L)
                t += 250
            }
        }
    }

    private fun checkFineLocationGrant(): Boolean {
        return (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION")
                == PackageManager.PERMISSION_GRANTED)
    }

    private fun checkFineLocationDenied(): Boolean {
        return (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION")
                == PackageManager.PERMISSION_DENIED)
    }

    private fun checkGEOStatus(): Boolean {
        val manager: LocationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun centerCameraByUser() {
        mapView!!.map.move(
            CameraPosition(Point(userLocationLayer!!.cameraPosition()?.target!!.latitude, userLocationLayer!!.cameraPosition()?.target!!.longitude), 16f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 0F),
            null)
    }

    override fun onObjectAdded(userLocationView: UserLocationView) {
        val pinIcon = userLocationView.pin.useCompositeIcon()

        userLocationView.accuracyCircle.fillColor = Color.BLUE and -0x66000001
    }

    override fun onObjectRemoved(userLocationView: UserLocationView) {}

    override fun onObjectUpdated(userLocationView: UserLocationView, objectEvent: ObjectEvent) {}
}