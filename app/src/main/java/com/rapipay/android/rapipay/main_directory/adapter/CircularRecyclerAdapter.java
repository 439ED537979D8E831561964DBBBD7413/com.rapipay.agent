package com.rapipay.android.rapipay.main_directory.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rapipay.android.rapipay.R;
import com.rapipay.android.rapipay.main_directory.Model.ImagePozo;

import java.util.ArrayList;

public class CircularRecyclerAdapter extends RecyclerView.Adapter<CircularRecyclerAdapter.ViewHolder> {

    private ArrayList<ImagePozo> mValues;
    private RecyclerView mRecyclerView;
    private Context context;
    private String type;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mImageView;
        public final LinearLayout mLayoutItem;
        public final TextView recycler_text;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.recycler_image);
            mLayoutItem = (LinearLayout) view.findViewById(R.id.layout_item);
            recycler_text = (TextView) view.findViewById(R.id.recycler_text);
        }
    }

    public CircularRecyclerAdapter(Context context, RecyclerView recyclerView, ArrayList<ImagePozo> items, String type) {
        mValues = items;
        mRecyclerView = recyclerView;
        this.context = context;
        this.type = type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_circular, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
           /* FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) holder.mImageView.getLayoutParams();
            if (mRecyclerView.getLayoutManager() instanceof GridLayoutManager) {
                layoutParams.height = 200;
            } else if (mRecyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                layoutParams.height = 600;
            } else {
                layoutParams.height = 800;
            }*/
        holder.mImageView.setImageDrawable(context.getResources().getDrawable(mValues.get(position).getImageUrl()));
        holder.recycler_text.setText(mValues.get(position).getImageName());
        if (type.equalsIgnoreCase("first"))
            holder.recycler_text.setTextColor(context.getResources().getColor(R.color.white));
        holder.mLayoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(mActivity, ItemDetailsActivity.class);
//                intent.putExtra(STRING_IMAGE_URI, mValues[position]);
//                intent.putExtra(STRING_IMAGE_POSITION, position);
//                mActivity.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
}

