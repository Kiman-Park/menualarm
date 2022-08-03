package com.kmpark0313.android.menualarm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class MyFirebaseMessagingService extends FirebaseMessagingService {



    //아래 메서드는 토큰을 메시지 발송 서버로 보냄
    //v1.1부터 토큰저장 안하고 토픽으로 알림
    @Override
    public void onNewToken(String token){
        //sendToken(token); //v1.1부터 발송서버 DB에 토큰 저장 안함
        //super.onNewToken(token);
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


            super.onMessageReceived(remoteMessage);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

            NotificationCompat.Builder builder = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (notificationManager.getNotificationChannel("channel10") == null) {
                    NotificationChannel channel = new NotificationChannel("channel10", "channel10", NotificationManager.IMPORTANCE_DEFAULT);
                    notificationManager.createNotificationChannel(channel);
                }
                builder = new NotificationCompat.Builder(getApplicationContext(), "channel10");
            } else {
                builder = new NotificationCompat.Builder(getApplicationContext());
            }

            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();

            builder.setContentTitle(title)
                    .setContentText(body)
                    .setSmallIcon(R.drawable.lunchtime);

            Notification notification = builder.build();
            notificationManager.notify(1, notification);

    }

    /* v1.1부터 토큰저장 안하고 토픽으로 알림
    private void sendToken(String token){
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder().add("Token", token).build();
        Request request = new Request.Builder().url("http://218.235.174.112/registertoken.php").post(body).build();

        try{
            client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    } */
}
