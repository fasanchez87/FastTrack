package com.ingenia.fasttrack.service;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ingenia.fasttrack.activity.EsperaTurno;
import com.ingenia.fasttrack.app.Config;
import com.ingenia.fasttrack.sharedPreferences.gestionSharedPreferences;
import com.ingenia.fasttrack.util.NotificationUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by FABiO on 07/10/2016.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService
{

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private gestionSharedPreferences sharedPreferences;


    private NotificationUtils notificationUtils;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        sharedPreferences = new gestionSharedPreferences(getApplicationContext());


        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null)
        {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0)
        {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData());

            try
            {
                Map<String, String> params = remoteMessage.getData();
                JSONObject object = new JSONObject(params);
                Log.e(TAG, "X - "+object.toString());
               // JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(object);
            }
            catch (Exception e)
            {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(String message)
    {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext()))
        {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        }
        else
        {
            // If the app is in background, firebase itself handles the notification
        }
    }

    private void handleDataMessage(JSONObject json)
    {
        Log.e(TAG, "push json: " + json.toString());

        try
        {
            //JSONObject data = json.getJSONObject("data");
            String title = json.getString("title");
            String message = json.getString("message");
            String imageUrl = json.getString("image");
            String timestamp = json.getString("timestamp");
            String keyMessage = json.getString("keyMessage");
            boolean isBackground = json.getBoolean("is_background");
            String ocultarModuloEsperaCliente = "ocultarModuloEsperaCliente";
            String habilitarCompraTicket = "habilitarCompraTicket";
            String pushNotificaClienteUso = "pushNotificaClienteUso";
            String notificarUsoTicket = "notificarUsoTicket";

            String payload = json.getString("ticket");
            JSONObject ticket = new JSONObject(payload);

            Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);
            Log.e(TAG, "isBackground: " + isBackground);
            Log.e(TAG, "payload: " + ticket.toString());
            Log.e(TAG, "imageUrl: " + imageUrl);
            Log.e(TAG, "timestamp: " + timestamp);

            if (!isBackground)
            {
                if (!NotificationUtils.isAppIsInBackground(getApplicationContext()))//IS FRONT APP
                {
                    if (keyMessage.equals("pushNotificaClienteTurnoAsesor"))
                    {
                        Intent intent = new Intent(Config.PUSH_NOTIFICATION_OCULTAR_PANTALLA_ESPERA_TURNO_ASESOR);
                        intent.putExtra("message", message);
                        Log.i("jovo",""+message);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                        //Vibrate Device
                        Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                        // Vibrate for 500 milliseconds
                        v.vibrate(5000);
                        // play notification sound
                        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                        notificationUtils.playNotificationSound();
                    }

                    if (keyMessage.equals("pushNotificaClienteUso"))
                    {
                        Intent intent = new Intent(Config.PUSH_NOTIFICATION_USO_TICKET);
                        intent.putExtra("message", message);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                        //Vibrate Device
                        Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                        // Vibrate for 500 milliseconds
                        v.vibrate(5000);
                        // play notification sound
                        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                        notificationUtils.playNotificationSound();
                    }
                }

                else

                if (NotificationUtils.isAppIsInBackground(getApplicationContext()))//IS BACKGROUND APP
                {
                    if (keyMessage.equals("pushNotificaClienteTurnoAsesor"))
                    {
                        Intent resultIntent = new Intent(MyFirebaseMessagingService.this, EsperaTurno.class);
                        resultIntent.putExtra("message", message);
                        resultIntent.putExtra("ocultarModuloEsperaCliente", ocultarModuloEsperaCliente);
                        resultIntent.putExtra("ticket", ticket.toString());
                        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(getApplicationContext(),1,
                                resultIntent,PendingIntent.FLAG_CANCEL_CURRENT);
                        // check for image attachment
                        if (TextUtils.isEmpty(imageUrl))
                        {
                            showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                        }

                        else
                        {
                            // image is present, show notification with image
                            showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                        }
                    }

                    if (keyMessage.equals("pushNotificaClienteUso"))
                    {
                        Intent resultIntent = new Intent(MyFirebaseMessagingService.this, EsperaTurno.class);
                        resultIntent.putExtra("message", message);
                        resultIntent.putExtra("notificarUsoTicket", notificarUsoTicket);
                        resultIntent.putExtra("ticket", ticket.toString());
                        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(getApplicationContext(),1,
                                resultIntent,PendingIntent.FLAG_CANCEL_CURRENT);
                        // check for image attachment
                        if (TextUtils.isEmpty(imageUrl))
                        {
                            showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                        }

                        else
                        {
                            // image is present, show notification with image
                            showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                        }
                    }
                }
            }


        }

        catch (JSONException e)
        {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        }

        catch (Exception e)
        {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent)
    {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl)
    {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}
