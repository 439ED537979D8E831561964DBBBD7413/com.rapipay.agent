package com.rapipay.android.agent.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.rapipay.android.agent.Model.PermissionPozo;
import com.rapipay.android.agent.R;

import java.util.List;

public class PermissionCheckAdapter extends BaseAdapter {

    private List<PermissionPozo> listViewItemDtoList = null;

    private Context ctx = null;

    public PermissionCheckAdapter(Context ctx, List<PermissionPozo> listViewItemDtoList) {
        this.ctx = ctx;
        this.listViewItemDtoList = listViewItemDtoList;
    }

    @Override
    public int getCount() {
        int ret = 0;
        if (listViewItemDtoList != null) {
            ret = listViewItemDtoList.size();
        }
        return ret;
    }

    @Override
    public Object getItem(int itemIndex) {
        Object ret = null;
        if (listViewItemDtoList != null) {
            ret = listViewItemDtoList.get(itemIndex);
        }
        return ret;
    }

    @Override
    public long getItemId(int itemIndex) {
        return itemIndex;
    }

    @Override
    public View getView(int itemIndex, View convertView, ViewGroup viewGroup) {

        ListViewItemViewHolder viewHolder = null;

        if (convertView != null) {
            viewHolder = (ListViewItemViewHolder) convertView.getTag();
        } else {
            convertView = View.inflate(ctx, R.layout.activity_list_view_with_checkbox_item, null);

            CheckBox listItemCheckbox = (CheckBox) convertView.findViewById(R.id.list_view_item_checkbox);

            TextView listItemText = (TextView) convertView.findViewById(R.id.list_view_item_text);

            viewHolder = new ListViewItemViewHolder(convertView);

            viewHolder.setItemCheckbox(listItemCheckbox);

            viewHolder.setItemTextView(listItemText);

            convertView.setTag(viewHolder);
        }

        PermissionPozo listViewItemDto = listViewItemDtoList.get(itemIndex);
        if (listViewItemDto.getStatus().equalsIgnoreCase("Y"))
            viewHolder.getItemCheckbox().setChecked(true);
        viewHolder.getItemTextView().setText(listViewItemDto.getServiceName());

        return convertView;
    }
}

