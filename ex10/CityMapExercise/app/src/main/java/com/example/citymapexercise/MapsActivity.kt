package com.example.citymapexercise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    override fun onMarkerClick(p0: Marker?): Boolean {
        Toast.makeText(this, p0!!.title, Toast.LENGTH_LONG).show()
        return true
    }

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true

        // Add a marker in Sydney and move the camera
        val helsinki = LatLng(60.169857,24.938379)
        val turku = LatLng(60.451813,22.266630)
        val lahti = LatLng(60.982571,25.661640)
        val oulu = LatLng(65.012093,25.465076)
        val tampere = LatLng(61.497856,23.759633)

        mMap.addMarker(MarkerOptions().position(helsinki).title("Marker in Helsinki"))
        mMap.addMarker(MarkerOptions().position(turku).title("Marker in Turku"))
        mMap.addMarker(MarkerOptions().position(lahti).title("Marker in Lahti"))
        mMap.addMarker(MarkerOptions().position(oulu).title("Marker in Oulu"))
        mMap.addMarker(MarkerOptions().position(tampere).title("Marker in Tampere"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(helsinki))
    }
}
