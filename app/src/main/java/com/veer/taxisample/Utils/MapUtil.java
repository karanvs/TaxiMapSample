package com.veer.taxisample.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.DrawableRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.veer.taxisample.R;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Brajendr on 7/5/2017.
 */

public class MapUtil {

  public static Marker getAddress(String adderess, Context context, GoogleMap map) {
    Marker returnMarker = null;
    Geocoder geoCoder = new Geocoder(context, Locale.getDefault());
    try {
      List<Address> addresses = geoCoder.getFromLocationName(adderess, 5);
      if (addresses.size() > 0) {
        Double lat = (double) (addresses.get(0).getLatitude());
        Double lon = (double) (addresses.get(0).getLongitude());

        Log.d("lat-long", "" + lat + "......." + lon);
        LatLng user = new LatLng(lat, lon);
        /*used marker for show the location */
        returnMarker=getMarkerBitmapFromView(R.drawable.map_image, context, map, user,R.layout.layout_marker_car);
        // Move the camera instantly to hamburg with a zoom of 15.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(user, -0.5f));

        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return returnMarker;
  }

  public static void centerMapOnMyLocation(GoogleMap map, Location location, Context context,
      boolean show) {
    try {
      map.setMyLocationEnabled(true);
      if (location != null) showCurrentLocMarker(location, context, map, show);
    } catch (SecurityException e) {

    }
  }

  private static void showCurrentLocMarker(Location location, Context context, final GoogleMap map,
      boolean show) {
    LatLng myLocation = null;
    myLocation = new LatLng(location.getLatitude(), location.getLongitude());
    map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 18f));
    getMarkerBitmapFromView(R.drawable.map_image, context, map, myLocation,R.layout.layout_custom_marker);
  }


  public static Marker getMarkerBitmapFromView(@DrawableRes int resId, final Context context,
      final GoogleMap map, final LatLng myLocation,int la) {

    final View customMarkerView =
        ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
            la, null);
    final ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.profile_image);
    markerImageView.setVisibility(View.VISIBLE);

    customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
    customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(),
        customMarkerView.getMeasuredHeight());
    customMarkerView.buildDrawingCache();
    Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(),
        customMarkerView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(returnedBitmap);
    canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
    Drawable drawable = customMarkerView.getBackground();
    if (drawable != null) drawable.draw(canvas);
    customMarkerView.draw(canvas);
    Bitmap bitmap = returnedBitmap;
    return map.addMarker(new MarkerOptions().snippet("My Location")
        .position(myLocation)
        .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));

  }
}
