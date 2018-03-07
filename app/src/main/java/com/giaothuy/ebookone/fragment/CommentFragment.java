package com.giaothuy.ebookone.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.giaothuy.ebookone.R;
import com.giaothuy.ebookone.activity.BaseFragment;
import com.giaothuy.ebookone.adapter.CommentAdapter;
import com.giaothuy.ebookone.api.ApiClient;
import com.giaothuy.ebookone.api.ApiInterface;
import com.giaothuy.ebookone.database.SessionManager;
import com.giaothuy.ebookone.model.Comment;
import com.giaothuy.ebookone.model.ServerResponse;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommentFragment extends BaseFragment {


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.btnGoogle)
    Button btnGoogle;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.edtComment)
    EditText edtComment;
    @BindView(R.id.ivAvatar)
    ImageView ivAvatar;
    @BindView(R.id.btnAddComment)
    Button btnAddComment;

    private Unbinder unbinder;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private String TAG = CommentFragment.class.getSimpleName();
    private CommentAdapter adapter;
    private List<Comment> list = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comment, container, false);
        unbinder = ButterKnife.bind(this, view);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        mAuth = FirebaseAuth.getInstance();

        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });


        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtComment.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getActivity(), "Bạn chưa nhập nội dung bình luận", Toast.LENGTH_SHORT).show();
                } else {
                    if (session.isLoggedIn()) {
                        addComment(session.getUserDetails().get(SessionManager.KEY_UID), edtComment.getText().toString());
                    } else {
                        showDialog("Xin lỗi, bạn vui lòng login bằng tài khoản google để thực hiện chức năng này");
                    }
                }
            }
        });

        adapter = new CommentAdapter(getActivity(), list);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        updateUI(firebaseUser);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        showProgressDialog();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            updateUI(null);
                        }
                        hideProgressDialog();
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            ivAvatar.setVisibility(View.VISIBLE);
            if (user.getPhotoUrl() != null) {
                Glide.with(getActivity()).load(user.getPhotoUrl().toString()).into(ivAvatar);
            } else {
                ivAvatar.setImageResource(R.mipmap.ic_user);
            }
            addUser(user.getUid(), user.getDisplayName(), user.getEmail(), user.getPhotoUrl().toString());
            session.createLoginSession(user.getDisplayName(), user.getEmail(), user.getUid(), user.getPhotoUrl().toString());
        } else {
            ivAvatar.setVisibility(View.GONE);
            tvName.setText("Not get Info");
        }
    }

    private void addUser(String uid, String name, String email, String avatar) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ServerResponse> call = apiService.createUser(uid, name, email, avatar);
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.body().isStatus()) {
                    Toast.makeText(getActivity(), "Login thanh cong", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Login that bai", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });
    }

    private void addComment(String uid, String message) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ServerResponse> call = apiService.addComment(uid, message);
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.body().isStatus()) {

                } else {
                    Toast.makeText(getActivity(), "Comment that bai", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });
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
        signIn();
    }
}