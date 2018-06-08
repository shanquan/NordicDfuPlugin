package com.byd.wwb.dfu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by wang.wenbin1 on 2017/11/3.
 */

public class NotificationActivity extends Activity {

    private Class getMainActivityClass() {
//        String packageName = reactContext.getPackageName();
//        Intent launchIntent = reactContext.getPackageManager().getLaunchIntentForPackage(packageName);
//        String className = launchIntent.getComponent().getClassName();
        try {
            return Class.forName("com.byd.ghydy.bracelet.MainActivity");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isTaskRoot()) {
            // Start the app before finishing
            final Intent intent = new Intent(this, getMainActivityClass());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtras(getIntent().getExtras()); // copy all extras
            startActivity(intent);
        }
        // Now finish, which will drop you to the activity at which you were at the top of the task stack
        finish();
    }
}
