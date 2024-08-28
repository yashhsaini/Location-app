package com.example.locationbasics

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.locationbasics.ui.theme.LocationBasicsTheme
import android.Manifest
import android.widget.Toast
import androidx.activity.viewModels
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel:LocationViewModel=viewModel()
            LocationBasicsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    myapp(viewModel)
                }
            }
        }
    }
}

@Composable
fun myapp(viewModel: LocationViewModel){
    val context= LocalContext.current
    val locationUtils=LocationUtils(context)
    LocationDisplay(locationUtils = locationUtils,viewModel, context =context )
}

@Composable
fun LocationDisplay( locationUtils:LocationUtils,
                     viewModel: LocationViewModel,
                    context: Context){

    val location=viewModel.location.value

    val address =location?.let{
        locationUtils.reverseGeoLocation(location)
    }

    val requestPermissionLauncher= rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = {permissions->
            if (permissions[Manifest.permission.ACCESS_COARSE_LOCATION]==true
                &&permissions[Manifest.permission.ACCESS_FINE_LOCATION]==true){
                //we have permission
                locationUtils.requestLocationUpdates(viewModel = viewModel)
            }else{
                // request for permission
                val rationaleRequired=ActivityCompat.shouldShowRequestPermissionRationale(

                    context as MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )||
                        ActivityCompat.shouldShowRequestPermissionRationale(

                            context as MainActivity,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                //if permission is not provided then display why permission is needed
                if(rationaleRequired){
                    // first time user doesnt give permiss then this toast pop up
                    Toast.makeText(context,"Location permission mandatory or this feature may not work ",
                        Toast.LENGTH_LONG)
                        .show()
                }else{
                    //after first time user denied to give permission this toast pops up
                    Toast.makeText(context,"Location permission mandatory .Please enable it in android settings ",
                        Toast.LENGTH_LONG)
                        .show()

                }


            }
        })
    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        if(location!=null){
            Text(text = "address${location.latitude}${location.longitude}\n$address")

        }
        else {
            Text(text = "location not available")
        }
        Button(onClick = {
            if (locationUtils.hasLocationPermission(context)){
            //permission granted already
                locationUtils.requestLocationUpdates(viewModel)
            } else{
            //request location permission
            requestPermissionLauncher.launch(
                arrayOf( Manifest.permission.ACCESS_COARSE_LOCATION
                , Manifest.permission.ACCESS_FINE_LOCATION)
            )

            }
        })
        {
            Text(text = "Get location")
            
        }
        
    }
    
}