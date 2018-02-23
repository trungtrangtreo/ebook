package com.giaothuy.ebookone.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.giaothuy.ebookone.R;
import com.giaothuy.ebookone.adapter.ChapterAdapter;
import com.giaothuy.ebookone.callback.ToolgeListener;
import com.giaothuy.ebookone.config.Constant;
import com.giaothuy.ebookone.model.NavHeader;
import com.giaothuy.ebookone.model.NavItem;
import com.giaothuy.ebookone.widget.AnimatedExpandableListView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChapterFragment extends Fragment {


    @BindView(R.id.lvExp)
    AnimatedExpandableListView lvExp;

    private ChapterAdapter adapter;
    private Unbinder unbinder;
    private List<NavHeader> listDataHeader;
    private HashMap<NavHeader, List<NavItem>> listDataChild;

    private SlidingMenu slidingMenu;

    private ToolgeListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (ToolgeListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + "must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chapter, container, false);
        unbinder = ButterKnife.bind(this, view);

        prepareListData();
        adapter = new ChapterAdapter(getActivity(), listDataHeader, listDataChild);
        lvExp.setAdapter(adapter);

        lvExp.expandGroup(2);

        lvExp.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (lvExp.isGroupExpanded(groupPosition)) {
                    lvExp.collapseGroupWithAnimation(groupPosition);
                } else {
                    lvExp.expandGroupWithAnimation(groupPosition);
                }

                if (groupPosition == 0) {
                    navPage(Constant.MOVE_PAGE.LOI_NGO, listDataHeader.get(groupPosition).getName());
                    listener.closeDrawer();
                    adapter.setSelectedIndex(0, 0);
                } else if (groupPosition == 1) {
                    navPage(Constant.MOVE_PAGE.LOI_MO_DAU, listDataHeader.get(groupPosition).getName());
                    listener.closeDrawer();
                    adapter.setSelectedIndex(0, 0);
                }
                return true;
            }
        });

        lvExp.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                int page = 0;
                if (groupPosition == 2) {
                    switch (childPosition) {
                        case 0:
                            page = Constant.MOVE_PAGE.CHUONG_1;
                            break;
                        case 1:
                            page = Constant.MOVE_PAGE.CHUONG_2;
                            break;
                        case 2:
                            page = Constant.MOVE_PAGE.CHUONG_3;
                            break;
                        case 3:
                            page = Constant.MOVE_PAGE.CHUONG_4;
                            break;
                        case 4:
                            page = Constant.MOVE_PAGE.CHUONG_5;
                            break;
                        case 5:
                            page = Constant.MOVE_PAGE.CHUONG_6;
                            break;
                    }

                } else if (groupPosition == 3) {
                    switch (childPosition) {
                        case 0:
                            page = Constant.MOVE_PAGE.CHUONG_7;
                            break;
                        case 1:
                            page = Constant.MOVE_PAGE.CHUONG_8;
                            break;
                        case 2:
                            page = Constant.MOVE_PAGE.CHUONG_9;
                            break;
                        case 3:
                            page = Constant.MOVE_PAGE.CHUONG_10;
                            break;
                        case 4:
                            page = Constant.MOVE_PAGE.CHUONG_11;
                            break;
                        case 5:
                            page = Constant.MOVE_PAGE.CHUONG_12;
                            break;
                    }
                } else if (groupPosition == 4) {
                    switch (childPosition) {
                        case 0:
                            page = Constant.MOVE_PAGE.CHUONG_13;
                            break;
                        case 1:
                            page = Constant.MOVE_PAGE.CHUONG_14;
                            break;
                        case 2:
                            page = Constant.MOVE_PAGE.CHUONG_15;
                            break;
                        case 3:
                            page = Constant.MOVE_PAGE.CHUONG_16;
                            break;
                        case 4:
                            page = Constant.MOVE_PAGE.CHUONG_17;
                            break;
                        case 5:
                            page = Constant.MOVE_PAGE.CHUONG_18;
                            break;
                        case 6:
                            page = Constant.MOVE_PAGE.CHUONG_19;
                            break;
                        case 7:
                            page = Constant.MOVE_PAGE.CHUONG_20;
                            break;
                    }
                }

                navPage(page, listDataChild.get(
                        listDataHeader.get(groupPosition)).get(
                        childPosition).getName());

                adapter.setSelectedIndex(childPosition, groupPosition);
                listener.closeDrawer();
                return false;
            }
        });
        return view;
    }

    private void navPage(int page, String name) {
        Intent intent = new Intent(Constant.SEND_BROADCAST);
        intent.putExtra(Constant.PAGE, page);
        intent.putExtra(Constant.TITLE, name);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    private void prepareListData() {

        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        // Adding header data
        listDataHeader.add(new NavHeader(getString(R.string.head)));
        listDataHeader.add(new NavHeader(getString(R.string.preface)));
        listDataHeader.add(new NavHeader(getString(R.string.header_one)));
        listDataHeader.add(new NavHeader(getString(R.string.header_two)));
        listDataHeader.add(new NavHeader(getString(R.string.header_three)));

        List<NavItem> list_one = new ArrayList<>();
        String arrOne[] = getActivity().getResources().getStringArray(R.array.chapter_one);
        for (int i = 0; i < arrOne.length; i++) {
            list_one.add(new NavItem(arrOne[i]));
        }

        List<NavItem> list_two = new ArrayList<>();
        String arrTwo[] = getActivity().getResources().getStringArray(R.array.chapter_two);
        for (int i = 0; i < arrOne.length; i++) {
            list_two.add(new NavItem(arrTwo[i]));
        }

        List<NavItem> list_three = new ArrayList<>();
        String arrThree[] = getActivity().getResources().getStringArray(R.array.chapter_three);
        for (int i = 0; i < arrOne.length; i++) {
            list_three.add(new NavItem(arrThree[i]));
        }

        List<NavItem> list_head = new ArrayList<>();

        listDataChild.put(listDataHeader.get(0), list_head); // Header, Child data
        listDataChild.put(listDataHeader.get(1), list_head);
        listDataChild.put(listDataHeader.get(2), list_one);
        listDataChild.put(listDataHeader.get(3), list_two);
        listDataChild.put(listDataHeader.get(4), list_three);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
