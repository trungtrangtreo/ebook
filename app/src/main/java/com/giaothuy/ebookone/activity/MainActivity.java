
package com.giaothuy.ebookone.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;

import com.giaothuy.ebookone.R;
import com.giaothuy.ebookone.callback.EventBackPress;
import com.giaothuy.ebookone.callback.ReplaceListener;
import com.giaothuy.ebookone.fragment.CommentFragment;
import com.giaothuy.ebookone.fragment.NewPostFragment;
import com.giaothuy.ebookone.fragment.ReadFileFragment;
import com.giaothuy.ebookone.fragment.ReplyFragment;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MainActivity extends BaseActivity implements ReplaceListener, EventBackPress {

    public MainActivity() {
        super(R.string.app_name);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSlidingMenu().setMode(SlidingMenu.LEFT_RIGHT);
        getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

        setContentView(R.layout.content_frame);

        replaceFragment(new ReadFileFragment(), R.id.content_frame);

        getSlidingMenu().setSecondaryMenu(R.layout.menu_frame_two);
        getSlidingMenu().setSecondaryShadowDrawable(R.drawable.shadowright);

        replaceFragment(new CommentFragment(), R.id.menu_frame_two);

    }

    @Override
    public void showAd() {
        super.showAd();

    }

    @Override
    public void onReplace() {
        replaceFragment(new NewPostFragment(), R.id.menu_frame_two);
    }

    @Override
    public void reply(String post_key) {
        Fragment fragment = new ReplyFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ReplyFragment.EXTRA_POST_KEY, post_key);
        fragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_from_left,
                        R.anim.enter_from_right, R.anim.exit_from_right,
                        R.anim.exit_from_left)
                .replace(R.id.menu_frame_two, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void replaceFragment(Fragment fragment, int id) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_from_left,
                        R.anim.enter_from_right, R.anim.exit_from_right,
                        R.anim.exit_from_left)
                .replace(id, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressFragment() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (getSupportFragmentManager().getBackStackEntryCount() <= 2) {
                System.exit(0);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
