package com.ntsoft.ihhq.controller.push;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.ntsoft.ihhq.R;
import com.ntsoft.ihhq.controller.login.StartActivity;


public class GcmIntentService extends IntentService {

    int i = 0;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty() && GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
            setNotificationData(extras);
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    public void setNotificationData(Bundle data) {
        parseMessage(data);
        sendNotification();
    }
    String message;
    private String parseMessage(Bundle data){
        message = "";
        if (data.containsKey("create_milestone")){
            message = data.getString("create_milestone");
        } else if (data.containsKey("create_payment")){
            message = data.getString("create_payment");
        } else if (data.containsKey("upload_document")){
            message = data.getString("upload_document");
        } else if (data.containsKey("new_support")){
            message = data.getString("new_support");
        }
        return message;
    }
    private void sendNotification() {
        Intent intent = new Intent(this, StartActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("type", "notification");
        NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, i++,
                intent,  PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.app_icon)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.app_icon))
                        .setContentTitle(getResources().getString(R.string.app_name))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(message))
                        .setSound(defaultSoundUri)
                        .setContentText(message);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(0, mBuilder.build());
    }


//    private void localBroadCast(PushModel pushModel) {
//        Intent intentNewPush = new Intent("pushData");
//        intentNewPush.putExtra(Constant.PUSH_DATA, pushModel);
//        LocalBroadcastManager.getInstance(this).sendBroadcast(intentNewPush);
//    }
}