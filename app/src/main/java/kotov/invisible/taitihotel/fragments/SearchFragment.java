package kotov.invisible.taitihotel.fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import kotov.invisible.taitihotel.R;
import kotov.invisible.taitihotel.dialogs.DatePickerDialog;

public class SearchFragment extends DialogFragment {

    private static final String ARG_CHECK_IN = "dateCheckIn";
    private static final String ARG_CHECK_OUT = "dateCheckOut";
    private static final String ARG_PEOPLE_COUNT = "peopleCount";

    private ImageView ivLogo;
    private ImageButton buttonIncrement;
    private ImageButton buttonDecrement;
    private Button buttonSearch;
    private EditText etPeopleCount;
    private Button buttonCheckIn;
    private Button buttonCheckOut;

    private OnFragmentInteractionListener mListener;

    // установка даты начала или окончания
    private View.OnClickListener listenerDatePickerButton = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            String date = ((Button) v).getText().toString();
            DialogFragment picker = DatePickerDialog.newInstance(date, new android.app.DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    Calendar c = Calendar.getInstance();
                    // год не менять
                    c.set(Calendar.MONTH, month);
                    c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    SimpleDateFormat sdf = new SimpleDateFormat(getResources().getString(R.string.format_date_full), Locale.getDefault());
                    ((Button) v).setText(sdf.format(c.getTime()));
                }
            });

            picker.show(getFragmentManager(), "datePicker");
        }
    };

    // Прибавить или отнять человека
    private View.OnClickListener listenerIncrementDecrement = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Integer etValue = 1; // default value

            try {
                etValue = Integer.parseInt(etPeopleCount.getText().toString());
            } catch (NumberFormatException e) {
                etPeopleCount.setText(String.valueOf(etValue));
            }

            switch (v.getId()) {
                case R.id.buttonPeopleIncrement:
                    if (etValue < 99) ++etValue;
                    break;
                case R.id.buttonPeopleDecrement:
                    if (etValue > 1) --etValue;
                    break;
            }
            etPeopleCount.setText(String.valueOf(etValue));
        }
    };

    // убрать фокус с эдит текста
    private TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                v.clearFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return true;
            }
            return false;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void setButtonsDate(Button buttonCheckIn, Button buttonCheckOut) {
        SimpleDateFormat sdf = new SimpleDateFormat(getResources().getString(R.string.format_date_full), Locale.getDefault());
        Calendar currentDate = Calendar.getInstance();
        // начало сезона
        Calendar seasonStart = Calendar.getInstance();
        seasonStart.set(Calendar.MONTH, getResources().getInteger(R.integer.season_start_month));
        seasonStart.set(Calendar.DAY_OF_MONTH, getResources().getInteger(R.integer.season_start_day));
        // конец сезона
        Calendar seasonEnd = Calendar.getInstance();
        seasonEnd.set(Calendar.MONTH, getResources().getInteger(R.integer.season_end_month));
        seasonEnd.set(Calendar.DAY_OF_MONTH, getResources().getInteger(R.integer.season_end_day));

        if (currentDate.before(seasonStart)) {
            buttonCheckIn.setText(sdf.format(seasonStart.getTime()));
            seasonStart.add(Calendar.DAY_OF_MONTH, 5);
            buttonCheckOut.setText(sdf.format(seasonStart.getTime()));
        } else if (currentDate.after(seasonEnd)) {
            buttonCheckIn.setText("Сезон закрыт");
            buttonCheckIn.setEnabled(false);
            buttonCheckOut.setText("Сезон закрыт");
            buttonCheckOut.setEnabled(false);
        } else {
            buttonCheckIn.setText(sdf.format(currentDate.getTime()));
            currentDate.add(Calendar.DAY_OF_MONTH, 5);
            buttonCheckOut.setText(sdf.format(currentDate.getTime()));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        etPeopleCount = (EditText) view.findViewById(R.id.etPeopleCount);
        etPeopleCount.setOnEditorActionListener(editorActionListener);

        buttonSearch = (Button) view.findViewById(R.id.buttonSearch);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchButtonPressed();
            }
        });

        buttonCheckIn = (Button) view.findViewById(R.id.buttonCheckIn);
        buttonCheckIn.setOnClickListener(listenerDatePickerButton);

        buttonCheckOut = (Button) view.findViewById(R.id.buttonCheckOut);
        buttonCheckOut.setOnClickListener(listenerDatePickerButton);

        setButtonsDate(buttonCheckIn, buttonCheckOut);

        buttonIncrement = (ImageButton) view.findViewById(R.id.buttonPeopleIncrement);
        buttonIncrement.setOnClickListener(listenerIncrementDecrement);

        buttonDecrement = (ImageButton) view.findViewById(R.id.buttonPeopleDecrement);
        buttonDecrement.setOnClickListener(listenerIncrementDecrement);

        ivLogo = (ImageView) view.findViewById(R.id.imageView);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            ivLogo.setVisibility(View.GONE);
        else
            ivLogo.setVisibility(View.VISIBLE);

        return view;
    }

    public void searchButtonPressed() {

        // преобразовать дату к виду 2017-05-05
        SimpleDateFormat sdfDevice = new SimpleDateFormat(getResources().getString(R.string.format_date_full), Locale.getDefault());
        SimpleDateFormat sdfServer = new SimpleDateFormat(getResources().getString(R.string.server_date_format), Locale.getDefault());

        String checkIn;
        String checkOut;
        Calendar in = Calendar.getInstance();
        Calendar out = Calendar.getInstance();

        try {
            in.setTime(sdfDevice.parse(buttonCheckIn.getText().toString()));
            out.setTime(sdfDevice.parse(buttonCheckOut.getText().toString()));
            checkIn = sdfServer.format(in.getTime());
            checkOut = sdfServer.format(out.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Ошибка преобразования даты.", Toast.LENGTH_LONG).show();
            return;
        }

        // проверка валидности дат
        if (in.compareTo(out) > 0) {
            Toast.makeText(getContext(), "Дата заезда не может быть позже даты выезда.", Toast.LENGTH_SHORT).show();
            return;
        } else if (in.compareTo(out) == 0) {
            Toast.makeText(getContext(), "Даты должны отличаться минимум на 1 день", Toast.LENGTH_SHORT).show();
            return;
        }

        // передать в мэйн
        Bundle args = new Bundle();
        args.putString(ARG_CHECK_IN, checkIn);
        args.putString(ARG_CHECK_OUT, checkOut);
        args.putInt(ARG_PEOPLE_COUNT, Integer.parseInt(etPeopleCount.getText().toString()));

        if (mListener != null) {
            mListener.onSearchButtonClick(args);
        }
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        /*
         * Строка в манифесте
         * android:configChanges="keyboardHidden|orientation|screenSize"
         * гарантирует что активити не будет пересоздаваться при этих сменах конфигурации
         * вместо этого вызывается этот метод, в котором можно их обработать
         */

        // убрать картинку из лэндскейпа
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
            ivLogo.setVisibility(View.GONE);
        else
            ivLogo.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onSearchButtonClick(Bundle args);
    }
}
