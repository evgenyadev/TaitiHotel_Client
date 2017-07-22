package kotov.invisible.taitihotel.ApiEngine;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class APIAnswer {

    @SerializedName("info")
    @Expose
    private String info;

    public String getInfo() {
        return info;
    }
}
