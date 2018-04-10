package com.giaothuy.ebookone.fragment;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.giaothuy.ebookone.R;
import com.giaothuy.ebookone.activity.BaseFragment;
import com.giaothuy.ebookone.callback.ReplaceListener;
import com.giaothuy.ebookone.model.Comment;
import com.giaothuy.ebookone.model.Post;
import com.giaothuy.ebookone.model.User;
import com.giaothuy.ebookone.viewholder.MyDividerItemDecoration;
import com.giaothuy.ebookone.viewholder.PostViewHolder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommentFragment extends BaseFragment {


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;


    @BindView(R.id.fab_new_post)
    FloatingActionButton fabNewPost;

    @BindView(R.id.tvNotComment)
    TextView tvNotComment;

    private Unbinder unbinder;
    //    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 123;

    private FirebaseAuth mAuth;
    private String TAG = CommentFragment.class.getSimpleName();
    private List<Comment> list = new ArrayList<>();
    private FirebaseRecyclerAdapter<Post, PostViewHolder> mAdapter;
    private DatabaseReference mDatabase;
    private ReplaceListener listener;
    private LinearLayoutManager mManager;
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(3);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comment, container, false);
        unbinder = ButterKnife.bind(this, view);

        final List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build());

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        fabNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() != null) {
                    listener.onReplace();
                } else {
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(providers)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initComment();

    }

    private void initComment() {
        Query postsQuery = mDatabase.child("posts").limitToFirst(10000);

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Post>()
                .setQuery(postsQuery, Post.class)
                .build();
        mAdapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(options) {

            @Override
            public PostViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new PostViewHolder(inflater.inflate(R.layout.item_comment, viewGroup, false));
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            protected void onBindViewHolder(final PostViewHolder viewHolder, int position, final Post model) {
                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole post view
                final String postKey = postRef.getKey();
                viewHolder.tvReply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.reply(postKey);
                    }
                });

                if (model.stars.containsKey(getUid())) {
                    viewHolder.ivLike.setImageResource(R.drawable.ic_favorite_pink);
                } else {
                    viewHolder.ivLike.setImageResource(R.drawable.vt_favorite);
                }

                // Bind Post to ViewHolder, setting OnClickListener for the star button
                viewHolder.bindToPost(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View starView) {
                        animateHeartButton(viewHolder.ivLike);

                        DatabaseReference globalPostRef = mDatabase.child("posts").child(postRef.getKey());
                        DatabaseReference userPostRef = mDatabase.child("user-posts").child(model.uid).child(postRef.getKey());

                        // Run two transactions
                        onStarClicked(globalPostRef);
                        onStarClicked(userPostRef);

                    }
                });
            }

        };

        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mManager);
        recyclerView.addItemDecoration(new MyDividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL, 0));
        recyclerView.setAdapter(mAdapter);
    }

    private void onStarClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Post p = mutableData.getValue(Post.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                if (p.stars.containsKey(getUid())) {
                    // Unstar the post and remove self from stars
                    p.starCount = p.starCount - 1;
                    p.stars.remove(getUid());
                } else {
                    // Star the post and add self to stars
                    p.starCount = p.starCount + 1;
                    p.stars.put(getUid(), true);
                }

                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (ReplaceListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(e.toString()
                    + " must implement OnHeadlineSelectedListener");
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess(mAuth.getCurrentUser());
            fabNewPost.setImageResource(R.drawable.vt_mode_edit);
        } else {
            fabNewPost.setImageResource(R.drawable.vt_login);
            tvNotComment.setVisibility(View.VISIBLE);
        }
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private void onAuthSuccess(FirebaseUser user) {
        String username = usernameFromEmail(user.getEmail());
        String urlAvatar = "";
        if (user.getPhotoUrl() != null) {
            urlAvatar = user.getPhotoUrl().toString();
        }
        writeNewUser(user.getUid(), username, user.getEmail(), urlAvatar);

    }

    private void writeNewUser(String userId, String name, String email, String avatar) {
        User user = new User(name, email, avatar);

        mDatabase.child("users").child(userId).setValue(user);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                mAuth = FirebaseAuth.getInstance();
                mDatabase = FirebaseDatabase.getInstance().getReference();
                initComment();
                mAdapter.startListening();
                fabNewPost.setImageResource(R.drawable.vt_mode_edit);
                tvNotComment.setVisibility(View.GONE);
            } else {

            }
        }
    }


    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    protected void cancle() {
        super.cancle();
    }

    @Override
    protected void agree() {
        super.agree();
//        signIn();
    }


    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
//            mAdapter.stopListening();
        }
    }

    private void animateHeartButton(final ImageView ivLike) {
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(ivLike, "rotation", 0f, 360f);
        rotationAnim.setDuration(300);
        rotationAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

        ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(ivLike, "scaleX", 0.2f, 1f);
        bounceAnimX.setDuration(300);
        bounceAnimX.setInterpolator(OVERSHOOT_INTERPOLATOR);

        ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(ivLike, "scaleY", 0.2f, 1f);
        bounceAnimY.setDuration(300);
        bounceAnimY.setInterpolator(OVERSHOOT_INTERPOLATOR);
        bounceAnimY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                ivLike.setImageResource(R.drawable.ic_favorite_pink);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }
        });

        animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);
        animatorSet.start();

    }
}
