package com.example.android_finalproject;

import android.content.Context;
import android.database.Cursor;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import com.example.android_finalproject.data.ContractLocation;

public class LocationListAdapter extends RecyclerView.Adapter<LocationListAdapter.LocationViewHolder> {
    private Cursor mCursor;
    private Context mContext;
    private ClickListener clickListener;
    public LocationListAdapter(Context context, Cursor cursor) {
        this.mContext = context;
        this.mCursor = cursor;
    }

    @Override
    public LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.location_list, parent, false);
        return new LocationViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) mCursor.close();
        mCursor = newCursor;
        if (newCursor != null) {
            this.notifyDataSetChanged();
        }
    }


    @Override
    public void onBindViewHolder(final LocationViewHolder holder, final int position) {
        if (!mCursor.moveToPosition(position))
            return;
        long id = mCursor.getLong(mCursor.getColumnIndex(ContractLocation.Location.ID));
        double longitude = mCursor.getDouble(mCursor.getColumnIndex(ContractLocation.Location.LONG));
        double latitude = mCursor.getDouble(mCursor.getColumnIndex(ContractLocation.Location.LAT));
        String name=mCursor.getString(mCursor.getColumnIndex(ContractLocation.Location.NAME));

        holder.IDTextView.setText(String.valueOf(id));
        holder.longitudeTextView.setText(String.valueOf(longitude));
        holder.latitudeTextView.setText(String.valueOf(latitude));
        holder.NameTextView.setText(name);
        holder.itemView.setTag(id);

        if(clickListener!=null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClick(position, v);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    clickListener.onItemLongClick(position,v);
                    return true;
                }
            });
        }

    }

    class LocationViewHolder extends RecyclerView.ViewHolder {

        TextView IDTextView;
        TextView longitudeTextView;
        TextView latitudeTextView;
        TextView NameTextView;
        public LocationViewHolder(View itemView) {
            super(itemView);
            IDTextView = (TextView) itemView.findViewById(R.id.tv_ID);
            longitudeTextView = (TextView) itemView.findViewById(R.id.tv_Lon);
            latitudeTextView=(TextView) itemView.findViewById(R.id.tv_Lat);
            NameTextView=(TextView)itemView.findViewById(R.id.tv_N);
        }
    }


    public void setOnItemClickListener(ClickListener clickListener){
        if(clickListener!=null){
            this.clickListener=clickListener;
        }
    }

    public interface ClickListener{
        void onItemClick(int position,View v);
        void onItemLongClick(int position,View v);
    }
}
