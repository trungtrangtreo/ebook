package com.giaothuy.ebookone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.giaothuy.ebookone.R;
import com.giaothuy.ebookone.model.NavHeader;
import com.giaothuy.ebookone.model.NavItem;
import com.giaothuy.ebookone.widget.AnimatedExpandableListView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by 1 on 2/23/2018.
 */

public class ChapterAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {

    private Context _context;
    private List<NavHeader> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<NavHeader, List<NavItem>> _listDataChild;

    private int selectedIndex, groupPosition_ = -1;

    public ChapterAdapter(Context context, List<NavHeader> listDataHeader,
                          HashMap<NavHeader, List<NavItem>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final NavItem navItem = (NavItem) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.item_data, null);
        }
        TextView tvName = convertView.findViewById(R.id.tvName);

        tvName.setText(navItem.getName());

        if (selectedIndex != -1 && childPosition == selectedIndex && groupPosition == groupPosition_) {
            tvName.setBackgroundColor(_context.getResources().getColor(R.color.bg_selected));
        } else {
            tvName.setBackgroundColor(_context.getResources().getColor(R.color.transparent));
        }

        if ((selectedIndex != -1 && groupPosition == 0) || (selectedIndex != -1 && groupPosition == 1)) {
            tvName.setBackgroundColor(_context.getResources().getColor(R.color.transparent));
        }

        return convertView;
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        NavHeader nvNavHeader = (NavHeader) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.item_header, null);
        }

        TextView tvName = convertView.findViewById(R.id.tvName);
        tvName.setText(nvNavHeader.getName());
        tvName.setCompoundDrawablesWithIntrinsicBounds(0, 0, isExpanded ? R.drawable.vt_keyboard_arrow_down : R.drawable.vt_keyboard_arrow_right, 0);

        if (groupPosition == 0 || groupPosition == 1) {
            tvName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void setSelectedIndex(int ind, int groupPosition) {
        selectedIndex = ind;
        this.groupPosition_ = groupPosition;
        notifyDataSetChanged();
    }
}
