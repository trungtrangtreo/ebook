package com.giaothuy.ebookone.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.giaothuy.ebookone.R;
import com.giaothuy.ebookone.callback.ToolgeListener;
import com.giaothuy.ebookone.fragment.ChapterFragment;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class BaseActivity extends SlidingFragmentActivity implements ToolgeListener {

    private int mTitleRes;
    protected ChapterFragment mFrag;
    protected SlidingMenu slidingMenu;

    public BaseActivity(int titleRes) {
        mTitleRes = titleRes;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(mTitleRes);

        // set the Behind View
        setBehindContentView(R.layout.menu_frame);
        if (savedInstanceState == null) {
            FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
            mFrag = new ChapterFragment();
            t.replace(R.id.menu_frame, mFrag);
            t.commit();
        }

        // customize the SlidingMenu
        slidingMenu = getSlidingMenu();
        slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
        slidingMenu.setShadowDrawable(R.drawable.shadow);
        slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        slidingMenu.setFadeDegree(0.35f);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void closeDrawer() {
        slidingMenu.toggle();

    }

    @Override
    public void showAd() {

    }
}
