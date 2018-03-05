
package com.giaothuy.ebookone.activity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.giaothuy.ebookone.R;
import com.giaothuy.ebookone.config.Constant;
import com.giaothuy.ebookone.fragment.CommentFragment;
import com.giaothuy.ebookone.fragment.ReadFileFragment;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MainActivity extends BaseActivity {

    public MainActivity() {
        super(R.string.app_name);
    }

    private InterstitialAd mInterstitialAd;

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

        MobileAds.initialize(this, Constant.APP_ID_AM);
        mInterstitialAd = new InterstitialAd(this);

        mInterstitialAd.setAdUnitId(Constant.CATE_AM);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Log.e(MainActivity.class.getSimpleName(), "Not load");
                }

            }
        }, 3000);
    }

    @Override
    public void showAd() {
        super.showAd();
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Toast.makeText(this, "not show", Toast.LENGTH_SHORT).show();
        }
    }
}
