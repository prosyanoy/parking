package sbs.pros.parking

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PointF
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yandex.mapkit.*
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.*
import com.yandex.mapkit.geometry.LinearRing
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polygon
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.location.FilteringMode
import com.yandex.mapkit.location.Location
import com.yandex.mapkit.location.LocationListener
import com.yandex.mapkit.location.LocationStatus
import com.yandex.mapkit.map.*
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.bottom_sheet_layout.*
import kotlinx.android.synthetic.main.bottom_sheet_parked_layout.*
import kotlinx.android.synthetic.main.bottom_sheet_reserve_layout.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import sbs.pros.parking.databinding.FragmentMapBinding
import sbs.pros.parking.menu.MenuViewModel
import sbs.pros.parking.model.PinData
import sbs.pros.parking.utils.drawLocationPoint
import sbs.pros.parking.utils.drawSimpleBitmap
import sbs.pros.parking.utils.setSafeOnClickListener
import sbs.pros.parking.utils.viewLifecycleLazy
import java.util.*


@AndroidEntryPoint
class MapFragment : Fragment(R.layout.fragment_map), ClusterListener, ClusterTapListener, UserLocationObjectListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, DrivingSession.DrivingRouteListener  {


    private val menuViewModel by activityViewModels<MenuViewModel>()

    private val url = "https://pros.sbs/parking/getting.php"

    private val TARGET_LOCATION = Point(43.590097, 39.721887)

    private var mapView: MapView? = null

    private var mapKit: MapKit? = null

    private var mapObjects: MapObjectCollection? = null

    private var selectedObject: MapObject? = null

    private var selectedPlacemark: PlacemarkMapObject? = null

    private var userLocationLayer: UserLocationLayer? = null

    private var clusterizedCollection: ClusterizedPlacemarkCollection? = null

    private var menuBottomSheetBehaviour: BottomSheetBehavior<NestedScrollView>? = null

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private lateinit var bottomSheetReserveBehavior: BottomSheetBehavior<ConstraintLayout>

    private lateinit var bottomSheetParkedBehavior: BottomSheetBehavior<ConstraintLayout>


    private var day = 0
    private var month = 0
    private var year = 0
    private var hour = 0
    private var minute = 0
    private var savedDay = 0
    private var savedMonth = 0
    private var savedYear = 0
    private var savedHour = 0
    private var savedMinute = 0


    private var seconds = 0
    private var running = false
    private var wasRunning = false

    private var drivingRouter: DrivingRouter? = null
    private var drivingSession: DrivingSession? = null

    private var userLocation = Point(43.6028, 39.7342)




    val binding by viewLifecycleLazy { FragmentMapBinding.bind( requireView()) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView = binding.mapview

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetMain.root)
        bottomSheetReserveBehavior = BottomSheetBehavior.from(binding.bottomSheetReserve.root)
        bottomSheetParkedBehavior = BottomSheetBehavior.from(binding.bottomSheetParked.root)



        //location
        mapKit = MapKitFactory.getInstance()
        mapKit?.resetLocationManagerToDefault()

        requestLocationPermission(grant = true)
        setUserLocationLayer()
        checkUserLocation()
        setupMenu()
        setClickListeners()




        mapView!!.map.move(
            CameraPosition(TARGET_LOCATION, 11.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 0F),
            null
        )
        mapObjects = mapView!!.map.mapObjects.addCollection()
        clusterizedCollection = mapView!!.map.mapObjects.addClusterizedPlacemarkCollection(this)

        getParkings(requireContext(), url, mapObjects!!, clusterizedCollection!!)

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            menuViewModel.title.collect{ binding.bottomMenu.menuTitle.text = it }
        }


        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter()
        mapObjects = mapView!!.map.mapObjects.addCollection()

        /*
        mapKit!!.createLocationManager().subscribeForLocationUpdates(0.0, 0, 0.0, true, FilteringMode.ON,
            object : LocationListener {
                override fun onLocationUpdated( location: Location) {
                    userLocation = Point(location.position.latitude, location.position.longitude)
                    Log.d(TAG, "User location: $userLocation")
                }

                override fun onLocationStatusUpdated( locationStatus: LocationStatus) {}
            })
*/


        if (savedInstanceState != null) {
            // Get the previous state of the stopwatch
            // if the activity has been
            // destroyed and recreated.
            seconds = savedInstanceState
                .getInt("seconds")
            running = savedInstanceState
                .getBoolean("running")
            wasRunning = savedInstanceState
                .getBoolean("wasRunning")
        }
        runTimer()

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


        binding.mapUi.uiMapMenuFAB.setSafeOnClickListener {
            menuBottomSheetBehaviour?.setState(BottomSheetBehavior.STATE_EXPANDED)
        }

        binding.bottomMenu.close.setSafeOnClickListener {
            menuBottomSheetBehaviour?.setState(BottomSheetBehavior.STATE_HIDDEN)
        }

        binding.bottomMenu.back.setSafeOnClickListener {
            navHost.navController.navigateUp()
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

            Response.Listener { response ->

                val jsonArray = JSONTokener(response).nextValue() as JSONArray
                for (i in 0 until jsonArray.length()) {
                    val coordinatesObject = jsonArray.getJSONObject(i).getString("coordinates")
                    val coordinates = JSONTokener(coordinatesObject).nextValue() as JSONObject

                    val point1 = coordinates.getString("point")
                    val point2 = JSONTokener(point1).nextValue() as JSONArray

                    val lat = point2.getDouble(0)
                    val lon = point2.getDouble(1)
                    val point = Point(lat, lon)

                    val list = JSONTokener(coordinates.getString("list")).nextValue() as JSONArray

                    var myList = mutableListOf<Point>()
                    for (i in 0 until list.length()) {
                        val coord1 = list.getJSONArray(i).toString()
                        val coord2 = JSONTokener(coord1).nextValue() as JSONArray
                        val latitude = coord2.getDouble(0)
                        val longitude = coord2.getDouble(1)
                        myList += Point(latitude, longitude)
                    }

                    val type = coordinates.getString("type")

                    val address = jsonArray.getJSONObject(i).getString("address")
                    val hour_cost = jsonArray.getJSONObject(i).getInt("hour_cost")
                    val id = jsonArray.getJSONObject(i).getInt("id")
                    val secure = jsonArray.getJSONObject(i).getInt("secure")
                    val around_the_clock = jsonArray.getJSONObject(i).getInt("around_the_clock")
                    val ev = jsonArray.getJSONObject(i).getInt("ev")
                    val disabled = jsonArray.getJSONObject(i).getInt("disabled")
                    val places = jsonArray.getJSONObject(i).getInt("places")
                    val free_places = jsonArray.getJSONObject(i).getInt("free_places")



                    if (type == "l") {
                        parking(context, mapObjects, clusterizedCollection, point, myList, address, hour_cost, id, secure, around_the_clock, ev, disabled, places, free_places)
                    } else if (type == "g") {
                        parkingG(context, mapObjects, clusterizedCollection, point, myList, address, hour_cost, id, secure, around_the_clock, ev, disabled, places, free_places)
                    }
                }
            },
            Response.ErrorListener {
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

                var lastState = "STATE_EXPANDED"

                setSelectedPlacemark(mapObject)
                when(parkingData.parking){
                    is PolylineMapObject -> setSelectedPolyline(parkingData.parking)
                    is PolygonMapObject -> setSelectedPolygon(parkingData.parking)
                }

                mapView!!.map.move(
                    CameraPosition(Point(point.latitude - 0.0001,point.longitude), 16f, 0.0f, 0.0f),
                    Animation(Animation.Type.SMOOTH, 0.5F),
                    null)

//create a route
                createRoute(userLocation!!, point)



//receiving info about the parking and setting up all the relevant variables
                val radius = 100 // corner radius, higher value = more rounded
                Glide.with(this)
                    .load("https://pros.sbs/parking/photo/${parkingData.id}.jpeg")
                    .override(300, 300)
                    .centerCrop() // scale image to fill the entire ImageView
                    .transform(RoundedCorners(radius))
                    .into(photo)

                binding.bottomSheetReserve.address.text = parkingData.address
                binding.bottomSheetMain.address.text = parkingData.address
                binding.bottomSheetParked.address.text = parkingData.address
                binding.bottomSheetMain.payment.text = parkingData.hour_cost.toString() + " ₽ / час"
                binding.bottomSheetParked.payment.text = parkingData.hour_cost.toString() + " ₽ / час"
                binding.bottomSheetMain.textParkingLots.text = "${parkingData.places} мест \n ${parkingData.free_places} свободных"
                binding.bottomSheetReserve.textParkingLots.text = "${parkingData.places} мест \n ${parkingData.free_places} свободных"


                if (parkingData.secure == 1) {
                    binding.bottomSheetMain.secure.setImageResource(R.drawable.ic_secure_active)
                    binding.bottomSheetReserve.secure.setImageResource(R.drawable.ic_secure_active)

                } else {
                    binding.bottomSheetMain.secure.setImageResource(R.drawable.ic_secure_inactive)
                    binding.bottomSheetReserve.secure.setImageResource(R.drawable.ic_secure_inactive)
                }

                if (parkingData.around_the_clock == 1) {
                    binding.bottomSheetMain.allDay.setImageResource(R.drawable.ic_247_active)
                    binding.bottomSheetReserve.allDay.setImageResource(R.drawable.ic_247_active)

                } else {
                    binding.bottomSheetMain.allDay.setImageResource(R.drawable.ic_247_inactive)
                    binding.bottomSheetReserve.allDay.setImageResource(R.drawable.ic_247_inactive)
                }

                if (parkingData.ev == 1) {
                    binding.bottomSheetMain.charge.setImageResource(R.drawable.ic_charge_active)
                    binding.bottomSheetReserve.charge.setImageResource(R.drawable.ic_charge_active)

                } else {
                    binding.bottomSheetMain.charge.setImageResource(R.drawable.ic_charge_inactive)
                    binding.bottomSheetReserve.charge.setImageResource(R.drawable.ic_charge_inactive)
                }

                if (parkingData.disabled == 1) {
                    binding.bottomSheetMain.disability.setImageResource(R.drawable.ic_disability_active)
                    binding.bottomSheetReserve.disability.setImageResource(R.drawable.ic_disability_active)

                } else {
                    binding.bottomSheetMain.disability.setImageResource(R.drawable.ic_disability_inactive)
                    binding.bottomSheetReserve.disability.setImageResource(R.drawable.ic_disability_inactive)
                }


//date, time, duration pickers setup
                date_picker.setOnClickListener {
                    getTimeDateCalendar()
                    DatePickerDialog(requireContext(), this, year, month, day).show()
                }

                time_picker.setOnClickListener {
                    getTimeDateCalendar()
                    TimePickerDialog(requireContext(), this, hour, minute, true).show()
                }


                //an array of possible durations for parking
                val parkingDurations = arrayOf("30 минут", "1 час", "3 часа", "1 день")

                duration_picker.setOnClickListener {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("Выбрать период")
                    builder.setItems(parkingDurations) { dialog, position ->
                        duration_picker.text = parkingDurations[position]
                    }
                    builder.show()
                }




//setting up bottomsheets
                reserve.setOnClickListener {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    bottomSheetParkedBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    bottomSheetReserveBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    binding.mapUi.uiMapParkingFAB.visibility = View.GONE


                    getTimeDateCalendar()

                    if (day < 10) {
                        if (month < 10) {
                            date_picker.text = "0$day/0$month/$year"
                        } else {
                            date_picker.text = "0$day/$month/$year"
                        }
                    } else {
                        if (month < 10) {
                            date_picker.text = "$day/0$month/$year"
                        } else {
                            date_picker.text = "$day/$month/$year"
                        }
                    }

                    if (hour < 10){
                        if (minute < 10){
                            time_picker.text = "0$hour:0$minute"
                        } else {
                            time_picker.text = "0$hour:$minute"
                        }
                    } else {
                        if (minute < 10){
                            time_picker.text = "$hour:0$minute"
                        } else {
                            time_picker.text = "$hour:$minute"
                        }
                    }
                }

                bottomSheetReserveBehavior.addBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback(){
                    override fun onStateChanged(bottomSheet: View, state: Int) {
                        when (state) {

                            BottomSheetBehavior.STATE_HIDDEN -> {}

                            BottomSheetBehavior.STATE_EXPANDED -> {
                                mapView!!.map.move(
                                    CameraPosition(Point(point.latitude - 0.0025,point.longitude), 16f, 0.0f, 0.0f),
                                    Animation(Animation.Type.SMOOTH, 0.5F),
                                    null)
                            }

                            BottomSheetBehavior.STATE_COLLAPSED ->{
                                mapView!!.map.move(
                                    CameraPosition(Point(point.latitude - 0.0001,point.longitude), 16f, 0.0f, 0.0f),
                                    Animation(Animation.Type.SMOOTH, 0.5F),
                                    null)
                            }

                            BottomSheetBehavior.STATE_DRAGGING -> {}
                            BottomSheetBehavior.STATE_SETTLING -> {}
                            BottomSheetBehavior.STATE_HALF_EXPANDED ->{}
                        }
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) { }
                })



                go_now.setOnClickListener {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    bottomSheetReserveBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    bottomSheetParkedBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    binding.mapUi.uiMapParkingFAB.visibility = View.GONE
                    seconds = 0
                    running = true
                }


                stop.setOnClickListener {
                    running = false
                }

                bottomSheetParkedBehavior.addBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback(){
                    override fun onStateChanged(bottomSheet: View, state: Int) {
                        when (state) {

                            BottomSheetBehavior.STATE_HIDDEN -> {}

                            BottomSheetBehavior.STATE_EXPANDED -> {
                                mapView!!.map.move(
                                    CameraPosition(Point(point.latitude - 0.0002,point.longitude), 16f, 0.0f, 0.0f),
                                    Animation(Animation.Type.SMOOTH, 0.5F),
                                    null)
                            }

                            BottomSheetBehavior.STATE_COLLAPSED ->{
                                mapView!!.map.move(
                                    CameraPosition(Point(point.latitude - 0.0001,point.longitude), 16f, 0.0f, 0.0f),
                                    Animation(Animation.Type.SMOOTH, 0.5F),
                                    null)
                            }

                            BottomSheetBehavior.STATE_DRAGGING -> {}
                            BottomSheetBehavior.STATE_SETTLING -> {}
                            BottomSheetBehavior.STATE_HALF_EXPANDED ->{}
                        }
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) { }
                })





                if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    binding.mapUi.uiMapParkingFAB.visibility = View.GONE
                    if (bottomSheetReserveBehavior.state != BottomSheetBehavior.STATE_COLLAPSED) {
                        bottomSheetReserveBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    }

                    if (bottomSheetParkedBehavior.state != BottomSheetBehavior.STATE_COLLAPSED) {
                        bottomSheetParkedBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    }
                }



                bottomSheetBehavior.addBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback(){
                    override fun onStateChanged(bottomSheet: View, state: Int) {
                        when (state) {

                            BottomSheetBehavior.STATE_HIDDEN -> {}

                            BottomSheetBehavior.STATE_EXPANDED -> {
                                lastState = "STATE_EXPANDED"
                                mapView!!.map.move(
                                    CameraPosition(Point(point.latitude - 0.0003,point.longitude), 16f, 0.0f, 0.0f),
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
                                lastState = "STATE_EXPANDED"
                            }
                        }
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) { }
                })

            }
            true
        }

    private fun parking(context: Context, mapObjects: MapObjectCollection, clusterizedCollection: ClusterizedPlacemarkCollection, point : Point, list : List<Point>, address : String, hour_cost : Int, id : Int, secure: Int, around_the_clock: Int, ev: Int, disabled: Int, places: Int, free_places: Int) {
        val polyline = mapObjects
            .addPolyline(Polyline(list))
            .apply { setStrokeColor(Color.rgb(13, 174, 252)) }

        val icon = clusterizedCollection.addPlacemark(
            point,
            ImageProvider.fromBitmap(
                drawSimpleBitmap(
                    "$hour_cost\u2006₽",
                    context
                )
            )
        )

        icon.addTapListener(parkingMapObjectTapListener)
        icon.setScaleFunction(listOf(PointF(1F, 0.5F)))
        icon.zIndex = 100.0f
        icon.userData = PinData(polyline, hour_cost, address, point, id, secure, around_the_clock, ev, disabled, places, free_places)

        clusterizedCollection.clusterPlacemarks(60.0, 15)
    }

    private fun parkingG(
        context: Context,
        mapObjects: MapObjectCollection,
        clusterizedCollection: ClusterizedPlacemarkCollection,
        point: Point,
        list: List<Point>,
        address: String,
        hour_cost: Int,
        id: Int,
        secure: Int,
        around_the_clock: Int,
        ev: Int,
        disabled: Int,
        places: Int,
        free_places: Int
    ) {
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
                drawSimpleBitmap(
                    "$hour_cost₽",
                    context
                )
            )
        )

        icon.addTapListener(parkingMapObjectTapListener)
        icon.setScaleFunction(listOf(PointF(1F, 0.5F)))
        icon.zIndex = 100.0f
        icon.userData = PinData(polygon, hour_cost, address, point, id, secure, around_the_clock, ev, disabled, places, free_places)

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


    private fun getTimeDateCalendar(){
        val cal = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH) + 1
        year = cal.get(Calendar.YEAR)
        hour = cal.get(Calendar.HOUR)
        minute = cal.get(Calendar.MINUTE)
    }


    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
        savedDay = day
        savedMonth = month + 1
        savedYear = year


        if (savedDay < 10) {
            if (savedMonth < 10) {
                date_picker.text = "0$savedDay/0$savedMonth/$savedYear"
            } else {
                date_picker.text = "0$savedDay/$savedMonth/$savedYear"
            }
        } else {
            if (savedMonth < 10) {
                date_picker.text = "$savedDay/0$savedMonth/$savedYear"
            } else {
                date_picker.text = "$savedDay/$savedMonth/$savedYear"
            }
        }
    }

    override fun onTimeSet(p0: TimePicker?, hour: Int, minute: Int) {
        savedHour = hour
        savedMinute = minute

        if (savedHour < 10){
            if (savedMinute < 10){
                time_picker.text = "0$savedHour:0$savedMinute"
            } else {
                time_picker.text = "0$savedHour:$savedMinute"
            }
        } else {
            if (savedMinute < 10){
                time_picker.text = "$savedHour:0$savedMinute"
            } else {
                time_picker.text = "$savedHour:$savedMinute"
            }
        }
    }





    // Save the state of the stopwatch
    // if it's about to be destroyed.
    override fun onSaveInstanceState(
        savedInstanceState: Bundle
    ) {
        savedInstanceState
            .putInt("seconds", seconds)
        savedInstanceState
            .putBoolean("running", running)
        savedInstanceState
            .putBoolean("wasRunning", wasRunning)
    }

    // Sets the NUmber of seconds on the timer.
    // The runTimer() method uses a Handler
    // to increment the seconds and
    // update the text view.
    private fun runTimer() {
        // Creates a new Handler
        val handler = Handler()
        // Call the post() method,
        // passing in a new Runnable.
        // The post() method processes
        // code without a delay,
        // so the code in the Runnable
        // will run almost immediately.
        handler.post(object : Runnable {
            override fun run() {
                val hours = seconds / 3600
                val minutes = seconds % 3600 / 60
                val secs = seconds % 60

                // Format the seconds into hours, minutes,
                // and seconds.
                val time = java.lang.String
                    .format(
                        Locale.getDefault(),
                        "%d:%02d:%02d", hours,
                        minutes, secs
                    )

                // Set the text view text.
                stopwatch.text = time

                // If running is true, increment the
                // seconds variable.
                if (running) {
                    seconds++
                }

                // Post the code again
                // with a delay of 1 second.
                handler.postDelayed(this, 1000)
            }
        })
    }

    override fun onDrivingRoutes(routes: MutableList<DrivingRoute>) {
        mapObjects!!.addPolyline(routes[0].geometry)
    }

    override fun onDrivingRoutesError(p0: Error) {
        Toast.makeText(context, "Не получилось построить маршрут", Toast.LENGTH_SHORT).show()
    }

    private fun createRoute(startPoint: Point, endPoint: Point) {
        val drivingOptions = DrivingOptions()
        val vehicleOptions = VehicleOptions()
        val requestPoints = ArrayList<RequestPoint>()
        requestPoints.add(
            RequestPoint(
                startPoint,
                RequestPointType.WAYPOINT,
                null
            )
        )
        requestPoints.add(
            RequestPoint(
                endPoint,
                RequestPointType.WAYPOINT,
                null
            )
        )
        drivingSession = drivingRouter?.requestRoutes(requestPoints, drivingOptions, vehicleOptions, this)
    }


}