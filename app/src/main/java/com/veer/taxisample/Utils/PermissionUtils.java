package com.veer.taxisample.Utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brajendr on 11/7/2016.
 */

public class PermissionUtils {

  public static boolean checkAndrequestPermissions(Activity activity, String[] permission,
      int requestCode) {
    int result;
    List<String> listPermissionsNeeded = new ArrayList<>();
    for (String p : permission) {
      result = ContextCompat.checkSelfPermission(activity, p);
      if (result != PackageManager.PERMISSION_GRANTED) {
        listPermissionsNeeded.add(p);
      }
    }
    if (!listPermissionsNeeded.isEmpty()) {
      ActivityCompat.requestPermissions(activity,
          listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), requestCode);
      return false;
    }
    return true;
  }

  public static boolean verifyPermissions(int[] grantResults) {
    // At least one result must be checked.
    if (grantResults.length < 1) {
      return false;
    }

    // Verify that each required permission has been granted, otherwise return false.
    for (int result : grantResults) {
      if (result != PackageManager.PERMISSION_GRANTED) {
        return false;
      }
    }
    return true;
  }

  public static boolean isRequestPermissionRequired() {
    int MyVersion = Build.VERSION.SDK_INT;
    if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
      return true;
    }
    return false;
  }
}
