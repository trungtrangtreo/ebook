package com.giaothuy.ebookone.viewholder;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.giaothuy.ebookone.R;
import com.giaothuy.ebookone.model.Post;
import com.giaothuy.ebookone.utils.ValidateUtils;
import com.github.marlonlom.utilities.timeago.TimeAgo;

public class PostViewHolder extends RecyclerView.ViewHolder {

    public TextView tvComment;
    public TextView tvName;
    public TextView tvTime;
    public ImageView ivAvatar;
    public TextView tvTilte;
    public TextView tvLike;
    public TextView tvReply;
    public ImageView ivLike;

    public PostViewHolder(View itemView) {
        super(itemView);

        tvComment = itemView.findViewById(R.id.tvComment);
        tvName = itemView.findViewById(R.id.tvName);
        tvTime = itemView.findViewById(R.id.tvTime);
        ivAvatar = itemView.findViewById(R.id.ivAvatar);
        tvTilte = itemView.findViewById(R.id.tvTilte);
        tvLike = itemView.findViewById(R.id.tvLike);
        tvReply = itemView.findViewById(R.id.tvReply);
        ivLike = itemView.findViewById(R.id.ivLike);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void bindToPost(Post post, View.OnClickListener starClickListener) {
        tvComment.setText(post.body);
        tvName.setText(post.author);
        Glide.with(ivAvatar.getContext()).load(post.avatar).apply(new RequestOptions().error(R.mipmap.ic_user)).into(ivAvatar);
        tvTime.setText(TimeAgo.using(post.timeStamp, ValidateUtils.getTimeAgo()));
        tvLike.setText(String.valueOf(post.starCount));
        ivLike.setOnClickListener(starClickListener);
    }
}
