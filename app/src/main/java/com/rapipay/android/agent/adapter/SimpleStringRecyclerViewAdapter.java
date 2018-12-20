package com.rapipay.android.agent.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import me.grantland.widget.AutofitTextView;
import com.rapipay.android.agent.Model.ImagePozo;
import com.rapipay.android.agent.Model.MasterPozo;
import com.rapipay.android.agent.R;

public class SimpleStringRecyclerViewAdapter extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

    private ArrayList<MasterPozo> mValues;
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

    public SimpleStringRecyclerViewAdapter(Context context, RecyclerView recyclerView, ArrayList<MasterPozo> items, String type) {
        mValues = items;
        mRecyclerView = recyclerView;
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
           /* FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) holder.mImageView.getLayoutParams();
            if (mRecyclerView.getLayoutManager() instanceof GridLayoutManager) {
                layoutParams.height = 200;
            } else if (mRecyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                layoutParams.height = 600;
            } else {
                layoutParams.height = 800;
            }*/
        holder.mImageView.setImageBitmap(byteConvert(mValues.get(position).getIcon()));
        holder.recycler_text.setText(mValues.get(position).getServiceTypeName());
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
    protected Bitmap byteConvert(byte[] decodedString) {
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
}
