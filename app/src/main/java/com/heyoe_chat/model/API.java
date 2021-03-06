package com.heyoe_chat.model;

/**
 * Created by dell17 on 4/15/2016.
 */
public class API {

//    public static String BASE_URL = "http://www.heyoe.com/heyoe/index.php/api/";
//    public static String BASE_AVATAR = "http://www.heyoe.com/heyoe/assets/images/avatars/";
//    public static String BASE_HEADER_PHOTO = "http://www.heyoe.com/heyoe/assets/images/header_photo/";
//    public static String BASE_POST_PHOTO = "http://www.heyoe.com/heyoe/assets/images/post_photo/";
//    public static String BASE_THUMBNAIL = "http://www.heyoe.com/heyoe/assets/images/thumb/";
//    public static String BASE_HEADER_VIDEO = "http://www.heyoe.com/heyoe/assets/videos/header_video/";
//    public static String BASE_POST_VIDEO = "http://www.heyoe.com/heyoe/assets/videos/post_video/";
//    public static String BASE_APP = "http://www.heyoe.com/heyoe/assets/images/app.png";

    public static String BASE_URL = "http://52.58.141.85/index.php/api/";
//    public static String BASE_AVATAR = "http://52.58.141.85/assets/images/avatars/";
    public static String BASE_HEADER_PHOTO = "http://52.58.141.85/assets/images/header_photo/";
    public static String BASE_POST_PHOTO = "http://52.58.141.85/assets/images/post_photo/";
    public static String BASE_THUMBNAIL = "http://52.58.141.85/assets/images/thumb/";
    public static String BASE_HEADER_VIDEO = "http://52.58.141.85/assets/videos/header_video/";
    public static String BASE_POST_VIDEO = "http://52.58.141.85/assets/videos/post_video/";
    public static String BASE_APP = "http://52.29.108.123/assets/images/app.png";

//    public static String BASE_URL = "http://localhost/heyoe_chat/index.php/api/";
////    public static String BASE_AVATAR = "http://192.168.2.17/heyoe_chat/assets/images/avatars/";
//    public static String BASE_HEADER_PHOTO = "http://localhost/heyoe_chat/assets/images/header_photo/";
//    public static String BASE_POST_PHOTO = "http://localhost/heyoe_chat/assets/images/post_photo/";
//    public static String BASE_THUMBNAIL = "http://localhost/heyoe_chat/assets/images/thumb/";
//    public static String BASE_HEADER_VIDEO = "http://localhost/heyoe_chat/assets/videos/header_video/";
//    public static String BASE_POST_VIDEO = "http://localhost/heyoe_chat/assets/videos/post_video/";
//    public static String BASE_APP = "http://localhost/heyoe_chat/assets/images/app.png";


    public static String BASE_YOUTUB_PREFIX = "http://i1.ytimg.com/vi/";
    public static String BASE_YOUTUB_SURFIX = "/hqdefault.jpg";

    public static String SINGUP = BASE_URL + "sign_up";
    public static String SINGIN = BASE_URL + "sign_in";
    public static String SINGIN_SOCIAL = BASE_URL + "sign_in_social";
    public static String NEW_POST = BASE_URL + "new_post";
    public static String REPOST = BASE_URL + "repost";
    public static String GET_NON_FRIENDS = BASE_URL + "get_non_friends";
    public static String INVITE_FRIEND = BASE_URL + "invite_friend";
    public static String SEND_CHECKIN_REQUEST = BASE_URL + "send_checkin_chat_request";
    public static String ACCEPT_CHECKIN_REQUEST = BASE_URL + "accept_checkin_chat_request";
    public static String SET_OFFLINE = BASE_URL + "set_offline";
    public static String GET_ONLINE_STATUS = BASE_URL + "get_online_status";
    public static String GET_INVITED_FRIENDS = BASE_URL + "get_invited_users";
    public static String GET_REQUESTED_USERS = BASE_URL + "get_requested_users";
    public static String ACCEPT_FRIEND = BASE_URL + "accept_friend";
    public static String REJECT_FRIEND = BASE_URL + "reject_friend";
    public static String CANCEL_INVITE = BASE_URL + "cancel_invite";
    public static String GET_ALL_USERS = BASE_URL + "get_all_users";
    public static String GET_ALL_POSTS = BASE_URL + "get_all_posts";
    public static String GET_ALL_FAVORITE_POSTS = BASE_URL + "get_all_favorite_posts";
    public static String GET_PROFILE = BASE_URL + "get_profile";
    public static String DELETE_MY_POST = BASE_URL + "delete_my_post";
    public static String EDIT_MY_POST = BASE_URL + "edit_my_post";

    public static String GET_FRIEND_LIST = BASE_URL + "get_friend_list";
    public static String GET_MY_FRIEND_LIST = BASE_URL + "get_my_friend_list";
    public static String BLOCK_FRIEND = BASE_URL + "block_friend";
    public static String DELETE_FRIEND = BASE_URL + "delete_friend";
    public static String RECOVER_FRIEND = BASE_URL + "recover_friend";

    public static String GET_BLACK_FRIENDS = BASE_URL + "get_black_friends";
    public static String ADD_BLACK_FRIEND = BASE_URL + "add_black_friend";
    public static String REMOVE_FROM_BLACK_LIST = BASE_URL + "remove_black_friend";
//    public static String DELETE_BLACK_FRIEND = BASE_URL + "delete_black_friend";
//    public static String BLOCK_BLACK_FRIEND = BASE_URL + "block_black_friend";
//    public static String RECOVER_BLACK_FRIEND = BASE_URL + "recover_black_friend";
    public static String SET_BLACK_PASSWORD = BASE_URL + "set_black_password";

    public static String GET_ACTIVITY = BASE_URL + "get_activity";
    public static String LIKE_POST = BASE_URL + "like_post";
    public static String SET_FAVORITE = BASE_URL + "set_favorite";
    public static String COMMENT_POST = BASE_URL + "comment_post";
    public static String SHARED_POST = BASE_URL + "share_post";
    public static String VIEWED_POST = BASE_URL + "viewed_post";
    public static String EDIT_CONTACT = BASE_URL + "edit_contact";
    public static String EDIT_ABOUTME = BASE_URL + "edit_aboutme";
    public static String CHANGE_PASSWORD = BASE_URL + "change_password";
    public static String GET_MY_POSTS = BASE_URL + "get_my_posts";
    public static String GET_LIKED_USERS = BASE_URL + "get_liked_users";
    public static String GET_DISLIKED_USERS = BASE_URL + "get_disliked_users";
    public static String GET_FRIEND_STATUS = BASE_URL + "get_friend_state";
    public static String SET_HEADER_MEDIA = BASE_URL + "set_header_media";
    public static String CHANGE_AVATAR = BASE_URL + "change_avatar";
    public static String GET_CHECKIN_USERS = BASE_URL + "get_checkin_users";

    public static String ENTER_CHECKIN = BASE_URL + "enter_checkin";
    public static String EXIT_CHECKIN = BASE_URL + "exit_checkin";

    public static String SEND_MESSAGE = BASE_URL + "send_message";
    public static String CLEAR_CHAT_HISTORY = BASE_URL + "clear_chat_history";

    public static String SEND_EMAIL = BASE_URL + "send_email";

    public static String GET_LAYER_TOKEN = BASE_URL + "get_layer_token";
}
