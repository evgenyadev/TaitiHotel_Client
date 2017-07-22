package kotov.invisible.taitihotel.data;

import android.os.Parcel;
import android.os.Parcelable;

public class OrderedRoomData implements Parcelable {

    public static final Creator<OrderedRoomData> CREATOR = new Creator<OrderedRoomData>() {
        @Override
        public OrderedRoomData createFromParcel(Parcel in) {
            return new OrderedRoomData(in);
        }

        @Override
        public OrderedRoomData[] newArray(int size) {
            return new OrderedRoomData[size];
        }
    };

    public String roomType;
    public int roomsCount;
    public int adultsCount;
    public int child_3;
    public int child_3_10;
    public float price;

    public OrderedRoomData() {
        this("", 0, 0, 0, 0, 0.0f);
    }

    public OrderedRoomData(String roomType, int roomsCount, int adultsCount, int child_3, int child_3_10, float price) {
        this.roomType = roomType;
        this.roomsCount = roomsCount;
        this.adultsCount = adultsCount;
        this.child_3 = child_3;
        this.child_3_10 = child_3_10;
        this.price = price;
    }

    private OrderedRoomData(Parcel in) {
        roomType = in.readString();
        roomsCount = in.readInt();
        adultsCount = in.readInt();
        child_3 = in.readInt();
        child_3_10 = in.readInt();
        price = in.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(roomType);
        dest.writeInt(roomsCount);
        dest.writeInt(adultsCount);
        dest.writeInt(child_3);
        dest.writeInt(child_3_10);
        dest.writeFloat(price);
    }
}
