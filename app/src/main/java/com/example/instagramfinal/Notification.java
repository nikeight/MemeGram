package com.example.instagramfinal;

public class Notification {

    private String postid;
    private String text;
    private String userid;
    private boolean isPost;

    public Notification() {
    }

    public Notification(String postid, String text, String userid, boolean isPost) {
        this.postid = postid;
        this.text = text;
        this.userid = userid;
        this.isPost = isPost;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public boolean isIsPost() {
        return isPost;
    }

    public void setIsPost(boolean ispost) {
        this.isPost = ispost;
    }
}
