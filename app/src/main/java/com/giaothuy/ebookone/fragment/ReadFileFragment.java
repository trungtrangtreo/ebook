package com.giaothuy.ebookone.fragment;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;

import com.giaothuy.ebookone.R;
import com.giaothuy.ebookone.callback.ToolgeListener;
import com.giaothuy.ebookone.config.Constant;
import com.giaothuy.ebookone.database.DatabaseHandler;
import com.giaothuy.ebookone.service.AlarmReceiver;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.listener.OnPageScrollListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;

import java.util.Calendar;

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

    @BindView(R.id.ivDrawer)
    ImageView ivDrawer;

    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.ivSetting)
    ImageView ivSetting;

    private Unbinder unbinder;
    private ToolgeListener listener;
    private DatabaseHandler databaseHandler;
    private int pageView = 0;
    private boolean isScroll = true;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_read_file, container, false);

        databaseHandler = new DatabaseHandler(getActivity());
        alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

        unbinder = ButterKnife.bind(this, view);


        if (databaseHandler.getPageCount() > 0) {
            pageView = databaseHandler.getPage(1);
        }

        pdfView.fromAsset(Constant.FILE_NAME)
                .defaultPage(pageView)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .enableSwipe(true)
                .onLoad(this)
                .enableDoubletap(false)
                .scrollHandle(new DefaultScrollHandle(getActivity()))
                .spacing(10) // in dp
                .onPageError(this)
                .onPageChange(new OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int page, int pageCount) {
                        if (isScroll) {
                            setTitle(page + 1);
                        }
                    }
                })
                .onPageScroll(new OnPageScrollListener() {
                    @Override
                    public void onPageScrolled(int page, float positionOffset) {

                    }
                })
                .pageFitPolicy(FitPolicy.BOTH)
                .load();


        ivDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.closeDrawer();
            }
        });

        ivSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getActivity(), ivSetting);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.poupup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.alarm:
                                showSeebar();
                                break;
                            case R.id.contact:
                                break;
                        }

                        return true;
                    }
                });
                popup.show();
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
                    + "must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter(Constant.SEND_BROADCAST));
        if (databaseHandler.getPageCount() > 0) {
            title.setText(databaseHandler.getTitle(1));
        }
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

        int page = pdfView.getCurrentPage();
        if (databaseHandler.getPageCount() > 0) {
            databaseHandler.updatePage(page + "", title.getText().toString());
        } else {
            databaseHandler.addPage(page + "", title.getText().toString());
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
            isScroll = false;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isScroll = true;
                }
            }, 1000);
        }
    };

    private void setTitle(int page) {
        if (page == 5) {
            title.setText("Lời ngỏ");
        } else if (page == 8) {
            title.setText("Lời nói đầu");
        } else if (page == 9) {
            title.setText("Chương 1: Dũng cảm mở lời, dám nói mới biết cách nói");
        } else if (page == 27) {
            title.setText("Chương 2: Đọc nhiều – đi nhiều, tích lũy kiến thức giao tiếp");
        } else if (page == 36) {
            title.setText("Chương 3: Bắt bệnh để làm chủ cuộc giao tiếp");
        } else if (page == 56) {
            title.setText("Chương 4: Nắm vững chừng mực trong giao tiếp");
        } else if (page == 73) {
            title.setText("LChương 5: Khen nhiều chê ít, tránh để lời nói làm hại đến thân");
        } else if (page == 80) {
            title.setText("Chương 6: Thêm gia vị hài hước cho giao tiếp");
        } else if (page == 98) {
            title.setText("Chương 7: Kĩ năng giao tiếp với mỗi hoàn cảnh và đối tượng khác nhau");
        } else if (page == 117) {
            title.setText("Chương 8: Cách giao tiếp với lãnh đạo để giành cơ hội phát triển nghề nghiệp");
        } else if (page == 139) {
            title.setText("Chương 9: Chốn công sở nhiều thị phi, biết cách ăn nói rất quan trọng");
        } else if (page == 157) {
            title.setText("Chương 10: Khéo ăn nói trong nghệ thuật bán hàng");
        } else if (page == 185) {
            title.setText("Chương 11: Rèn luyện tài đàm phán, luôn nắm chắc phần thắng");
        } else if (page == 209) {
            title.setText("Chương 12: Kĩ năng diễn thuyết sinh động");
        } else if (page == 227) {
            title.setText("Chương 13: Nắm chắc kĩ năng ngôn ngữ giúp bạn hòa nhập buổi tiệc");
        } else if (page == 238) {
            title.setText("Chương 14: Những lời nói ngọt ngào trong tình yêu");
        } else if (page == 252) {
            title.setText("Chương 15: Từ chối khéo léo để không làm mất lòng người khác");
        } else if (page == 268) {
            title.setText("Chương 16: Khéo ăn nói khi nhờ người khác giúp đỡ");
        } else if (page == 282) {
            title.setText("Chương 17: Nghệ thuật thuyết phục");
        } else if (page == 294) {
            title.setText("Chương 18: Con người khó tránh việc mắc lỗi, cần thành khẩn khi xin lỗi");
        } else if (page == 304) {
            title.setText("Chương 19: Lời nói thật dễ nghe, khéo léo trong phê bình");
        } else if (page == 314) {
            title.setText("Chương 20: Nghệ thuật an ủi làm ấm lòng người khác");
        }
    }

    private void showSeebar() {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View view = factory.inflate(R.layout.dialog_seebar, null);
        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        dialog.setView(view);
        dialog.show();

        SeekBar seekBar = view.findViewById(R.id.seekBar);
        final TextView tvCountSeebar = view.findViewById(R.id.tvCountSeebar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvCountSeebar.setText(String.valueOf(progress) + " phút");
                if (progress > 0) {
                    Calendar calendar = Calendar.getInstance();
                    Intent intent = new Intent(getActivity(), AlarmReceiver.class);
                    intent.putExtra(Constant.TITLE, title.getText().toString());
                    pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + progress * 60 * 1000, pendingIntent);
                } else {
                    alarmManager.cancel(pendingIntent);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }
}
