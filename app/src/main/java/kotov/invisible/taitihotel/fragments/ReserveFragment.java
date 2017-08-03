package kotov.invisible.taitihotel.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import kotov.invisible.taitihotel.ApiEngine.APIAnswer;
import kotov.invisible.taitihotel.ApiEngine.APIError;
import kotov.invisible.taitihotel.ApiEngine.ErrorLog;
import kotov.invisible.taitihotel.ApiEngine.ErrorUtils;
import kotov.invisible.taitihotel.ApiEngine.Order;
import kotov.invisible.taitihotel.App;
import kotov.invisible.taitihotel.MainActivity;
import kotov.invisible.taitihotel.MyUtils.PhoneInfo;
import kotov.invisible.taitihotel.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReserveFragment extends Fragment {

    private Spinner spnrFrom;
    private TextView tv1;
    private TextView tv2;
    private TextView tv3;
    private Spinner spnrTo;
    private EditText etPhone;
    private Button btnReserve;
    private EditText etName;
    private ProgressBar pbSending;
    private RelativeLayout rlMain;
    private RelativeLayout rlOnSent;
    private Button btnExit;
    private Button btnToMain;
    private Call<APIAnswer> sendOrderCall;

    private OnFragmentInteractionListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reserve, container, false);

        pbSending = (ProgressBar) v.findViewById(R.id.pbSending);
        etName = (EditText) v.findViewById(R.id.etName);
        etPhone = (EditText) v.findViewById(R.id.etPhone);
        tv1 = (TextView) v.findViewById(R.id.tv1);
        btnExit = (Button) v.findViewById(R.id.btnExit);
        btnToMain = (Button) v.findViewById(R.id.btnToMain);
        tv2 = (TextView) v.findViewById(R.id.tv2);
        rlMain = (RelativeLayout) v.findViewById(R.id.rlReserveMain);
        rlOnSent = (RelativeLayout) v.findViewById(R.id.rlReserveOnSent);
        spnrFrom = (Spinner) v.findViewById(R.id.spnrFrom);
        spnrTo = (Spinner) v.findViewById(R.id.spnrTo);
        btnReserve = (Button) v.findViewById(R.id.btnReserve);

        btnReserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onReserveButtonPressed();
            }
        });
        btnToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onMainPressed();
                }
            }
        });
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        // установка спиннеров
        String[] hours = new String[]{"00 : 00", "01 : 00", "02 : 00", "03 : 00", "04 : 00",
                "05 : 00", "06 : 00", "07 : 00", "08 : 00", "09 : 00", "10 : 00", "11 : 00",
                "12 : 00", "13 : 00", "14 : 00", "15 : 00", "16 : 00", "17 : 00", "18 : 00",
                "19 : 00", "20 : 00", "21 : 00", "22 : 00", "23 : 00"};
        ArrayAdapter<String> hoursAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, hours);
        hoursAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnrFrom.setAdapter(hoursAdapter);
        spnrTo.setAdapter(hoursAdapter);

        spnrFrom.setSelection(8);
        spnrTo.setSelection(21);

        // форматирование номера телефона
        etPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        // вписать номер телефона устройства

        String phone = PhoneInfo.getPhoneNum(getActivity());
        if (!"null".equals(phone))
            etPhone.setText(phone);

        return v;
    }

    private void onReserveButtonPressed() {

        // check fields
        if (etName.getText().length() == 0) {
            etName.setError("Введите имя");
            return;
        }

        String regex = "(\\+?\\d{1,3}( |-|\\.)?)?(\\(?\\d{3}\\)?|\\d{3}|\\d{2})( |-|\\.)?(\\d{3}( |-|\\.)?\\d{4})";
        if (!etPhone.getText().toString().matches(regex)) {
            etPhone.setError("Неверный формат номера");
            return;
        }

        // disable views
        setEnabledViews(false);

        // collect and send data
        Order order = new Order();

        order.setName(String.valueOf(etName.getText()));
        order.setPhone(String.valueOf(etPhone.getText()));
        order.setTime_from(spnrFrom.getSelectedItemPosition());
        order.setTime_to(spnrTo.getSelectedItemPosition());
        order.setOrderedRoomsData(((MainActivity) getActivity()).getOrderedRoomsData());
        order.setDate_check_in(((MainActivity) getActivity()).mDateCheckIn);
        order.setDate_check_out(((MainActivity) getActivity()).mDateCheckOut);

        // send data
        sendOrderCall = App.getApi().requestAdd(order);
        sendOrderCall.enqueue(new Callback<APIAnswer>() {
            @Override
            public void onResponse(Call<APIAnswer> call, Response<APIAnswer> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getInfo().equals("Order added."))
                        onDataSent();
                } else {
                    APIError error = ErrorUtils.parseError(response);
                    Toast.makeText(getContext(), error.getCode() + " " + error.getMessage(), Toast.LENGTH_LONG).show();
                    ErrorLog.logd(getContext(), error);
                    setEnabledViews(true);
                }
            }

            @Override
            public void onFailure(Call<APIAnswer> call, Throwable t) {
                if (!call.isCanceled()) {
                    Toast.makeText(getActivity(), "Ошибка. " + t.getMessage(), Toast.LENGTH_LONG).show();
                    setEnabledViews(true);
                }
            }
        });
    }

    private void onDataSent() {
        pbSending.setVisibility(View.GONE);
        rlMain.setVisibility(View.GONE);
        btnReserve.setVisibility(View.GONE);

        ((MainActivity) getActivity()).setDataSent(true);

        rlOnSent.setVisibility(View.VISIBLE);

        Animation anim = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in);
        rlOnSent.startAnimation(anim);

    }

    private void setEnabledViews(boolean b) {
        if (b)
            pbSending.setVisibility(View.GONE);
        else
            pbSending.setVisibility(View.VISIBLE);


        etName.setEnabled(b);
        etPhone.setEnabled(b);
        spnrFrom.setEnabled(b);
        spnrTo.setEnabled(b);
        btnReserve.setEnabled(b);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

        if (sendOrderCall != null)
            sendOrderCall.cancel();
    }

    public interface OnFragmentInteractionListener {
        void onMainPressed();
    }
}
