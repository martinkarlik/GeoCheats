package com.example.geocheats.ui

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.geocheats.R
import com.example.geocheats.databinding.FragmentMapsBinding

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment() {

    private val callback = OnMapReadyCallback { googleMap ->
        val guess = LatLng(-34.0, 151.0)
        val someOtherPlace = LatLng(34.0, 22.0)

        googleMap.addMarker(MarkerOptions().position(guess).title("Marker in Sydney"))
        googleMap.addMarker(MarkerOptions().position(someOtherPlace).title("Marker somewhere else"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(guess))

        // https://github.com/googlemaps/android-samples/blob/master/ApiDemos/kotlin/app/src/gms/java/com/example/kotlindemos/BasicMapDemoActivity.kt
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}