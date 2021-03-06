package com.heyoe_chat.controller.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.heyoe_chat.R;
import com.heyoe_chat.controller.DetailPostActivity;
import com.heyoe_chat.controller.HomeActivity;
import com.heyoe_chat.controller.MediaPlayActivity;
import com.heyoe_chat.controller.UserListActivity;
import com.heyoe_chat.controller.fragments.MainFragment;
import com.heyoe_chat.model.API;
import com.heyoe_chat.model.CommentModel;
import com.heyoe_chat.model.PostModel;
import com.heyoe_chat.utilities.FileUtility;
import com.heyoe_chat.utilities.StringUtility;
import com.heyoe_chat.utilities.TimeUtility;
import com.heyoe_chat.utilities.UIUtility;
import com.heyoe_chat.utilities.Utils;
import com.heyoe_chat.utilities.image_downloader.UrlImageViewCallback;
import com.heyoe_chat.utilities.image_downloader.UrlRectangleImageViewHelper;
import com.heyoe_chat.widget.MyCircularImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.kaede.tagview.Constants;

/**
 * Created by dell17 on 4/18/2016.
 */
public class PostAdapter extends BaseAdapter {
    LayoutInflater layoutInflater;
    ArrayList<PostModel> arrayList;
    Activity activity ;
    public PostAdapter (Activity activity, ArrayList<PostModel> arrayList) {
        this.activity = activity;
        this.arrayList = arrayList;
        layoutInflater = activity.getLayoutInflater();
    }
    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final PostModel postModel = arrayList.get(position);
        View view = convertView;
        if (postModel.getFriendStatus().equals("active")) {
            view = layoutInflater.inflate(R.layout.item_post_for_friend, null);

            ImageButton ibFavorite = (ImageButton)view.findViewById(R.id.iv_ipff_favorite);
            ibFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (postModel.getFavorite().equals("favorite")) {
                        MainFragment.setFavorite(position, false);
                    } else {
                        MainFragment.setFavorite(position, true);
                    }
                }
            });
            ImageView ivLikeCount = (ImageView)view.findViewById(R.id.iv_ipff_like_count);
            ImageView ivDislikeCount = (ImageView)view.findViewById(R.id.iv_ipff_dislike_count);
            ImageView ivComments = (ImageView)view.findViewById(R.id.iv_ipff_comments);

            ivLikeCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, UserListActivity.class);
                    intent.putExtra("type", "like");
                    intent.putExtra("post", postModel);
                    activity.startActivityForResult(intent, 107);
                }
            });

            ivDislikeCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, UserListActivity.class);
                    intent.putExtra("type", "dislike");
                    intent.putExtra("post", postModel);
                    activity.startActivityForResult(intent, 107);
                }
            });
            ivComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, DetailPostActivity.class);
                    intent.putExtra("post", postModel);
                    activity.startActivityForResult(intent, 101);
                }
            });
            TextView tvLikeCount = (TextView)view.findViewById(R.id.tv_ipff_like_count);
            TextView tvDislike = (TextView)view.findViewById(R.id.tv_ipff_dislike_count);

            TextView[] tvNames = new TextView[3];
            tvNames[0] = (TextView)view.findViewById(R.id.tv_ipff_name1);
            tvNames[1] = (TextView)view.findViewById(R.id.tv_ipff_name2);
            tvNames[2] = (TextView)view.findViewById(R.id.tv_ipff_name3);

            TextView[]  tvComments = new TextView[3];
            tvComments[0] = (TextView)view.findViewById(R.id.tv_ipff_comment1);
            tvComments[1] = (TextView)view.findViewById(R.id.tv_ipff_comment2);
            tvComments[2] = (TextView)view.findViewById(R.id.tv_ipff_comment3);

            LinearLayout[] llComments = new LinearLayout[3];
            llComments[0] = (LinearLayout)view.findViewById(R.id.ll_ipff_comment1);
            llComments[1] = (LinearLayout)view.findViewById(R.id.ll_ipff_comment2);
            llComments[2] = (LinearLayout)view.findViewById(R.id.ll_ipff_comment3);

            TextView tvViewedCount = (TextView)view.findViewById(R.id.tv_ipff_viewed_count);
            TextView tvSharedCount = (TextView)view.findViewById(R.id.tv_ipff_shared_count);

            if (postModel.getFavorite().equals("favorite")) {
                ibFavorite.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_favorite_star_white));
            } else {
                ibFavorite.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_favorite_star_empty));
            }
            if (postModel.getLike().equals("like")) {
                ivLikeCount.setImageDrawable(activity.getResources().getDrawable(R.drawable.green_like_count));
                ivDislikeCount.setImageDrawable(activity.getResources().getDrawable(R.drawable.dislike_count_empty));
            } else if (postModel.getLike().equals("dislike")) {
                ivLikeCount.setImageDrawable(activity.getResources().getDrawable(R.drawable.like_count_empty));
                ivDislikeCount.setImageDrawable(activity.getResources().getDrawable(R.drawable.green_dislike_count));
            } else {
                ivLikeCount.setImageDrawable(activity.getResources().getDrawable(R.drawable.like_count_empty));
                ivDislikeCount.setImageDrawable(activity.getResources().getDrawable(R.drawable.dislike_count_empty));
            }

            if (postModel.getCommented().equals("yes")) {
                ivComments.setImageDrawable(activity.getResources().getDrawable(R.drawable.green_comment_count));
            } else {
                ivComments.setImageDrawable(activity.getResources().getDrawable(R.drawable.comment_count_empty));
            }

            tvLikeCount.setText(postModel.getLike_count());
            tvDislike.setText(postModel.getDislike_count());
            tvViewedCount.setText(postModel.getViewed_count());
            tvSharedCount.setText(postModel.getShared_count());


            int commentCount = postModel.getArrComments().size() > 3 ? 3 : postModel.getArrComments().size();
            for (int i = 0; i < commentCount; i ++) {
                CommentModel commentModel = postModel.getArrComments().get(i);
                tvNames[i].setText(commentModel.getFullname());
                String str1 = commentModel.getComment();
                CharSequence str2 = StringUtility.trimTrailingWhitespace(Html.fromHtml(str1));
                tvComments[i].setText(str2);
                llComments[i].setVisibility(View.VISIBLE);
            }
            LinearLayout llComment = (LinearLayout)view.findViewById(R.id.ll_comment);
            if (commentCount == 0) {
                llComment.setVisibility(View.GONE);
            } else {
                llComment.setVisibility(View.VISIBLE);
            }
            TextView tvCheckoutAllComments = (TextView)view.findViewById(R.id.tv_ipff_checkout_all_comments);
            tvCheckoutAllComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, DetailPostActivity.class);
                    intent.putExtra("post", postModel);
                    intent.putExtra("from", 0);
                    activity.startActivityForResult(intent, 101);
                }
            });



            ////
        } else {
            view = layoutInflater.inflate(R.layout.item_post_for_nonfriend, null);

            MyCircularImageView[] civFriends = new MyCircularImageView[3];
            civFriends[0] = (MyCircularImageView)view.findViewById(R.id.civ_ipff_friend_avatar1);
            civFriends[1] = (MyCircularImageView)view.findViewById(R.id.civ_ipff_friend_avatar2);
            civFriends[2] = (MyCircularImageView)view.findViewById(R.id.civ_ipff_friend_avatar3);

            TextView tvBanner = (TextView)view.findViewById(R.id.tv_ipff_banner_text);

            tvBanner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, UserListActivity.class);
                    intent.putExtra("type", "like");
                    intent.putExtra("post", postModel);
                    activity.startActivityForResult(intent, 107);
                }
            });
            civFriends[0].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, UserListActivity.class);
                    intent.putExtra("type", "like");
                    intent.putExtra("post", postModel);
                    activity.startActivityForResult(intent, 107);
                }
            });
            civFriends[1].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, UserListActivity.class);
                    intent.putExtra("type", "like");
                    intent.putExtra("post", postModel);
                    activity.startActivityForResult(intent, 107);
                }
            });
            civFriends[2].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, UserListActivity.class);
                    intent.putExtra("type", "like");
                    intent.putExtra("post", postModel);
                    activity.startActivityForResult(intent, 107);
                }
            });
            ////make banner text ==start
            int friendCount = postModel.getArrLiked_friends().size();

            String strBannerText = "<b>";
            Long finalLikedDate = null;
            if (friendCount > 0) {
                finalLikedDate = Long.parseLong(postModel.getArrLiked_friends().get(0).getTime());
            }

            for (int i = 0; i < friendCount; i ++) {
                Long likedTime = Long.parseLong(postModel.getArrLiked_friends().get(i).getTime());
                if (finalLikedDate > likedTime) {
                    finalLikedDate = likedTime;
                }
                if (i < 3) {
                    civFriends[i].setVisibility(View.VISIBLE);
                    if (!postModel.getArrLiked_friends().get(i).getAvatar().equals("")) {
                        UrlRectangleImageViewHelper.setUrlDrawable(civFriends[i], postModel.getArrLiked_friends().get(i).getAvatar(), R.drawable.default_user, new UrlImageViewCallback() {
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
                        civFriends[i].setImageDrawable(activity.getResources().getDrawable(R.drawable.default_user));
                    }
                }
                String fullname = postModel.getArrLiked_friends().get(i).getFullname();
                String firstName = fullname.substring(0, fullname.indexOf(" "));

                if (friendCount == 1) {
                    strBannerText = "<b>" + firstName + " " + "</b>";
                    break;
                } else {
                    String str = firstName + ", ";
                    if (friendCount < 4) {
                        if (i < friendCount - 1) {
                            strBannerText = strBannerText + str;
                        } else {
                            strBannerText = strBannerText.substring(0, strBannerText.length() - 2) + "</b>" + " "
                                    + activity.getResources().getString(R.string.and) + " "
                                    + "<b>" + firstName + " " + "</b>";
                            continue;
                        }
                    } else {
                        if (i < 3) {
                            strBannerText = strBannerText + str;
                        } else {
                            strBannerText = strBannerText.substring(0, strBannerText.length() - 2) + "</b>" + " "
                                    + activity.getResources().getString(R.string.and) + " "
                                    + "<b>" + String.valueOf(friendCount - 3) + " "+ activity.getResources().getString(R.string.people) + " " + "</b>";
                            continue;
                        }
                    }
                }
            }
            String strLikedMedia = "";
            if (postModel.getMedia_type().equals("post_photo")) {
                strLikedMedia = activity.getResources().getString(R.string.like_a_photo_from);
            } else {
                strLikedMedia = activity.getResources().getString(R.string.like_a_video_from);
            }
            strBannerText = strBannerText + strLikedMedia + " "
                    + "<b>" + postModel.getPoster_fullname().substring(0, postModel.getPoster_fullname().indexOf(" ")) + " - " + "</b>";

            if (finalLikedDate != null) {
                strBannerText = strBannerText + TimeUtility.countTime(activity, finalLikedDate);
            }

            //make banner text == end

            tvBanner.setText(Html.fromHtml(strBannerText));


            //add friend
            ImageButton ibAddFriend = (ImageButton)view.findViewById(R.id.ib_ipff_add_friend);
            if (postModel.getPoster_celebrity().equals("yes")) {
                    ibAddFriend.setVisibility(View.GONE);
            }
            if (postModel.getFriendStatus().equals("waiting")) {
                ibAddFriend.setImageDrawable(activity.getResources().getDrawable(R.drawable.sandglass_small));
            } else {
                ibAddFriend.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_green_plus));
            }
            ibAddFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (postModel.getFriendStatus().equals("none")) {
                        MainFragment.addFriend(position);
                    }
                }
            });
        }

        MyCircularImageView myCircularImageView = (MyCircularImageView)view.findViewById(R.id.civ_ipff_avatar);
        myCircularImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(activity, ProfileActivity.class);
//                intent.putExtra("user_id", postModel.getPoster_id());
//                activity.startActivityForResult(intent, 103);
                HomeActivity.navigateToProfile(postModel.getPoster_id());
            }
        });
        if (!postModel.getPoster_avatar().equals("")) {
            UrlRectangleImageViewHelper.setUrlDrawable(myCircularImageView, postModel.getPoster_avatar(), R.drawable.default_user, new UrlImageViewCallback() {
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
        }


        final ImageView ivMedia = (ImageView)view.findViewById(R.id.iv_ipff_media);
        ivMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, DetailPostActivity.class);
                intent.putExtra("post", postModel);
                intent.putExtra("from", 0);
                activity.startActivityForResult(intent, 101);
            }
        });

        String imageUrl = "";
        String videoUrl = "";
        if (postModel.getImageWidth() != 0 && postModel.getImageHeight() != 0) {
            double ratio = ((double)postModel.getImageHeight() / (double)postModel.getImageWidth());
            double height = ratio * UIUtility.getScreenWidth(activity);
            UIUtility.setImageViewSize(ivMedia, UIUtility.getScreenWidth(activity), (int)height);
        } else {
            double height = (int)( UIUtility.getScreenWidth(activity) * 0.75);
            UIUtility.setImageViewSize(ivMedia, UIUtility.getScreenWidth(activity),(int) height);
        }
        if (postModel.getMedia_type().equals("post_photo")) {
            imageUrl = API.BASE_POST_PHOTO + postModel.getMedia_url();

        } else if (postModel.getMedia_type().equals("post_video")){
            imageUrl = API.BASE_THUMBNAIL + postModel.getMedia_url().substring(0, postModel.getMedia_url().length() - 3) + "jpg";
            videoUrl = API.BASE_POST_VIDEO + postModel.getMedia_url();
        } else if (postModel.getMedia_type().equals("youtube")) {
            imageUrl = API.BASE_YOUTUB_PREFIX + FileUtility.getFilenameFromPath(postModel.getMedia_url()) + API.BASE_YOUTUB_SURFIX;
            videoUrl = postModel.getMedia_url();
        }
        if (!postModel.getMedia_url().equals("")) {
            ivMedia.setVisibility(View.VISIBLE);
            UrlRectangleImageViewHelper.setUrlDrawable(ivMedia, imageUrl, R.drawable.default_tour, new UrlImageViewCallback() {
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
        }else {
            ivMedia.setVisibility(View.GONE);
        }

        ImageButton ibPlay = (ImageButton)view.findViewById(R.id.ib_ipff_play);
        if (postModel.getMedia_type().equals("post_photo") || postModel.getMedia_type().equals("")) {
            ibPlay.setVisibility(View.GONE);
        } else {
            ibPlay.setVisibility(View.VISIBLE);
        }
        final String finalVideoUrl = videoUrl;
        ibPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, MediaPlayActivity.class);
                intent.putExtra("url", finalVideoUrl);
                if (postModel.getMedia_type().equals("post_video")) {
                    intent.putExtra("type", "video");
                } else {
                    intent.putExtra("type", "youtube");
                }
                activity.startActivity(intent);
            }
        });
        TextView tvName = (TextView)view.findViewById(R.id.tv_ipff_fullname);
        tvName.setText(postModel.getPoster_fullname());
        tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity.navigateToProfile(postModel.getPoster_id());
            }
        });
        TextView tvPostedDate = (TextView)view.findViewById(R.id.tv_ipff_date);
        tvPostedDate.setText(TimeUtility.countTime(activity, Long.parseLong(postModel.getPosted_date()) ));

        TextView tvDescription = (TextView)view.findViewById(R.id.tv_ipff_description);
        String str1 = postModel.getDescription();
//        if (str1.lastIndexOf("\n") != 0) {
//            str1 = str1.substring(0, str1.length() - 1);
//        }
        CharSequence str2 = StringUtility.trimTrailingWhitespace(Html.fromHtml(str1));
        tvDescription.setText(str2);
        tvDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, DetailPostActivity.class);
                intent.putExtra("post", postModel);
                intent.putExtra("from", 0);
                activity.startActivityForResult(intent, 101);
            }
        });

        //hashtag
//        final TagView hashTagView = (TagView)view.findViewById(R.id.ipff_hashtag);
        List<String> hashTags = new ArrayList<>();
        int lineMargin = 10;
        int tagMargin = 10;
        int mWidth = UIUtility.getScreenWidth(activity);
        int total = 0;
        int listIndex = 1;// List Index
        int index_bottom = 1;// The Tag to add below
        int index_header = 1;// The header tag of this line

        RelativeLayout rlTagContainer = (RelativeLayout)view.findViewById(R.id.rl_ipff_tag_container);
        View view_pre = null;
        if (postModel.getHashtag().length() > 0) {
           hashTags = Arrays.asList(postModel.getHashtag().split("#"));
            final List<String> finalHashTags = hashTags;
            for (int j = 0; j < hashTags.size(); j ++) {
//                Tag tag = new Tag("#" + hashTags.get(j));
//                tag.layoutColor = activity.getResources().getColor(R.color.transparent);
//                tag.tagTextColor =  activity.getResources().getColor(R.color.green);
//                hashTagView.addTag(tag);
                View view1 = activity.getLayoutInflater().inflate(R.layout.text_layout, null);
                view1.setId(listIndex);

                TextView textView = (TextView)view1.findViewById(R.id.tag);
                textView.setText("#" + hashTags.get(j));
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textView.getLayoutParams();
                params.setMargins(10, 5, 10, 5);
                textView.setLayoutParams(params);
                final int finalJ = j;
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (MainFragment.isFavorite) {
                            HomeActivity.navigateForHashTag(true, finalHashTags.get(finalJ));
                        } else {
                            HomeActivity.navigateForHashTag(false, finalHashTags.get(finalJ));
                        }
                    }
                });

                RelativeLayout.LayoutParams tagParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                //tagParams.setMargins(0, 0, 0, 0);

                //add margin of each line
                tagParams.bottomMargin = lineMargin;
                // calculate　of tag layout width
                float tagWidth = textView.getPaint().measureText(textView.getText().toString()) + 10 + 10;

                if (mWidth <= total + tagMargin + tagWidth + me.kaede.tagview.Utils.dpToPx(activity, Constants.LAYOUT_WIDTH_OFFSET)) {
                    //need to add in new line
                    tagParams.addRule(RelativeLayout.BELOW, index_bottom);
                    // initialize total param (layout padding left & layout padding right)
                    total = rlTagContainer.getPaddingLeft() + rlTagContainer.getPaddingRight();
                    index_bottom = listIndex;
                    index_header = listIndex;
                } else {
                    //no need to new line
                    tagParams.addRule(RelativeLayout.ALIGN_TOP, index_header);
                    //not header of the line
                    if (listIndex != index_header) {
                        tagParams.addRule(RelativeLayout.RIGHT_OF, listIndex - 1);
                        tagParams.leftMargin = tagMargin;
                        total += tagMargin;
                        if (view_pre!=null && view_pre.getWidth() < view1.getWidth()) {
                            index_bottom = listIndex;
                        }
                    }


                }


                total += tagWidth;
                rlTagContainer.addView(view1, tagParams);
                view_pre = view1;
                listIndex++;
            }
        }
//        final List<String> finalHashTags = hashTags;
//        if (hashTagView != null && finalHashTags != null) {
//            hashTagView.setOnTagClickListener(new OnTagClickListener() {
//
//                @Override
//                public void onTagClick(Tag tag, int position) {
//                    if (MainFragment.isFavorite) {
//                        HomeActivity.navigateForHashTag(true, finalHashTags.get(position));
//                    } else {
//                        HomeActivity.navigateForHashTag(false, finalHashTags.get(position));
//                    }
//                }
//            });
//        }

        ///social icons
        ImageButton ibLike = (ImageButton)view.findViewById(R.id.ib_ipff_like);
        ibLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Long.parseLong(TimeUtility.getCurrentTimeStamp()) - postModel.getClickedTime() > 15 && !postModel.getLike().equals("dislike")) {
                    if (postModel.getLike().equals("like")) {
                        MainFragment.setLike(position, "cancel_like");
                    } else if (postModel.getLike().equals("none")) {
                        MainFragment.setLike(position, "like");
                    }
                } else {
                    Utils.showToast(activity, activity.getResources().getString(R.string.wait_one_minute));
                }

            }
        });
        ImageButton ibDislike = (ImageButton)view.findViewById(R.id.ib_ipff_dislike);
        ibDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Long.parseLong(TimeUtility.getCurrentTimeStamp()) - postModel.getClickedTime() > 15 && !postModel.getLike().equals("like")) {
                    if (postModel.getLike().equals("dislike")) {
                        MainFragment.setLike(position, "cancel_dislike");
                    } else if (postModel.getLike().equals("none")) {
                        MainFragment.setLike(position, "dislike");
                    }
                }else {
                    Utils.showToast(activity, activity.getResources().getString(R.string.wait_one_minute));
                }

            }
        });
        if (postModel.getLike().equals("like")) {
            ibLike.setImageDrawable(activity.getResources().getDrawable(R.drawable.btn_like_green));
            ibDislike.setImageDrawable(activity.getResources().getDrawable(R.drawable.btn_dislike));
        } else if (postModel.getLike().equals("dislike")) {
            ibLike.setImageDrawable(activity.getResources().getDrawable(R.drawable.btn_like));
            ibDislike.setImageDrawable(activity.getResources().getDrawable(R.drawable.btn_dislike_green));
        } else {
            ibLike.setImageDrawable(activity.getResources().getDrawable(R.drawable.btn_like));
            ibDislike.setImageDrawable(activity.getResources().getDrawable(R.drawable.btn_dislike));
        }


        ImageButton ibComment = (ImageButton)view.findViewById(R.id.ib_ipff_comment);
        ibComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, DetailPostActivity.class);
                intent.putExtra("post", postModel);
                intent.putExtra("from", 0);
                activity.startActivityForResult(intent, 101);
            }
        });
        if (postModel.getCommented().equals("yes")) {
            ibComment.setImageDrawable(activity.getResources().getDrawable(R.drawable.btn_comment_green));
        } else {
            ibComment.setImageDrawable(activity.getResources().getDrawable(R.drawable.btn_comment));
        }

        ImageButton ibShare = (ImageButton)view.findViewById(R.id.ib_ipff_share);
        ibShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                MainFragment.sharing(position);
                HomeActivity.navigateToRepost(postModel);
            }
        });

        return view;
    }

}
