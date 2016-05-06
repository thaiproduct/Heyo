package com.heyoe.controller.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.NetworkError;
import com.android.volley.error.NoConnectionError;
import com.android.volley.error.ParseError;
import com.android.volley.error.ServerError;
import com.android.volley.error.TimeoutError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.CustomMultipartRequest;
import com.android.volley.request.CustomRequest;
import com.android.volley.toolbox.Volley;
import com.commonsware.cwac.richedit.RichEditText;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.heyoe.R;
import com.heyoe.controller.DetailPostActivity;
import com.heyoe.controller.HomeActivity;
import com.heyoe.model.API;
import com.heyoe.model.Constant;
import com.heyoe.model.PostModel;
import com.heyoe.model.UserModel;
import com.heyoe.utilities.BitmapUtility;
import com.heyoe.utilities.FileUtility;
import com.heyoe.utilities.UIUtility;
import com.heyoe.utilities.Utils;
import com.heyoe.utilities.VideoPlay;
import com.heyoe.utilities.camera.AlbumStorageDirFactory;
import com.heyoe.utilities.camera.BaseAlbumDirFactory;
import com.heyoe.utilities.camera.FroyoAlbumDirFactory;
import com.heyoe.utilities.image_downloader.UrlImageViewCallback;
import com.heyoe.utilities.image_downloader.UrlRectangleImageViewHelper;
import com.heyoe.widget.MyCircularImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewPostFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener{

    private static final int take_photo_from_gallery = 1;
    private static final int take_photo_from_camera = 2;
    private static final int take_video_from_gallery = 3;
    private static final int take_video_from_camera = 4;

    private static final String JPEG_FILE_PREFIX = "Heyoe_Compose_photo_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    private static final String VIDEO_FILE_PREFIX = "Heyoe_Compose_video_";
    private static final String VIDEO_FILE_SUFFIX = ".mp4";

    private ImageButton ibTag, ibPhoto, ibVideo, ibCheckin;
    private Button btnPost;
    private MyCircularImageView myCircularImageView;
    private TextView tvFullname;
    private static RichEditText richEditor;
    private ImageView imageView;
//    private VideoView videoView;
    private ImageButton ibPlay;
    private EditText etYotubeUrl;

    private Activity mActivity;
    private String photoPath, videoPath, thumbPath, youtubePath;
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;

    private ArrayList<UserModel> arrFriends;

    boolean isEdit;
    PostModel postModel;

    public NewPostFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_new_post, container, false);
        initVariable();
        initUI(view);
        getFriends();
        return view;
    }

    private void initVariable() {
        mActivity = getActivity();
        initMediaPath();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }
        arrFriends = new ArrayList<>();
        isEdit = getArguments().getBoolean("isEdit");
        if (isEdit) {
            postModel = (PostModel)getArguments().getSerializable("post");
        }
    }
    private void initMediaPath() {
        photoPath = "";
        youtubePath = "";
        videoPath = "";
        thumbPath = "";
        mediaType = "";
    }
    private void initUI(View view) {
        myCircularImageView = (MyCircularImageView)view.findViewById(R.id.civ_compose_avatar);
        String avatar = Utils.getFromPreference(mActivity, Constant.AVATAR);
        if (!avatar.equals("")) {
            UrlRectangleImageViewHelper.setUrlDrawable(myCircularImageView, API.BASE_AVATAR + avatar, R.drawable.default_user, new UrlImageViewCallback() {
                @Override
                public void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url, boolean loadedFromCache) {
                    if (!loadedFromCache) {
                        ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
                        scale.setDuration(10);
                        scale.setInterpolator(new OvershootInterpolator());
                        imageView.startAnimation(scale);
                    }
                }
            });
        } else {
            myCircularImageView.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.default_user));
        }
        tvFullname = (TextView)view.findViewById(R.id.tv_compose_fullname);
        tvFullname.setText(Utils.getFromPreference(mActivity, Constant.FULLNAME));

        etYotubeUrl = (EditText)view.findViewById(R.id.et_compose_youtube_url);
        etYotubeUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textToPaste = "";

                ClipboardManager clipboard = (ClipboardManager)mActivity.getSystemService(Context.CLIPBOARD_SERVICE);

                if (clipboard.hasPrimaryClip()) {
                    ClipData clip = clipboard.getPrimaryClip();

                    // if you need text data only, use:
                    if (clip.getDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN))
                        // WARNING: The item could cantain URI that points to the text data.
                        // In this case the getText() returns null and this code fails!
                        textToPaste = clip.getItemAt(0).getText().toString();

                    // or you may coerce the data to the text representation:
                    textToPaste = clip.getItemAt(0).coerceToText(mActivity).toString();
                }
                if (!TextUtils.isEmpty(textToPaste)) {
                    etYotubeUrl.setText(textToPaste);
                    initMediaPath();
                    youtubePath = etYotubeUrl.getText().toString();
                    String imageUrl = API.BASE_YOUTUB_PREFIX + FileUtility.getFilenameFromPath(youtubePath) + API.BASE_YOUTUB_SURFIX;
                    ibPlay.setVisibility(View.VISIBLE);
                    if (!youtubePath.equals("")) {
                        UrlRectangleImageViewHelper.setUrlDrawable(imageView, imageUrl, R.drawable.default_tour, new UrlImageViewCallback() {
                            @Override
                            public void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url, boolean loadedFromCache) {
                                if (!loadedFromCache) {

                                    ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
                                    scale.setDuration(500);
                                    scale.setInterpolator(new OvershootInterpolator());
                                    imageView.startAnimation(scale);
                                }
                            }
                        });
                    }
                }
                else {
                    etYotubeUrl.setVisibility(View.GONE);
                }
            }
        });
        ibTag = (ImageButton)view.findViewById(R.id.ib_compose_tag);
        ibCheckin = (ImageButton)view.findViewById(R.id.ib_compose_checkin);
        ibPhoto = (ImageButton)view.findViewById(R.id.ib_compose_photo);
        ibVideo = (ImageButton)view.findViewById(R.id.ib_compose_video);
        ibPlay =  (ImageButton)view.findViewById(R.id.ib_compose_play);

        btnPost = (Button)view.findViewById(R.id.btn_compose_post);


        ibTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTagDialog();

            }
        });
        ibCheckin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckinClick();
            }
        });
        ibPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showPictureChooseDialog();
            }
        });
        ibVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showVideoChooseDialog();
            }
        });
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEdit) {
                    editMedia();
                } else {
                    if (checkValue()) {
                        postMedia();
                    }
                }
            }
        });

        richEditor=(RichEditText)view.findViewById(R.id.editor);
        richEditor.enableActionModes(true);

        imageView = (ImageView)view.findViewById(R.id.iv_compose);
        UIUtility.setImageViewSize(imageView, UIUtility.getScreenWidth(mActivity), UIUtility.getScreenWidth(mActivity));
        setData();
    }
    private void setData() {
        if (isEdit && postModel != null) {
            richEditor.setText(Html.fromHtml(postModel.getDescription()));
            String imageUrl = "";
            if (postModel.getMedia_type().equals("youtube")) {
                imageUrl = API.BASE_YOUTUB_PREFIX + FileUtility.getFilenameFromPath(postModel.getMedia_url()) + API.BASE_YOUTUB_SURFIX;
            } else if (postModel.getMedia_type().endsWith("post_photo")) {
                imageUrl = API.BASE_POST_PHOTO + postModel.getMedia_url();
            } else if (postModel.getMedia_type().endsWith("post_video")){
                ibPlay.setVisibility(View.VISIBLE);
                imageUrl = API.BASE_THUMBNAIL + postModel.getMedia_url().substring(0, postModel.getMedia_url().length() - 3) + "jpg";
            }
            if (!postModel.getMedia_url().equals("")) {
                UrlRectangleImageViewHelper.setUrlDrawable(imageView, imageUrl, R.drawable.default_tour, new UrlImageViewCallback() {
                    @Override
                    public void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url, boolean loadedFromCache) {
                        if (!loadedFromCache) {

                            ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
                            scale.setDuration(500);
                            scale.setInterpolator(new OvershootInterpolator());
                            imageView.startAnimation(scale);
                        }
                    }
                });
            }
        }
    }
    private void getFriends() {
        Utils.showProgress(mActivity);

        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.DEVICE_TYPE, Constant.ANDROID);
        params.put(Constant.DEVICE_TOKEN, Utils.getFromPreference(mActivity, Constant.DEVICE_TOKEN));
        params.put("my_id", Utils.getFromPreference(mActivity, Constant.USER_ID));


        CustomRequest signinRequest = new CustomRequest(Request.Method.POST, API.GET_FRIEND_LIST, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Utils.hideProgress();
                        try {
                            String status = response.getString("status");
                            if (status.equals("200")) {
                                JSONArray jsonArray = response.getJSONArray("data");
                                int userCount = jsonArray.length();
                                for (int i = 0; i < userCount; i ++)  {

                                    JSONObject userObject = jsonArray.getJSONObject(i);

                                    String user_id = userObject.getString("user_id");
                                    String fullname = userObject.getString("fullname");
                                    String email = userObject.getString("email");
                                    String city = userObject.getString("city");
                                    String country = userObject.getString("country");
                                    String birthday = userObject.getString("birthday");
                                    String gender = userObject.getString("gender");
                                    String celebrity = userObject.getString("celebrity");
                                    String about_you = userObject.getString("about_you");
                                    String friend_count = userObject.getString("friend_count");
                                    String avatar = userObject.getString("avatar");
                                    String header_photo_url = userObject.getString("header_photo");
                                    String header_video_url = userObject.getString("header_video");


                                    UserModel userModel = new UserModel();

                                    userModel.setUser_id(user_id);
                                    userModel.setFullname(fullname);
                                    userModel.setEmail(email);
                                    userModel.setCity(city);
                                    userModel.setCountry(country);
                                    userModel.setBirthday(birthday);
                                    userModel.setGender(gender);
                                    userModel.setCelebrity(celebrity);
                                    userModel.setAbout_you(about_you);
                                    userModel.setFriend_count(friend_count);
                                    userModel.setAvatar(avatar);
                                    userModel.setHeader_photo(header_photo_url);
                                    userModel.setHeader_video(header_video_url);

                                    String friend_status = userObject.getString("status");
                                    if (friend_status.equals("active")) {
                                        arrFriends.add(userModel);
                                    } else {
                                    }

                                }



                            } else  if (status.equals("400")) {
                                Utils.showOKDialog(mActivity, getResources().getString(R.string.access_denied));
                            } else if (status.equals("402")) {
//                                Utils.showOKDialog(mActivity, getResources().getString(R.string.incorrect_password));
                            }
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Utils.hideProgress();
                        Toast.makeText(mActivity, error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
        requestQueue.add(signinRequest);
    }

    String mediaType;
    String description;
    private boolean checkValue() {
//        description = richEditor.getText().toString();
//        description = TextUtils.htmlEncode(richEditor.getText().toString());
        description = Html.toHtml(richEditor.getText());
        if (photoPath.length() > 0) {
            mediaType = "post_photo";
            return true;
        }
        if (videoPath.length() > 0) {
            if (thumbPath.length() == 0) {
                Utils.showToast(mActivity, "Cannot get thumbnail");
                return false;
            }
            mediaType = "post_video";
            return true;
        }
        if (youtubePath.length() > 0) {
            mediaType = "youtube";
            return true;
        }
        Utils.showOKDialog(mActivity, getResources().getString(R.string.take_more_media));
        return false;
    }
    private void postMedia() {
        Utils.showProgress(mActivity);
        CustomMultipartRequest customMultipartRequest = new CustomMultipartRequest(API.NEW_POST,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Utils.hideProgress();
                        try {
                            String status = response.getString("status");
                            if (status.equals("200")) {
                                Utils.showToast(mActivity, getResources().getString(R.string.post_success));
                                UIUtility.hideSoftKeyboard(mActivity);

                                if (mediaType.equals("post_video")) {
                                    FileUtility.deleteFile(videoPath);
                                    FileUtility.deleteFile(thumbPath);

                                } else if (mediaType.equals("post_photo")) {
                                    FileUtility.deleteFile(photoPath);
                                }

                                HomeActivity.navigateTo(0);
                            } else  if (status.equals("400")) {
                                Utils.showOKDialog(mActivity, getResources().getString(R.string.access_denied));
                            }else  if (status.equals("401")) {
                                Utils.showOKDialog(mActivity, getResources().getString(R.string.post_failed));
                            }

                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Utils.hideProgress();
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(mActivity, "TimeoutError", Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            //TODO
                            Toast.makeText(mActivity, "AuthFailureError", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            //TODO
                            Toast.makeText(mActivity, "ServerError", Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            //TODO
                            Toast.makeText(mActivity, "NetworkError", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            //TODO
                            Toast.makeText(mActivity, "ParseError", Toast.LENGTH_LONG).show();
                        } else {
                            //TODO
                            Toast.makeText(mActivity, "UnknownError", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        customMultipartRequest
                .addStringPart(Constant.DEVICE_TYPE, Constant.ANDROID)
                .addStringPart(Constant.DEVICE_TOKEN, Utils.getFromPreference(mActivity, Constant.DEVICE_TOKEN))
                .addStringPart("my_id", Utils.getFromPreference(mActivity, Constant.USER_ID))
                .addStringPart("media_type", mediaType)
                .addStringPart("description", description);
        customMultipartRequest.addStringPart("width", String.valueOf(imageWidth));
        customMultipartRequest.addStringPart("height", String.valueOf(imageHeight));


        if (mediaType.equals("post_video")) {
            customMultipartRequest.addVideoPart("post_video", videoPath);
            customMultipartRequest.addImagePart("thumb", thumbPath);

        } else if (mediaType.equals("post_photo")) {
            customMultipartRequest.addImagePart("post_photo", photoPath);
            customMultipartRequest.addImagePart("thumb", thumbPath);

        } else if (mediaType.equals("youtube")) {
            customMultipartRequest.addStringPart("media", youtubePath);
            customMultipartRequest.addStringPart("width", "0");
            customMultipartRequest.addStringPart("height", "0");
        }


        RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
        requestQueue.add(customMultipartRequest);

    }
    private void editMedia() {
        description = Html.toHtml(richEditor.getText());
        mediaType = "";
        if (photoPath.length() > 0) {
            mediaType = "post_photo";
        } else if (videoPath.length() > 0) {
            mediaType = "post_video";
        } else if (youtubePath.length() > 0) {
            mediaType = "youtube";
        }
        Utils.showProgress(mActivity);
        CustomMultipartRequest customMultipartRequest = new CustomMultipartRequest(API.EDIT_MY_POST,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Utils.hideProgress();
                        try {
                            String status = response.getString("status");
                            if (status.equals("200")) {
                                Utils.showToast(mActivity, getResources().getString(R.string.post_success));
                                UIUtility.hideSoftKeyboard(mActivity);

                                DetailPostActivity.postModel.setDescription(description);
                                if (mediaType.equals("")) {

                                } else if (mediaType.equals("post_video")) {
                                    FileUtility.deleteFile(videoPath);
                                    FileUtility.deleteFile(thumbPath);
                                    DetailPostActivity.postModel.setMedia_type(mediaType);
                                    DetailPostActivity.postModel.setMedia_url(response.getJSONObject("data").getString("media_url"));

                                } else if (mediaType.equals("post_photo")) {
                                    FileUtility.deleteFile(photoPath);
                                    FileUtility.deleteFile(thumbPath);
                                    DetailPostActivity.postModel.setMedia_type(mediaType);
                                    DetailPostActivity.postModel.setMedia_url(response.getJSONObject("data").getString("media_url"));
                                } else {
                                    DetailPostActivity.postModel.setMedia_type(mediaType);
                                    DetailPostActivity.postModel.setMedia_url(youtubePath);
                                }

                                DetailPostActivity.pushFragment(0);
                            } else  if (status.equals("400")) {
                                Utils.showOKDialog(mActivity, getResources().getString(R.string.access_denied));
                            }else  if (status.equals("401")) {
                                Utils.showOKDialog(mActivity, getResources().getString(R.string.post_failed));
                            }

                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Utils.hideProgress();
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(mActivity, "TimeoutError", Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            //TODO
                            Toast.makeText(mActivity, "AuthFailureError", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            //TODO
                            Toast.makeText(mActivity, "ServerError", Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            //TODO
                            Toast.makeText(mActivity, "NetworkError", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            //TODO
                            Toast.makeText(mActivity, "ParseError", Toast.LENGTH_LONG).show();
                        } else {
                            //TODO
                            Toast.makeText(mActivity, "UnknownError", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        customMultipartRequest
                .addStringPart(Constant.DEVICE_TYPE, Constant.ANDROID)
                .addStringPart(Constant.DEVICE_TOKEN, Utils.getFromPreference(mActivity, Constant.DEVICE_TOKEN))
                .addStringPart("my_id", Utils.getFromPreference(mActivity, Constant.USER_ID))
                .addStringPart("post_id", postModel.getPost_id())
                .addStringPart("description", description);


        customMultipartRequest.addStringPart("media_changed", "yes");
        customMultipartRequest.addStringPart("media_type", mediaType);
        if (mediaType.equals("post_video")) {
            customMultipartRequest.addVideoPart("post_video", videoPath);
            customMultipartRequest.addImagePart("thumb", thumbPath);
            customMultipartRequest.addStringPart("width", String.valueOf(imageWidth));
            customMultipartRequest.addStringPart("height", String.valueOf(imageHeight));
            customMultipartRequest.addStringPart("media_type", mediaType);

        } else if (mediaType.equals("post_photo")) {
            customMultipartRequest.addImagePart("post_photo", photoPath);
            customMultipartRequest.addImagePart("thumb", thumbPath);
            customMultipartRequest.addStringPart("width", String.valueOf(imageWidth));
            customMultipartRequest.addStringPart("height", String.valueOf(imageHeight));

        } else if (mediaType.equals("youtube")) {
            customMultipartRequest.addStringPart("media", youtubePath);
            customMultipartRequest.addStringPart("width", "0");
            customMultipartRequest.addStringPart("height", "0");
        } else {
            customMultipartRequest.addStringPart("media_changed", "no");
        }


        RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
        requestQueue.add(customMultipartRequest);

    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

//        UIUtility.hideSoftKeyboard(mActivity);
    }

    public static void inputTag(String strTag) {
        String str = "<b>" + strTag + "</b>";
        richEditor.append(Html.fromHtml(str));

    }

    public void showTagDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.dlg_tag_selection, null);
//        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView)dialogView.findViewById(R.id.actv_tag_selection);
//        ListView listView = (ListView)dialogView.findViewById(R.id.lv_tag_selection);
        final ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < arrFriends.size(); i ++) {
            String str = "@" +  arrFriends.get(i).getFullname();
            arrayList.add(str);
        }
        ArrayAdapter<String> searchAgentAdapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_list_item_1,arrayList);
//        TagSelectionAutoCompleteAdapter searchAgentAdapter = new TagSelectionAutoCompleteAdapter(mActivity, R.layout.item_tag_selection, arrayList);
//        listView.setAdapter(searchAgentAdapter);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                inputTag(arrayList.get(position));
//
//            }
//        });
        builder.setCancelable(true);
        builder.setView(dialogView);
        builder.setAdapter(searchAgentAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                inputTag(arrayList.get(which));
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

//    get google place
    public void onCheckinClick() {
        // Construct an intent for the place picker
        try {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(mActivity);
            // Start the intent by requesting a result,
            // identified by a request code.
            startActivityForResult(intent, Constant.REQUEST_PLACE_PICKER);

        } catch (GooglePlayServicesRepairableException e) {
            GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), getActivity(), 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            Toast.makeText(getActivity(), "Google Play Services is not available.",
                    Toast.LENGTH_LONG)
                    .show();
        }
    }


    // A place has been received; use requestCode to track the request.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Constant.REQUEST_PLACE_PICKER
                && resultCode == Activity.RESULT_OK) {

            // The user has selected a place. Extract the name and address.
            final Place place = PlacePicker.getPlace(data, mActivity);

            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            String attributions = PlacePicker.getAttributions(data);

            String str = "<font color='#03a6a8'>"  + "<b>" + name + "</b></font>";
            richEditor.append(Html.fromHtml(str));
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

        switch (requestCode) {
            case take_photo_from_gallery:
                if (resultCode == Activity.RESULT_OK) {

                    etYotubeUrl.setVisibility(View.GONE);
                    ibPlay.setVisibility(View.GONE);

                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = mActivity.getContentResolver().query(
                            selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    initMediaPath();
                    photoPath = cursor.getString(columnIndex);
                    cursor.close();

                    Bitmap bitmap = BitmapUtility.adjustBitmap(photoPath);

                    imageView.setImageBitmap(bitmap);

                    imageWidth = bitmap.getWidth();
                    imageHeight = bitmap.getHeight();

                    photoPath = BitmapUtility.saveBitmap(bitmap, Constant.MEDIA_PATH + "heyoe", FileUtility.getFilenameFromPath(photoPath));

                    //crop thumbnail
                    Bitmap cropBitmap = BitmapUtility.cropBitmapCenter(bitmap);
//                    Bitmap cropBitmap = BitmapUtility.cropBitmapAnySize(bitmap, bitmap.getWidth(), bitmap.getWidth());
                    // save croped thumbnail
                    thumbPath = BitmapUtility.saveBitmap(cropBitmap, Constant.MEDIA_PATH, "heyoe_thumb");

                }
                break;
            case take_photo_from_camera: {
                if (resultCode == Activity.RESULT_OK) {

                    etYotubeUrl.setVisibility(View.GONE);
                    ibPlay.setVisibility(View.GONE);
                    setPic();
                }
                break;
            }
            case take_video_from_gallery:
                if (resultCode == Activity.RESULT_OK) {

                    etYotubeUrl.setVisibility(View.GONE);

                    Uri selectedVideoUri = data.getData();

                    videoPath = getVideoPath(selectedVideoUri);

                    if (videoPath != null) {
                        Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Video.Thumbnails.MINI_KIND);

                        ibPlay.setVisibility(View.VISIBLE);
                        //crop thumbnail
                        Bitmap cropBitmap = BitmapUtility.cropBitmapCenter(thumbnail);
//                        Bitmap cropBitmap = BitmapUtility.cropBitmapAnySize(thumbnail, thumbnail.getWidth(), thumbnail.getWidth());
                        // save croped thumbnail
                        thumbPath = BitmapUtility.saveBitmap(cropBitmap, Constant.MEDIA_PATH, "heyoe_thumb");

                        //adjust and resave thumbnail
                        Bitmap bitmap = BitmapUtility.adjustBitmap(thumbPath);

                        imageView.setImageBitmap(bitmap);

                        imageWidth = bitmap.getWidth();
                        imageHeight = bitmap.getHeight();

                        thumbPath = BitmapUtility.saveBitmap(bitmap, Constant.MEDIA_PATH + "heyoe", FileUtility.getFilenameFromPath(thumbPath));


                    }

                }
                break;
            case take_video_from_camera:
                if (resultCode == Activity.RESULT_OK) {

                    etYotubeUrl.setVisibility(View.GONE);

                    if (videoPath.length() > 0) {
                        Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Video.Thumbnails.MINI_KIND);

                        ibPlay.setVisibility(View.VISIBLE);
                        //crop thumbnail
                        Bitmap cropBitmap = BitmapUtility.cropBitmapCenter(thumbnail);
//                        Bitmap cropBitmap = BitmapUtility.cropBitmapAnySize(thumbnail, thumbnail.getWidth(), thumbnail.getWidth());
                        // save croped thumbnail
                        thumbPath = BitmapUtility.saveBitmap(cropBitmap, Constant.MEDIA_PATH, "heyoe_thumb");

                        //adjust and resave thumbnail
                        Bitmap bitmap = BitmapUtility.adjustBitmap(thumbPath);

                        imageView.setImageBitmap(bitmap);

                        imageWidth = bitmap.getWidth();
                        imageHeight = bitmap.getHeight();

//                        FileUtility.deleteFile(thumbPath);
                        thumbPath = BitmapUtility.saveBitmap(bitmap, Constant.MEDIA_PATH + "heyoe", FileUtility.getFilenameFromPath(thumbPath));


                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // User cancelled the video capture
                    Toast.makeText(mActivity, "User cancelled the video capture.",Toast.LENGTH_LONG).show();
                } else {
                    // Video capture failed, advise user
                    Toast.makeText(mActivity, "Video capture failed.",Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    public String getVideoPath(Uri uri) {

        String path = "";
        try {
            Cursor cursor = mActivity.getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            String document_id = cursor.getString(0);
            document_id = document_id.substring(document_id.lastIndexOf(":")+1);
            cursor.close();

            cursor = mActivity.getContentResolver().query(android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    null, MediaStore.Video.Media._ID + " = ? ", new String[]{document_id}, null);
            cursor.moveToFirst();
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return path;
    }


    //    choose video
    private void showVideoChooseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(Constant.INDECATOR);
        builder.setMessage(getResources().getString(R.string.choose_video));
        builder.setCancelable(true);
        builder.setPositiveButton("Camera",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        etYotubeUrl.setVisibility(View.GONE);
                        captureVideoFromCamera();
                        dialog.cancel();
                    }
                });
        builder.setNegativeButton("Gallery",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        etYotubeUrl.setVisibility(View.GONE);
                        takeVideoFromGallery();
                        dialog.cancel();
                    }
                });
        builder.setNeutralButton("Youtube", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                takeYoutubeVideoUrl();
                dialog.cancel();

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void takeYoutubeVideoUrl() {
        etYotubeUrl.setVisibility(View.VISIBLE);

        Intent videoClient = new Intent(Intent.ACTION_VIEW);
//        videoClient.setData(Uri.parse("http://m.youtube.com/watch?v="+videoId));
        videoClient.setData(Uri.parse("http://m.youtube.com/"));
        startActivityForResult(videoClient, 1234);
    }

    private void takeVideoFromGallery()
    {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.putExtra("return-data", true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), take_video_from_gallery);
    }





    private Uri fileUri;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private void captureVideoFromCamera() {
// create new Intentwith with Standard Intent action that can be
        // sent to have the camera application capture an video and return it.
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        // create a file to save the video
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
        initMediaPath();
        videoPath = fileUri.getPath();
        // set the image file name
        intent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);

        // set the video image quality to high
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

        // set max time limit
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 20);
//        or
//        intent.putExtra("android.intent.extra.durationLimit", 30000);

        // start the Video Capture Intent
        startActivityForResult(intent, take_video_from_camera);
    }
    /** Create a file Uri for saving an image or video */
    private Uri getOutputMediaFileUri(int type){

        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private File getOutputMediaFile(int type){

        // Check that the SDCard is mounted
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "HeyoeVideo");


        // Create the storage directory(MyCameraVideo) if it does not exist
        if (! mediaStorageDir.exists()){

            if (! mediaStorageDir.mkdirs()){


                Toast.makeText(mActivity, "Failed to create directory HeyoeVideo.",
                        Toast.LENGTH_LONG).show();

                Log.d("MyCameraVideo", "Failed to create directory HeyoeVideo.");
                return null;
            }
        }


        // Create a media file name

        // For unique file name appending current timeStamp with file name
        java.util.Date date= new java.util.Date();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(date.getTime());

        File mediaFile;

        if(type == MEDIA_TYPE_VIDEO) {

            // For unique video file name appending current timeStamp with file name
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    VIDEO_FILE_PREFIX + timeStamp + VIDEO_FILE_SUFFIX);

        } else {
            return null;
        }

        return mediaFile;
    }







    ///photo choose dialog
    public void showPictureChooseDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(Constant.INDECATOR);
        builder.setMessage(getResources().getString(R.string.choose_avatar));
        builder.setCancelable(true);
        builder.setPositiveButton("Camera",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dispatchTakePictureIntent();
                        dialog.cancel();
                    }
                });
        builder.setNegativeButton("Gallery",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        takePictureFromGallery();
                        dialog.cancel();
                    }
                });
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.cancel();

            }
        });
//        dialog.setCancelable(true);
//        dialog.setCanceledOnTouchOutside(false);
        AlertDialog alert = builder.create();
        alert.show();
    }
    //////////////////take a picture from gallery
    private void takePictureFromGallery()
    {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, take_photo_from_gallery);

//        Intent intent = new Intent(mActivity, TakeMediaActivity.class);
//        intent.putExtra("mediaType", 0);
//        startActivity(intent);
    }
    /////////////capture photo
    public void dispatchTakePictureIntent() {
//        Intent intent = new Intent(mActivity, TakeMediaActivity.class);
//        intent.putExtra("mediaType", 1);
//        startActivity(intent);

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = null;
        try {
            f = setUpPhotoFile();
            initMediaPath();
            photoPath = f.getAbsolutePath();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        } catch (IOException e) {
            e.printStackTrace();
            f = null;
            photoPath = "";
        }
        startActivityForResult(takePictureIntent, take_photo_from_camera);
    }
    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        initMediaPath();
        photoPath = f.getAbsolutePath();
        return f;
    }
    private File createImageFile() throws IOException {

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }
    private File getAlbumDir() {

        File storageDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir("AllyTours");
            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }
        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }
        return storageDir;
    }



    private void setPic() {
        if (photoPath == null) {
            return;
        }

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
        int targetW = imageView.getWidth();
        int targetH = imageView.getWidth();

		/* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) && (targetH > 0)) {
            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        }

		/* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapUtility.adjustBitmap(photoPath);

        imageView.setImageBitmap(bitmap);

        imageWidth = bitmap.getWidth();
        imageHeight = bitmap.getHeight();

        photoPath = BitmapUtility.saveBitmap(bitmap, Constant.MEDIA_PATH + "heyoe", FileUtility.getFilenameFromPath(photoPath));

        //crop thumbnail
        Bitmap cropBitmap = BitmapUtility.cropBitmapCenter(bitmap);
//        Bitmap cropBitmap = BitmapUtility.cropBitmapAnySize(bitmap, bitmap.getWidth(), bitmap.getWidth());
        // save croped thumbnail
        thumbPath = BitmapUtility.saveBitmap(cropBitmap, Constant.MEDIA_PATH, "heyoe_thumb");
    }
    int imageWidth = 0 , imageHeight = 0;


}
