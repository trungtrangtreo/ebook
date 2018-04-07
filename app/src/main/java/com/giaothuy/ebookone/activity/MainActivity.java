
package com.giaothuy.ebookone.activity;

import android.os.Bundle;

import com.giaothuy.ebookone.R;
import com.giaothuy.ebookone.fragment.CommentFragment;
import com.giaothuy.ebookone.fragment.ReadFileFragment;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MainActivity extends BaseActivity {

    public MainActivity() {
        super(R.string.app_name);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSlidingMenu().setMode(SlidingMenu.LEFT_RIGHT);
        getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

        setContentView(R.layout.content_frame);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, new ReadFileFragment())
                .commit();

        getSlidingMenu().setSecondaryMenu(R.layout.menu_frame_two);
        getSlidingMenu().setSecondaryShadowDrawable(R.drawable.shadowright);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.menu_frame_two, new CommentFragment())
                .commit();


    }

    @Override
    public void showAd() {
        super.showAd();

    }

}
