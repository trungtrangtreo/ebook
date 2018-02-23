package com.giaothuy.ebookone.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.giaothuy.ebookone.R;
import com.giaothuy.ebookone.callback.ToolgeListener;
import com.giaothuy.ebookone.config.Constant;
import com.giaothuy.ebookone.database.DatabaseHandler;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.listener.OnPageScrollListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReadFileFragment extends Fragment implements OnPageChangeListener, OnLoadCompleteListener,
        OnPageErrorListener {

    @BindView(R.id.pdfView)
    PDFView pdfView;

    @BindView(R.id.rlTop)
    RelativeLayout rlTop;

    @BindView(R.id.title)
    TextView title;

    private Unbinder unbinder;

    private ToolgeListener listener;

    private DatabaseHandler databaseHandler;

    private int pageView=0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_read_file, container, false);

        databaseHandler = new DatabaseHandler(getActivity());
        unbinder = ButterKnife.bind(this, view);


        if(databaseHandler.getPageCount()>0)
        {
            pageView=databaseHandler.getPage(1);
        }

        pdfView.fromAsset(Constant.FILE_NAME)
                .defaultPage(pageView)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .enableSwipe(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(getActivity()))
                .spacing(10) // in dp
                .onPageError(this)
                .onPageScroll(new OnPageScrollListener() {
                    @Override
                    public void onPageScrolled(int page, float positionOffset) {

                    }
                })
                .pageFitPolicy(FitPolicy.BOTH)
                .load();


        rlTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.closeDrawer();
            }
        });

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (ToolgeListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter(Constant.SEND_BROADCAST));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        if (broadcastReceiver != null) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
        }


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        int page=pdfView.getCurrentPage();
        if (databaseHandler.getPageCount() > 0) {
            databaseHandler.updatePage(page+"");
        } else {
            databaseHandler.addPage(page+"");
        }
    }

    @Override
    public void loadComplete(int nbPages) {

    }

    @Override
    public void onPageChanged(int page, int pageCount) {

    }

    @Override
    public void onPageError(int page, Throwable t) {

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            pdfView.jumpTo(intent.getIntExtra(Constant.PAGE, 0) - 1, true);
            title.setText(intent.getStringExtra(Constant.TITLE));
        }
    };

}
