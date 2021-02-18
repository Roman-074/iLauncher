package com.benny.openlauncher.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.ErrorListener;

/**
 * Created by Phí Văn Tuấn on 27/7/2018.
 */

public class RegistrationIntentService extends IntentService {
    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = new String[]{"global"};
    private String token;

    public RegistrationIntentService() {
        super(TAG);
    }

    protected void onHandleIntent(Intent intent) {
        try {
            OneSignalTokenGenerate();
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
        }
    }

    private void OneSignalTokenGenerate() {
//        try {
//            this.token = OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getPushToken();
//            Log.i(TAG, "Registration Token: " + this.token);
//            RegisterToken1();
//        } catch (Exception e) {
//            Log.e("error token", " " + e.getMessage());
//            e.printStackTrace();
//        }
    }

    private void RegisterToken1() {
//        try {
//            Volley.newRequestQueue(this).add(new StringRequest(1, SecureEnvironment.getString("api_gcm"), new Listener<String>() {
//                public void onResponse(String response) {
//                    if (response != null) {
//                        try {
//                            if (!response.equals("") && new JSONObject(response).getBoolean(NotificationCompat.CATEGORY_STATUS)) {
//                                Common.setPrefBoolean(RegistrationIntentService.this, "isToken", true);
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }, new ErrorListener() {
//                public void onErrorResponse(VolleyError error) {
//                    Log.e("error", " " + error.getMessage());
//                }
//            }) {
//                protected Map<String, String> getParams() {
//                    Map<String, String> params = new HashMap();
//                    params.put("app_id", SecureEnvironment.getString("app_id"));
//                    params.put("device_token", RegistrationIntentService.this.token);
//                    return params;
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            stopSelf();
//        }
    }
}
