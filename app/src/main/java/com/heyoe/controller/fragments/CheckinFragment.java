package com.heyoe.controller.fragments;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.CustomRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.heyoe.R;
import com.heyoe.controller.ChatActivity;
import com.heyoe.controller.HomeActivity;
import com.heyoe.controller.UserListActivity;
import com.heyoe.model.API;
import com.heyoe.model.Constant;
import com.heyoe.model.UserModel;
import com.heyoe.utilities.Utils;
import com.heyoe.utilities.image_downloader.UrlImageViewCallback;
import com.heyoe.utilities.image_downloader.UrlRectangleImageViewHelper;
import com.heyoe.widget.MyCircularImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class CheckinFragment extends Fragment {

    private ListView lvHome;
    private PullToRefreshListView mPullRefreshHomeListView;
    private CheckinUserAdapter checkinUserAdapter;
    private Activity mActivity;
    static boolean isLast;
    static int offset;
    private ArrayList<UserModel> arrUsers;

    String checkin;

    public CheckinFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_checkin_user, container, false);

        initVariables();
        initUI(view);
        onCheckinClick();
        return view;
    }
    private void initVariables() {
        mActivity = getActivity();
        isLast = false;
        offset = 0;
        arrUsers = new ArrayList<>();
        checkin = "";

    }
    private void initUI(View view) {
        ///create listview
        mPullRefreshHomeListView = (PullToRefreshListView) view.findViewById(R.id.lv_checkin_user);
        mPullRefreshHomeListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                if (!isLast) {
                    getCheckinUsers();
                }
                mPullRefreshHomeListView.onRefreshComplete();

            }
        });
        mPullRefreshHomeListView.setOnPullEventListener(new PullToRefreshBase.OnPullEventListener<ListView>() {
            @Override
            public void onPullEvent(PullToRefreshBase<ListView> refreshView, PullToRefreshBase.State state, PullToRefreshBase.Mode direction) {
                if (state == PullToRefreshBase.State.RELEASE_TO_REFRESH && direction == PullToRefreshBase.Mode.PULL_FROM_START) {
                    isLast = false;
                    offset = 0;
                    arrUsers.clear();
                    getCheckinUsers();
                }
            }
        });

        lvHome = mPullRefreshHomeListView.getRefreshableView();
        lvHome.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        checkinUserAdapter = new CheckinUserAdapter(arrUsers);
        lvHome.setAdapter(checkinUserAdapter);
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constant.REQUEST_PLACE_PICKER
                && resultCode == Activity.RESULT_OK) {

            // The user has selected a place. Extract the name and address.
            final Place place = PlacePicker.getPlace(data, mActivity);

            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            String attributions = PlacePicker.getAttributions(data);
            checkin = name.toString();
            UserListActivity.setTitle(checkin);
            enterCheckin();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    private void getCheckinUsers() {

        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.DEVICE_TYPE, Constant.ANDROID);
        params.put(Constant.DEVICE_TOKEN, Utils.getFromPreference(mActivity, Constant.DEVICE_TOKEN));
        params.put("user_id", Utils.getFromPreference(mActivity, Constant.USER_ID));
        params.put("checkin", checkin);
        params.put("offset", String.valueOf(offset));

        CustomRequest signinRequest = new CustomRequest(Request.Method.POST, API.GET_CHECKIN_USERS, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            if (status.equals("200")) {
                                JSONArray jsonArray = response.getJSONArray("data");
                                int userCount = jsonArray.length();
                                if (userCount == 0) {
                                    isLast = true;
                                }
                                offset ++;
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
                                    String qb_id = userObject.getString("qb_id");
                                    String onlineStatus = userObject.getString("online_status");
                                    String postCount = userObject.getString("post_count");
                                    String friend_status = userObject.getString("friend_status");

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
                                    userModel.setQb_id(qb_id);
                                    userModel.setFriendStatus(friend_status);

                                    if (onlineStatus.equals("on")) {
                                        userModel.setOnline(true);
                                    } else {
                                        userModel.setOnline(false);
                                    }


                                    arrUsers.add(userModel);
                                }

                                checkinUserAdapter.notifyDataSetChanged();

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
    private void sendFriendRequest(String user_id, final int position) {
        Utils.showProgress(mActivity);

        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.DEVICE_TYPE, Constant.ANDROID);
        params.put(Constant.DEVICE_TOKEN, Utils.getFromPreference(mActivity, Constant.DEVICE_TOKEN));
        params.put("my_id", Utils.getFromPreference(mActivity, Constant.USER_ID));
        params.put("user_id", user_id);

        CustomRequest signinRequest = new CustomRequest(Request.Method.POST, API.INVITE_FRIEND, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Utils.hideProgress();
                        try {
                            String status = response.getString("status");
                            if (status.equals("200")) {
                                arrUsers.get(position).setFriendStatus("waiting");
                                checkinUserAdapter.notifyDataSetChanged();
                                Utils.showOKDialog(mActivity, getResources().getString(R.string.invite_sucess));
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
    private void enterCheckin() {
        Utils.showProgress(mActivity);

        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.DEVICE_TYPE, Constant.ANDROID);
        params.put(Constant.DEVICE_TOKEN, Utils.getFromPreference(mActivity, Constant.DEVICE_TOKEN));
        params.put("user_id", Utils.getFromPreference(mActivity, Constant.USER_ID));
        params.put("checkin", checkin);

        CustomRequest signinRequest = new CustomRequest(Request.Method.POST, API.ENTER_CHECKIN, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Utils.hideProgress();
                        try {
                            String status = response.getString("status");
                            if (status.equals("200")) {
                                getCheckinUsers();
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
    private void exitCheckin() {
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.DEVICE_TYPE, Constant.ANDROID);
        params.put(Constant.DEVICE_TOKEN, Utils.getFromPreference(mActivity, Constant.DEVICE_TOKEN));
        params.put("user_id", Utils.getFromPreference(mActivity, Constant.USER_ID));
        params.put("checkin", checkin);

        CustomRequest signinRequest = new CustomRequest(Request.Method.POST, API.EXIT_CHECKIN, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Utils.hideProgress();
                        try {
                            String status = response.getString("status");
                            if (status.equals("200")) {

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

    public class CheckinUserAdapter extends BaseAdapter {

        LayoutInflater mlayoutInflater;
        ArrayList<UserModel> arrFriends;
        public CheckinUserAdapter (ArrayList<UserModel> arrFriends) {
            mlayoutInflater = LayoutInflater.from(mActivity);
            this.arrFriends = arrFriends;
        }
        @Override
        public int getCount() {
            return arrFriends.size();
        }

        @Override
        public Object getItem(int position) {
            return arrFriends.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = mlayoutInflater.inflate(R.layout.item_checkin_user, null);
            }
            MyCircularImageView myCircularImageView = (MyCircularImageView)view.findViewById(R.id.civ_imf_friend_avatar3);
            if (!arrFriends.get(position).getAvatar().equals("")) {
                UrlRectangleImageViewHelper.setUrlDrawable(myCircularImageView, API.BASE_AVATAR + arrFriends.get(position).getAvatar(), R.drawable.default_user, new UrlImageViewCallback() {
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
            }else {
                myCircularImageView.setImageDrawable(getResources().getDrawable(R.drawable.default_user));
            }
            myCircularImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    HomeActivity.navigateToProfile(arrFriends.get(position).getUser_id());
                }
            });
            TextView tvName = (TextView)view.findViewById(R.id.tv_imf_friend_name);
            tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    HomeActivity.navigateToProfile(arrFriends.get(position).getUser_id());
                }
            });
            TextView tvAboutMe = (TextView)view.findViewById(R.id.tv_imf_aboutme);
            ImageButton ibAdd = (ImageButton)view.findViewById(R.id.ib_imf_add);
            ImageButton ibChat = (ImageButton)view.findViewById(R.id.ib_imf_chat);
            tvName.setText(arrFriends.get(position).getFullname());
            tvAboutMe.setText(arrFriends.get(position).getAbout_you());
            ibAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (arrFriends.get(position).getFriendStatus().equals("none")){
                        sendFriendRequest(arrFriends.get(position).getUser_id(), position);
                    }

                }
            });

            if (arrFriends.get(position).getFriendStatus().equals("waiting")) {
                ibAdd.setImageDrawable(getResources().getDrawable(R.drawable.sandglass_small));
            } else if (arrFriends.get(position).getFriendStatus().equals("none")){
                ibAdd.setImageDrawable(getResources().getDrawable(R.drawable.ic_green_plus));
            } else {
                ibAdd.setVisibility(View.INVISIBLE);
            }
            ibChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mActivity, ChatActivity.class);
                    intent.putExtra("userModel", arrFriends.get(position));
                    startActivity(intent);
                }
            });
            return view;
        }
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        exitCheckin();
    }
}
