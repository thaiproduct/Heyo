package com.heyoe_chat.controller.layer_chat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.heyoe_chat.R;
import com.heyoe_chat.controller.App;
import com.heyoe_chat.controller.MediaPlayActivity;
import com.heyoe_chat.controller.push.GcmBroadcastReceiver;
import com.heyoe_chat.model.API;
import com.heyoe_chat.model.Constant;
import com.heyoe_chat.model.Global;
import com.heyoe_chat.utilities.UIUtility;
import com.heyoe_chat.utilities.Utils;
import com.heyoe_chat.utilities.image_downloader.UrlImageViewCallback;
import com.heyoe_chat.utilities.image_downloader.UrlRectangleImageViewHelper;
import com.heyoe_chat.widget.MyCircularImageView;
import com.layer.atlas.AtlasAddressBar;
import com.layer.atlas.AtlasHistoricMessagesFetchLayout;
import com.layer.atlas.AtlasMessageComposer;
import com.layer.atlas.AtlasMessagesRecyclerView;
import com.layer.atlas.AtlasTypingIndicator;
import com.layer.atlas.messagetypes.generic.GenericCellFactory;
import com.layer.atlas.messagetypes.location.LocationCellFactory;
import com.layer.atlas.messagetypes.location.LocationSender;
import com.layer.atlas.messagetypes.singlepartimage.SinglePartImageCellFactory;
import com.layer.atlas.messagetypes.text.TextCellFactory;
import com.layer.atlas.messagetypes.text.TextSender;
import com.layer.atlas.messagetypes.threepartimage.CameraSender;
import com.layer.atlas.messagetypes.threepartimage.GallerySender;
import com.layer.atlas.messagetypes.threepartimage.ThreePartImageCellFactory;
import com.layer.atlas.typingindicators.BubbleTypingIndicatorFactory;
import com.layer.atlas.util.Util;
import com.layer.atlas.util.views.SwipeableItem;
import com.layer.sdk.LayerClient;
import com.layer.sdk.exceptions.LayerConversationException;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.ConversationOptions;
import com.layer.sdk.messaging.Message;

import java.util.List;

public class MessagesListActivity extends BaseActivity {
    private UiState mState;
    private Conversation mConversation;

    private AtlasAddressBar mAddressBar;
    private AtlasHistoricMessagesFetchLayout mHistoricFetchLayout;
    private AtlasMessagesRecyclerView mMessagesList;
    private AtlasTypingIndicator mTypingIndicator;
    private AtlasMessageComposer mMessageComposer;
    private LinearLayout llBackground;

    boolean isBlackFriend;
    int blacker_id ;
    String currentChatPage;
    String avatar, fullname, user_id;

    public MessagesListActivity() {
        super(R.layout.activity_messages_list, true);
    }

    private void setUiState(UiState state) {
        if (mState == state) return;
        mState = state;
        switch (state) {
            case ADDRESS:
                mAddressBar.setVisibility(View.VISIBLE);
                mAddressBar.setSuggestionsVisibility(View.VISIBLE);
                mHistoricFetchLayout.setVisibility(View.GONE);
                mMessageComposer.setVisibility(View.GONE);
                break;

            case ADDRESS_COMPOSER:
                mAddressBar.setVisibility(View.VISIBLE);
                mAddressBar.setSuggestionsVisibility(View.VISIBLE);
                mHistoricFetchLayout.setVisibility(View.GONE);
                mMessageComposer.setVisibility(View.VISIBLE);
                break;

            case ADDRESS_CONVERSATION_COMPOSER:
                mAddressBar.setVisibility(View.VISIBLE);
                mAddressBar.setSuggestionsVisibility(View.GONE);
                mHistoricFetchLayout.setVisibility(View.VISIBLE);
                mMessageComposer.setVisibility(View.VISIBLE);
                break;

            case CONVERSATION_COMPOSER:
                mAddressBar.setVisibility(View.GONE);
                mAddressBar.setSuggestionsVisibility(View.GONE);
                mHistoricFetchLayout.setVisibility(View.VISIBLE);
                mMessageComposer.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (App.routeLogin(this)) {
            if (!isFinishing()) finish();
            return;
        }

        mAddressBar = ((AtlasAddressBar) findViewById(R.id.conversation_launcher))
                .init(getLayerClient(), getParticipantProvider(), getPicasso())
                .setOnConversationClickListener(new AtlasAddressBar.OnConversationClickListener() {
                    @Override
                    public void onConversationClick(AtlasAddressBar addressBar, Conversation conversation) {
                        setConversation(conversation, true);
                        setTitle(true);
                    }
                })
                .setOnParticipantSelectionChangeListener(new AtlasAddressBar.OnParticipantSelectionChangeListener() {
                    @Override
                    public void onParticipantSelectionChanged(AtlasAddressBar addressBar, final List<String> participantIds) {
                        if (participantIds.isEmpty()) {
                            setConversation(null, false);
                            return;
                        }
                        try {
                            setConversation(getLayerClient().newConversation(new ConversationOptions().distinct(true), participantIds), false);
                        } catch (LayerConversationException e) {
                            setConversation(e.getConversation(), false);
                        }
                    }
                })
                .addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (mState == UiState.ADDRESS_CONVERSATION_COMPOSER) {
                            mAddressBar.setSuggestionsVisibility(s.toString().isEmpty() ? View.GONE : View.VISIBLE);
                        }
                    }
                })
                .setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            setUiState(UiState.CONVERSATION_COMPOSER);
                            setTitle(true);
                            return true;
                        }
                        return false;
                    }
                });

        mHistoricFetchLayout = ((AtlasHistoricMessagesFetchLayout) findViewById(R.id.historic_sync_layout))
                .init(getLayerClient())
                .setHistoricMessagesPerFetch(25);

        mMessagesList = ((AtlasMessagesRecyclerView) findViewById(R.id.messages_list))
                .init(getLayerClient(), getParticipantProvider(), getPicasso())
                .addCellFactories(
                        new TextCellFactory(),
                        new ThreePartImageCellFactory(this, getLayerClient(), getPicasso()),
                        new LocationCellFactory(this, getPicasso()),
                        new SinglePartImageCellFactory(this, getLayerClient(), getPicasso()),
                        new GenericCellFactory())
                .setOnMessageSwipeListener(new SwipeableItem.OnSwipeListener<Message>() {
                    @Override
                    public void onSwipe(final Message message, int direction) {
                        new AlertDialog.Builder(MessagesListActivity.this)
                                .setMessage(R.string.alert_message_delete_message)
                                .setNegativeButton(R.string.alert_button_cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // TODO: simply update this one message
                                        mMessagesList.getAdapter().notifyDataSetChanged();
                                        dialog.dismiss();
                                    }
                                })
                                .setNeutralButton(R.string.alert_button_delete_my_devices, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        message.delete(LayerClient.DeletionMode.ALL_MY_DEVICES);
                                    }
                                })
                                .setPositiveButton(R.string.alert_button_delete_all_participants, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        message.delete(LayerClient.DeletionMode.ALL_PARTICIPANTS);
                                    }
                                })
                                .show();
                    }
                });

        mTypingIndicator = new AtlasTypingIndicator(this)
                .init(getLayerClient())
                .setTypingIndicatorFactory(new BubbleTypingIndicatorFactory())
                .setTypingActivityListener(new AtlasTypingIndicator.TypingActivityListener() {
                    @Override
                    public void onTypingActivityChange(AtlasTypingIndicator typingIndicator, boolean active) {
                        mMessagesList.setFooterView(active ? typingIndicator : null);
                    }
                });

        mMessageComposer = ((AtlasMessageComposer) findViewById(R.id.message_composer))
                .init(getLayerClient(), getParticipantProvider())
                .setTextSender(new TextSender())
                .addAttachmentSenders(
                        new CameraSender(R.string.attachment_menu_camera, R.drawable.ic_photo_camera_white_24dp, this),
                        new GallerySender(R.string.attachment_menu_gallery, R.drawable.ic_photo_white_24dp, this),
                        new LocationSender(R.string.attachment_menu_location, R.drawable.ic_place_white_24dp, this))
                .setOnMessageEditTextFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            setUiState(UiState.CONVERSATION_COMPOSER);
                            setTitle(true);
                        }
                    }
                });

        // Get or create Conversation from Intent extras

        Intent intent = getIntent();
        if (intent != null) {
            user_id = intent.getStringExtra("user_id");
            blacker_id = getIntent().getIntExtra("blacker_id", 0);
            isBlackFriend = getIntent().getBooleanExtra("is_black_chat", false);
            currentChatPage = getIntent().getStringExtra("page");
            avatar = getIntent().getStringExtra("avatar");
            fullname = getIntent().getStringExtra("fullname");

            if (intent.hasExtra(GcmBroadcastReceiver.LAYER_CONVERSATION_KEY)) {
                Uri conversationId = (Uri)intent.getSerializableExtra(GcmBroadcastReceiver.LAYER_CONVERSATION_KEY);
                mConversation = getLayerClient().getConversation(conversationId);
            }
            if (mConversation == null) {
                try {
                    mConversation = getLayerClient().newConversation(new ConversationOptions().distinct(true), user_id);
                } catch (LayerConversationException e) {
                    mConversation = e.getConversation();
                }
            }


            Global.getInstance().isChatting = true;
            Global.getInstance().currentChattingPage = currentChatPage;
            if (mConversation != null) {
                if (mConversation.getParticipants().get(0).equals(Utils.getFromPreference(this, Constant.USER_ID))) {
                    Global.getInstance().currentChattingUserId = mConversation.getParticipants().get(1);
                } else {
                    Global.getInstance().currentChattingUserId = mConversation.getParticipants().get(0);
                }
            }
        }

        setConversation(mConversation, mConversation != null);
        init();
    }
    private void init() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageButton ibBack = (ImageButton)toolbar.findViewById(R.id.ib_back);
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtility.hideSoftKeyboard(MessagesListActivity.this);
                setDataAndFinish();
            }
        });
        TextView tvName = (TextView)toolbar.findViewById(R.id.tv_chat_fullname);
        tvName.setText(fullname);
        MyCircularImageView circularImageView = (MyCircularImageView)toolbar.findViewById(R.id.civUser);
        if (avatar.length() != 0) {
            String opponentAvatarURL =  avatar;
            UrlRectangleImageViewHelper.setUrlDrawable(circularImageView, opponentAvatarURL, R.drawable.default_user, new UrlImageViewCallback() {
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
        circularImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessagesListActivity.this, MediaPlayActivity.class);
                intent.putExtra("type", "photo");
                intent.putExtra("url", avatar);
                startActivity(intent);
            }
        });
        llBackground = (LinearLayout)findViewById(R.id.ll_message_list);
        if (isBlackFriend) {
            llBackground.setBackgroundColor(getResources().getColor(R.color.black));
            mMessageComposer.setBackgroundColor(getResources().getColor(R.color.black));
            mMessageComposer.setBlack();
            mHistoricFetchLayout.setBackgroundColor(getResources().getColor(R.color.black));
//            mMessagesList.setBackgroundColor(getResources().getColor(R.color.black));
        }
    }

    @Override
    public void onBackPressed() {
        mMessageComposer.hideAttachmentMenu();
    }

    private void setDataAndFinish() {
        //release chat

        Global.getInstance().isChatting = false;
        Global.getInstance().currentChattingUserId = "";
        Global.getInstance().currentChattingPage = "";

        Intent intent = getIntent();
        intent.putExtra("conversation_id", mConversation.getId());
        intent.putExtra("user_id", user_id);
        setResult(RESULT_OK, intent);
        finish();
    }
    @Override
    protected void onResume() {
        // Clear any notifications for this conversation
        GcmBroadcastReceiver.getNotifications(this).clear(mConversation);
        super.onResume();
        setTitle(mConversation != null);
    }

    @Override
    protected void onPause() {
        // Update the notification position to the latest seen
        GcmBroadcastReceiver.getNotifications(this).clear(mConversation);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Utils.saveToPreference(this, "is_black", "white");
        super.onDestroy();
    }

    public void setTitle(boolean useConversation) {
        if (!useConversation) {
            setTitle(R.string.title_select_conversation);
        } else {
            setTitle(Util.getConversationTitle(getLayerClient(), getParticipantProvider(), mConversation));
        }
    }

    private void setConversation(Conversation conversation, boolean hideLauncher) {
        mConversation = conversation;
        mHistoricFetchLayout.setConversation(conversation);
        mMessagesList.setConversation(conversation);
        mTypingIndicator.setConversation(conversation);
        mMessageComposer.setConversation(conversation);

        // UI state
        if (conversation == null) {
            setUiState(UiState.ADDRESS);
            return;
        }

        if (hideLauncher) {
            setUiState(UiState.CONVERSATION_COMPOSER);
            return;
        }

        if (conversation.getHistoricSyncStatus() == Conversation.HistoricSyncStatus.INVALID) {
            // New "temporary" conversation
            setUiState(UiState.ADDRESS_COMPOSER);
        } else {
            setUiState(UiState.ADDRESS_CONVERSATION_COMPOSER);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_details:
//                if (mConversation == null) return true;
//
//                return true;
//
//            case R.id.action_sendlogs:
//                LayerClient.sendLogs(getLayerClient(), this);
//                return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mConversation.isDeleted()) {
            mMessageComposer.onActivityResult(this, requestCode, resultCode, data);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        mMessageComposer.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private enum UiState {
        ADDRESS,
        ADDRESS_COMPOSER,
        ADDRESS_CONVERSATION_COMPOSER,
        CONVERSATION_COMPOSER
    }
}
