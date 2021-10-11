package com.flayone.taxcc.taxcomparecalculate.widget;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;

public class XActionBarDrawerToggle implements XDrawerLayout.DrawerListener {

    /**
     * Allows an implementing Activity to return an {@link XActionBarDrawerToggle.Delegate} to use
     * with XActionBarDrawerToggle.
     */
    public interface DelegateProvider {

        /**
         * @return Delegate to use for ActionBarDrawableToggles, or null if the Activity
         * does not wish to override the default behavior.
         */
        @Nullable
        XActionBarDrawerToggle.Delegate getDrawerToggleDelegate();
    }

    public interface Delegate {

        /**
         * Set the Action Bar's up indicator drawable and content description.
         *
         * @param upDrawable     - Drawable to set as up indicator
         * @param contentDescRes - Content description to set
         */
        void setActionBarUpIndicator(Drawable upDrawable, @StringRes int contentDescRes);

        /**
         * Set the Action Bar's up indicator content description.
         *
         * @param contentDescRes - Content description to set
         */
        void setActionBarDescription(@StringRes int contentDescRes);

        /**
         * Returns the drawable to be set as up button when DrawerToggle is disabled
         */
        Drawable getThemeUpIndicator();

        /**
         * Returns the context of ActionBar
         */
        Context getActionBarThemedContext();

        /**
         * Returns whether navigation icon is visible or not.
         * Used to print warning messages in case developer forgets to set displayHomeAsUp to true
         */
        boolean isNavigationVisible();
    }

    private final XActionBarDrawerToggle.Delegate mActivityImpl;
    private final XDrawerLayout mXDrawerLayout;

    private DrawerArrowDrawable mSlider;
    private boolean mDrawerSlideAnimationEnabled = true;
    private Drawable mHomeAsUpIndicator;
    boolean mDrawerIndicatorEnabled = true;
    private boolean mHasCustomUpIndicator;
    private final int mOpenDrawerContentDescRes;
    private final int mCloseDrawerContentDescRes;
    // used in toolbar mode when DrawerToggle is disabled
    View.OnClickListener mToolbarNavigationClickListener;
    // If developer does not set displayHomeAsUp, DrawerToggle won't show up.
    // DrawerToggle logs a warning if this case is detected
    private boolean mWarnedForDisplayHomeAsUp = false;

    /**
     * Construct a new XActionBarDrawerToggle.
     *
     * <p>The given {@link Activity} will be linked to the specified {@link XDrawerLayout} and
     * its Actionbar's Up button will be set to a custom drawable.
     * <p>This drawable shows a Hamburger icon when drawer is closed and an arrow when drawer
     * is open. It animates between these two states as the drawer opens.</p>
     *
     * <p>String resources must be provided to describe the open/close drawer actions for
     * accessibility services.</p>
     *
     * @param activity                  The Activity hosting the drawer. Should have an ActionBar.
     * @param drawerLayout              The XDrawerLayout to link to the given Activity's ActionBar
     * @param openDrawerContentDescRes  A String resource to describe the "open drawer" action
     *                                  for accessibility
     * @param closeDrawerContentDescRes A String resource to describe the "close drawer" action
     *                                  for accessibility
     */
    public XActionBarDrawerToggle(Activity activity, XDrawerLayout drawerLayout,
                                 @StringRes int openDrawerContentDescRes,
                                 @StringRes int closeDrawerContentDescRes) {
        this(activity, null, drawerLayout, null, openDrawerContentDescRes,
                closeDrawerContentDescRes);
    }

    /**
     * Construct a new XActionBarDrawerToggle with a Toolbar.
     * <p>
     * The given {@link Activity} will be linked to the specified {@link XDrawerLayout} and
     * the Toolbar's navigation icon will be set to a custom drawable. Using this constructor
     * will set Toolbar's navigation click listener to toggle the drawer when it is clicked.
     * <p>
     * This drawable shows a Hamburger icon when drawer is closed and an arrow when drawer
     * is open. It animates between these two states as the drawer opens.
     * <p>
     * String resources must be provided to describe the open/close drawer actions for
     * accessibility services.
     * <p>
     * Please use {@link #XActionBarDrawerToggle(Activity, XDrawerLayout, int, int)} if you are
     * setting the Toolbar as the ActionBar of your activity.
     *
     * @param activity                  The Activity hosting the drawer.
     * @param toolbar                   The toolbar to use if you have an independent Toolbar.
     * @param drawerLayout              The XDrawerLayout to link to the given Activity's ActionBar
     * @param openDrawerContentDescRes  A String resource to describe the "open drawer" action
     *                                  for accessibility
     * @param closeDrawerContentDescRes A String resource to describe the "close drawer" action
     *                                  for accessibility
     */
    public XActionBarDrawerToggle(Activity activity, XDrawerLayout drawerLayout,
                                 Toolbar toolbar, @StringRes int openDrawerContentDescRes,
                                 @StringRes int closeDrawerContentDescRes) {
        this(activity, toolbar, drawerLayout, null, openDrawerContentDescRes,
                closeDrawerContentDescRes);
    }

    /**
     * In the future, we can make this constructor public if we want to let developers customize
     * the
     * animation.
     */
    XActionBarDrawerToggle(Activity activity, Toolbar toolbar, XDrawerLayout drawerLayout,
                          DrawerArrowDrawable slider, @StringRes int openDrawerContentDescRes,
                          @StringRes int closeDrawerContentDescRes) {
        if (toolbar != null) {
            mActivityImpl = new XActionBarDrawerToggle.ToolbarCompatDelegate(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDrawerIndicatorEnabled) {
                        toggle();
                    } else if (mToolbarNavigationClickListener != null) {
                        mToolbarNavigationClickListener.onClick(v);
                    }
                }
            });
        } else if (activity instanceof XActionBarDrawerToggle.DelegateProvider) { // Allow the Activity to provide an impl
            mActivityImpl = ((XActionBarDrawerToggle.DelegateProvider) activity).getDrawerToggleDelegate();
        } else {
            mActivityImpl = new XActionBarDrawerToggle.FrameworkActionBarDelegate(activity);
        }

        mXDrawerLayout = drawerLayout;
        mOpenDrawerContentDescRes = openDrawerContentDescRes;
        mCloseDrawerContentDescRes = closeDrawerContentDescRes;
        if (slider == null) {
            mSlider = new DrawerArrowDrawable(mActivityImpl.getActionBarThemedContext());
        } else {
            mSlider = slider;
        }

        mHomeAsUpIndicator = getThemeUpIndicator();
    }

    /**
     * Synchronize the state of the drawer indicator/affordance with the linked XDrawerLayout.
     *
     * <p>This should be called from your <code>Activity</code>'s
     * {@link Activity#onPostCreate(android.os.Bundle) onPostCreate} method to synchronize after
     * the XDrawerLayout's instance state has been restored, and any other time when the state
     * may have diverged in such a way that the XActionBarDrawerToggle was not notified.
     * (For example, if you stop forwarding appropriate drawer events for a period of time.)</p>
     */
    public void syncState() {
        if (mXDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            setPosition(1);
        } else {
            setPosition(0);
        }
        if (mDrawerIndicatorEnabled) {
            setActionBarUpIndicator(mSlider,
                    mXDrawerLayout.isDrawerOpen(GravityCompat.START) ?
                            mCloseDrawerContentDescRes : mOpenDrawerContentDescRes);
        }
    }

    /**
     * This method should always be called by your <code>Activity</code>'s
     * {@link Activity#onConfigurationChanged(android.content.res.Configuration)
     * onConfigurationChanged}
     * method.
     *
     * @param newConfig The new configuration
     */
    public void onConfigurationChanged(Configuration newConfig) {
        // Reload drawables that can change with configuration
        if (!mHasCustomUpIndicator) {
            mHomeAsUpIndicator = getThemeUpIndicator();
        }
        syncState();
    }

    /**
     * This method should be called by your <code>Activity</code>'s
     * {@link Activity#onOptionsItemSelected(android.view.MenuItem) onOptionsItemSelected} method.
     * If it returns true, your <code>onOptionsItemSelected</code> method should return true and
     * skip further processing.
     *
     * @param item the MenuItem instance representing the selected menu item
     * @return true if the event was handled and further processing should not occur
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item != null && item.getItemId() == android.R.id.home && mDrawerIndicatorEnabled) {
            toggle();
            return true;
        }
        return false;
    }

    void toggle() {
        int drawerLockMode = mXDrawerLayout.getDrawerLockMode(GravityCompat.START);
        if (mXDrawerLayout.isDrawerVisible(GravityCompat.START)
                && (drawerLockMode != XDrawerLayout.LOCK_MODE_LOCKED_OPEN)) {
            mXDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (drawerLockMode != XDrawerLayout.LOCK_MODE_LOCKED_CLOSED) {
            mXDrawerLayout.openDrawer(GravityCompat.START);
        }
    }

    /**
     * Set the up indicator to display when the drawer indicator is not
     * enabled.
     * <p>
     * If you pass <code>null</code> to this method, the default drawable from
     * the theme will be used.
     *
     * @param indicator A drawable to use for the up indicator, or null to use
     *                  the theme's default
     * @see #setDrawerIndicatorEnabled(boolean)
     */
    public void setHomeAsUpIndicator(Drawable indicator) {
        if (indicator == null) {
            mHomeAsUpIndicator = getThemeUpIndicator();
            mHasCustomUpIndicator = false;
        } else {
            mHomeAsUpIndicator = indicator;
            mHasCustomUpIndicator = true;
        }

        if (!mDrawerIndicatorEnabled) {
            setActionBarUpIndicator(mHomeAsUpIndicator, 0);
        }
    }

    /**
     * Set the up indicator to display when the drawer indicator is not
     * enabled.
     * <p>
     * If you pass 0 to this method, the default drawable from the theme will
     * be used.
     *
     * @param resId Resource ID of a drawable to use for the up indicator, or 0
     *              to use the theme's default
     * @see #setDrawerIndicatorEnabled(boolean)
     */
    public void setHomeAsUpIndicator(int resId) {
        Drawable indicator = null;
        if (resId != 0) {
            indicator = mXDrawerLayout.getResources().getDrawable(resId);
        }
        setHomeAsUpIndicator(indicator);
    }

    /**
     * @return true if the enhanced drawer indicator is enabled, false otherwise
     * @see #setDrawerIndicatorEnabled(boolean)
     */
    public boolean isDrawerIndicatorEnabled() {
        return mDrawerIndicatorEnabled;
    }

    /**
     * Enable or disable the drawer indicator. The indicator defaults to enabled.
     *
     * <p>When the indicator is disabled, the <code>ActionBar</code> will revert to displaying
     * the home-as-up indicator provided by the <code>Activity</code>'s theme in the
     * <code>android.R.attr.homeAsUpIndicator</code> attribute instead of the animated
     * drawer glyph.</p>
     *
     * @param enable true to enable, false to disable
     */
    public void setDrawerIndicatorEnabled(boolean enable) {
        if (enable != mDrawerIndicatorEnabled) {
            if (enable) {
                setActionBarUpIndicator(mSlider,
                        mXDrawerLayout.isDrawerOpen(GravityCompat.START) ?
                                mCloseDrawerContentDescRes : mOpenDrawerContentDescRes);
            } else {
                setActionBarUpIndicator(mHomeAsUpIndicator, 0);
            }
            mDrawerIndicatorEnabled = enable;
        }
    }

    /**
     * @return DrawerArrowDrawable that is currently shown by the XActionBarDrawerToggle.
     */
    @NonNull
    public DrawerArrowDrawable getDrawerArrowDrawable() {
        return mSlider;
    }

    /**
     * Sets the DrawerArrowDrawable that should be shown by this XActionBarDrawerToggle.
     *
     * @param drawable DrawerArrowDrawable that should be shown by this XActionBarDrawerToggle
     */
    public void setDrawerArrowDrawable(@NonNull DrawerArrowDrawable drawable) {
        mSlider = drawable;
        syncState();
    }

    /**
     * Specifies whether the drawer arrow should animate when the drawer position changes.
     *
     * @param enabled if this is {@code true} then the animation will run, else it will be skipped
     */
    public void setDrawerSlideAnimationEnabled(boolean enabled) {
        mDrawerSlideAnimationEnabled = enabled;
        if (!enabled) {
            setPosition(0);
        }
    }

    /**
     * @return whether the drawer slide animation is enabled
     */
    public boolean isDrawerSlideAnimationEnabled() {
        return mDrawerSlideAnimationEnabled;
    }

    /**
     * {@link XDrawerLayout.DrawerListener} callback method. If you do not use your
     * XActionBarDrawerToggle instance directly as your XDrawerLayout's listener, you should call
     * through to this method from your own listener object.
     *
     * @param drawerView  The child view that was moved
     * @param slideOffset The new offset of this drawer within its range, from 0-1
     */
    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        if (mDrawerSlideAnimationEnabled) {
            setPosition(Math.min(1f, Math.max(0, slideOffset)));
        } else {
            setPosition(0); // disable animation.
        }
    }

    /**
     * {@link XDrawerLayout.DrawerListener} callback method. If you do not use your
     * XActionBarDrawerToggle instance directly as your XDrawerLayout's listener, you should call
     * through to this method from your own listener object.
     *
     * @param drawerView Drawer view that is now open
     */
    @Override
    public void onDrawerOpened(View drawerView) {
        setPosition(1);
        if (mDrawerIndicatorEnabled) {
            setActionBarDescription(mCloseDrawerContentDescRes);
        }
    }

    /**
     * {@link XDrawerLayout.DrawerListener} callback method. If you do not use your
     * XActionBarDrawerToggle instance directly as your XDrawerLayout's listener, you should call
     * through to this method from your own listener object.
     *
     * @param drawerView Drawer view that is now closed
     */
    @Override
    public void onDrawerClosed(View drawerView) {
        setPosition(0);
        if (mDrawerIndicatorEnabled) {
            setActionBarDescription(mOpenDrawerContentDescRes);
        }
    }

    /**
     * {@link XDrawerLayout.DrawerListener} callback method. If you do not use your
     * XActionBarDrawerToggle instance directly as your XDrawerLayout's listener, you should call
     * through to this method from your own listener object.
     *
     * @param newState The new drawer motion state
     */
    @Override
    public void onDrawerStateChanged(int newState) {
    }

    /**
     * Returns the fallback listener for Navigation icon click events.
     *
     * @return The click listener which receives Navigation click events from Toolbar when
     * drawer indicator is disabled.
     * @see #setToolbarNavigationClickListener(android.view.View.OnClickListener)
     * @see #setDrawerIndicatorEnabled(boolean)
     * @see #isDrawerIndicatorEnabled()
     */
    public View.OnClickListener getToolbarNavigationClickListener() {
        return mToolbarNavigationClickListener;
    }

    /**
     * When DrawerToggle is constructed with a Toolbar, it sets the click listener on
     * the Navigation icon. If you want to listen for clicks on the Navigation icon when
     * DrawerToggle is disabled ({@link #setDrawerIndicatorEnabled(boolean)}, you should call this
     * method with your listener and DrawerToggle will forward click events to that listener
     * when drawer indicator is disabled.
     *
     * @see #setDrawerIndicatorEnabled(boolean)
     */
    public void setToolbarNavigationClickListener(
            View.OnClickListener onToolbarNavigationClickListener) {
        mToolbarNavigationClickListener = onToolbarNavigationClickListener;
    }

    void setActionBarUpIndicator(Drawable upDrawable, int contentDescRes) {
        if (!mWarnedForDisplayHomeAsUp && !mActivityImpl.isNavigationVisible()) {
            Log.w("XActionBarDrawerToggle", "DrawerToggle may not show up because NavigationIcon"
                    + " is not visible. You may need to call "
                    + "actionbar.setDisplayHomeAsUpEnabled(true);");
            mWarnedForDisplayHomeAsUp = true;
        }
        mActivityImpl.setActionBarUpIndicator(upDrawable, contentDescRes);
    }

    void setActionBarDescription(int contentDescRes) {
        mActivityImpl.setActionBarDescription(contentDescRes);
    }

    Drawable getThemeUpIndicator() {
        return mActivityImpl.getThemeUpIndicator();
    }

    private void setPosition(float position) {
        if (position == 1f) {
            mSlider.setVerticalMirror(true);
        } else if (position == 0f) {
            mSlider.setVerticalMirror(false);
        }
        mSlider.setProgress(position);
    }

    private static class FrameworkActionBarDelegate implements XActionBarDrawerToggle.Delegate {
        private final Activity mActivity;
        private XActionBarDrawerToggleHoneycomb.SetIndicatorInfo mSetIndicatorInfo;

        FrameworkActionBarDelegate(Activity activity) {
            mActivity = activity;
        }

        @Override
        public Drawable getThemeUpIndicator() {
            if (Build.VERSION.SDK_INT >= 18) {
                final TypedArray a = getActionBarThemedContext().obtainStyledAttributes(null,
                        new int[] {android.R.attr.homeAsUpIndicator},
                        android.R.attr.actionBarStyle, 0);
                final Drawable result = a.getDrawable(0);
                a.recycle();
                return result;
            }
            return XActionBarDrawerToggleHoneycomb.getThemeUpIndicator(mActivity);
        }

        @Override
        public Context getActionBarThemedContext() {
            final ActionBar actionBar = mActivity.getActionBar();
            if (actionBar != null) {
                return actionBar.getThemedContext();
            }
            return mActivity;
        }

        @Override
        public boolean isNavigationVisible() {
            final ActionBar actionBar = mActivity.getActionBar();
            return actionBar != null
                    && (actionBar.getDisplayOptions() & ActionBar.DISPLAY_HOME_AS_UP) != 0;
        }

        @Override
        public void setActionBarUpIndicator(Drawable themeImage, int contentDescRes) {
            final ActionBar actionBar = mActivity.getActionBar();
            if (actionBar != null) {
                if (Build.VERSION.SDK_INT >= 18) {
                    actionBar.setHomeAsUpIndicator(themeImage);
                    actionBar.setHomeActionContentDescription(contentDescRes);
                } else {
                    actionBar.setDisplayShowHomeEnabled(true);
                    mSetIndicatorInfo = XActionBarDrawerToggleHoneycomb.setActionBarUpIndicator(
                            mActivity, themeImage, contentDescRes);
                    actionBar.setDisplayShowHomeEnabled(false);
                }
            }
        }

        @Override
        public void setActionBarDescription(int contentDescRes) {
            if (Build.VERSION.SDK_INT >= 18) {
                final ActionBar actionBar = mActivity.getActionBar();
                if (actionBar != null) {
                    actionBar.setHomeActionContentDescription(contentDescRes);
                }
            } else {
                mSetIndicatorInfo = XActionBarDrawerToggleHoneycomb.setActionBarDescription(
                        mSetIndicatorInfo, mActivity, contentDescRes);
            }
        }
    }

    /**
     * Used when DrawerToggle is initialized with a Toolbar
     */
    static class ToolbarCompatDelegate implements XActionBarDrawerToggle.Delegate {

        final Toolbar mToolbar;
        final Drawable mDefaultUpIndicator;
        final CharSequence mDefaultContentDescription;

        ToolbarCompatDelegate(Toolbar toolbar) {
            mToolbar = toolbar;
            mDefaultUpIndicator = toolbar.getNavigationIcon();
            mDefaultContentDescription = toolbar.getNavigationContentDescription();
        }

        @Override
        public void setActionBarUpIndicator(Drawable upDrawable, @StringRes int contentDescRes) {
            mToolbar.setNavigationIcon(upDrawable);
            setActionBarDescription(contentDescRes);
        }

        @Override
        public void setActionBarDescription(@StringRes int contentDescRes) {
            if (contentDescRes == 0) {
                mToolbar.setNavigationContentDescription(mDefaultContentDescription);
            } else {
                mToolbar.setNavigationContentDescription(contentDescRes);
            }
        }

        @Override
        public Drawable getThemeUpIndicator() {
            return mDefaultUpIndicator;
        }

        @Override
        public Context getActionBarThemedContext() {
            return mToolbar.getContext();
        }

        @Override
        public boolean isNavigationVisible() {
            return true;
        }
    }
}
