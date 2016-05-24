package cn.fundview.app.tool;

import android.util.Log;

/**
 * Created by lict on 2016/5/12.
 */
public class LogUtils {

    public static void e(String msg) {

        if(Constants.DEBUG)
        Log.e(Constants.TAG,msg);
    }

    public static void e(String tag,String msg) {

        if(Constants.DEBUG)
            Log.e(tag,msg);
    }

    public static void d(String msg) {

        if(Constants.DEBUG)
            Log.d(Constants.TAG,msg);
    }

    public static void d(String tag,String msg) {

        if(Constants.DEBUG)
            Log.d(tag,msg);
    }

    public static void i(String msg) {

        if(Constants.DEBUG)
            Log.i(Constants.TAG,msg);
    }

    public static void i(String tag,String msg) {

        if(Constants.DEBUG)
            Log.i(tag,msg);
    }

    public static void v(String msg) {

        if(Constants.DEBUG)
            Log.v(Constants.TAG,msg);
    }

    public static void v(String tag,String msg) {

        if(Constants.DEBUG)
            Log.v(tag,msg);
    }
}
