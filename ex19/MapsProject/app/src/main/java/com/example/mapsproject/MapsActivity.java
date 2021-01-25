package com.example.mapsproject;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.example.ngoctri.mapdirectionsample.DirectionsParser;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.navigation.NavigationView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.io.Reader;

import pl.droidsonroids.gif.GifImageView;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;


    EditText uInput;
    TextView lengthOfRoute;
    TextView timeOfRoute;
    Button oneWayGen;
    Button twoWayGen;
    NavigationView navView;
    DrawerLayout drawerLayout;
    GifImageView gif;
    LinearLayout textLayout;
    Button cancelButton;
    String positionID;

    double number;

    Boolean foundRoute = true;
    Boolean twoWay = false;
    Boolean manualPosition = false;

    String userInput;
    String timeTo;
    String distTo;
    String timeToD;
    String timeToD1;
    String timeToD2;

    double rangeFlaw = 0.5;
    double latPerDeg = 110.579;
    double longPerDeg = 111.320;
    double latToKm1;
    double longToKm1;
    double latToKm2;
    double longToKm2;
    double distanceTo;
    double inputNumber;
    double newDistTo;

    Marker marker;
    GoogleMap mMap;
    MarkerOptions markerOptions;

    Intent intent;

    LatLng curLoc;
    LatLng newLoc1;
    LatLng newLoc2; //0 angle
    LatLng currentLocation;

    List<Polyline> polyLines = new ArrayList<Polyline>();
    ArrayList<LatLng> listPoints;
    ArrayList points = null;
    PolylineOptions polylineOptions = null;

    TaskParserTwoWay parser;
    TaskParserOneWay parser1;
    TaskParserOneWay parser2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        if(!isNetworkAvailable()){
            //System.exit(0);
        }

        Places.initialize(getApplicationContext(), "AIzaSyA-_L7tHjo52UGl0ktyy34BwIhccRO2Ss8");
        PlacesClient placesClient = Places.createClient(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLocation();
        listPoints = new ArrayList<>();
        gif = (GifImageView) findViewById(R.id.gif);
        textLayout = (LinearLayout) findViewById(R.id.textLayout);
        lengthOfRoute = (TextView) findViewById(R.id.lengthTo);
        timeOfRoute = (TextView) findViewById(R.id.timeTo);
        cancelButton = (Button) findViewById(R.id.cancel);

        intent = getIntent();
        if(getIntent().getExtras()!=null){
            rangeFlaw = intent.getDoubleExtra("RANGEFLAW",0.5);
            manualPosition = intent.getBooleanExtra("SWITCH", false);
        } else {
            rangeFlaw = 0.5;
        }
        Log.d("CR", String.valueOf(rangeFlaw));
        Log.d("CR", String.valueOf(intent.getBooleanExtra("SWITCH",true)));


        gif.setVisibility(View.GONE);
        textLayout.setVisibility(View.GONE);
        cancelButton.setVisibility(View.GONE);

    }
    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    if(!manualPosition){
                        currentLocation = new LatLng(location.getLatitude(),location.getLongitude());
                    }
                    //Toast.makeText(getApplicationContext(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    assert supportMapFragment != null;
                    supportMapFragment.getMapAsync(MapsActivity.this);
                }
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        //**********
        //Xml catch
        uInput = (EditText) findViewById(R.id.userInput);
        oneWayGen = (Button) findViewById(R.id.oneWayButton);
        twoWayGen = (Button) findViewById(R.id.twoWayButton);
        navView = (NavigationView) findViewById(R.id.nav_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        oneWayGen.setOnClickListener(new View.OnClickListener() {
            //Pressing generate button
            @Override
            public void onClick(View v) {

                //Pressing one way
                if(currentLocation == null){
                    Toast.makeText(getBaseContext(),"Please select an origin point or use GPS", Toast.LENGTH_LONG).show();
                    return;
                }

                if(uInput.getText().toString().matches("") || Double.valueOf(uInput.getText().toString()) == 0){
                    Toast.makeText(getApplicationContext(), "Input needs to be bigger than 0", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(Double.valueOf(uInput.getText().toString()) <= rangeFlaw){
                    Toast.makeText(getBaseContext(),"Input cannot be same or bigger than the range flaw", Toast.LENGTH_LONG).show();
                    return;
                }
                if(!polyLines.isEmpty()){
                    for(Polyline line : polyLines)
                    {
                        line.remove();
                    }
                    polyLines.clear();
                }
                    newDistTo = 0;

                    gif.setVisibility(View.VISIBLE);
                    textLayout.setVisibility(View.GONE);
                    cancelButton.setVisibility(View.VISIBLE);
                    foundRoute = true;
                    twoWay = false;

                    parser1 = new TaskParserOneWay();
                    parser1.execute();

                cancelButton.setOnClickListener(new View.OnClickListener(){
                    //Pressing cancel button.
                    @Override
                    public void onClick(View v) {
                        Log.d("CR", "Pressed cancel one way");
                        parser1.cancel(true);

                        gif.setVisibility(View.GONE);
                        cancelButton.setVisibility(View.GONE);
                    }
                });


                //Close input after generate
                    View view = getCurrentFocus();
                    if(view != null){
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }

                }
        });
        twoWayGen.setOnClickListener(new View.OnClickListener(){
            //Pressing two way
            @Override
            public void onClick(View v) {
                if(currentLocation == null){
                    Toast.makeText(getBaseContext(),"Please select an origin point or use GPS", Toast.LENGTH_LONG).show();
                    return;
                }
                if(uInput.getText().toString().matches("") || Integer.valueOf(uInput.getText().toString()) == 0){
                    Toast.makeText(getApplicationContext(), "Invalid input", Toast.LENGTH_SHORT);
                    return;
                }
                if(!polyLines.isEmpty()){
                    for(Polyline line : polyLines)
                    {
                        line.remove();
                    }
                    polyLines.clear();
                }
                gif.setVisibility(View.VISIBLE);
                textLayout.setVisibility(View.GONE);
                cancelButton.setVisibility(View.VISIBLE);
                newDistTo = 0;



                parser = new TaskParserTwoWay();
                parser.execute();
                twoWay = true;

                parser2 = new TaskParserOneWay();
                parser2.execute();


            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener(){
            //Pressing cancel button.
            @Override
            public void onClick(View v) {
                Log.d("CR", "Pressed cancel");
                if(parser != null) {
                    //Two way parser
                    parser.cancel(true);
                }
                //One way parser
                parser2.cancel(true);
                if(!polyLines.isEmpty()){
                    for (Polyline line : polyLines) {
                        line.remove();
                        polyLines.remove(line);
                    }
                }

                cancelButton.setVisibility(View.GONE);
                gif.setVisibility(View.GONE);
            }
        });



        //**********
        //First marker
        if(!manualPosition) {
            LatLng latLng = new LatLng(currentLocation.latitude,currentLocation.longitude);
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("I am here!");
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
            googleMap.addMarker(markerOptions);
        } else {
            //Do search for input
            //Search bar
            AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                    getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG, Place.Field.ID, Place.Field.NAME));

            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {
                    Log.d("CR",String.valueOf(place.getId()));
                    if(place.getId() != null){
                        positionID = place.getId();

                        MarkerOptions markerOptions = new MarkerOptions().position(place.getLatLng());
                        googleMap.animateCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 12));
                        mMap.addMarker(markerOptions);

                        currentLocation = new LatLng(place.getLatLng().latitude,place.getLatLng().longitude);
                        Log.d("CR", "Place: " + place.getName() + ", " + place.getId());
                        Log.d("CR", place.getLatLng().toString());
                    }

                }

                @Override
                public void onError(Status status) {
                    Log.i("CR", "An error occurred: " + status);
                }
            });

        }
    }

    public void OneWayPoint(){
        //Makes new latlng variables and logs them
        //One way route
        userInput = uInput.getText().toString();
        number = Integer.valueOf(userInput);
        inputNumber = number;

        double angle = Math.toRadians(Math.random()*360);
        double x = Math.cos(angle)*(number);
        double y = Math.sin(angle)*(number);

        latToKm1 = y / latPerDeg;
        longToKm1 = x / longPerDeg;

        Log.d("CR", String.valueOf(number));
        double dist = Math.sqrt(Math.pow(x,2)+Math.pow(y,2));
        Log.d("CR", "X " + String.valueOf(x));
        Log.d("CR", "Y " + String.valueOf(y));
        curLoc = new LatLng(currentLocation.latitude,currentLocation.longitude);
        newLoc1 = new LatLng(currentLocation.latitude+ latToKm1, currentLocation.longitude+ longToKm1);
        twoWay = false;
    }
    public void TwoWayPoint(){
        //Makes new latlng variables and logs them
        //Two way route


        userInput = uInput.getText().toString();
        number = Double.valueOf(userInput);
        inputNumber = number;
        double angle = Math.floor(Math.random() * Math.floor(6));

        double randomRad = Math.toRadians(Math.random()*360);

        double x1 = Math.cos(angle+randomRad)*(number/3);
        double y1 = Math.sin(angle+randomRad)*(number/3);

        double x2 = Math.cos(0+randomRad)*(number/3);
        double y2 = Math.sin(0+randomRad)*(number/3);
        Log.d("CR", "1stAngle: " +  angle);
        Log.d("CR", "2ndAngle" + angle);

        latToKm1 = y1 / latPerDeg;
        longToKm1 = x1 / longPerDeg;
        latToKm2 = y2 / latPerDeg;
        longToKm2 = x2 / longPerDeg;




        //Log.d("CR", "DistanceTwo " + String.valueOf());
        curLoc = new LatLng(currentLocation.latitude,currentLocation.longitude);
        newLoc1 = new LatLng(currentLocation.latitude + latToKm1, currentLocation.longitude + longToKm1);
        newLoc2 = new LatLng(currentLocation.latitude+ latToKm2, currentLocation.longitude+ longToKm2);


    }
    @Override
    //Check for permissions
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLocation();
                }
                break;
        }
    }

    public String requestDirectionURLOneWay(LatLng origin, LatLng dest){
        //Url for requesting
        Log.d("CR", "origin: " + origin.latitude + ", " + origin.longitude);
        Log.d("CR", "dest: " + dest.longitude + ", " + origin.longitude);

        String str_org;
        if(manualPosition){
            str_org = "origin=place_id:" + positionID;
        } else {
            str_org = "origin=" + origin.latitude +","+origin.longitude;
        }
        //Value of destination
        String str_dest = "destination=" + dest.latitude+","+dest.longitude;
        //Set value enable the sensor
        String sensor = "sensor=false";
        //Mode for find direction
        String mode = "mode=walking";
        //Build the full param
        String param = str_org +"&" + str_dest + "&" +sensor+"&" +mode;
        //Output format
        String output = "json";
        //Create url to request
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?"+ str_org + "&" + str_dest + "&" + mode + "&" + "key=AIzaSyA-_L7tHjo52UGl0ktyy34BwIhccRO2Ss8";
        Log.d("Exce", url);

        return url;
    }
    public String requestDirectionURLTwoWay(LatLng origin, LatLng dest){
        //Url for requesting
        Log.d("CR", "origin1: " + origin.latitude + ", " + origin.longitude);
        Log.d("CR", "dest1: " + dest.longitude + ", " + origin.longitude);
        Log.d("CR", "viaorigin: " + origin.latitude + ", " + origin.longitude);
        Log.d("CR", "viadest: " + newLoc2.longitude + ", " + origin.longitude);

        //Value of origin
        String str_org;
        if(manualPosition){
            str_org = "origin=place_id:" + positionID;
        } else {
            str_org = "origin=" + origin.latitude +","+origin.longitude;
        }
        //Value of destination
        String str_dest = "destination=" + dest.latitude+","+dest.longitude;
        //Value of destination2
        String str_dest1 = "destination=" + origin.latitude + origin.longitude;
        //Set via point
        String str_via = "via:" + newLoc2.latitude+","+newLoc2.longitude;
        //Set value enable the sensor
        String sensor = "sensor=false";
        //Mode for find direction
        String mode = "mode=walking";
        //Output format
        String output = "json";
        //Create url to request
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?"+ str_org + "&" + str_dest + "&waypoints=" + str_via + "&"  + mode + "&" + "key=AIzaSyA-_L7tHjo52UGl0ktyy34BwIhccRO2Ss8";
        Log.d("Exce", url);

        return url;
    }

    public String requestDirections(String reqUrl){
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try{
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            //Response result
            inputStream = httpURLConnection.getInputStream();
            Reader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while((line = bufferedReader.readLine()) != null){
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStream.close();
            inputStreamReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            httpURLConnection.disconnect();
        }
        return responseString;
    }


    public class TaskParserOneWay extends AsyncTask<String, Void, List<List<HashMap<String, String>>> >{
        //Task after getting response.

        List<List<HashMap<String, String>>> routes = null;
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            timeToD1 = "";
            foundRoute = true;
            //Whole algorithm inside Task type. Won't work otherwise.
            while (foundRoute) {
                if(isCancelled()) break;
                //Cooldown for search
                try{
                    Thread.sleep(2000);
                } catch(InterruptedException ie){
                    Log.d("CR", "Sleep fail");
                }
                if(!twoWay){
                    OneWayPoint();
                }
                String url = requestDirectionURLOneWay(curLoc,newLoc1);
                String responseString = "";
                responseString = requestDirections(url);

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(responseString);
                    DirectionsParser directionsParser = new DirectionsParser();
                    routes = directionsParser.parse(jsonObject);
                    JSONArray routeArray = jsonObject.getJSONArray("routes");
                    JSONObject routes1 = routeArray.getJSONObject(0);

                    JSONArray newTempARr = routes1.getJSONArray("legs");
                    JSONObject newDisTimeOb = newTempARr.getJSONObject(0);

                    JSONObject distOb = newDisTimeOb.getJSONObject("distance");
                    JSONObject timeOb = newDisTimeOb.getJSONObject("duration");

                    String distStr = distOb.getString("text").replace(" km", "");
                    distanceTo = Double.valueOf(distStr);
                    String timeStr;

                    timeStr = timeOb.getString("text");

                    timeToD = timeStr;
                    timeTo = timeOb.getString("text"); //Copy to textbox
                    distTo = distOb.getString("text"); //Copy to textbox

                    Log.d("CR", distStr);
                    Log.d("CR", distOb.getString("text"));
                    Log.d("CR", timeOb.getString("text"));

                    if (twoWay || inputNumber-rangeFlaw < distanceTo && distanceTo < inputNumber + rangeFlaw){
                        //TODO: Korjaa twowaylle
                        foundRoute = false;
                        timeToD1 = timeStr;
                        newDistTo += distanceTo;

                        Log.d("CR", "FOUND");

                    } else {
                        Log.d("CR", "NOT FOUND");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //['rows'][0]['elements'][0]['distance']['text']
            }

            return routes;

        }


        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists){
            AddPoints(lists);
            FinishRoute();
        }

    }

    public class TaskParserTwoWay extends AsyncTask<String, Void, List<List<HashMap<String, String>>> >{
        //Task after getting response.

        List<List<HashMap<String, String>>> routes = null;
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            timeToD2 = "";
            foundRoute = true;
            //Whole algorithm inside Task type. Won't work otherwise.
            while (foundRoute) {
                if(isCancelled()) break;
                try{
                    Thread.sleep(2000);
                } catch(InterruptedException ie){
                    Log.d("CR", "Sleep fail");
                }
                TwoWayPoint();
                String url = requestDirectionURLTwoWay(curLoc, newLoc1);
                String responseString = "";
                responseString = requestDirections(url);

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(responseString);
                    DirectionsParser directionsParser = new DirectionsParser();
                    routes = directionsParser.parse(jsonObject);
                    JSONArray routeArray = jsonObject.getJSONArray("routes");
                    JSONObject routes1 = routeArray.getJSONObject(0);

                    JSONArray newTempARr = routes1.getJSONArray("legs");
                    JSONObject newDisTimeOb = newTempARr.getJSONObject(0);

                    JSONObject distOb = newDisTimeOb.getJSONObject("distance");
                    JSONObject timeOb = newDisTimeOb.getJSONObject("duration");

                    String distStr = distOb.getString("text").replace(" km", "");
                    distanceTo = Double.valueOf(distStr);
                    String timeStr;

                    timeStr = timeOb.getString("text");


                    timeToD = timeStr;

                    timeTo = timeOb.getString("text"); //Copy to textbox
                    distTo = distOb.getString("text"); //Copy to textbox

                    Log.d("CR", distStr);
                    Log.d("CR", timeStr);
                    Log.d("CR", distOb.getString("text"));
                    Log.d("CR", timeOb.getString("text"));

                    //TODO: Korjaa
                    if ((inputNumber / 3 * 2) - (rangeFlaw/3 * 2) < distanceTo && distanceTo < (inputNumber / 3 * 2) + (rangeFlaw/3 * 2)) {
                        foundRoute = false;
                        newDistTo += distanceTo;
                        timeToD2 = timeStr;
                        Log.d("CR", "FOUND");

                    } else {
                        Log.d("CR", "NOT FOUND");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //['rows'][0]['elements'][0]['distance']['text']
            }

            return routes;

        }


        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists){
            AddPoints(lists);
        }

    }

    public void AddPoints(List<List<HashMap<String, String>>> lists){

        //Display route on map
        points = new ArrayList();
        polylineOptions = new PolylineOptions();

        //Prepare options and points for drawing
        for (List<HashMap<String, String>> path : lists){
            //points = new ArrayList();
            //polylineOptions = new PolylineOptions();

            for(HashMap<String, String> point : path){
                double lat = Double.parseDouble(point.get("lat"));
                double lon = Double.parseDouble(point.get("lon"));

                points.add(new LatLng(lat,lon));

            }
            polylineOptions.addAll(points);
            polylineOptions.width(15);
            polylineOptions.color(Color.BLUE);
            polylineOptions.geodesic(true);

        }
        //Add the polyline to connect beginning and end
        //polyLineM = polylineOptions;
        DrawPolyLine(polylineOptions);
    }

    private void DrawPolyLine(PolylineOptions polylineOptions) {
        //Add polyline
        if (polylineOptions != null){



            polyLines.add(this.mMap.addPolyline(polylineOptions));
            //FinishRoute();

        } else {
            Toast.makeText(getApplicationContext(), "Direction not found", Toast.LENGTH_SHORT).show();
        }
    }

    public void FinishRoute(){
        //TODO: Voidaan käyttää kutsumaan metodeja onnistuneen reitin löydyttyä (e. lisätä reitti listaan)
        //Draw marker
        markerOptions = new MarkerOptions();
        markerOptions.position(newLoc1);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        if (listPoints.size() == 0){
            marker = mMap.addMarker(markerOptions);
            listPoints.add(newLoc1);
        } else {
            marker.setPosition(newLoc1);
        }

        //Visibility change and text for objects
        lengthOfRoute.setText("Length: " + newDistTo + " km");
        if(twoWay) {
            timeOfRoute.setText("Duration (walking): " + timeToD1 + " + " + timeToD2);
        } else {
            timeOfRoute.setText("Duration (walking): " + timeToD1);
        }
        gif.setVisibility(View.GONE);
        textLayout.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.GONE);

    }

}



