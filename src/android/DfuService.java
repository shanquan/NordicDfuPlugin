package com.byd.wwb.dfu;

import android.app.Activity;
import no.nordicsemi.android.dfu.DfuBaseService;

/**
 * Created by wang.wenbin1 on 2017/11/3.
 */

public class DfuService extends DfuBaseService {
    @Override
    protected Class<? extends Activity> getNotificationTarget() {
        return NotificationActivity.class;
    }

    @Override
    protected boolean isDebug() {
        return true;
    }
}
