package com.giaothuy.ebookone.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.giaothuy.ebookone.R;
import com.giaothuy.ebookone.model.Comment;

import java.util.List;

/**
 * Created by 1 on 3/7/2018.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {

    private Context context;
    private List<Comment> list;

    public CommentAdapter(Context context, List<Comment> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Comment comment = list.get(position);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(R.mipmap.ic_user);
        Glide.with(context).setDefaultRequestOptions(requestOptions).load(comment.getAvatar()).into(holder.ivAvatar);
        holder.tvComment.setText(comment.getMessage());
        holder.tvTime.setText(comment.getEmail());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivAvatar;
        public TextView tvComment;
        public TextView tvName;
        public TextView tvTime;

        public MyViewHolder(View view) {
            super(view);
            tvTime = view.findViewById(R.id.tvTime);
            tvName = view.findViewById(R.id.tvName);
            tvComment = view.findViewById(R.id.tvComment);
            ivAvatar = view.findViewById(R.id.ivAvatar);

        }
    }
}
