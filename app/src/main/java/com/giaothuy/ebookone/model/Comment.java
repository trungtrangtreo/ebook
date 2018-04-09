package com.giaothuy.ebookone.model;

import com.google.firebase.database.IgnoreExtraProperties;

// [START comment_class]
@IgnoreExtraProperties
public class Comment {

    public String uid;
    public String author;
    public String text;
    public String avatar;
    public long timeStamp;

    public Comment() {
        // Default constructor required for calls to DataSnapshot.getValue(Comment.class)
    }

    public Comment(String uid, String author, String text, String avatar, long timeStamp) {
        this.uid = uid;
        this.author = author;
        this.text = text;
        this.avatar = avatar;
        this.timeStamp = timeStamp;
    }

    public Comment(String uid, String author, String text) {
        this.uid = uid;
        this.author = author;
        this.text = text;
    }

}
// [END comment_class]
