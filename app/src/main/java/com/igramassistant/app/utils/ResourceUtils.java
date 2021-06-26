package com.igramassistant.app.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.ArrayRes;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import androidx.core.content.ContextCompat;


import com.igramassistant.app.App;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;


public class ResourceUtils {

    public static String getString(@StringRes int stringId) {
        return App.getAppContext().getString(stringId);
    }

    public static Drawable getDrawable(@DrawableRes int drawableId) {
        return ContextCompat.getDrawable(App.getAppContext(), drawableId);
    }

    public static int getColor(@ColorRes int colorId) {
        return ContextCompat.getColor(App.getAppContext(), colorId);
    }

    public static int getDimen(@DimenRes int dimenId) {
        return (int) App.getAppContext().getResources().getDimension(dimenId);
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static ArrayList<String> getStringArrayList(@ArrayRes int stringArrayID) {
        ArrayList<String> strings = new ArrayList<>();
        String[] stringArray = App.getAppContext().getResources().getStringArray(stringArrayID);
        Collections.addAll(strings, stringArray);
        return strings;
    }

    public static ArrayList<Integer> getIntArrayList(@ArrayRes int stringArrayID) {
        ArrayList<Integer> integers = new ArrayList<>();
        int[] intArray = App.getAppContext().getResources().getIntArray(stringArrayID);
        for (int i = 0; i < intArray.length; i++) {
            integers.add(intArray[i]);
        }
        return integers;
    }

    public static String getThemeName(Context context, Resources.Theme theme) {
        try {
            int mThemeResId;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Field fThemeImpl = theme.getClass().getDeclaredField("mThemeImpl");
                if (!fThemeImpl.isAccessible()) fThemeImpl.setAccessible(true);
                Object mThemeImpl = fThemeImpl.get(theme);
                Field fThemeResId = mThemeImpl.getClass().getDeclaredField("mThemeResId");
                if (!fThemeResId.isAccessible()) fThemeResId.setAccessible(true);
                mThemeResId = fThemeResId.getInt(mThemeImpl);
            } else {
                Field fThemeResId = theme.getClass().getDeclaredField("mThemeResId");
                if (!fThemeResId.isAccessible()) fThemeResId.setAccessible(true);
                mThemeResId = fThemeResId.getInt(theme);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return theme.getResources().getResourceEntryName(mThemeResId);
            }
            return context.getResources().getResourceEntryName(mThemeResId);
        } catch (Exception e) {
            // Theme returned by application#getTheme() is always Theme.DeviceDefault
            return "AppTheme";
        }
    }

    public static String getThemeName(@StyleRes int theme) {
        return App.getAppContext().getResources().getResourceEntryName(theme);
    }
}
