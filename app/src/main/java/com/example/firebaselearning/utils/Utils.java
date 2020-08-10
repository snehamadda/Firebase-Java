package com.example.firebaselearning.utils;

import android.view.View;

public class Utils {
    public static void setVisibility(View view, boolean isShow) {
        if (isShow) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }
}
