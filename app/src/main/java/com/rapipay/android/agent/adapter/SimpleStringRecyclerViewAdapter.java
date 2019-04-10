package com.rapipay.android.agent.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.rapipay.android.agent.Model.ImagePozo;
import com.rapipay.android.agent.R;

import java.util.ArrayList;

import me.grantland.widget.AutofitTextView;

public class SimpleStringRecyclerViewAdapter extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

    private ArrayList<ImagePozo> mValues;
    private RecyclerView mRecyclerView;
    private Context context;
    private String type;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mImageView;
        public final LinearLayout mLayoutItem;
        public final AutofitTextView recycler_text;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.recycler_image);
            mLayoutItem = (LinearLayout) view.findViewById(R.id.layout_item);
            recycler_text = (AutofitTextView) view.findViewById(R.id.recycler_text);
        }
    }

    public SimpleStringRecyclerViewAdapter(Context context, RecyclerView recyclerView, ArrayList<ImagePozo> items, String type) {
        this.mValues = items;
        this.mRecyclerView = recyclerView;
        this.context = context;
        this.type = type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_list_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mImageView.setImageDrawable(context.getResources().getDrawable(mValues.get(position).getImageUrl()));
        holder.recycler_text.setText(mValues.get(position).getImageName());
        if (type.equalsIgnoreCase("first"))
            holder.recycler_text.setTextColor(context.getResources().getColor(R.color.white));
        holder.mLayoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
}
