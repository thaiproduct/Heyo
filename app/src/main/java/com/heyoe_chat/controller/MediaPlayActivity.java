package com.heyoe_chat.controller;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.heyoe_chat.R;
import com.heyoe_chat.utilities.BitmapUtility;
import com.heyoe_chat.utilities.FileUtility;
import com.heyoe_chat.utilities.UIUtility;
import com.heyoe_chat.utilities.Utils;
import com.heyoe_chat.utilities.VideoPlay;
import com.heyoe_chat.utilities.image_downloader.UrlImageViewCallback;
import com.heyoe_chat.utilities.image_downloader.UrlRectangleImageViewHelper;

import java.io.File;

public class MediaPlayActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private static final int RECOVERY_DIALOG_REQUEST = 1;

    private YouTubePlayerView youTubeView;
    private VideoView videoView;
    private ImageView imageView;
    String url;
    String type;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_media_play);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ImageButton ibBack = (ImageButton) toolbar.findViewById(R.id.ib_back);
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        url = getIntent().getStringExtra("url");
        type = getIntent().getStringExtra("type");
        if (type.equals("youtube")) {
            initYoutube();
        } else if (type.equals("video") || type.equals("qb_video")) {
            initVideoview();
        } else if (type.equals("photo")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            initImageView();
        }else if (type.equals("qb_photo")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            initImageViewForChatPhoto();
            initImageView();
        }


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private ScaleGestureDetector scaleGestureDetector;
    private Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();
    PointF startPoint = new PointF();
    PointF midPoint = new PointF();
    float oldDist = 1f;
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;

    int mode = NONE;
    String DOWNLOAD_IMAGE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator;

    private void initImageViewForChatPhoto() {
        imageView = (ImageView) findViewById(R.id.imageview);
        imageView.setVisibility(View.VISIBLE);

        final String filename = FileUtility.getFilenameFromPath(url) + ".jpg";

        final DownloadManager manager = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
//        final String fileName = url.substring(url.lastIndexOf('/') + 1);
        ///creat download request
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

        if (FileUtility.checkFileExist(filename, DOWNLOAD_IMAGE_PATH)) {
            File file = new File(DOWNLOAD_IMAGE_PATH + filename);
            file.delete();
        }
        ///set destinatin storage path
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
        Utils.showProgress(this);
        final long downloadId = manager.enqueue(request);
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean downloading = true;
                    while (downloading) {

                        DownloadManager.Query q = new DownloadManager.Query();
                        q.setFilterById(downloadId);

                        Cursor cursor = manager.query(q);
                        cursor.moveToFirst();
                        int bytes_downloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                        int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                        ///calculate download progress status
                        final double dl_progress = bytes_total != 0 ? (int) ((bytes_downloaded * 100l) / bytes_total) : 0;
                        if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                            downloading = false;
                        }
                        cursor.close();
                    }
                    Bitmap bitmap = BitmapUtility.getBitmap(DOWNLOAD_IMAGE_PATH + filename, 1);
                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();

                    double ratio = ((double) height / (double) width);
                    double height1 = ratio * UIUtility.getScreenWidth(MediaPlayActivity.this);
                    UIUtility.setImageViewSize(imageView, UIUtility.getScreenWidth(MediaPlayActivity.this), (int) height1);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    imageView.setImageBitmap(bitmap);
                    Utils.hideProgress();
                }
            });

        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void initImageView() {

        int width = getIntent().getIntExtra("width", 0);
        int height = getIntent().getIntExtra("height", 0);

        imageView = (ImageView) findViewById(R.id.imageview);
        imageView.setVisibility(View.VISIBLE);
//        init imageview size
        if (width != 0 && height != 0) {
            double ratio = ((double) height / (double) width);
            double height1 = ratio * UIUtility.getScreenWidth(this);
            UIUtility.setImageViewSize(imageView, UIUtility.getScreenWidth(this), (int) height1);
        }
        if (!url.equals("")) {
            UrlRectangleImageViewHelper.setUrlDrawable(imageView, url, R.drawable.default_tour, new UrlImageViewCallback() {
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

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ImageView view = (ImageView) v;
                System.out.println("matrix=" + savedMatrix.toString());
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        savedMatrix.set(matrix);
                        startPoint.set(event.getX(), event.getY());
                        mode = DRAG;
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        oldDist = spacing(event);
                        if (oldDist > 10f) {
                            savedMatrix.set(matrix);
                            midPoint(midPoint, event);
                            mode = ZOOM;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        mode = NONE;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mode == DRAG) {
                            matrix.set(savedMatrix);
                            matrix.postTranslate(event.getX() - startPoint.x, event.getY() - startPoint.y);
                        } else if (mode == ZOOM) {
                            float newDist = spacing(event);
                            if (newDist > 10f) {
                                matrix.set(savedMatrix);
                                float scale = newDist / oldDist;
                                matrix.postScale(scale, scale, midPoint.x, midPoint.y);
                            }
                        }
                        break;
                }
                view.setImageMatrix(matrix);
                return true;
            }

            @SuppressLint("FloatMath")
            private float spacing(MotionEvent event) {
                float x = event.getX(0) - event.getX(1);
                float y = event.getY(0) - event.getY(1);
                return (float) Math.sqrt(x * x + y * y);
            }

            private void midPoint(PointF point, MotionEvent event) {
                float x = event.getX(0) + event.getX(1);
                float y = event.getY(0) + event.getY(1);
                point.set(x / 2, y / 2);
            }
        });
    }


    private void initVideoview() {
        videoView = (VideoView) findViewById(R.id.videoview);
        videoView.setVisibility(View.VISIBLE);

        VideoPlay videoPlay = new VideoPlay(this, videoView, url);
        videoPlay.playVideo();
    }


    private void initYoutube() {
        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        youTubeView.setVisibility(View.VISIBLE);
        // Initializing video player with developer key
        youTubeView.initialize(getResources().getString(R.string.google_android_key), this);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        if (!wasRestored) {

            // loadVideo() will auto play video
            // Use cueVideo() method, if you don't want to play it automatically
            youTubePlayer.loadVideo(FileUtility.getFilenameFromPath(url));

            // Hiding player controls
            youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);

        }

    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = String.format(getString(R.string.Youtube_play_error), youTubeInitializationResult.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(getResources().getString(R.string.google_android_key), this);
        }
    }

    private YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerView) findViewById(R.id.youtube_view);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (type.equals("youtube")) {
            // ATTENTION: This was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
            client.connect();
            Action viewAction = Action.newAction(
                    Action.TYPE_VIEW, // TODO: choose an action type.
                    "MediaPlay Page", // TODO: Define a title for the content shown.
                    // TODO: If you have web page content that matches this app activity's content,
                    // make sure this auto-generated web page URL is correct.
                    // Otherwise, set the URL to null.
                    Uri.parse("http://host/path"),
                    // TODO: Make sure this auto-generated app deep link URI is correct.
                    Uri.parse("android-app://com.heyoe_chat/http/host/path")///com.heyoe_chat-> must same as package name in AndroidManifest.xml
            );
            AppIndex.AppIndexApi.start(client, viewAction);
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (type.equals("youtube")) {
            // ATTENTION: This was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
            Action viewAction = Action.newAction(
                    Action.TYPE_VIEW, // TODO: choose an action type.
                    "MediaPlay Page", // TODO: Define a title for the content shown.
                    // TODO: If you have web page content that matches this app activity's content,
                    // make sure this auto-generated web page URL is correct.
                    // Otherwise, set the URL to null.
                    Uri.parse("http://host/path"),
                    // TODO: Make sure this auto-generated app deep link URI is correct.
                    Uri.parse("android-app://com.heyoe_chat/http/host/path")   ///com.heyoe_chat-> must same as package name in AndroidManifest.xml
            );
            AppIndex.AppIndexApi.end(client, viewAction);
            client.disconnect();
        }

    }
}
