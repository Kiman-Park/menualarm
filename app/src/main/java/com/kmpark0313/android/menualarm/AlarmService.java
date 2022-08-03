package com.kmpark0313.android.menualarm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;


public class AlarmService extends JobIntentService {

    long now = System.currentTimeMillis();
    Date date = new Date(now);
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년MM월dd일");
    String getTime = dateFormat.format(date);
    String data;

    String rice;
    String soup;
    String sidedish1;
    String sidedish2;
    String sidedish3;
    String sidedish4;

    JSONObject obj = new JSONObject();
    int responseStatusCode;

    //AlarmReceiver class에서 실행하는 함수
    static void enqueueWork(Context context, Intent work){
        enqueueWork(context, AlarmService.class, 1000, work);
    }



    // 여기에 notification 소스??
    @Override
    public void onHandleWork(Intent intent) {

        //notification에 데이터가져와서 보여주기 위한 json 파싱 코드(intentservice class는 별도 thread로 동작)
        try {
            String site = "http://218.235.174.112/todayMenu.php";
            URL url = new URL(site);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            responseStatusCode = conn.getResponseCode();
            InputStream is = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            BufferedReader br = new BufferedReader(isr);

            String str = null;
            str = br.readLine();

            StringBuffer buf = new StringBuffer();
            buf.append(str);
            data = buf.toString();
            obj = new JSONObject(data);

            rice = obj.optString("rice");
            soup = obj.optString("soup");
            sidedish1 = obj.optString("sidedish1");
            sidedish2 = obj.optString("sidedish2");
            sidedish3 = obj.optString("sidedish3");
            sidedish4 = obj.optString("sidedish4");

            conn.disconnect();

        } catch (Exception e) {
        }

        //AlarmService class에서 실행한 noti 결과를 MainActivity로 다시 되돌려줌
        new Intent(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder builder = getNotificationBuilder("channel1", "channel1");
        // (아래)알림이 처음들어왔을때 상단 표시줄에 출력되었다가 사라지는 메시지(Ticker)
        builder.setTicker("오늘의 메뉴가 도착했습니다");
        builder.setSmallIcon(android.R.drawable.ic_menu_search);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        builder.setLargeIcon(bitmap);
        // (아래)확인하지 않은 알람 갯수를 표시 setNumber
        //builder.setNumber(100);


        builder.setContentTitle(getTime + " 오늘의 메뉴");
        //builder.setContentText("밥 : " + rice + "\n" + "국,찌개 : " + soup + "\n" + "반찬1 : " + sidedish1 + "\n" + "반찬2 : " + sidedish2 + "\n" + "반찬3 : " + sidedish3 + "\n"+ "반찬4 : " + sidedish4 + "\n");

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        //bigTextStyle.setBigContentTitle(getTime + " 오늘의 메뉴");
        //bigTextStyle.setSummaryText("밥 : " + rice + "\n" + "국,찌개 : " + soup + "\n" + "반찬1 : " + sidedish1 + "\n" + "반찬2 : " + sidedish2 + "\n" + "반찬3 : " + sidedish3 + "\n"+ "반찬4 : " + sidedish4 + "\n");
        bigTextStyle.bigText("밥 : " + rice + "\n" + "국,찌개 : " + soup + "\n" + "반찬1 : " + sidedish1 + "\n" + "반찬2 : " + sidedish2 + "\n" + "반찬3 : " + sidedish3 + "\n"+ "반찬4 : " + sidedish4 + "\n");
        builder.setStyle(bigTextStyle);

        Notification notification = builder.build();
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // (아래)??
        manager.notify(10, notification);

    }


    public NotificationCompat.Builder getNotificationBuilder(String id, String name){
        NotificationCompat.Builder builder = null;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            //(아래)메시지관리 객체
            NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            //(아래)채널 객체
            NotificationChannel channel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
            //(아래)단말기 LED 여부
            channel.enableLights(true);
            //(아래)LED 색상
            channel.setLightColor(android.R.color.holo_red_dark);
            //(아래)진동 여부
            channel.enableVibration(true);
            //(아래)notification 관리 객체에 채널 연결
            manager.createNotificationChannel(channel);
            //(아래)메시지 생성 객체 생성
            builder = new NotificationCompat.Builder(this, id);
        }else{
            builder = new NotificationCompat.Builder(this);
        }
        return builder;
    }
}
