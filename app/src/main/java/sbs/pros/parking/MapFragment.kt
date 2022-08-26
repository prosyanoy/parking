package sbs.pros.parking

import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomsheet.BottomSheetBehavior
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sbs.pros.parking.databinding.FragmentMapBinding
import sbs.pros.parking.model.PinData
import sbs.pros.parking.utils.*
import sbs.pros.parking.utils.drawLocationPoint
import sbs.pros.parking.utils.drawSimpleBitmap
import sbs.pros.parking.utils.viewLifecycleLazy

@AndroidEntryPoint
class MapFragment : Fragment(R.layout.fragment_map), ClusterListener, ClusterTapListener, UserLocationObjectListener {


    private val url = "https://pros.sbs/parking/getting.php"

    private val TARGET_LOCATION = Point(43.590097, 39.721887)

    private var mapView: MapView? = null

    private var mapKit: MapKit? = null

    private var selectedObject: MapObject? = null

    private var selectedPlacemark: PlacemarkMapObject? = null

    private var userLocationLayer: UserLocationLayer? = null

    private var clusterizedCollection: ClusterizedPlacemarkCollection? = null

    private var menuBottomSheetBehaviour: BottomSheetBehavior<NestedScrollView>? = null

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private val binding by viewLifecycleLazy { FragmentMapBinding.bind( requireView()) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView = binding.mapview

        bottomSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.bottom_sheet))

        //location
        mapKit = MapKitFactory.getInstance()
        mapKit?.resetLocationManagerToDefault()

        requestLocationPermission(grant = true)
        setUserLocationLayer()
        checkUserLocation()
        setupMenu()
        setClickListeners()


        mapView!!.map.move(
            CameraPosition(TARGET_LOCATION, 13.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 0F),
            null
        )
        val mapObjects = mapView!!.map.mapObjects.addCollection()
        clusterizedCollection = mapView!!.map.mapObjects.addClusterizedPlacemarkCollection(this)

        getParkings(requireContext(), url, mapObjects, clusterizedCollection!!)
    }

    private fun setupMenu(){
        val menuBinding = binding.bottomMenu.bottomSheet

        val navHost = NavHostFragment()
        childFragmentManager.beginTransaction().replace(R.id.menu_host_fragment, navHost).commitNow()
        navHost.navController.setGraph(R.navigation.menu_nav, Bundle())

        navHost.navController.addOnDestinationChangedListener {
                controller, destination, arguments ->
            binding.bottomMenu.back.isVisible = destination.id != R.id.menuFragment
        }

        menuBottomSheetBehaviour = BottomSheetBehavior.from(menuBinding)
        menuBottomSheetBehaviour?.state = BottomSheetBehavior.STATE_HIDDEN


        //binding.mapUi.uiMapMenuFAB.setSafeOnClickListener {
           // menuBottomSheetBehaviour?.setState(BottomSheetBehavior.STATE_EXPANDED)
        //}

        binding.bottomMenu.close.setSafeOnClickListener {
            menuBottomSheetBehaviour?.setState(BottomSheetBehavior.STATE_HIDDEN)
        }

        binding.bottomMenu.back.setSafeOnClickListener {
            navHost.navController.navigateUp()
        }

        binding.mapUi.uiMapMenuFAB.setSafeOnClickListener {
            findNavController().navigateSafe(R.id.action_mapFragment_to_landlordMenu)
        }

    }


    override fun onStop() {
        mapView?.onStop()
        mapKit?.onStop()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        mapKit?.onStart()
        mapView?.onStart()
    }

    override fun onClusterAdded(cluster: Cluster) {
        // We setup cluster appearance and tap handler in this method
        cluster.appearance.setIcon(
            ImageProvider.fromAsset(requireContext(), "search_result.png"))
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
    private fun setClickListeners() {

        val uiMapLocationFAB = binding.mapUi.uiMapLocationFAB
        uiMapLocationFAB.setOnClickListener {
            if (userLocationLayer!!.cameraPosition()?.target != null) {
                centerCameraByUser()
            } else {
                if (checkGEOStatus()) {
                    requestLocationPermission(grant = true, denied = true)
                } else {
                    geoStatusDialog()
                }
            }
        }
        val uiMapInfoFAB = binding.mapUi.uiMapInfoFAB

        val uiMapAccessibleFAB = binding.mapUi.uiMapAccessibleFAB
        uiMapAccessibleFAB.setOnClickListener {
            if (uiMapInfoFAB.visibility == View.GONE) {
                uiMapAccessibleFAB.setIconTintResource(R.color.primary)
                uiMapInfoFAB.visibility = View.VISIBLE
            }else {
                uiMapAccessibleFAB.setIconTintResource(R.color.black)
                uiMapInfoFAB.visibility = View.GONE
            }
        }
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

    private val parkingMapObjectTapListener =
        MapObjectTapListener { mapObject, point ->

            clearSelection()

            if (mapObject is PlacemarkMapObject) {
                val parkingData = mapObject.userData as PinData

                var lastState = "STATE_HALF_EXPANDED"

                setSelectedPlacemark(mapObject)
                when(parkingData.parking){
                    is PolylineMapObject -> setSelectedPolyline(parkingData.parking)
                    is PolygonMapObject -> setSelectedPolygon(parkingData.parking)
                }

                binding.bottomSheetMain.addressText.text = parkingData.address

                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                    binding.mapUi.uiMapParkingFAB.visibility = View.GONE
                }

                mapView!!.map.move(
                    CameraPosition(Point(point.latitude - 0.0001,point.longitude), 16f, 0.0f, 0.0f),
                    Animation(Animation.Type.SMOOTH, 0.5F),
                    null)

                bottomSheetBehavior.addBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback(){
                    override fun onStateChanged(bottomSheet: View, state: Int) {
                        when (state) {

                            BottomSheetBehavior.STATE_HIDDEN -> {}

                            BottomSheetBehavior.STATE_EXPANDED -> {
                                lastState = "STATE_EXPANDED"
                                mapView!!.map.move(
                                    CameraPosition(Point(point.latitude - 0.0002,point.longitude), 16f, 0.0f, 0.0f),
                                    Animation(Animation.Type.SMOOTH, 0.5F),
                                    null)
                            }

                            BottomSheetBehavior.STATE_COLLAPSED ->{
                                mapView!!.map.move(
                                    CameraPosition(Point(mapView!!.map.cameraPosition.target.latitude + 0.0002, mapView!!.map.cameraPosition.target.longitude ), 16f, 0.0f, 0.0f),
                                    Animation(Animation.Type.SMOOTH, 0.5F),
                                    null)
                                clearSelection()
                                binding.mapUi.uiMapParkingFAB.visibility = View.VISIBLE
                            }

                            BottomSheetBehavior.STATE_DRAGGING -> {}
                            BottomSheetBehavior.STATE_SETTLING -> {}
                            BottomSheetBehavior.STATE_HALF_EXPANDED ->{

                                if (lastState == "STATE_EXPANDED"){
                                    mapView!!.map.move(
                                        CameraPosition(Point(point.latitude - 0.0001,point.longitude), 16f, 0.0f, 0.0f),
                                        Animation(Animation.Type.SMOOTH, 0.5F),
                                        null)
                                }
                                lastState = "STATE_HALF_EXPANDED"
                            }
                        }
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) { }
                })

            }
            true
        }

    private fun parking(context: Context, mapObjects: MapObjectCollection, clusterizedCollection: ClusterizedPlacemarkCollection, point : Point, list : List<Point>, address : String, hour_cost : Int, id : Int) {
        val polyline = mapObjects
            .addPolyline(Polyline(list))
            .apply { setStrokeColor(Color.rgb(13, 174, 252)) }

        val icon = clusterizedCollection.addPlacemark(
            point,
            ImageProvider.fromBitmap(
                sbs.pros.parking.utils.drawSimpleBitmap(
                    "$hour_cost\u2006₽",
                    context
                )
            )
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
        polygon.fillColor = lightBlue
        polygon.strokeColor = darkBlue
        polygon.strokeWidth = 1.0f
        polygon.zIndex = 100.0f

        val icon = clusterizedCollection.addPlacemark(
            point,
            ImageProvider.fromBitmap(
                sbs.pros.parking.utils.drawSimpleBitmap(
                    "$hour_cost₽",
                    context
                )
            )
        )

        icon.addTapListener(parkingMapObjectTapListener)
        icon.setScaleFunction(listOf(PointF(1F, 0.5F)))
        icon.zIndex = 100.0f
        icon.userData = PinData(polygon, hour_cost, address, point)

        clusterizedCollection.clusterPlacemarks(60.0, 15)
    }

    private fun requestLocationPermission(grant : Boolean = false, denied : Boolean = false) {
        if(checkFineLocationDenied() && denied){
            fineLocationDialog()
        } else if (!checkFineLocationGrant() && grant)
        {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf("android.permission.ACCESS_FINE_LOCATION"),
                Constants.PERMISSIONS_REQUEST_FINE_LOCATION
            )
        }
    }

    private fun setSelectedPlacemark(mapObject: PlacemarkMapObject){
        val style = IconStyle().apply { scale = 1.3f }
        val parkingData = mapObject.userData as PinData

        mapObject.setIcon(ImageProvider.fromBitmap(drawSimpleBitmap("${parkingData.hour_cost}\u2006₽", requireContext(), green)))
        mapObject.setIconStyle(style)
        selectedPlacemark = mapObject
    }

    private fun setSelectedPolyline(polyline: PolylineMapObject){
        polyline.setStrokeColor(green)
        selectedObject = polyline
    }

    private fun setSelectedPolygon(polygon: PolygonMapObject){
        polygon.fillColor = lightGreen
        polygon.strokeColor = darkGreen
        selectedObject = polygon
    }


    private fun clearSelection(){
        selectedObject?.let {
            when(it){
                is PolylineMapObject -> {
                    it.setStrokeColor(blue)
                }

                is PolygonMapObject -> {
                    it.fillColor = lightBlue
                    it.strokeColor = darkBlue
                }
            }
        }

        selectedPlacemark?.let {
            val parkingData = it.userData as PinData
            it.setIcon(ImageProvider.fromBitmap(drawSimpleBitmap("${parkingData.hour_cost}\u2006₽", requireContext())))
        }
    }



    private fun fineLocationDialog(){
        val dialogView = layoutInflater.inflate(R.layout.dialog_intent_settings, null)

        val customDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .show()

        val dialogText = dialogView.findViewById<TextView>(R.id.intentSettingsTextView)
        dialogText.text = getString(R.string.intent_settings_text_view_fine_location)

        val dialogButton = dialogView.findViewById<Button>(R.id.intentSettingsButton)
        dialogButton.setOnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:" + requireActivity().packageName)
            startActivity(intent)
            customDialog.dismiss()
        }
    }


    private fun geoStatusDialog(){
        val dialogView = layoutInflater.inflate(R.layout.dialog_intent_settings, null)

        val customDialog = AlertDialog.Builder(requireContext())
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

        GlobalScope.launch {
            while (true){
                if (checkGEOStatus()){
                    customDialog.dismiss()
                }
                delay(250L)
            }
        }
    }



    private fun setUserLocationLayer(){
        userLocationLayer = mapKit?.createUserLocationLayer(mapView!!.mapWindow)
        userLocationLayer!!.isVisible = true
        userLocationLayer!!.isHeadingEnabled = false
        userLocationLayer!!.setObjectListener(this)
    }

    private fun checkFineLocationGrant(): Boolean {
        return (ContextCompat.checkSelfPermission(requireContext(), "android.permission.ACCESS_FINE_LOCATION")
                == PackageManager.PERMISSION_GRANTED)
    }

    private fun checkFineLocationDenied(): Boolean {
        return (ContextCompat.checkSelfPermission(requireContext(), "android.permission.ACCESS_FINE_LOCATION")
                == PackageManager.PERMISSION_DENIED)
    }

    private fun checkGEOStatus(): Boolean {
        val manager: LocationManager = requireContext().getSystemService(LOCATION_SERVICE) as LocationManager
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun centerCameraByUser() {
        mapView!!.map.move(
            CameraPosition(Point(userLocationLayer!!.cameraPosition()?.target!!.latitude, userLocationLayer!!.cameraPosition()?.target!!.longitude), 16f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 0F),
            null)
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


    override fun onObjectAdded(userLocationView: UserLocationView) {
        userLocationView.arrow.setIcon(ImageProvider.fromBitmap(drawLocationPoint()))
        userLocationView.arrow.setIconStyle(IconStyle().setAnchor(PointF(0.5f, 0.5f))
            .setRotationType(RotationType.ROTATE)
            .setZIndex(1f)
            .setScale(0.5f))

        val pinIcon = userLocationView.pin.useCompositeIcon()
        pinIcon.setIcon(
            "pin",
            ImageProvider.fromBitmap(drawLocationPoint()),
            IconStyle().setAnchor(PointF(0.5f, 0.5f))
                .setRotationType(RotationType.ROTATE)
                .setZIndex(1f)
                .setScale(0.5f)
        )
        userLocationView.accuracyCircle.fillColor = Color.BLUE and -0x66000001
    }

    override fun onObjectRemoved(userLocationView: UserLocationView) {}

    override fun onObjectUpdated(userLocationView: UserLocationView, objectEvent: ObjectEvent) {}

    companion object {
        private var currentSelection : MapObject? = null
        private val node = currentSelection
        fun changeSelection(parking: MapObject?) {
            if (node is PolylineMapObject) {
                node.setStrokeColor(Color.rgb(13, 174, 252))
            } else if (node is PolygonMapObject) {
                node.strokeColor = Color.rgb(13, 174, 252)
            }
            var currentSelection = parking

            if (parking is PolylineMapObject) {
                parking.setStrokeColor(green)
            } else if (parking is PolygonMapObject) {
                parking.strokeColor = green
            }
        }


        private const val ZOOM_DURATION = 0.5f
        const val FONT_SIZE = 22f
        const val MARGIN_SIZE = 3f
        const val STROKE_SIZE = 3f
        const val POINTS_ZOOM = 13 //9-12.99 (точки)

        private val blue = Color.rgb(13, 174, 252)
        private val lightBlue = Color.rgb(91, 200, 252)
        private val darkBlue = Color.rgb(9, 133, 192)
        private val green = Color.rgb(57, 180, 36)
        private val lightGreen = Color.rgb(92, 233, 70)
        private val darkGreen = Color.rgb(30, 141, 13)

    }

}