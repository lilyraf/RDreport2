package com.ict602.rdreport;

public class NewsItem {
    private int id;
    private String title;
    private String description;
    private String imageName;
    private String author;
    private String publishDate;

    public NewsItem(int id, String title, String description, String imageName, String author, String publishDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageName = imageName;
        this.author = author;
        this.publishDate = publishDate;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageName() {
        return imageName;
    }

    public String getAuthor() {
        return author;
    }

    public String getPublishDate() {
        return publishDate;
    }
}
