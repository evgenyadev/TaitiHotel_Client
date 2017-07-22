package kotov.invisible.taitihotel.dialogs;

import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import kotov.invisible.taitihotel.R;

public class DatePickerDialog extends DialogFragment {

    private static final String ARG_DATE = "date";

    private static android.app.DatePickerDialog.OnDateSetListener mListener;
    private String mDate;

    public static DatePickerDialog newInstance(String date, android.app.DatePickerDialog.OnDateSetListener listener) {
        DatePickerDialog fragment = new DatePickerDialog();
        Bundle args = new Bundle();

        mListener = listener;
        args.putString(ARG_DATE, date);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDate = getArguments().getString(ARG_DATE);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // получить дату, написанную на кнопке
        Calendar calendar = Calendar.getInstance();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(getResources().getString(R.string.format_date_full), Locale.getDefault());
            calendar.setTime(sdf.parse(mDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // создать пикер с этой датой
        android.app.DatePickerDialog pickerDialog = new android.app.DatePickerDialog(getActivity(), mListener, year, month, day);

        // установить пределы дат
        setPickerLimits(pickerDialog);

        // убрать год из пикера, работает не на всех девайсах
        try {
            // добавить другие устройства, у которых поле с годом отличается от year
            pickerDialog.getDatePicker().findViewById(Resources.getSystem().getIdentifier("year", "id", "android")).setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pickerDialog;
    }

    private void setPickerLimits(android.app.DatePickerDialog pickerDialog) {
        Calendar currentDate = Calendar.getInstance(); // текущая дата (время на девайсе)
        Calendar seasonStart = Calendar.getInstance();
        Calendar seasonEnd = Calendar.getInstance();
        // начало сезона отдыха
        seasonStart.set(Calendar.MONTH, getResources().getInteger(R.integer.season_start_month));
        seasonStart.set(Calendar.DAY_OF_MONTH, getResources().getInteger(R.integer.season_start_day));

        try {
            // -60 секунд для избежания исключения
            if (seasonStart.before(currentDate))
                // если начало сезона раньше текущей даты
                pickerDialog.getDatePicker().setMinDate(currentDate.getTime().getTime() - 60);
            else
                // если текущая дата раньше начала сезона
                pickerDialog.getDatePicker().setMinDate(seasonStart.getTime().getTime() - 60);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        // дата окончания сезона
        seasonEnd.set(Calendar.MONTH, getResources().getInteger(R.integer.season_end_month));
        seasonEnd.set(Calendar.DAY_OF_MONTH, getResources().getInteger(R.integer.season_end_day));
        try {
            //
            pickerDialog.getDatePicker().setMaxDate(seasonEnd.getTime().getTime());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
