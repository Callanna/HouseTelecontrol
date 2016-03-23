package com.github.callanna.iflylibaray;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(this, MyDemoService.class));

        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_stop).setOnClickListener(this);
        findViewById(R.id.btn_close).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.btn_start) {
            MyApplication.getInstance().wakeupService.startReconginzer();

        } else if (i == R.id.btn_stop) {
            MyApplication.getInstance().wakeupService.stopReconginzer();

        }else if(i == R.id.btn_close){
            MyApplication.getInstance().wakeupService.closeService();
        }
    }
}
