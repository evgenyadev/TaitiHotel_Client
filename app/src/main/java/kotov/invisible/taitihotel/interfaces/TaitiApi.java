package kotov.invisible.taitihotel.interfaces;

import java.util.List;

import kotov.invisible.taitihotel.ApiEngine.APIAnswer;
import kotov.invisible.taitihotel.ApiEngine.Device;
import kotov.invisible.taitihotel.ApiEngine.Order;
import kotov.invisible.taitihotel.ApiEngine.Price;
import kotov.invisible.taitihotel.ApiEngine.RoomsGroup;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface TaitiApi {
    String prefix = "";

    @GET(prefix + "api/v1/device.getInfo")
    Call<Device> deviceGetInfo(@Query("pseudo_id") String _pseudo_id);

    @GET(prefix + "api/v1/room.searchByRange")
    Call<List<RoomsGroup>> roomSearchByRange(@Query("check_in") String _checkIn, @Query("check_out") String _checkOut);

    @GET(prefix + "api/v1/price.getAll")
    Call<List<Price>> priceGetAll();

    @PUT(prefix + "api/v1/device.add")
    Call<Device> deviceAdd(@Body Device device);

    @POST(prefix + "api/v1/request.add")
    Call<APIAnswer> requestAdd(@Body Order order);
}

