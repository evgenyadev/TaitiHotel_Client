package kotov.invisible.taitihotel;


import android.app.Application;
import android.os.Build;
import android.util.Log;

import kotov.invisible.taitihotel.interfaces.TaitiApi;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class App extends Application {

    private static TaitiApi taitiApi;
    public static Retrofit retrofit;

    public static TaitiApi getApi() {
        return taitiApi;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // detect emulator
        String url;
        if ( "vbox86p".equals(Build.PRODUCT))
            url = getResources().getString(R.string.ip_from_emulator);
        else
            url = getResources().getString(R.string.ip_from_device);

        retrofit = new Retrofit.Builder().baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        taitiApi = retrofit.create(TaitiApi.class);

    }
}
