package com.veer.taxisample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.veer.taxisample.Utils.PermissionUtils;

public class SplashScreen extends AppCompatActivity {
  private String[] requiredPermissions =
      { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION };
  private final int Request_Multiple_permissions = 1;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash_screen);
    askforPermission();
  }


  private void askforPermission() {
    if (PermissionUtils.isRequestPermissionRequired()) {
      if (PermissionUtils.checkAndrequestPermissions(this, requiredPermissions, 1)) {
        navigateToMain();
      } else {
        ActivityCompat.requestPermissions(this, requiredPermissions, 1);
      }
    } else {
      //continue with the normal flow
      navigateToMain();
    }
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    switch (requestCode) {
      case Request_Multiple_permissions: {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          // permissions granted.
          navigateToMain();
        } else {
          Toast.makeText(this, "Permission required to show property locations", Toast.LENGTH_SHORT)
              .show();
        }
      }
    }
  }

  private void navigateToMain() {
    new Handler().postDelayed(new Runnable() {
      @Override public void run() {
        Intent intent = new Intent(SplashScreen.this, MapScreen.class);
        startActivity(intent);
        finish();
      }
    },3000);

  }
}
