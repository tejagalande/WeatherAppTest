package com.example.weatherapptest

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.location.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    private lateinit var mFusedLocationClient : FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (!isLocationEnabled()){
            Toast.makeText(this,
            "Location is turned off. Please turn it ON.",
            Toast.LENGTH_SHORT).show()


//            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//            startActivity(intent)
            showAlertDialog()
        }
        else{
//            Toast.makeText(this,
//            "Location is already turned ON.",
//            Toast.LENGTH_SHORT).show()

            Dexter.withContext(this)
                    .withPermissions(
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                    .withListener(object : MultiplePermissionsListener{
                        override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                            if (p0!!.areAllPermissionsGranted()){

                                requestLocationData()

                            }

                            if (p0.isAnyPermissionPermanentlyDenied){
                                Toast.makeText(
                                        this@MainActivity,
                                        "You have denied location permission",
                                        Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onPermissionRationaleShouldBeShown
                                    (p0: MutableList<PermissionRequest>?, p1: PermissionToken?) {
                          showAlertDialog()
                        }
                    }).onSameThread().check()
        }

    }


    @SuppressLint("MissingPermission")
    private fun requestLocationData(){
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallBack,
                Looper.myLooper()
        )
    }

    private val mLocationCallBack = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult?) {
            val mLastLocation : Location = p0!!.lastLocation
            val latitude = mLastLocation.latitude
            Log.i("Current Latitude","$latitude")

            val longitude = mLastLocation.longitude
            Log.i("Current Longitude","$longitude")
        }
    }

    private fun showAlertDialog(){
        AlertDialog.Builder(this)
                .setMessage("It look like you have turned off permissions")
                .setPositiveButton(
                        "Go TO SETTING"
                ){ _, _ ->
                    try {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package",packageName,null)
                        intent.data = uri
                        startActivity(intent)
                    }
                    catch (e : ActivityNotFoundException){ e.printStackTrace() }

                }
                .setNegativeButton("Cancel"){
                    dialog, _ ->
                    dialog.dismiss()
                }.show()
    }

    private fun isLocationEnabled() : Boolean{

        val locationManager : LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        )
    }
}