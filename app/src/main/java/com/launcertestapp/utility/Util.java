package com.launcertestapp.utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.launcertestapp.R;

import java.nio.charset.StandardCharsets;

public class Util {

    private static Activity nt_act;
    private static ProgressDialog dialog;
    private static long mLastClickTime = 0;

    public static boolean isNetworkAvailable(Activity context) {

        boolean isWifiConn = false;
        boolean isMobileConn = false;
//        boolean isEthernetConn = false;

        try {
            if (context == null) return false;
            ConnectivityManager connMgr = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null) {
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    isWifiConn |= networkInfo.isConnected();
                }
                if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    isMobileConn |= networkInfo.isConnected();
                }
            } else {
                return false;
            }

            if (isWifiConn) return isWifiConn;
            else if (isMobileConn) return isMobileConn;
//            else if (isEthernetConn) return isEthernetConn;
            else {
                internetAlert(context);
                return false;
            }
        } catch (Exception e) {
            internetAlert(context);
            return false;
        }
    }

    public static void hideKeyboardFrom(Activity activity, View view) {
        try {
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showToastAlert(Activity act, String message, LinearLayout linearLayout) {
        if (linearLayout != null)
            linearLayout.setBackground(act.getResources().getDrawable(R.drawable.rounded_cover_with_alert));
        if (Util.hasData(message))
            Toast.makeText(act, message, Toast.LENGTH_LONG).show();
    }

    public static void showToast(Activity act, String message) {
        if (Util.hasData(message))
            Toast.makeText(act, message, Toast.LENGTH_LONG).show();
    }

    public static String isEmptyOrNull(String strVal) {
        return (strVal != null && !TextUtils.isEmpty(strVal) && !strVal.toLowerCase().equalsIgnoreCase("null")) ? strVal : "";
    }

    public static boolean hasData(String strVal) {
        return strVal != null && !TextUtils.isEmpty(strVal) && !strVal.toLowerCase().equalsIgnoreCase("null");
    }

    public static void internetAlert(Activity activity) {
        try {
            Util.showToast(activity, activity.getString(R.string.check_internet_connection));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean btnClickTime(double pauseTime) {
        // mis-clicking prevention, using threshold of 1000 ms
        pauseTime = pauseTime * 1000;
        if (SystemClock.elapsedRealtime() - mLastClickTime < pauseTime) {
            return false;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        return true;
    }

    public static String ifNull(String strValue, String strDefault) {
        return (strValue != null && !strValue.trim().equalsIgnoreCase("") && !strValue.toLowerCase().equalsIgnoreCase("null")) ? strValue : strDefault;
    }

    public static String encodeKey(String strKey) {
        // Sending side
        if (strKey.equalsIgnoreCase("") || strKey.equalsIgnoreCase("NA"))
            return strKey;
        else
            return Base64.encodeToString(Util.isEmptyOrNull(strKey).getBytes(StandardCharsets.UTF_8), Base64.DEFAULT).replace("\n", "");
    }

    public static String decodeKey(String strKey) {
        // Receiving side
        if (strKey.equalsIgnoreCase("") || strKey.equalsIgnoreCase("NA"))
            return strKey;
        else
            return new String(Base64.decode(Util.isEmptyOrNull(strKey), Base64.DEFAULT), StandardCharsets.UTF_8);
    }

    public static void LogE(String strKey, String strMessage) {
        // TODO: Umesh: comment before go live
//        Log.e(strKey, strMessage);
    }

}
