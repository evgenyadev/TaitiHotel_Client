package kotov.invisible.taitihotel.dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import kotov.invisible.taitihotel.ApiEngine.RoomsGroup;
import kotov.invisible.taitihotel.R;
import kotov.invisible.taitihotel.data.OrderedRoomData;
import kotov.invisible.taitihotel.data.PriceData;

public class OrderRoomsDialog extends DialogFragment {

    private static final String ARG_CHECK_IN = "dateCheckIn";
    private static final String ARG_CHECK_OUT = "dateCheckOut";
    private static final String ARG_ROOM_TYPE = "roomsType";
    private static final String ARG_ROOMS_COUNT = "roomsCount";
    private static final String ARG_ROOMS_CAPACITY = "roomsCapacity";
    private static final String ARG_ORDER_DATA = "orderedRoomsData";
    private static final String ARG_PRICE_DATA = "priceData";
    private static OnClickListener mListener;
    private String mCheckIn;
    private String mCheckOut;
    private String mRoomsType;
    private int mRoomsCount;
    private int mRoomsCapacity;
    private OrderedRoomData mOrderedRoomData;
    private PriceData mPriceData;
    private Spinner mSpnrRoomCount;
    private Spinner mSpnrAdultCount;
    private Spinner mSpnrChild_3;
    private Spinner mSpnrChild_3_10;
    private TextView mTvCurrOrderMoney;
    private float mRoomsPrice = 0.0f;
    private AdapterView.OnItemSelectedListener spnrsSelectedItemListener = new AdapterView.OnItemSelectedListener() {

        private int callsCounter;

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            // игнорировать первый вызов при установке слушателя для 4-х спиннеров
            if (callsCounter++ < 4) return;

            // Спрятать спиннеры если количество номеров == 0
            if (parent.getId() == R.id.spnrRoomCount)
                if (position == 0)
                    spinnersVisible(false);
                else if (mSpnrAdultCount.getVisibility() == View.INVISIBLE)
                    spinnersVisible(true);

            // Разделить дату по количествам дней в месяцах
            // CheckIn 2017-08-30; CheckOut 2017-09-03 ->
            // august:2; september:3
            Map<String, Integer> daysInMonths = splitOnMonths(/* mCheckIn, mCheckOut */);

            // подсчет стоимости проживания в номере
            mRoomsPrice = calcPrice(daysInMonths);

            // вывести во вью
            mTvCurrOrderMoney.setText(new DecimalFormat("##.##").format(mRoomsPrice));
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    /**
     * @param roomsGroup      информация о типе, количестве и вместимости номеров
     * @param orderedRoomData хранит положения спиннеров (информация о заказе)
     * @param priceData       хранит инфо о ценах на весь сезон
     * @param checkIn         дата заезда
     * @param checkOut        дата выезда
     * @return помещает все в Bundle() и передает в другие методы
     */
    public static OrderRoomsDialog newInstance(RoomsGroup roomsGroup,
                                               OrderedRoomData orderedRoomData,
                                               PriceData priceData,
                                               String checkIn,
                                               String checkOut,
                                               OnClickListener listener) {
        mListener = listener;
        OrderRoomsDialog fragment = new OrderRoomsDialog();
        Bundle args = new Bundle();

        args.putString(ARG_ROOM_TYPE, roomsGroup.getRoomType());
        args.putInt(ARG_ROOMS_COUNT, roomsGroup.getCount());
        args.putInt(ARG_ROOMS_CAPACITY, roomsGroup.getCapacity());
        args.putParcelable(ARG_ORDER_DATA, orderedRoomData);
        args.putParcelable(ARG_PRICE_DATA, priceData);
        args.putString(ARG_CHECK_IN, checkIn);
        args.putString(ARG_CHECK_OUT, checkOut);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setCancelable(false); // не реагировать на нажатия мимо диалога

        if (getArguments() != null) {
            mRoomsType = getArguments().getString(ARG_ROOM_TYPE);
            mRoomsCount = getArguments().getInt(ARG_ROOMS_COUNT);
            mRoomsCapacity = getArguments().getInt(ARG_ROOMS_CAPACITY);
            mOrderedRoomData = getArguments().getParcelable(ARG_ORDER_DATA);
            mPriceData = getArguments().getParcelable(ARG_PRICE_DATA);
            mCheckIn = getArguments().getString(ARG_CHECK_IN);
            mCheckOut = getArguments().getString(ARG_CHECK_OUT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_order_rooms, container, false);

        if (getArguments() == null)
            return v;

        getDialog().setTitle(mRoomsType);

        mTvCurrOrderMoney = (TextView) v.findViewById(R.id.tvCurrOrderMoney);
        mTvCurrOrderMoney.setText("0");

        mSpnrRoomCount = (Spinner) v.findViewById(R.id.spnrRoomCount);
        mSpnrAdultCount = (Spinner) v.findViewById(R.id.spnrAdultCount);
        mSpnrChild_3_10 = (Spinner) v.findViewById(R.id.spnrChild_3_10);
        mSpnrChild_3 = (Spinner) v.findViewById(R.id.spnrChild_3);

        mSpnrRoomCount.setOnItemSelectedListener(spnrsSelectedItemListener);
        mSpnrAdultCount.setOnItemSelectedListener(spnrsSelectedItemListener);
        mSpnrChild_3_10.setOnItemSelectedListener(spnrsSelectedItemListener);
        mSpnrChild_3.setOnItemSelectedListener(spnrsSelectedItemListener);

        Button btnOk = (Button) v.findViewById(R.id.btnOrderDialogOk);
        Button btnCancel = (Button) v.findViewById(R.id.btnOrderDialogCancel);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSaveExit();
            }
        });

        // установка ограничений спиннеров по максимально возможной вместимости номеров
        setSpinnersItems(/* mRoomsCount, mRoomsCapacity */);

        // в случае уже имеющегося заказа - отображение его во вью
        // и установка видимости спиннеров
        if (mOrderedRoomData.roomsCount != 0) {
            mSpnrAdultCount.setVisibility(View.VISIBLE);
            mSpnrChild_3.setVisibility(View.VISIBLE);
            mSpnrChild_3_10.setVisibility(View.VISIBLE);

            loadOrderDataToViews();
        }

        return v;
    }

    private Map<String, Integer> splitOnMonths() {
        Map<String, Integer> daysInMonths = new HashMap<>();

        // форматировать дату в календарь
        Calendar cCheckIn = Calendar.getInstance();
        Calendar cCheckOut = Calendar.getInstance();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(getResources().getString(R.string.server_date_format), Locale.getDefault());
            cCheckIn.setTime(sdf.parse(mCheckIn));
            cCheckOut.setTime(sdf.parse(mCheckOut));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // установить указатель на дату заезда
        // итерации по месяцам, от даты заезда до выезда
        for (int currentMonth = cCheckIn.get(Calendar.MONTH); currentMonth <= cCheckOut.get(Calendar.MONTH); /* end */) {
            int days;
            // cCheckIn в дальнейшем будет 1 числом каждого месяца

            if (currentMonth == cCheckOut.get(Calendar.MONTH))
                days = cCheckOut.get(Calendar.DAY_OF_MONTH) - cCheckIn.get(Calendar.DAY_OF_MONTH);
            else
                days = cCheckIn.getActualMaximum(Calendar.DAY_OF_MONTH) - cCheckIn.get(Calendar.DAY_OF_MONTH) + 1;

            if (days != 0)
                daysInMonths.put(intMonthToString(currentMonth), days);

            /* end */
            // переставить указатель на 1 число следующего месяца
            cCheckIn.set(Calendar.DAY_OF_MONTH, 1);
            cCheckIn.set(Calendar.MONTH, ++currentMonth);
        }

        return daysInMonths;
    }

    /**
     * @param daysInMonths название месяца : количество дней в нем
     * @return общая цена проживания
     */
    private float calcPrice(Map<String, Integer> daysInMonths) {
        int roomsCount = mSpnrRoomCount.getSelectedItemPosition();
        int adultsCount = mSpnrAdultCount.getSelectedItemPosition();
        int child3Count = mSpnrChild_3.getSelectedItemPosition();
        int child3_10Count = mSpnrChild_3_10.getSelectedItemPosition();
        float price = 0.0f;

        if (mPriceData == null)
            return price;

        // для номеров со стоимостью за целый номер в сутки (Двухкомнатный)
        if (mRoomsType.equals("Двухкомнатный")) {
            for (Map.Entry<String, Integer> entry : daysInMonths.entrySet()) {
                int roomsPrice = mPriceData.getPrice(entry.getKey()) * roomsCount * entry.getValue();
                price += roomsPrice;
            }
            // для номеров со стоимостью за каждого человека в сутки (Стандарт, Улучшенный (1,2))
        } else
            for (Map.Entry<String, Integer> entry : daysInMonths.entrySet()) {
                int adultPrice = mPriceData.getPrice(entry.getKey()) * adultsCount * entry.getValue();
                int child3Price = mPriceData.getChild_3_Price() * child3Count * entry.getValue();
                float child3_10Price = mPriceData.getChild_3_10_Price(entry.getKey()) * child3_10Count * entry.getValue();
                price += adultPrice + child3Price + child3_10Price;
            }

        return price;
    }

    private String intMonthToString(int calendarMonth) {
        String[] months = new String[]{
                "january", "february", "march", "april", "may", "june", "july",
                "august", "september", "october", "november", "december"};

        return months[calendarMonth];
    }

    private void loadOrderDataToViews() {
        mSpnrRoomCount.setSelection(mOrderedRoomData.roomsCount);
        mSpnrAdultCount.setSelection(mOrderedRoomData.adultsCount);
        mSpnrChild_3.setSelection(mOrderedRoomData.child_3);
        mSpnrChild_3_10.setSelection(mOrderedRoomData.child_3_10);
        mRoomsPrice = mOrderedRoomData.price;
        mTvCurrOrderMoney.setText(new DecimalFormat("##.##").format(mRoomsPrice));
    }

    private void spinnersVisible(Boolean isVisible) {
        if (isVisible) {
            mSpnrAdultCount.setVisibility(View.VISIBLE);
            mSpnrChild_3.setVisibility(View.VISIBLE);
            mSpnrChild_3_10.setVisibility(View.VISIBLE);
        } else {
            mSpnrAdultCount.setVisibility(View.INVISIBLE);
            mSpnrChild_3.setVisibility(View.INVISIBLE);
            mSpnrChild_3_10.setVisibility(View.INVISIBLE);

            mSpnrAdultCount.setSelection(0);
            mSpnrChild_3.setSelection(0);
            mSpnrChild_3_10.setSelection(0);
        }
    }

    private void setSpinnersItems() {
        // Спиннер комнат
        Integer[] roomsCountList = new IntArray(0, mRoomsCount).make();
        ArrayAdapter<Integer> roomsCountAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, roomsCountList);
        roomsCountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpnrRoomCount.setAdapter(roomsCountAdapter);

        // Спиннер взрослых
        Integer[] peopleCountList = new IntArray(0, mRoomsCapacity).make();
        ArrayAdapter<Integer> peopleCountAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, peopleCountList);
        peopleCountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpnrAdultCount.setAdapter(peopleCountAdapter);

        // Спиннер детей от 3-х до 10-ти
        // использовать peopleCountList и тот же адаптер
        mSpnrChild_3_10.setAdapter(peopleCountAdapter);

        // Спиннер детей до 3-х
        // использовать peopleCountList и готовый адаптер
        mSpnrChild_3.setAdapter(peopleCountAdapter);
    }

    private void checkSaveExit() {
        /*
         * получить инфо о выборе
         */
        int roomCount = mSpnrRoomCount.getSelectedItemPosition();
        int adultCount = mSpnrAdultCount.getSelectedItemPosition();
        int children_3 = mSpnrChild_3.getSelectedItemPosition();
        int children_3_10 = mSpnrChild_3_10.getSelectedItemPosition();

        /*
         * проверить допустимость выбранного
         */
        // если людей, занимающих место меньше чем заказанных номеров
        if ((adultCount + children_3_10) < roomCount) {
            Toast.makeText(getContext(), "Номеров больше чем людей.", Toast.LENGTH_SHORT).show();
            return;
        }

        // если людей больше, чем места в заказанных номерах
        int freeSpace = roomCount * (mRoomsCapacity / mRoomsCount);
        int people = adultCount + children_3_10;
        if (people > freeSpace) {
            Toast.makeText(getContext(), "В выбранных номерах мест " + freeSpace + ", а людей " + people + ".", Toast.LENGTH_SHORT).show();
            return;
        }

        /*
         * сохранить
         */

        mOrderedRoomData.roomType = mRoomsType;
        mOrderedRoomData.roomsCount = roomCount;
        mOrderedRoomData.adultsCount = adultCount;
        mOrderedRoomData.child_3 = children_3;
        mOrderedRoomData.child_3_10 = children_3_10;
        mOrderedRoomData.price = mRoomsPrice;

        // оповестить слушателей
        if (mListener != null)
            mListener.onDialogOkPressed(roomCount);

        // выйти
        dismiss();
    }

    public interface OnClickListener {
        void onDialogOkPressed(int roomsCount);
    }

    private class IntArray {
        /*
         * используется в спиннерах, заполняет массив целых
         */
        private int startFrom, endOn;

        private IntArray(int startFrom1, int endOn1) {
            this.startFrom = startFrom1;
            this.endOn = endOn1;
        }

        private Integer[] make() throws IllegalArgumentException {
            if (startFrom > endOn)
                throw new IllegalArgumentException("'endOn' should be bigger than 'startFrom'");

            Integer[] array = new Integer[endOn - startFrom + 1];
            for (int i = 0, val = startFrom; val <= endOn; val++, i++) {
                array[i] = val;
            }
            return array;
        }
    }
}