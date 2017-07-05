package com.veer.taxisample;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.veer.taxisample.Utils.DirectionsJSONParser;
import com.veer.taxisample.Utils.MapUtil;
import com.veer.taxisample.Utils.PopMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONObject;

/**
 * Created by Brajendr on 11/7/2016.
 */

public class MapScreen extends MapContainerActivity
    implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
    LocationListener {
  private GoogleMap googleMap;
  private LinearLayout drawerLayout;
  private GoogleApiClient googleApiClient;
  private LocationRequest mLocationRequest;
  Location mCurrentLocation;
  Marker mDestinationMarker;
  String mLastUpdateTime;
  private static final long INTERVAL = 1000 * 10;
  private static final long FASTEST_INTERVAL = 1000 * 5;
  private int dialog_count = 0;
  private final int REQUEST_CHECK_SETTINGS = 101;
  private ProgressDialog progress;
  private boolean isMapLoaded = false;
  private boolean shouldshowDialog = true;
  Handler handler=new Handler();

  protected void createLocationRequest() {
    mLocationRequest = new LocationRequest();
    mLocationRequest.setInterval(INTERVAL);
    mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
  }

  @Override protected void onStart() {
    super.onStart();
    if (googleApiClient != null) {
      googleApiClient.connect();
    }
  }

  @Override protected void onStop() {
    super.onStop();
    if (googleApiClient != null) {
      googleApiClient.disconnect();
    }
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    drawerLayout = getLayout();
    getData();
    createLocationRequest();
    googleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .build();
  }

  private void getData() {
    if (getIntent() != null) {
      shouldshowDialog = getIntent().getBooleanExtra("showDialog", true);
    }
  }

  @Override protected void startDemo() {
    googleMap = getMap();
    googleMap.getUiSettings().setMapToolbarEnabled(true);
    isMapLoaded = true;
  }

  @Override public void onConnected(@Nullable Bundle bundle) {
    //if (!Utility.isAgent(this))
    checkLocationProviders();
  }

  protected void checkLocationProviders() {
    LocationSettingsRequest.Builder builder =
        new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);

    PendingResult<LocationSettingsResult> result =
        LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());

    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
      @Override public void onResult(LocationSettingsResult locationSettingsResult) {

        final Status status = locationSettingsResult.getStatus();
        final LocationSettingsStates LS_state = locationSettingsResult.getLocationSettingsStates();
        switch (status.getStatusCode()) {
          case LocationSettingsStatusCodes.SUCCESS:
            // All location settings are satisfied. The client can initialize location
            // requests here.
            GetUserLocation();

            break;
          case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
            // Location settings are not satisfied. But could be fixed by showing the user
            // a dialog.
            try {
              // Show the dialog by calling startResolutionForResult(),
              // and check the result in onActivityResult().
              status.startResolutionForResult(MapScreen.this, REQUEST_CHECK_SETTINGS);
            } catch (IntentSender.SendIntentException e) {
              // Ignore the error.
            }
            break;
          case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
            // Location settings are not satisfied. However, we have no way to fix the
            // settings so we won't show the dialog.
            PopMessage.makeshorttoast(MapScreen.this, "Oops, we can not ge the location");
            finish();
            break;
        }
      }
    });
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
    switch (requestCode) {
      case REQUEST_CHECK_SETTINGS:
        switch (resultCode) {
          case Activity.RESULT_OK:
            // All required changes were successfully made
            GetUserLocation();//FINALLY YOUR OWN METHOD TO GET YOUR USER LOCATION HERE
            break;
          case Activity.RESULT_CANCELED:
            // The user was asked to change settings, but chose not to
            PopMessage.makeshorttoast(this, "Location required !");
            break;
          default:
            break;
        }
        break;
    }
  }

  private void GetUserLocation() {
    try {
      PendingResult<Status> pendingResult =
          LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,
              mLocationRequest, this);
    } catch (SecurityException e) {

    }
  }

  @Override public void onConnectionSuspended(int i) {

  }

  @Override public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

  }

  @Override public void onLocationChanged(Location location) {
    mCurrentLocation = location;
    if (dialog_count < 1) {
      dialog_count++;
      if (isMapLoaded) {
        MapUtil.centerMapOnMyLocation(googleMap, mCurrentLocation, this, shouldshowDialog);
      } else {
        new Handler().postDelayed(new Runnable() {
          @Override public void run() {
            if (isMapLoaded) {
              MapUtil.centerMapOnMyLocation(googleMap, mCurrentLocation, MapScreen.this,
                  shouldshowDialog);
            }
          }
        }, 500);
      }
    }
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_map, menu);

    final MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
    final SearchView searchView = (SearchView) myActionMenuItem.getActionView();
    searchView.setQueryHint("Search address");
    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override public boolean onQueryTextSubmit(String query) {
        if (!query.isEmpty()) {
          googleMap.clear();
          MapUtil.centerMapOnMyLocation(googleMap, mCurrentLocation, MapScreen.this,
              shouldshowDialog);
          LatLng destination;
              mDestinationMarker= MapUtil.getAddress(query, MapScreen.this, googleMap);
          destination=new LatLng(mDestinationMarker.getPosition().latitude,
              mDestinationMarker.getPosition().longitude);
          LatLng myLocation = null;
          myLocation = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
          new DownloadTask().execute(
              DirectionsJSONParser.getDirectionsUrl(myLocation, destination));
        }
        return false;
      }

      @Override public boolean onQueryTextChange(String s) {
        return false;
      }
    });

    return true;
  }

  // Fetches data from url passed
  private class DownloadTask extends AsyncTask<String, Void, String> {

    // Downloading data in non-ui thread
    @Override protected String doInBackground(String... url) {

      // For storing data from web service
      String data = "";

      try {
        // Fetching the data from web service
        Log.e("da",url[0]);
        data = DirectionsJSONParser.downloadUrl(url[0]);
      } catch (Exception e) {
        Log.d("Background Task", e.toString());
      }
      return data;
    }

    // Executes in UI thread, after the execution of
    // doInBackground()
    @Override protected void onPostExecute(String result) {
      super.onPostExecute(result);

      ParserTask parserTask = new ParserTask();

      // Invokes the thread for parsing the JSON data
      parserTask.execute(result);
    }
  }

  /**
   * A class to parse the Google Places in JSON format
   */
  private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

    // Parsing the data in non-ui thread
    @Override protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

      JSONObject jObject;
      List<List<HashMap<String, String>>> routes = null;

      try {
        Log.e("obj",jsonData[0]);
        jObject = new JSONObject(jsonData[0]);
        DirectionsJSONParser parser = new DirectionsJSONParser();

        // Starts parsing data
        routes = parser.parse(jObject);
      } catch (Exception e) {
        e.printStackTrace();
      }
      return routes;
    }

    // Executes in UI thread, after the parsing process
    @Override protected void onPostExecute(final List<List<HashMap<String, String>>> result) {
      ArrayList<LatLng> points = null;
      PolylineOptions lineOptions = null;
      MarkerOptions markerOptions = new MarkerOptions();
      String distance = "";
      String duration = "";

      if (result.size() < 1) {
        Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
        return;
      }

      // Traversing through all the routes
      for (int i = 0; i < result.size(); i++) {
        points = new ArrayList<LatLng>();
        lineOptions = new PolylineOptions();
        lineOptions.width(1.0f);

        // Fetching i-th route
        List<HashMap<String, String>> path = result.get(i);

        // Fetching all the points in i-th route
        for (int j = 0; j < path.size(); j++) {
          HashMap<String, String> point = path.get(j);

          if (j == 0) {    // Get distance from the list
            distance = (String) point.get("distance");
            continue;
          } else if (j == 1) { // Get duration from the list
            duration = (String) point.get("duration");
            continue;
          }

          double lat = Double.parseDouble(point.get("lat"));
          double lng = Double.parseDouble(point.get("lng"));
          LatLng position = new LatLng(lat, lng);

          points.add(position);
        }

        // Adding all the points in the route to LineOptions
        lineOptions.addAll(points);
        lineOptions.width(2);
        lineOptions.color(Color.RED);
      }

      //Toast.makeText(MapScreen.this,"Distance:" + distance + ", Duration:" + duration,
      //   Toast.LENGTH_LONG).show();

      final ArrayList<LatLng> finalPoints = points;
      Snackbar.make(drawerLayout, "Route mapped ", Snackbar.LENGTH_INDEFINITE)
          .setAction("Navigate", new View.OnClickListener() {
            @Override public void onClick(View view) {
              PopMessage.makeshorttoast(MapScreen.this, "Navigation started");
              navigateCab(finalPoints);
            }
          })
          .show();
      // Drawing polyline in the Google Map for the i-th route;
      googleMap.addPolyline(lineOptions);
    }
  }

  private void navigateCab(final ArrayList<LatLng> points) {
    update=new UpdateLoc(points);
    handler.postDelayed(update,100);
  }
  UpdateLoc update;

  class UpdateLoc implements Runnable {
    ArrayList<LatLng> points;
    int index;

    public void setLatLong(int index) {
      this.index=index;
    }

    public UpdateLoc(ArrayList<LatLng> p) {
      this.points=p;
      index=points.size()-1;
    }

    @Override public void run() {
      if(index>0)
      {
        mDestinationMarker.setPosition(points.get(index));
        update.setLatLong(--index);
        handler.postDelayed(update,100);
      }
      else
      {
        PopMessage.makeshorttoast(MapScreen.this,"Cab has reached you");
        handler.removeCallbacks(update);
      }
    }
  }


  private void drawLines(List<List<HashMap<String, String>>> result) {
    ArrayList<LatLng> points = null;
    PolylineOptions lineOptions = null;
    MarkerOptions markerOptions = new MarkerOptions();

    for (int i = 0; i < result.size(); i++) {
      points = new ArrayList<>();
      lineOptions = new PolylineOptions();

      // Fetching i-th route
      List<HashMap<String, String>> path = result.get(i);

      // Fetching all the points in i-th route
      for (int j = 0; j < path.size(); j++) {
        HashMap<String, String> point = path.get(j);

        double lat = Double.parseDouble(point.get("lat"));
        double lng = Double.parseDouble(point.get("lng"));
        LatLng position = new LatLng(lat, lng);

        points.add(position);
      }

      // Adding all the points in the route to LineOptions
      lineOptions.addAll(points);
      lineOptions.width(2);
      lineOptions.color(Color.RED);
    }
    googleMap.addPolyline(lineOptions);
  }
}
