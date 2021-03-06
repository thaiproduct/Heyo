package com.heyoe_chat.utilities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 1/26/2016.
 */
public class SocialUtility {
    private void sendEmail(Context mContext, String[] recipients, String subject){
        Intent intent = new Intent(Intent.ACTION_SEND);
        if(intent == null){
            Utils.showToast(mContext, "Not Available to send mail.");
        }else{
//            String[] recipients = {"fafuserservices@gmail.com"};
            intent.putExtra(Intent.EXTRA_EMAIL, recipients);
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_CC, DeviceUtility.getDeviceName());
            intent.setType("message/rfc822");
            mContext.startActivity(Intent.createChooser(intent, "Send mail"));
        }

    }
    public static void sendEmail(Context mContext, String toAddress) {
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[] { toAddress });
        email.putExtra(Intent.EXTRA_SUBJECT, "Perceptual Yoga");
        email.putExtra(Intent.EXTRA_TEXT,
                "Namaste\n\nKeep track of your Yoga activity such as Practice, Teaching and Learning, browse our members database, share your achievements and more. \nDownload Perpetual Yoga \n Find it at your AllytoursApplication Store and Google Play \n https://play.google.com/store?hl=en&tab=i8");
        email.setType("message/rfc822");
        mContext.startActivity(Intent.createChooser(email,
                "Choose an Email client :"));
    }

    public static void call(Context mContext, String phone) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phone));
//        mContext.startActivity(callIntent);
    }
    private void sharingText(Context mContext, String subject, String title, String shareBody){

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, subject);

        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        mContext.startActivity(Intent.createChooser(sharingIntent, title));

    }
    public static String printKeyHash(Context context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            Log.e("Package Name=", context.getApplicationContext().getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        }
        catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }
}
