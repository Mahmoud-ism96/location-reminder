package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import java.util.*

class SelectLocationFragment : BaseFragment(), OnMapReadyCallback {

    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding

    private lateinit var map: GoogleMap
    private val REQUEST_LOCATION_PERMISSION = 1

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var marker: Marker? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        getCurrentUserLocation()

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.selectButton.setOnClickListener {
            onLocationSelected()
        }

        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {

        map = googleMap

        setMapStyle(map)
        setPoiClick(map)
        setMarkClick(map)

        enableMyLocation()

    }

    private fun onLocationSelected() {
        _viewModel.latitude.value = marker?.position?.latitude
        _viewModel.longitude.value = marker?.position?.longitude
        _viewModel.reminderSelectedLocationStr.value = marker?.title
        _viewModel.navigationCommand.value = NavigationCommand.Back
    }

    private fun setPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener { poi ->
            map.clear()
            marker = map.addMarker(
                MarkerOptions().position(poi.latLng).title(poi.name).icon(
                    BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_AZURE
                    )
                )
            )
            marker?.showInfoWindow()

            binding.selectButton.visibility = View.VISIBLE
        }
    }

    private fun setMarkClick(map: GoogleMap) {
        map.setOnMapClickListener { latLng ->

            map.clear()

            val snippet = String.format(
                Locale.getDefault(), "Lat: %1$.5f, Long: %2$.5f", latLng.latitude, latLng.longitude
            )
            marker = map.addMarker(
                MarkerOptions().position(latLng).title(getString(R.string.dropped_pin))
                    .snippet(snippet)
            )

            marker?.showInfoWindow()

            binding.selectButton.visibility = View.VISIBLE
        }
    }

    private fun enableMyLocation(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Handler().postDelayed({
                getUserLocation()
            }, 1500)
            map.isMyLocationEnabled = true
            true
        } else {
            this.requestPermissions(
                arrayOf<String>(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ), REQUEST_LOCATION_PERMISSION
            )
            false
        }
    }

    @SuppressLint("MissingPermission")
    private fun getUserLocation() {
        try {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    location.let {
                        val userLocation = LatLng(location.latitude, location.longitude)
                        map.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                userLocation, 16.5f
                            )
                        )
                    }
                } else {
                    getCurrentUserLocation()
                }
            }
        } catch (err: Exception) {
            Toast.makeText(
                requireContext(),
                "Please turn your location back on",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getCurrentUserLocation() {
        fusedLocationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY,
            object : CancellationToken() {
                override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                    CancellationTokenSource().token

                override fun isCancellationRequested() = false
            }).addOnSuccessListener { location: Location? ->
            if (location == null) Toast.makeText(
                requireContext(), "Cannot get location.", Toast.LENGTH_SHORT
            ).show()
            else {
                val userLocation = LatLng(location.latitude, location.longitude)
                map.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        userLocation, 16.5f
                    )
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            getUserLocation()
            map.isMyLocationEnabled = true
        } else {
            Toast.makeText(
                requireContext(),
                "Failed To Get Current Location, please give permission to track your location",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setMapStyle(map: GoogleMap) {
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

}
