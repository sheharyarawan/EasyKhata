package com.example.easykhata.ui.fragments.account
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easykhata.R
import com.example.easykhata.data.models.UiState
import com.example.easykhata.data.models.account.SelectedLocation
import com.example.easykhata.databinding.FragmentKarobarAddressBinding
import com.example.easykhata.ui.adapters.LocationSearchAdapter
import com.example.easykhata.ui.viewModels.account.KarobarAddressViewModel
import com.example.easykhata.ui.viewModels.account.ProfileViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
@AndroidEntryPoint
class KarobarAddressFragment : Fragment(R.layout.fragment_karobar_address) {
    private lateinit var binding: FragmentKarobarAddressBinding
    private lateinit var mapView: MapView
    private var mapLibreMap: MapLibreMap? = null
    private var isUpdatingSearchText = false
    private val addressViewModel: KarobarAddressViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by activityViewModels()
    private lateinit var locationSearchAdapter: LocationSearchAdapter
    private val locationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val granted =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                        permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            if (granted) {
                getCurrentLocation()
            } else if (isAdded) {
                Toast.makeText(
                    requireContext(),
                    "Location permission is required to find your current location",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentKarobarAddressBinding.bind(view)
        mapView = binding.locationMap
        mapView.onCreate(savedInstanceState)
        profileViewModel.resetUpdateBusinessInfoState()
        setupLocationSearchRecyclerView()
        setupLocationSearch()
        observeSearchState()
        observeUpdateBusinessInfoState()
        setUpMap()
        setUpClicks()
        profileViewModel.loadActiveBusinessInfo()
    }
    private fun setupLocationSearchRecyclerView() {
        locationSearchAdapter = LocationSearchAdapter { location ->
            addressViewModel.onLocationSelected(
                SelectedLocation(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    address = location.address
                )
            )
            isUpdatingSearchText = true
            binding.etSearchLocation.setText(location.name)
            isUpdatingSearchText = false
            binding.locationSearchResultsRv.visibility = View.GONE
            binding.etSearchLocation.clearFocus()
            mapLibreMap?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(location.latitude, location.longitude),
                    16.0
                )
            )
        }
        binding.locationSearchResultsRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = locationSearchAdapter
        }
    }
    private fun setupLocationSearch() {
        binding.etSearchLocation.doAfterTextChanged { text ->
            if (isUpdatingSearchText) return@doAfterTextChanged
            addressViewModel.searchLocations(text.toString())
        }
    }
    private fun observeSearchState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                addressViewModel.searchState.collect { state ->
                    when (state) {
                        is UiState.Idle -> {
                            hideSearchResults()
                        }
                        is UiState.Loading -> {
                            hideSearchResults()
                        }
                        is UiState.Success -> {
                            val results = state.data
                            locationSearchAdapter.submitList(results)
                            binding.locationSearchResultsRv.visibility =
                                if (results.isNotEmpty() &&
                                    binding.etSearchLocation.hasFocus()
                                ) {
                                    View.VISIBLE
                                } else {
                                    View.GONE
                                }
                        }
                        is UiState.Error -> {
                            hideSearchResults()
                            Toast.makeText(
                                requireContext(),
                                state.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }
    private fun hideSearchResults() {
        binding.locationSearchResultsRv.visibility = View.GONE
    }
    private fun observeUpdateBusinessInfoState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                profileViewModel.updateBusinessInfoState.collect { state ->
                    when (state) {
                        is UiState.Idle -> {
                            binding.btnConfirmLocation.isEnabled = true
                            hideProgress()
                        }
                        is UiState.Loading -> {
                            binding.btnConfirmLocation.isEnabled = false
                            showProgress()
                        }
                        is UiState.Success -> {
                            hideProgress()
                            binding.btnConfirmLocation.isEnabled = true
                            Toast.makeText(
                                requireContext(),
                                "Business address updated successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            profileViewModel.resetUpdateBusinessInfoState()
                            findNavController().navigateUp()
                        }
                        is UiState.Error -> {
                            hideProgress()
                            binding.btnConfirmLocation.isEnabled = true
                            Toast.makeText(
                                requireContext(),
                                state.message,
                                Toast.LENGTH_LONG
                            ).show()
                            profileViewModel.resetUpdateBusinessInfoState()
                        }
                    }
                }
            }
        }
    }
    private fun setUpMap() {
        mapView.getMapAsync { map ->
            mapLibreMap = map
            map.cameraPosition = CameraPosition.Builder()
                .target(LatLng(31.5204, 74.3587))
                .zoom(12.0)
                .build()
            map.setStyle(
                "https://tiles.openfreemap.org/styles/liberty"
            )
            map.addOnCameraIdleListener {
                val center = map.cameraPosition.target
                addressViewModel.selectLocationFromCoordinates(
                    latitude = center!!.latitude,
                    longitude = center.longitude
                )
            }
            requestLocationPermission()
        }
    }
    private fun requestLocationPermission() {
        val fineLocationGranted =
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        val coarseLocationGranted =
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        if (fineLocationGranted || coarseLocationGranted) {
            getCurrentLocation()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        val fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            null
        ).addOnSuccessListener { location ->
            if (!isAdded) return@addOnSuccessListener
            if (location != null) {
                val currentLocation = LatLng(
                    location.latitude,
                    location.longitude
                )
                mapLibreMap?.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        currentLocation,
                        15.0
                    )
                )

            } else {
                Toast.makeText(
                    requireContext(),
                    "Unable to get current location",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }.addOnFailureListener {
            if (!isAdded) return@addOnFailureListener
            Toast.makeText(
                requireContext(),
                "Failed to get current location",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    private fun setUpClicks() {
        binding.apply {
            locationToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            btnZoomIn.setOnClickListener {
                mapLibreMap?.animateCamera(
                    CameraUpdateFactory.zoomIn()
                )
            }
            btnZoomOut.setOnClickListener {
                mapLibreMap?.animateCamera(
                    CameraUpdateFactory.zoomOut()
                )
            }
            btnConfirmLocation.setOnClickListener {
                confirmSelectedLocation()
            }
        }
    }
    private fun confirmSelectedLocation() {
        val selectedLocation =
            (addressViewModel.selectedLocation.value as? UiState.Success)?.data
        if (selectedLocation == null) {
            Toast.makeText(
                requireContext(),
                "Please select a location first",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val business =
            (profileViewModel.businessInfoState.value as? UiState.Success)?.data
        if (business == null) {
            Toast.makeText(
                requireContext(),
                "Business information is still loading. Please try again.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        profileViewModel.updateActiveBusinessInfo(
            businessName = business.businessName,
            businessType = business.businessType,
            businessAddress = selectedLocation.address
        )
    }
    private fun showProgress() {
        binding.expenseProgressOverlay.visibility = View.VISIBLE
    }
    private fun hideProgress() {
        binding.expenseProgressOverlay.visibility = View.GONE
    }
    override fun onStart() {
        super.onStart()
        if (::mapView.isInitialized) mapView.onStart()
    }
    override fun onResume() {
        super.onResume()
        if (::mapView.isInitialized) mapView.onResume()
    }
    override fun onPause() {
        if (::mapView.isInitialized) mapView.onPause()
        super.onPause()
    }
    override fun onStop() {
        if (::mapView.isInitialized) mapView.onStop()
        super.onStop()
    }
    override fun onLowMemory() {
        super.onLowMemory()
        if (::mapView.isInitialized) mapView.onLowMemory()
    }
    override fun onSaveInstanceState(outState: Bundle) {
        if (::mapView.isInitialized) {
            mapView.onSaveInstanceState(outState)
        }
        super.onSaveInstanceState(outState)
    }
    override fun onDestroyView() {
        if (::mapView.isInitialized) {
            mapView.onDestroy()
        }
        mapLibreMap = null
        super.onDestroyView()
    }
}