package com.heyoe_chat.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by dell17 on 4/10/2016.
 */
public class PostModel implements Serializable{
    String post_id;
    String posted_date;
    String poster_id;
    String poster_avatar;
    String poster_celebrity;
    String media_type;
    String media_url;
    String like_count;
    String dislike_count;
    String comment_count;
    String shared_count;
    String viewed_count;
    String like;
    String favorite;
    String commented;
    String friendStatus;
    String poster_fullname;
    String  hashtag;

//    public String getFriend_tag() {
//        return friend_tag;
//    }
//
//    public void setFriend_tag(String friend_tag) {
//        this.friend_tag = friend_tag;
//    }
//
//    public String getFriend_ids() {
//        return friend_ids;
//    }
//
//    public void setFriend_ids(String friend_ids) {
//        this.friend_ids = friend_ids;
//    }

    public String getHashtag() {
        return hashtag;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    Long clickedTime;
    int imageWidth, imageHeight;

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public Long getClickedTime() {
        return clickedTime;
    }

    public void setClickedTime(Long clickedTime) {
        this.clickedTime = clickedTime;
    }

    public String getPoster_fullname() {
        return poster_fullname;
    }

    public void setPoster_fullname(String poster_fullname) {
        this.poster_fullname = poster_fullname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    String description;

    public String getDislike_count() {
        return dislike_count;
    }

    public void setDislike_count(String dislike_count) {
        this.dislike_count = dislike_count;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getFavorite() {
        return favorite;
    }

    public void setFavorite(String favorite) {
        this.favorite = favorite;
    }

    public String getCommented() {
        return commented;
    }

    public void setCommented(String commented) {
        this.commented = commented;
    }

    public String getFriendStatus() {
        return friendStatus;
    }

    public void setFriendStatus(String friendStatus) {
        this.friendStatus = friendStatus;
    }

    ArrayList<UserModel> arrLiked_friends = new ArrayList<>();

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getPosted_date() {
        return posted_date;
    }

    public void setPosted_date(String posted_date) {
        this.posted_date = posted_date;
    }

    public String getPoster_id() {
        return poster_id;
    }

    public void setPoster_id(String poster_id) {
        this.poster_id = poster_id;
    }

    public String getPoster_avatar() {
        return poster_avatar;
    }

    public void setPoster_avatar(String poster_avatar) {
        this.poster_avatar = poster_avatar;
    }

    public String getPoster_celebrity() {
        return poster_celebrity;
    }

    public void setPoster_celebrity(String poster_celebrity) {
        this.poster_celebrity = poster_celebrity;
    }

    public String getMedia_type() {
        return media_type;
    }

    public void setMedia_type(String media_type) {
        this.media_type = media_type;
    }

    public String getMedia_url() {
        return media_url;
    }

    public void setMedia_url(String media_url) {
        this.media_url = media_url;
    }

    public String getLike_count() {
        return like_count;
    }

    public void setLike_count(String like_count) {
        this.like_count = like_count;
    }



    public String getComment_count() {
        return comment_count;
    }

    public void setComment_count(String comment_count) {
        this.comment_count = comment_count;
    }

    public String getShared_count() {
        return shared_count;
    }

    public void setShared_count(String shared_count) {
        this.shared_count = shared_count;
    }

    public String getViewed_count() {
        return viewed_count;
    }

    public void setViewed_count(String viewed_count) {
        this.viewed_count = viewed_count;
    }


    public ArrayList<UserModel> getArrLiked_friends() {
        return arrLiked_friends;
    }

    public void setArrLiked_friends(ArrayList<UserModel> arrLiked_friends) {
        this.arrLiked_friends = arrLiked_friends;
    }

    public ArrayList<CommentModel> getArrComments() {
        return arrComments;
    }

    public void setArrComments(ArrayList<CommentModel> arrComments) {
        this.arrComments = arrComments;
    }

    ArrayList<CommentModel> arrComments = new ArrayList<>();
}
