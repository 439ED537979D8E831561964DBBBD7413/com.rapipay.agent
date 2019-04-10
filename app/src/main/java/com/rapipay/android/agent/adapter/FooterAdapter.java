package com.rapipay.android.agent.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.rapipay.android.agent.Model.HeaderePozo;
import com.rapipay.android.agent.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import me.grantland.widget.AutofitTextView;

public class FooterAdapter extends RecyclerView.Adapter<FooterAdapter.ViewHolder> {

    private ArrayList<HeaderePozo> mValues;
    private RecyclerView mRecyclerView;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mImageView;
        public final AutofitTextView recycler_text;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.recycler_image);
            recycler_text = (AutofitTextView) view.findViewById(R.id.recycler_text);
        }
    }

    public FooterAdapter(Context context, RecyclerView recyclerView, ArrayList<HeaderePozo> items) {
        mValues = items;
        mRecyclerView = recyclerView;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_layout, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.recycler_text.setText(mValues.get(position).getHeaderData());
        loadImageFromStorage(mValues.get(position).getHeaderValue(),holder.mImageView,mValues.get(position).getPath());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    private void loadImageFromStorage(String name, ImageView view, String path)
    {
        try {
            File f=new File(path, name);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            view.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }
}





