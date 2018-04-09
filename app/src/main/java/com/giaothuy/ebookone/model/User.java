package com.giaothuy.ebookone.model;

import com.google.firebase.database.IgnoreExtraProperties;

// [START blog_user_class]
@IgnoreExtraProperties
public class User {

    public String username;
    public String email;
    public String avatar;

    public User() {
    }

    public User(String username, String email, String avatar) {
        this.username = username;
        this.email = email;
        this.avatar = avatar;
    }

}
// [END blog_user_class]
