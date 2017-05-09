package com.ingenia.fasttrack.service;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.iid.InstanceID;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.ingenia.fasttrack.app.Config;
import com.ingenia.fasttrack.sharedPreferences.gestionSharedPreferences;
import com.ingenia.fasttrack.vars.vars;
import com.ingenia.fasttrack.volley.ControllerSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by FABiO on 07/10/2016.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService
{
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();
    private String _urlWebServiceUpdateToken;
    vars var;
    private gestionSharedPreferences msharedPreferences;



    @Override
    public void onTokenRefresh()
    {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        msharedPreferences = new gestionSharedPreferences(this);

        var = new vars();

        // Saving reg id to shared preferences
        storeRegIdInPref(refreshedToken);

        Log.i("TokenFCM", "Firebase reg id:onTokenRefresh " + refreshedToken);
        // sending reg id to your server
        sendRegistrationToServer(refreshedToken);

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(final String refreshedToken)
    {
        // Add custom implementation, as needed.
        _urlWebServiceUpdateToken = var.ipServer.concat("/ws/UpdateTokenFCM");

        Log.e("tokenFCM", ""+refreshedToken);
        Log.e("idDevice", ""+msharedPreferences.getString("deviceID"));
        Log.e("MyToken", ""+msharedPreferences.getString("MyTokenAPI"));

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, _urlWebServiceUpdateToken, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {
                            Boolean status = response.getBoolean("status");
                            String message = response.getString("message");

                            if(status)
                            {

                                Log.e(TAG, "Se registro exitosamente GCM al Server: UpdateTokenFCM");
                               // Toast.makeText(MyFirebaseInstanceIDService.this, "Se registro exitosamente FCM al Server::SERVICIO INTENTSERVICE", Toast.LENGTH_SHORT).show();

                            }

                            else
                            {
                                Log.e(TAG, "Fallo al registrar GCM al Server: UpdateTokenFCM");
                               // Toast.makeText(MyFirebaseInstanceIDService.this, "Error, FCM al Server:UpdateTokenFCM", Toast.LENGTH_SHORT).show();

                            }
                        }
                        catch (JSONException e)
                        {
                            //progressBar.setVisibility(View.GONE);
                            Log.e(TAG, ""+ e.getMessage().toString());
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {

                    }

                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("WWW-Authenticate", "xBasic realm=".concat(""));
                headers.put("tokenFCM", refreshedToken);
                headers.put("idDevice", InstanceID.getInstance(getApplicationContext()).getId());
                //headers.put("MyToken", msharedPreferences.getString("MyTokenAPI"));
                return headers;
            }
        };

        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "");
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void storeRegIdInPref(String token)
    {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.commit();
    }
}

