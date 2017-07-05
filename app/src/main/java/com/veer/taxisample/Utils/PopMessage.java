package com.veer.taxisample.Utils;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by abhinav on 01/06/2016.
 */
public class PopMessage {

    public static void makeshorttoast(Context context, String message )
    {
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show();
    }
    public static void makelongtoast(Context context, String message )
    {
        Toast.makeText(context,message, Toast.LENGTH_LONG).show();
    }


}
