package com.example.permissao

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var view : View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        view = findViewById(R.id.bt_load_img)
    }

    fun callReadFromSDCard(view: View) {}
    fun callWriteOnSDCard(view: View) {}
    fun callLoadImage(view: View) {}

    private val locationListener: LocationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    Toast.makeText(applicationContext,
                            "Lat: $location.latitude | Long: $location.longitude",
                            Toast.LENGTH_SHORT).show()
                }
                override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            }

    private fun readMyCurrentCoordinates() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!isGPSEnabled && !isNetworkEnabled) {
            snackbarShow("Ative os serviços necessários", view, this)
        } else {
            if (isGPSEnabled) {
                try {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            5000L, 0f, locationListener)
                } catch(ex: SecurityException) {
                    Log.d("Permissao", "Security Exception")
                }
            }
            else if (isNetworkEnabled) {
                try {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            5000L, 0f, locationListener)
                } catch(ex: SecurityException) {
                    Log.d("Permissao", "Security Exception")
                }
            }
        }
    }

    private fun snackbarShow(msg : String, view : View, context : Context){
        Snackbar.make(context, view, msg, Snackbar.LENGTH_LONG).show()
    }

    val REQUEST_PERMISSIONS_CODE = 128
    fun callAccessLocation(view: View?) {
        val permissionAFL = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
        val permissionACL = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)

        if (permissionAFL != PackageManager.PERMISSION_GRANTED &&
                permissionACL != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                callDialog("É preciso liberar ACCESS_FINE_LOCATION",
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
            } else {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_PERMISSIONS_CODE)
            }
        } else {
            readMyCurrentCoordinates()
        }
    }

    private fun callDialog(mensagem: String, permissions: Array<String>) {
        var mDialog = AlertDialog.Builder(this)
                .setTitle("Permissão")
                .setMessage(mensagem)
                .setPositiveButton("Ok")
                { dialog, id ->
                    ActivityCompat.requestPermissions(
                            this@MainActivity, permissions,
                            REQUEST_PERMISSIONS_CODE)
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel")
                { dialog, id ->
                    dialog.dismiss()
                }
        mDialog.show()
    }
}