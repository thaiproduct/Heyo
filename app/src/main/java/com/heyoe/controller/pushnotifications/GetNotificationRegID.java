package com.heyoe.controller.pushnotifications;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.heyoe.model.Constant;
import com.heyoe.utilities.Utils;

/**
 * Created by Administrator on 12/29/2015.
 */
public class GetNotificationRegID {
    ///////////////////gcm
    private String regId;


    GoogleCloudMessaging gcm;

    Context mContext;

    public GetNotificationRegID(Context context) {
        mContext = context;
    }
    ////get gcm register key
    public void registerInBackground() {
        new AsyncTask() {
            @Override
            protected String doInBackground(Object[] params) {
                String msg;
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(mContext);
                    }
                    regId = gcm.register(GcmServiceManager.PROJECT_NUMBER);
                    msg = "Device registered, registration ID: " + regId;

                    sendRegistrationId(regId);

                    storeRegistrationId(mContext, regId);
//                    Log.i(TAG, msg);
                } catch (Exception ex) {
                    msg = "Error :" + ex.getMessage();
//                    Log.e(TAG, msg);
                }
                return msg;
            }
        }.execute(null, null, null);
    }

    private void sendRegistrationId(String regId) {

    }

    private void storeRegistrationId(Context context, String regId) throws Exception {
        Utils.saveToPreference(context, Constant.DEVICE_TOKEN, regId);
    }


}
