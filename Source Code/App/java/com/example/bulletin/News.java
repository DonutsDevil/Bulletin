package com.example.bulletin;
/**
 News Class to have a instance of News in the ArrayList of News
 Its Constructor has 3@Params
 @PARAM newsType: whats the type of the news
 @PARAM newsTile: title of the news
 @PARAM newsDateTime date and time of the news
* */
public class News {
    /*
    * Private members cause we don't want other classes to use this class members
    * Public methods cause other classes will need information of the current news
    * */
    private String newsType;
    private String newsTitle;
    private String newsDateTime;
    private String newsUrl;


    public News(String newsType, String newsTitle, String newsDateTime, String newsUrl){
        this.newsType = newsType;
        this.newsTitle = newsTitle;
        this.newsDateTime = newsDateTime;
        this.newsUrl = newsUrl;
    }

    // when call returns the type of the news for the current news
    public String getNewsType(){
        return newsType;
    }

    // when call returns time and date of the news for the current news
    public String getNewsDateTime() {
        return newsDateTime;
    }

    // when call returns the TITLE of the news for the current news
    public String getNewsTitle() {
        return newsTitle;
    }

    public String getNewsUrl(){
        return newsUrl;
    }
}
