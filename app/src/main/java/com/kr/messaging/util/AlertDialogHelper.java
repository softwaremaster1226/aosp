package com.kr.messaging.util;

import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.view.Gravity;

import java.util.Calendar;

public class AlertDialogHelper {
    public static Calendar future;
    public static boolean isSchedule = false;
    public static void adjustAlertDialog(AlertDialog dialog, Drawable drawable) {
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setBackgroundDrawable(drawable);
    }
}
