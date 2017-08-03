package kotov.invisible.taitihotel.ApiEngine;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import kotov.invisible.taitihotel.data.OrderedRoomData;

public class Order {

    @SerializedName("orderedRoomsData")
    @Expose
    private List<OrderedRoomData> orderedRoomsData = new ArrayList<>();

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("phone")
    @Expose
    private String phone;

    @SerializedName("time_from")
    @Expose
    private int time_from;

    @SerializedName("time_to")
    @Expose
    private int time_to;

    @SerializedName("date_check_in")
    @Expose
    private String date_check_in;

    @SerializedName("date_check_out")
    @Expose
    private String date_check_out;

    public String getDate_check_in() {
        return date_check_in;
    }

    public void setDate_check_in(String date_check_in) {
        this.date_check_in = date_check_in;
    }

    public String getDate_check_out() {
        return date_check_out;
    }

    public void setDate_check_out(String date_check_out) {
        this.date_check_out = date_check_out;
    }

    public List<OrderedRoomData> getOrderedRoomsData() {
        return orderedRoomsData;
    }

    public void setOrderedRoomsData(List<OrderedRoomData> orderedRoomsData) {
        this.orderedRoomsData = orderedRoomsData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getTime_from() {
        return time_from;
    }

    public void setTime_from(int time_from) {
        this.time_from = time_from;
    }

    public int getTime_to() {
        return time_to;
    }

    public void setTime_to(int time_to) {
        this.time_to = time_to;
    }
}
