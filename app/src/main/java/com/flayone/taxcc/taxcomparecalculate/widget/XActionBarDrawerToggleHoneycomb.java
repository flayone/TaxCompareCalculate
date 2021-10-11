package com.flayone.taxcc.taxcomparecalculate.widget;

import android.app.ActionBar;
import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import java.lang.reflect.Method;

public class XActionBarDrawerToggleHoneycomb {
    private static final String TAG = "ActionBarDrawerToggleHC";

    private static final int[] THEME_ATTRS = new int[] {
            android.R.attr.homeAsUpIndicator
    };

    public static XActionBarDrawerToggleHoneycomb.SetIndicatorInfo setActionBarUpIndicator(Activity activity,
                                                                                          Drawable drawable, int contentDescRes) {
        XActionBarDrawerToggleHoneycomb.SetIndicatorInfo info = new XActionBarDrawerToggleHoneycomb.SetIndicatorInfo(activity);
        if (info.setHomeAsUpIndicator != null) {
            try {
                final ActionBar actionBar = activity.getActionBar();
                info.setHomeAsUpIndicator.invoke(actionBar, drawable);
                info.setHomeActionContentDescription.invoke(actionBar, contentDescRes);
            } catch (Exception e) {
                Log.w(TAG, "Couldn't set home-as-up indicator via JB-MR2 API", e);
            }
        } else if (info.upIndicatorView != null) {
            info.upIndicatorView.setImageDrawable(drawable);
        } else {
            Log.w(TAG, "Couldn't set home-as-up indicator");
        }
        return info;
    }

    public static XActionBarDrawerToggleHoneycomb.SetIndicatorInfo setActionBarDescription(XActionBarDrawerToggleHoneycomb.SetIndicatorInfo info, Activity activity,
                                                                                          int contentDescRes) {
        if (info == null) {
            info = new XActionBarDrawerToggleHoneycomb.SetIndicatorInfo(activity);
        }
        if (info.setHomeAsUpIndicator != null) {
            try {
                final ActionBar actionBar = activity.getActionBar();
                info.setHomeActionContentDescription.invoke(actionBar, contentDescRes);
                if (Build.VERSION.SDK_INT <= 19) {
                    // For API 19 and earlier, we need to manually force the
                    // action bar to generate a new content description.
                    actionBar.setSubtitle(actionBar.getSubtitle());
                }
            } catch (Exception e) {
                Log.w(TAG, "Couldn't set content description via JB-MR2 API", e);
            }
        }
        return info;
    }

    public static Drawable getThemeUpIndicator(Activity activity) {
        final TypedArray a = activity.obtainStyledAttributes(THEME_ATTRS);
        final Drawable result = a.getDrawable(0);
        a.recycle();
        return result;
    }

    static class SetIndicatorInfo {
        public Method setHomeAsUpIndicator;
        public Method setHomeActionContentDescription;
        public ImageView upIndicatorView;

        SetIndicatorInfo(Activity activity) {
            try {
                setHomeAsUpIndicator = ActionBar.class.getDeclaredMethod("setHomeAsUpIndicator",
                        Drawable.class);
                setHomeActionContentDescription = ActionBar.class.getDeclaredMethod(
                        "setHomeActionContentDescription", Integer.TYPE);

                // If we got the method we won't need the stuff below.
                return;
            } catch (NoSuchMethodException e) {
                // Oh well. We'll use the other mechanism below instead.
            }

            final View home = activity.findViewById(android.R.id.home);
            if (home == null) {
                // Action bar doesn't have a known configuration, an OEM messed with things.
                return;
            }

            final ViewGroup parent = (ViewGroup) home.getParent();
            final int childCount = parent.getChildCount();
            if (childCount != 2) {
                // No idea which one will be the right one, an OEM messed with things.
                return;
            }

            final View first = parent.getChildAt(0);
            final View second = parent.getChildAt(1);
            final View up = first.getId() == android.R.id.home ? second : first;

            if (up instanceof ImageView) {
                // Jackpot! (Probably...)
                upIndicatorView = (ImageView) up;
            }
        }
    }

    private XActionBarDrawerToggleHoneycomb() {
    }
}