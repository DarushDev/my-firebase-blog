package com.example.myfirebaseblog;

/**
 * Created by Lenovo Desktop on 12/9/2017.
 */

public class Blog {
    private String title;
    private String desc;
    private String imageUrl;
    private String username;

    public Blog(String title, String desc, String imageUrl, String username) {
        this.title = title;
        this.desc = desc;
        this.imageUrl = imageUrl;
        this.username = username;
    }

    public Blog() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
