package com.example.mca;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MemberRecyclerViewAdapter extends RecyclerView.Adapter<MemberRecyclerViewAdapter.ViewHolder> {

    private List<JSONObject> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    MemberRecyclerViewAdapter(Context context, List<JSONObject> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.rv_member_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        JSONObject cur = mData.get(position);
        try {
            String member_id = cur.getString("member_id");
            String first_name = cur.getString("first_name");
            String mid_name = cur.getString("mid_name");
            String last_name = cur.getString("last_name");

            String full_name = get_name(first_name,mid_name,last_name);

            holder.member_id_tv.setText(member_id);
            holder.full_name_tv.setText(full_name);

        } catch (JSONException e) {
            e.printStackTrace();
        }



    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView member_id_tv;
        TextView full_name_tv;


        ViewHolder(View itemView) {
            super(itemView);
            member_id_tv = itemView.findViewById(R.id.tv_membership_id);
            full_name_tv = itemView.findViewById(R.id.tv_full_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        try {
            return mData.get(id).getString("member_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    private String get_name(String first, String mid, String last)
    {
        String name;
        if (mid.equals(""))
        {
            name = first + " " + last;
        }
        else
        {
            name = first + " " + mid + " " + last;
        }
        return name;
    }
}