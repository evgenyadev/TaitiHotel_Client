package kotov.invisible.taitihotel.dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import kotov.invisible.taitihotel.R;

public class ShowInfoDialog extends DialogFragment {

    private static final String ARG_ROOM_TYPE = "roomType";
    private static final String ARG_DESCRIPTION = "description";

    public static ShowInfoDialog newInstance(String room_type, String description) {
        Bundle args = new Bundle();

        ShowInfoDialog fragment = new ShowInfoDialog();
        args.putString(ARG_DESCRIPTION, description);
        args.putString(ARG_ROOM_TYPE, room_type);
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_show_info, container, false);

        String roomType;
        String description;

        if (getArguments() != null) {
            roomType = getArguments().getString(ARG_ROOM_TYPE);
            description = getArguments().getString(ARG_DESCRIPTION);
        } else
            return v;

        // Dialog title
        getDialog().setTitle(roomType);

        // Dialog body
        TextView tvShowInfo = (TextView) v.findViewById(R.id.tvShowInfo);
        tvShowInfo.setText(description);

        // btn do nothing
        Button btnOk = (Button) v.findViewById(R.id.buttonShowInfoOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return v;
    }
}
