package com.lincoln.commonutil;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        startAppThread();
        if (PackageForegroundUtil.needPermissionForBlocking(this)) {
            PackageForegroundUtil.requetUsageAccessSetting(this);
        }

    }

    private void startAppThread() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    String packageTop = PackageForegroundUtil.getForegroundPackageName(MainActivity.this);
                    LogUtil.d("package:" + packageTop);
                    try {
                        Thread.sleep(5 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }
}
