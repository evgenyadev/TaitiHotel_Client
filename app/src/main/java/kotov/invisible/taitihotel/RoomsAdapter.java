package kotov.invisible.taitihotel;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import kotov.invisible.taitihotel.ApiEngine.RoomsGroup;

public class RoomsAdapter extends RecyclerView.Adapter<RoomsAdapter.ViewHolder> {

    public static final String TAG = "RoomsAdapter";
    private List<RoomsGroup> roomsGroupList;
    private OnItemClickListener mListener;

    public RoomsAdapter(List<RoomsGroup> roomsGroupList, OnItemClickListener listener) {
        this.roomsGroupList = roomsGroupList;
        this.mListener = listener;
    }

    @Override
    public RoomsAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item_rooms, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RoomsGroup roomsGroup = roomsGroupList.get(position);
        holder.bind(roomsGroup, mListener);
    }

    @Override
    public int getItemCount() {
        return roomsGroupList == null ? 0 : roomsGroupList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View v, RoomsGroup roomsGroup);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView room_type;
        TextView available;
        ImageButton infoButton;
        ImageButton orderButton;

        ViewHolder(View itemView) {
            super(itemView);
            orderButton = (ImageButton) itemView.findViewById(R.id.orderButton);
            infoButton = (ImageButton) itemView.findViewById(R.id.infoButton);
            room_type = (TextView) itemView.findViewById(R.id.room_type);
            available = (TextView) itemView.findViewById(R.id.room_available);
        }

        void bind(final RoomsGroup roomsGroup, final OnItemClickListener listener) {
            room_type.setText(String.valueOf(roomsGroup.getRoomType()));
            available.setText(String.valueOf("свободных номеров: " + roomsGroup.getCount()));
            infoButton.setContentDescription(roomsGroup.getDescription());

            infoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(v, roomsGroup);
                }
            });
            orderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(v, roomsGroup);
                }
            });
        }

    }

}
