package com.veer.taxisample;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

/**
 * Created by Brajendr on 11/7/2016.
 */

public abstract class MapContainerActivity extends AppCompatActivity implements OnMapReadyCallback {
  private GoogleMap mMap;
  private LinearLayout drawerLayout;

  protected int getLayoutId() {
    return R.layout.activity_map_container;
  }

  LinearLayout getLayout()
  {
    return drawerLayout;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(getLayoutId());
    drawerLayout=(LinearLayout) findViewById(R.id.container);
    setUpMap();
  }


  @Override protected void onResume() {
    super.onResume();
    setUpMap();
  }

  @Override public void onMapReady(GoogleMap map) {
    if (mMap != null) {
      return;
    }
    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    mMap = map;

    startDemo();
  }

  private void setUpMap() {
    ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
  }

  /**
   * Run the demo-specific code.
   */
  protected abstract void startDemo();

  protected GoogleMap getMap() {
    return mMap;
  }

}
