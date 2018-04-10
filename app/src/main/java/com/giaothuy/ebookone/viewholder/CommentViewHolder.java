package com.giaothuy.ebookone.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.giaothuy.ebookone.R;

/**
 * Created by 1 on 4/9/2018.
 */

public class CommentViewHolder extends RecyclerView.ViewHolder {

    public TextView authorView;
    public TextView bodyView;
    public TextView tvTime;
    public ImageView ivAvatar;
    public RelativeLayout rlLike;

    public CommentViewHolder(View itemView) {
        super(itemView);

        authorView = itemView.findViewById(R.id.tvName);
        bodyView = itemView.findViewById(R.id.tvComment);
        tvTime = itemView.findViewById(R.id.tvTime);
        ivAvatar = itemView.findViewById(R.id.ivAvatar);
        rlLike = itemView.findViewById(R.id.rlLike);
    }
}
