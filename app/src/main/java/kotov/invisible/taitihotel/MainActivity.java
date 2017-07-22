package kotov.invisible.taitihotel;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import kotov.invisible.taitihotel.ApiEngine.APIError;
import kotov.invisible.taitihotel.ApiEngine.ErrorLog;
import kotov.invisible.taitihotel.ApiEngine.ErrorUtils;
import kotov.invisible.taitihotel.ApiEngine.User;
import kotov.invisible.taitihotel.MyUtils.PhoneInfo;
import kotov.invisible.taitihotel.data.OrderedRoomData;
import kotov.invisible.taitihotel.fragments.OrderFragment;
import kotov.invisible.taitihotel.fragments.ReserveFragment;
import kotov.invisible.taitihotel.fragments.SearchFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity implements SearchFragment.OnFragmentInteractionListener,
        OrderFragment.OnFragmentInteractionListener, ReserveFragment.OnFragmentInteractionListener {

    private static final int REQUEST_READ_PHONE_STATE = 0;

    private static final String ARG_ORDERED_ROOMS_DATA = "orderedRoomsData";

    private static final String ARG_CHECK_IN = "dateCheckIn";
    private static final String ARG_CHECK_OUT = "dateCheckOut";
    private static final String ARG_PEOPLE_COUNT = "peopleCount";

    OrderFragment fragmOrder;
    SearchFragment fragmSearch;
    ReserveFragment fragmReserve;

    private boolean isDataSent = false;

    private List<OrderedRoomData> orderedRoomsData;
    private String mDateCheckIn;
    private String mDateCheckOut;
    private int mPeopleCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // если не нужно спрашивать разрешений, отправить сразу
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M)
            registerDevice();
        else {
            int permissionCheck = this.checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE);
            if (permissionCheck != PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
            } else
                registerDevice();
        }
        fragmSearch = (SearchFragment) getSupportFragmentManager().findFragmentByTag("search");
        if (fragmSearch == null) {
            fragmSearch = new SearchFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragmSearch, "search")
                    .commit();
        }

    }

    public List<OrderedRoomData> getOrderedRoomsData() {
        return orderedRoomsData;
    }

    private void registerDevice() {
        User user = new User();
        user.setPseudoId(PhoneInfo.getUniqueId());
        user.setName(PhoneInfo.getPhoneName());
        user.setPhoneNum(PhoneInfo.getPhoneNum(this));

        App.getApi().userAdd(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {

                } else {
                    APIError error = ErrorUtils.parseError(response);
                    ErrorLog.logd(MainActivity.this, error);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Ошибка. " + t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        registerDevice();
    }

    @Override
    public void onSearchButtonClick(Bundle args) {
        mDateCheckIn = args.getString(ARG_CHECK_IN);
        mDateCheckOut = args.getString(ARG_CHECK_OUT);
        mPeopleCount = args.getInt(ARG_PEOPLE_COUNT);

        isDataSent = false;

        fragmOrder = OrderFragment.newInstance(mDateCheckIn, mDateCheckOut, mPeopleCount);

        getSupportFragmentManager().beginTransaction()
                .hide(fragmSearch)
                .addToBackStack("main")
                .add(R.id.fragment_container, fragmOrder, "order")
                .commit();
    }

    @Override
    public void onOrderButtonClick(Bundle args) {

        // сохранить результат работы fragmOrder
        orderedRoomsData = new ArrayList<>();
        ArrayList<OrderedRoomData> roomsData = args.getParcelableArrayList(ARG_ORDERED_ROOMS_DATA);
        if (roomsData != null)
            orderedRoomsData.addAll(roomsData);

        // запустить fragmReserve
        fragmReserve = ReserveFragment.newInstance("1", "2");

        getSupportFragmentManager().beginTransaction()
                .addToBackStack("order")
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .add(R.id.fragment_container, fragmReserve, "reserve")
                .hide(fragmOrder)
                .commit();
    }

    public void setDataSent(Boolean b) {
        isDataSent = b;
    }

    @Override
    public void onBackPressed() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (f instanceof ReserveFragment && isDataSent) {
            getSupportFragmentManager().popBackStack("main", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else
            super.onBackPressed();
    }

    @Override
    public void onMainPressed() {
        getSupportFragmentManager().popBackStack("main", FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
}
