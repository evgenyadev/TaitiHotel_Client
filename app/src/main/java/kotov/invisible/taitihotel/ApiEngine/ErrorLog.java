package kotov.invisible.taitihotel.ApiEngine;

import android.content.Context;
import android.util.Log;

public class ErrorLog {

    public static void logd(Context c, APIError e) {
        Log.d("mydebug", c.getClass().getName() + ":\n"
                + e.getCode() + " " + e.getMessage() + " " + e.getBody());
    }

}
