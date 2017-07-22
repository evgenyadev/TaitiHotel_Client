package kotov.invisible.taitihotel.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kotov.invisible.taitihotel.ApiEngine.APIError;
import kotov.invisible.taitihotel.ApiEngine.ErrorLog;
import kotov.invisible.taitihotel.ApiEngine.ErrorUtils;
import kotov.invisible.taitihotel.ApiEngine.Price;
import kotov.invisible.taitihotel.ApiEngine.RoomsGroup;
import kotov.invisible.taitihotel.App;
import kotov.invisible.taitihotel.R;
import kotov.invisible.taitihotel.RoomsAdapter;
import kotov.invisible.taitihotel.data.OrderedRoomData;
import kotov.invisible.taitihotel.data.PriceData;
import kotov.invisible.taitihotel.dialogs.OrderRoomsDialog;
import kotov.invisible.taitihotel.dialogs.ShowInfoDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderFragment extends DialogFragment {

    private static final String ARG_CHECK_IN = "dateCheckIn";
    private static final String ARG_CHECK_OUT = "dateCheckOut";
    private static final String ARG_PEOPLE_COUNT = "peopleCount";
    private static final String ARG_ORDERED_ROOMS_DATA = "orderedRoomsData";

    boolean isGetPricesDone = false;
    boolean isSearchDone = false;
    TextView infoRoomText;
    TextView infoRoomDates;
    Button btnOrder;
    ProgressBar pbLoadingContent;
    RelativeLayout infoBar;
    private int totalCapacity;
    private Map<String, OrderedRoomData> orderedRoomsGroup;
    private Map<String, PriceData> prices;
    private RecyclerView recyclerView;
    private List<RoomsGroup> roomsGroupList;

    private String mDateCheckIn;
    private String mDateCheckOut;
    private int mPeopleCount;
    private OnFragmentInteractionListener mListener;

    public static OrderFragment newInstance(String dateCheckIn, String dateCheckOut, int peopleCount) {
        OrderFragment fragment = new OrderFragment();
        Bundle args = new Bundle();

        args.putString(ARG_CHECK_IN, dateCheckIn);
        args.putString(ARG_CHECK_OUT, dateCheckOut);
        args.putInt(ARG_PEOPLE_COUNT, peopleCount);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true); // вызывать onCreate один раз при создании фрагмента

        orderedRoomsGroup = new HashMap<>();

        if (getArguments() != null) {
            mDateCheckIn = getArguments().getString(ARG_CHECK_IN);
            mDateCheckOut = getArguments().getString(ARG_CHECK_OUT);
            mPeopleCount = getArguments().getInt(ARG_PEOPLE_COUNT);
        }

        // Получить доступные номера
        roomsGroupList = new ArrayList<>();
        App.getApi().roomSearchByRange(mDateCheckIn, mDateCheckOut).enqueue(new Callback<List<RoomsGroup>>() {
            @Override
            public void onResponse(Call<List<RoomsGroup>> call, Response<List<RoomsGroup>> response) {
                if (response.isSuccessful()) {
                    totalCapacity = 0;
                    if (response.body() != null) {
                        for (RoomsGroup roomsGroup : response.body()) {
                            roomsGroupList.add(roomsGroup);
                            totalCapacity += roomsGroup.getCapacity();
                        }
                    }
                    isSearchDone = true;
                    ifReadyShowViews();
                } else {
                    APIError error = ErrorUtils.parseError(response);
                    Toast.makeText(getContext(), error.getCode() + " " + error.getMessage(), Toast.LENGTH_LONG).show();
                    ErrorLog.logd(getContext(), error);
                }
            }

            @Override
            public void onFailure(Call<List<RoomsGroup>> call, Throwable t) {
                pbLoadingContent.setVisibility(View.GONE);
                // todo добавить повтор
                Toast.makeText(getActivity(), "Ошибка. " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // Получить список цен на номера
        prices = new HashMap<>();
        App.getApi().priceGetAll().enqueue(new Callback<List<Price>>() {
            @Override
            public void onResponse(Call<List<Price>> call, Response<List<Price>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        for (Price price : response.body()) {
                            PriceData values = new PriceData(
                                    price.getMay(),
                                    price.getJune(),
                                    price.getJuly(),
                                    price.getAugust(),
                                    price.getSeptember(),
                                    price.getChild3Price(),
                                    price.getChild310Discount());
                            prices.put(price.getRoomType(), values);
                        }
                    }
                    isGetPricesDone = true;
                    ifReadyShowViews();
                } else {
                    APIError error = ErrorUtils.parseError(response);
                    Toast.makeText(getContext(), error.getCode() + " " + error.getMessage(), Toast.LENGTH_LONG).show();
                    ErrorLog.logd(getContext(), error);
                }
            }

            @Override
            public void onFailure(Call<List<Price>> call, Throwable t) {
                pbLoadingContent.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Ошибка. " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void ifReadyShowViews() {
        if (isGetPricesDone && isSearchDone) {
            // обновить infoBar
            if (roomsGroupList.isEmpty())
                infoRoomText.setText("Нет свободных ");
            else
                infoRoomText.setText("Доступные номера ");

            SimpleDateFormat sdfFrom = new SimpleDateFormat(getResources().getString(R.string.server_date_format), Locale.getDefault());
            SimpleDateFormat sdfTo = new SimpleDateFormat(getResources().getString(R.string.format_date_short), Locale.getDefault());

            String in = null;
            String out = null;
            try {
                in = sdfTo.format(sdfFrom.parse(mDateCheckIn));
                out = sdfTo.format(sdfFrom.parse(mDateCheckOut));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String dateInfo = "c " + in + " по " + out;
            infoRoomDates.setText(dateInfo);

            // показать весь контент во фрагменте + анимация
            Animation slideInRight = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right);
            Animation slideInLeft = AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_in_left);

            pbLoadingContent.setVisibility(View.GONE);
            infoBar.setVisibility(View.VISIBLE);
            infoBar.startAnimation(slideInRight);

            recyclerView.getAdapter().notifyDataSetChanged();
            recyclerView.startAnimation(slideInLeft);


            // предупреждение о нехватке мест
            if (mPeopleCount > totalCapacity && totalCapacity != 0) {
                Toast.makeText(getContext(), "Всего спальных мест во всех номерах в списке - " + totalCapacity, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void onRecViewButtonsClick(final View v, RoomsGroup roomsGroup) {

        String roomType = roomsGroup.getRoomType();

        if (v.getId() == R.id.infoButton)
            ShowInfoDialog.newInstance(roomType, roomsGroup.getDescription())
                    .show(getFragmentManager(), "ShowInfo");

        if (v.getId() == R.id.orderButton) {
            // если такого ключа ещё нет, то создать
            if (!orderedRoomsGroup.containsKey(roomType))
                orderedRoomsGroup.put(roomType, new OrderedRoomData());

            // todo придумать что делать если нет списка цен prices.clear();

            OrderRoomsDialog dlg;
            dlg = OrderRoomsDialog.newInstance(
                    roomsGroup,
                    orderedRoomsGroup.get(roomType),
                    prices.get(roomType),
                    mDateCheckIn,
                    mDateCheckOut,
                    new OrderRoomsDialog.OnClickListener() {
                        @Override
                        public void onDialogOkPressed(int roomsCount) {
                            updateButtons(v, roomsCount);
                        }
                    });
            dlg.show(getFragmentManager(), "AddOrder");
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_order, container, false);

        infoRoomText = (TextView) v.findViewById(R.id.tvAvailableRoomsText);
        infoRoomDates = (TextView) v.findViewById(R.id.tvAvailableRoomsDates);
        btnOrder = (Button) v.findViewById(R.id.btnOrderMoney);
        pbLoadingContent = (ProgressBar) v.findViewById(R.id.pbLoadingContent);
        infoBar = (RelativeLayout) v.findViewById(R.id.infoBar);
        recyclerView = (RecyclerView) v.findViewById(R.id.rooms_recycler_view);

        // create recycler view and set on child click listener
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        RoomsAdapter roomsAdapter = new RoomsAdapter(roomsGroupList, new RoomsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final View v, RoomsGroup roomsGroup) {
                onRecViewButtonsClick(v, roomsGroup);
            }
        });

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOrderButtonPressed();
            }
        });

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(roomsAdapter);

        return v;
    }

    private void cleanZeroOrders() {
        Iterator<Map.Entry<String, OrderedRoomData>> i = orderedRoomsGroup.entrySet().iterator();
        while (i.hasNext()) {
            int roomsCount = i.next().getValue().roomsCount;
            if (roomsCount == 0)
                i.remove();
        }
    }

    private void updateButtons(View callerButton, int roomCount) {
        // Очистить список он сброшенных заказов
        cleanZeroOrders();
        // изменить цвет кнопки, если заказаны номера или вернуть прежний цвет в случае отмены заказа
        if (roomCount != 0)
            callerButton.setBackgroundResource(R.drawable.round_corners_button_blue);
        else
            callerButton.setBackgroundResource(R.drawable.round_corners_button_green);

        // показать/скрыть кнопку заказа с суммой заказа + анимация
        float totalMoney = 0.0f;
        for (OrderedRoomData entry : orderedRoomsGroup.values())
            totalMoney += entry.price;
        if (totalMoney != 0) {
            Animation slideUp = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_down);

            btnOrder.setVisibility(View.VISIBLE);
            btnOrder.startAnimation(slideUp);
            String infoMoney = "Всего " + new DecimalFormat("##.##").format(totalMoney) + " грн.";
            btnOrder.setText(infoMoney);
        } else
            btnOrder.setVisibility(View.GONE);
    }

    public void onOrderButtonPressed() {

        // сохранить результат работы fragmOrder
        Bundle roomsGroupList = new Bundle();

        ArrayList<OrderedRoomData> roomsData = new ArrayList<>();
        for (OrderedRoomData orderedRoomData : orderedRoomsGroup.values()) {
            roomsData.add(orderedRoomData);
        }

        roomsGroupList.putParcelableArrayList(ARG_ORDERED_ROOMS_DATA, roomsData);

        if (mListener != null) {
            mListener.onOrderButtonClick(roomsGroupList);
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onOrderButtonClick(Bundle args);
    }
}
