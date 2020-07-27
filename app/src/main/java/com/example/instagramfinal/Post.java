package com.example.instagramfinal;

public class Post {

    private String description;
    private String imageUrl;
    private String postid;
    private String publisher;

    public Post() {
        // An empty Constructor to prevent from crashing.
    }

    public Post(String description, String imageUrl, String postid, String publisher) {
        this.description = description;
        this.imageUrl = imageUrl;
        this.postid = postid;
        this.publisher = publisher;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
