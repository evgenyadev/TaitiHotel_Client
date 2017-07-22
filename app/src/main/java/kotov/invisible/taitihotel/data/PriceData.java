package kotov.invisible.taitihotel.data;

import android.os.Parcel;
import android.os.Parcelable;

public class PriceData implements Parcelable {

    public static final Creator<PriceData> CREATOR = new Creator<PriceData>() {
        @Override
        public PriceData createFromParcel(Parcel in) {
            return new PriceData(in);
        }

        @Override
        public PriceData[] newArray(int size) {
            return new PriceData[size];
        }
    };

    private int may;
    private int june;
    private int july;
    private int august;
    private int september;
    private int child_3_price;
    private int child_3_10_discount;

    public PriceData(int may, int june, int july, int august, int september, int child_3_price, int child_3_10_discount) {
        this.may = may;
        this.june = june;
        this.july = july;
        this.august = august;
        this.september = september;
        this.child_3_price = child_3_price;
        this.child_3_10_discount = child_3_10_discount;
    }

    public Integer getChild_3_Price() {
        return this.child_3_price;
    }

    public Float getChild_3_10_Price(String month) {
        return this.getPrice(month) * (100 - this.child_3_10_discount) / 100.0f;
    }

    public Integer getPrice(String month) {
        switch (month) {
            case "may":
                return this.may;
            case "june":
                return this.june;
            case "july":
                return this.july;
            case "august":
                return this.august;
            case "september":
                return this.september;
            default:
                return 0;
        }
    }

    protected PriceData(Parcel in) {
        may = in.readInt();
        june = in.readInt();
        july = in.readInt();
        august = in.readInt();
        september = in.readInt();
        child_3_price = in.readInt();
        child_3_10_discount = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(may);
        dest.writeInt(june);
        dest.writeInt(july);
        dest.writeInt(august);
        dest.writeInt(september);
        dest.writeInt(child_3_price);
        dest.writeInt(child_3_10_discount);
    }
}
