package kotov.invisible.taitihotel.ApiEngine;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Price {

    @SerializedName("child_3_10_discount")
    @Expose
    private Integer child310Discount;
    @SerializedName("june")
    @Expose
    private Integer june;
    @SerializedName("september")
    @Expose
    private Integer september;
    @SerializedName("may")
    @Expose
    private Integer may;
    @SerializedName("august")
    @Expose
    private Integer august;
    @SerializedName("july")
    @Expose
    private Integer july;
    @SerializedName("child_3_price")
    @Expose
    private Integer child3Price;
    @SerializedName("room_type")
    @Expose
    private String roomType;

    public Integer getChild310Discount() {
        return child310Discount;
    }

    public void setChild310Discount(Integer child310Discount) {
        this.child310Discount = child310Discount;
    }

    public Integer getJune() {
        return june;
    }

    public void setJune(Integer june) {
        this.june = june;
    }

    public Integer getSeptember() {
        return september;
    }

    public void setSeptember(Integer september) {
        this.september = september;
    }

    public Integer getMay() {
        return may;
    }

    public void setMay(Integer may) {
        this.may = may;
    }

    public Integer getAugust() {
        return august;
    }

    public void setAugust(Integer august) {
        this.august = august;
    }

    public Integer getJuly() {
        return july;
    }

    public void setJuly(Integer july) {
        this.july = july;
    }

    public Integer getChild3Price() {
        return child3Price;
    }

    public void setChild3Price(Integer child3Price) {
        this.child3Price = child3Price;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

}