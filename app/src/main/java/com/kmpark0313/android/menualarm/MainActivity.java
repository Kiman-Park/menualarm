package com.kmpark0313.android.menualarm;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {


    private static final String tag = "MyTag";

    TextView text1;
    TextView text2;
    TextView text3;
    Button button;
    Switch switchButton;
    String rice;
    String soup;
    String sidedish1;
    String sidedish2;
    String sidedish3;
    String sidedish4;
    String day_replace;
    String baseUrl = "http://218.235.174.112/";
    String version;
    String piVersionName;
    int piVersionCode;
    SharedPreferences preferences;
    int getSP;

    //조회데이터로 사용할 날짜 추출
    long now1 = System.currentTimeMillis();
    Date date1 = new Date(now1);
    SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
    String getTime1 = dateFormat1.format(date1);
    String dayfromtext3;



    Calendar cal = Calendar.getInstance();

    private DatePickerDialog.OnDateSetListener callbackMethod;

    String month1;
    String day;
    String day1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //엑션바(타이틀바) 숨기기(아래 두줄)
        ActionBar actionbar = getSupportActionBar();
        //actionbar.hide();
        piversion();
        version();
        //verCheck();

        //Log.v(tag, piVersionName);
        //Log.v(tag, version);



        /*(22.06.09)기존 1.0버전 사용자들 1.1로 올라오면서
        token이용알림과 topic이용한 알람 두개 모두 받지 않도록 하기 위해
        기존 token 삭제하는 코드*/
        //FirebaseMessaging.getInstance().deleteToken();
        /*(22.6.10) 기존토큰 삭제 하니 topic 알람안와서 주석처리함
        이게 살아있으면 앱 처음 설치하면 토픽알람 안오고
        스위치 off -> on 해줘야 그때부터 알람옴.. */

        //기본으로 fcmreceiver 토픽 구독하도록..(처음 설치하자마자)
        FirebaseMessaging.getInstance().subscribeToTopic("fcmreceive");



        text1 = (TextView) findViewById(R.id.textView);
        text2 = (TextView) findViewById(R.id.textView2);
        text3 = (TextView) findViewById(R.id.textView3);
        button = (Button) findViewById(R.id.button);
        switchButton = (Switch) findViewById(R.id.switch1);
        text3.setText(getTime1);
        dayfromtext3 = text3.getText().toString();
        day_replace = dayfromtext3.replaceAll("-", "");
        index();
        Log.v(tag, "실행지점1");

        this.InitializeListener();

        //thread.start();
        Log.v(tag, "실행지점2");




        //fcm notification 수신여부 체크 위한 sharedPreferences 추가(2022.05.20) 스위치버튼
        //getSharedPreferences("파일이름", 모드값);
        preferences = getSharedPreferences("fcmReceiveCheck", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();


        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked == true){
                    //putInt("Key값", 데이터);
                    editor.clear();
                    editor.putInt("value", 1);
                    editor.commit();
                    //fcmreceive 토픽 구독소스 추가(22.06.08)
                    FirebaseMessaging.getInstance().subscribeToTopic("fcmreceive");
                } else {
                    //putInt("Key값", 데이터);
                    editor.clear();
                    editor.putInt("value", 0);
                    editor.commit();
                    //fcmreceive 토픽 구독취소소스 추가(22.06.08)
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("fcmreceive");
                }
            }
        });

        /* 직전 종료시 switch button 상태를 불러오기 위함.(알림 끈 상태로 종료시, 상태를 sharedpreference에 저장 후
        다음 시작시 값을 불러와 끈 상태로 종료 했으면 스위치 버튼 꺼진상태로 앱 실행) */
        getSP = preferences.getInt("value",1);
        if(getSP == 0){
            switchButton.setChecked(false);
        }

    }


    public void InitializeListener(){
        Log.v(tag, "실행지점3");
        callbackMethod = new DatePickerDialog.OnDateSetListener()
        {

            @Override
            //사용자가 datepicker dialog의 확인버튼을 눌렀을때 실행되는 함수
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {

                //(아래) 앞서 조회된 rice의 변수값을 비워서, 메뉴등록 안된날짜 조회했을때 if조건에 걸려 toast메시지 보내도록
                rice = null;

                String year2 = String.valueOf(year);

                Log.v(tag, "실행지점4");
                int month = monthOfYear + 1;
                if(0 < month && month < 10){
                    month1 = "0" + String.valueOf(month);
                } else {
                    month1 = String.valueOf(month);
                }

                if(0 < dayOfMonth && dayOfMonth < 10){
                    day1 = "0" + String.valueOf(dayOfMonth);
                } else {
                    day1 = String.valueOf(dayOfMonth);
                }

                text3.setText(year2 + "-" + month1 + "-" + day1);
                dayfromtext3 = text3.getText().toString();
                day_replace = dayfromtext3.replaceAll("-", "");
                index();
                //dayfromtext3 = text3.getText().toString();
                //day = dayfromtext3.replaceAll("-", "");
                Log.v(tag, "실행지점5 "+day_replace);
            }
        };
    }

    //datepicker 열었을떄 실행되어 연월일 가져오는 함수
    public void OnClickHandler(View view)

    {Log.v(tag, "실행지점6");


        DatePickerDialog dialog = new DatePickerDialog
                (this, callbackMethod,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DATE));

        dialog.show();
    }


    public void btn1(View view) {

        String spiltyear = null;
        String spiltmonth = null;
        String spiltday = null;

        dayfromtext3 = text3.getText().toString();
        day_replace = dayfromtext3.replaceAll("-", "");

        //dialog title에 조회대상 일자를 표시(XXXX년 XX월 XX일 오늘의 메뉴)하고자 한 몸부림(나눴다가 붙혔다가...)
        String[] dayary = dayfromtext3.split("-");
        for(int i = 0; i <= dayary.length; i++){
            if(i==0){
                spiltyear = dayary[0];
            }
            if(i==1){
                spiltmonth = dayary[1];
            }
            if(i==2){
                spiltday = dayary[2];
            }
        }
        String getTime = spiltyear + "년" + spiltmonth + "월" + spiltday + "일";


        index();


        Log.v(tag, "실행지점7 " + dayfromtext3);


        //thread는 run()메소드가 모두 실행되면 알아서 종료된다
        //thread.run();


        //alarmBroadcastReceiver();
        if(rice == null){
            Toast t1 = Toast.makeText(this, "해당일자에 등록된 메뉴가 없습니다.", Toast.LENGTH_LONG);
            t1.show();
            Log.v(tag, "실행지점8");
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getTime + " 오늘의 메뉴");
            builder.setMessage(
                            "밥 : " + rice + "\n" +
                            "국,찌개 : " + soup + "\n" +
                            "반찬1 : " + sidedish1 + "\n" +
                            "반찬2 : " + sidedish2 + "\n" +
                            "반찬3 : " + sidedish3 + "\n" +
                            "반찬4 : " + sidedish4 + "\n");
            builder.setIcon(R.mipmap.checked);
            builder.setPositiveButton("확인", null);

            builder.show();
            //(아래) 앞서 조회된 rice의 변수값을 비워서, 메뉴등록 안된날짜 조회했을때 if조건에 걸려 toast메시지 보내도록
            rice = null;
        }
    }


    //Intent는 각 컴포넌트(엑티비티, 서비스, 콘텐츠프로바이더, 브로드캐스트리시버), 호출시 사용하며, 각 컴포넌트들은 보통 클래스로 되어있음.
    //Intent는 안드로이드 OS에게 "저 이거 실행시켜주세요~" 하고 요청하는것
    //pending intent가 intent를 활용하여 특정 컴포넌트에게 방송을 보냄


    /*public void alarmBroadcastReceiver(){
        //(아래) 첫번째 파라미터(this) : 메인엑티비티 자신, 두번째 파라미터(AlarmReceiver.class) : 호출할 클래스
        Intent intent = new Intent(this, AlarmReceiver.class);

        //(아래) getActivity, getService, getBroadcast별로 파라미터가 다르다.
        //(아래) 첫번째 파라미터 : context, 두번째 파라미터 : pending인텐트 가져올때 구분하는 코드, 세번째 파라미터 : 실행할 intent, 네번재 파라미터 : 플래그
        //(아래) FLAG 관련 : 만약 사용자가 알람을 여러개 설정해 pendingIntent가 여러개 존재하지 않아도 되는 상황이면 flag_update_current 또는 flag_calcle_current로 해도 무방
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        Calendar cal = getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        //cal.set(year, month-1, day, hour, minute);
        cal.set(DAY_OF_WEEK, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY);
        cal.set(HOUR_OF_DAY, 11);

        //알람 반복주기를 설정(세번째 파라미터 intervalmillis)
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 24*60*60*1000, pendingIntent);
    }*/

    //'메뉴확인'버튼 클릭 시 서버에서 정보 받아오는 네트워크 스레드
    /*class NetworkThread extends Thread {


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



        int responseStatusCode;
        JSONObject obj = new JSONObject();

        public void run(){
            try{

                String day_replace = dayfromtext3.replaceAll("-", "");
                Log.v(tag, "실행지점9(get요청전)" + day_replace);

                String site = "http://218.235.174.112/todayMenu.php?day=" + day_replace;

                URL url = new URL(site);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                //여기부터 실행안됨..(4.22)
                conn.connect();
                Log.v(tag, "실행지점10" + day_replace);
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


            }
            catch (Exception e){
            }
        }
    } */

    //데이터 클래스 정의(별도 class파일로 뺌(22.04.25)
    /*public class RetrofitRepo{
        String rice;
        String soup;
        String sidedish1;
        String sidedish2;
        String sidedish3;
        String sidedish4;

        public String getRice() {
            return rice;
        }

        public String getSoup(){
            return soup;
        }

        public String getSidedish1(){
            return sidedish1;
        }

        public String getSidedish2(){
            return sidedish2;
        }

        public String getSidedish3(){
            return sidedish3;
        }

        public String getSidedish4(){
            return sidedish4;
        }
    }*/


    //retrofit으로 http 통신 후 데이터 받아와서 string으로 변환
    public void index(){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Log.v(tag, "실행지점9(get요청전)" + day_replace);
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        Call<RetrofitRepo> call = retrofitService.getMenu(day_replace);
        call.enqueue(new Callback<RetrofitRepo>(){

            @Override
            public void onResponse(Call<RetrofitRepo> call, Response<RetrofitRepo> response) {


                try {
                    JSONObject obj = null;
                    obj = new JSONObject(new Gson().toJson(response.body()));

                    rice = obj.optString("rice");
                    soup = obj.optString("soup");
                    sidedish1 = obj.optString("sidedish1");
                    sidedish2 = obj.optString("sidedish2");
                    sidedish3 = obj.optString("sidedish3");
                    sidedish4 = obj.optString("sidedish4");
                    Log.v(tag, "실행지점10" + day_replace);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<RetrofitRepo> call, Throwable t) {

            }
            });
    }

    //retrofit으로 http 통신 후 서버에서 버전정보 받아와서 version 변수에 넣어줌. 이후 현재어플의 versionName과 비교하여 최신여부 확인
    //현재 앱의 versionName과 versionCode는 app 레벨의 build.gradle에 설정되어 있음.
    //versioncode는 사용자에게 보여지지 않고 1씩 증가, versionname은 사용자에게 공개되고 개발자가 임의로 설정
    public void version(){

        Retrofit retrofit2 = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitService2 retrofitService2 = retrofit2.create(RetrofitService2.class);
        Call<RetrofitRepo2> call = retrofitService2.getVersion();
        call.enqueue(new Callback<RetrofitRepo2>(){

            @Override
            public void onResponse(Call<RetrofitRepo2> call, Response<RetrofitRepo2> response) {


                try {
                    JSONObject obj = null;
                    obj = new JSONObject(new Gson().toJson(response.body()));

                    Log.d(tag, "버전받아오기 " + obj.toString());
                    version = obj.optString("version");
                    Log.d(tag, "버전받아오기2 " + version);

                    if (!piVersionName.equals(version)) {
                        Log.d(tag, "버전받아오기3 " + version+" "+piVersionName);
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("업데이트가 필요합니다");
                        builder.setMessage("플레이스토어에서 최신버전으로 업데이트 해주세요\n\n현재버전 : " + piVersionName + "\n최신버전 : " + version);
                        Log.d(tag, "버전받아오기4 " + version+" "+piVersionName);
                        builder.setIcon(R.mipmap.checked);
                        builder.setPositiveButton("업데이트", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // 여기에 마켓으로 이동 코드 입력
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.kmpark0313.android.menualarm"));
                                startActivity(intent);
                            }
                        });
                        builder.setNegativeButton("취소", null);
                        builder.show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<RetrofitRepo2> call, Throwable t) {

            }
        });


    }

    //현재 앱의 버전네임버전코트를 가져오는 함수
    public void piversion(){
        try {
            PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            piVersionName = pi.versionName;
            piVersionCode = pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /*public void verCheck() {
        if (!piVersionName.equals(version)) {
            Log.d(tag, "버전받아오기3 " + version);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("업데이트가 필요합니다.");
            builder.setMessage("마켓에서 최신버전으로 업데이트 해주세요\n\n현재버전 : " + piVersionName + "\n최신버전 : " + version);
            Log.d(tag, "버전받아오기4 " + version);
            builder.setIcon(R.mipmap.checked);
            builder.setPositiveButton("확인", null);
            builder.show();
        }
    }*/
}