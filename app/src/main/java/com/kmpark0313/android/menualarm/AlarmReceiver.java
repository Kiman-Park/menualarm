package com.kmpark0313.android.menualarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kmpark0313.android.menualarm.AlarmService;

//앞의 MainActivity의 intent와 pending intent에서 전달해 주는 broadcast를 받는 receiver class
public class AlarmReceiver extends BroadcastReceiver {

    private static Context context;

    @Override
    public void onReceive(Context context, Intent intent){
        //리시버에서 MainAcvitivy에서 전달해준 broadcast를 받은(Receiver)다음에 AlarmService class로 전달해주는 intent
        Intent serviceintent = new Intent(context, AlarmService.class);
        //MainActivity에서 intent를 수신하면  AlarmService class의 enqueueWork메서드를 실행함
        AlarmService.enqueueWork(context, serviceintent); //service 시작
    }
}
