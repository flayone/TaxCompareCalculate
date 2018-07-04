package com.flayone.taxcc.taxcomparecalculate;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableWrapper;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;

import static android.graphics.PorterDuff.Mode.SRC_ATOP;

/**
 * Created by liyayu on 2018/2/8.
 */
public class Coloring {

    private static final int BOUNDS = 1500;
    private static final int BRIGHTNESS_THRESHOLD = 180;
    private static final int FADE_DURATION = 200;

    private float rad = 0;

    private static final Object mInitializerLock;

    private static Coloring mInstance;
    static {
        mInitializerLock = new Object();
    }

    /**
     * Destroys everything related to coloring.<br>
     */
    public static synchronized void destroy() {
        mInstance = null;
    }

    /**
     * Returns the singleton factory object.
     *
     * @return The only available {@code Coloring}
     */
    public static Coloring get() {
        if (mInstance == null) {
            synchronized (mInitializerLock) {
                if (mInstance == null) {
                    mInstance = new Coloring();
                }
            }
        }
        return mInstance;
    }

    //自定义设置 GradientDrawable 中 CornerRadius属性的默认值
    public float getRad() {
        return rad;
    }

    public void setRad(float rad) {
        this.rad = rad;
    }

    /* **********  Factory methods go below this line  ********** */

    /**
     * Converts a String hex color value to an Integer color value.<br>
     * <br>
     * <b>Supported formats:</b><br>
     * <ul>
     * <li>#aaRRggBb</li>
     * <li>0xaaRRggBb</li>
     * <li>0XaaRRggBb</li>
     * <li>#RRggBb</li>
     * <li>0xRRggBb</li>
     * <li>0XRRggBb</li>
     * </ul>
     *
     * @param colorString String value of the desired color
     * @return Integer value for the color, or gray if something goes wrong
     */
    public int decodeColor(String colorString) {
        if (colorString == null || colorString.trim().isEmpty())
            return Color.GRAY;

        if (colorString.startsWith("#"))
            colorString = colorString.replace("#", "");

        if (colorString.startsWith("0x"))
            colorString = colorString.replace("0x", "");

        if (colorString.startsWith("0X"))
            colorString = colorString.replace("0X", "");

        int alpha = 255, red = 255, green = 255, blue = 255;

        try {
            if (colorString.length() == 8) {
                alpha = Integer.parseInt(colorString.substring(0, 2), 16);
                red = Integer.parseInt(colorString.substring(2, 4), 16);
                green = Integer.parseInt(colorString.substring(4, 6), 16);
                blue = Integer.parseInt(colorString.substring(6, 8), 16);
            } else if (colorString.length() == 6) {
                alpha = 255;
                red = Integer.parseInt(colorString.substring(0, 2), 16);
                green = Integer.parseInt(colorString.substring(2, 4), 16);
                blue = Integer.parseInt(colorString.substring(4, 6), 16);
            } else {
                return Color.GRAY;
            }
            return Color.argb(alpha, red, green, blue);
        } catch (NumberFormatException e) {
            return Color.GRAY;
        }
    }

    /**
     * Blends given color with white background. This means that a full color<br>
     * with transparency (alpha) will be lightened to make it look like it is<br>
     * rendered over a white background. Resulting color will be non-transparent.
     *
     * @param color Color to use for blending
     * @return Lightened color to match a white underlay render
     */
    public int alphaBlendWithWhite(int color) {
        float alpha = Color.alpha(color) / 255f;
        int origR = Color.red(color);
        int origG = Color.green(color);
        int origB = Color.blue(color);
        int white = 255;

        // rule: outputRed = (foregroundRed * foregroundAlpha) + (backgroundRed * (1.0 - foregroundAlpha))
        int r = (int) ((origR * alpha) + (white * (1.0 - alpha)));
        if (r > 255)
            r = 255;
        int g = (int) ((origG * alpha) + (white * (1.0 - alpha)));
        if (g > 255)
            g = 255;
        int b = (int) ((origB * alpha) + (white * (1.0 - alpha)));
        if (b > 255)
            b = 255;

        return Color.argb(255, r, g, b);
    }

    /**
     * Makes the given color a little bit darker.
     *
     * @param color Original color that needs to be darker
     * @return Darkened original color
     */
    public int darkenColor(int color) {
        //颜色加深深度值
        int amount = 60;

        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        int a = Color.alpha(color);

        if (r - amount >= 0) {
            r -= amount;
        } else {
            r = 0;
        }

        if (g - amount >= 0) {
            g -= amount;
        } else {
            g = 0;
        }

        if (b - amount >= 0) {
            b -= amount;
        } else {
            b = 0;
        }

        return Color.argb(a, r, g, b);
    }

    /**
     * Makes the given color a little bit lighter.
     *
     * @param color Original color that needs to be lighter
     * @return Lightened original color
     */
    public int lightenColor(int color) {
        int amount = 60;

        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        int a = Color.alpha(color);

        if (r + amount <= 255) {
            r += amount;
        } else {
            r = 255;
        }

        if (g + amount <= 255) {
            g += amount;
        } else {
            g = 255;
        }

        if (b + amount <= 255) {
            b += amount;
        } else {
            b = 255;
        }

        return Color.argb(a, r, g, b);
    }

    /**
     * Creates a new drawable (implementation of the Drawable object may vary depending on OS version).<br>
     * Drawable will be colored with given color, and clipped to match given boundaries.
     *
     * @param color  Integer color used to color the output drawable
     * @param bounds Four-dimensional vector bounds
     * @return Colored and clipped drawable object
     */
    @SuppressWarnings("UnusedDeclaration")
    public Drawable createDrawable(int color, Rect bounds) {
        // init normal state drawable
        Drawable drawable = new GradientDrawable(Orientation.BOTTOM_TOP, new int[]{
                color, color
        }).mutate();
        if (color == Color.TRANSPARENT) {
            drawable.setAlpha(0);
        }
        drawable.setBounds(bounds);
        return drawable;
    }

    /**
     * Colors the given drawable to a specified color. Uses mode SRC_ATOP.
     *
     * @param context  Which context to use
     * @param drawable Which drawable to color
     * @param color    Which color to use
     * @return A colored drawable ready for use
     */
    public Drawable colorDrawable(Context context, Drawable drawable, int color) {
        if (!(drawable instanceof BitmapDrawable)) {
            return colorUnknownDrawable(drawable, color);
        }

        Bitmap original = ((BitmapDrawable) drawable).getBitmap();
        Bitmap copy = Bitmap.createBitmap(original.getWidth(), original.getHeight(), original.getConfig());

        Paint paint = new Paint();
        Canvas c = new Canvas(copy);
        paint.setColorFilter(new PorterDuffColorFilter(color, SRC_ATOP));
        c.drawBitmap(original, 0, 0, paint);

        return new BitmapDrawable(context.getResources(), copy);
    }

    /**
     * Colors the given drawable to a specified color set using the drawable wrapping technique.
     *
     * @param drawable    Which drawable to color
     * @param colorStates Which color set to use
     * @return A colored drawable ready to use
     */
    public Drawable colorDrawableWrap(Drawable drawable, ColorStateList colorStates) {
        if (drawable != null) {
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTintList(drawable, colorStates);
            DrawableCompat.setTintMode(drawable, SRC_ATOP);
            drawable = DrawableCompat.unwrap(drawable);
            return drawable;
        }
        return null;
    }

    /**
     * Colors the given drawable to a specified color using the drawable wrapping technique.
     *
     * @param drawable Which drawable to color
     * @param color    Which color to use
     * @return A colored drawable ready to use
     */
    public Drawable colorDrawableWrap(Drawable drawable, int color) {
        if (drawable != null) {
            Drawable wrapped = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(wrapped, color);
            DrawableCompat.setTintMode(wrapped, SRC_ATOP);
            return DrawableCompat.unwrap(wrapped);
        }
        return null;
    }

    /**
     * Tries to clone and just color filter the drawable. Uses mode SRC_ATOP.
     *
     * @param drawable Which drawable to color
     * @param color    Which color to use
     * @return A colored drawable ready for use
     */
    @SuppressWarnings("RedundantCast")
    public Drawable colorUnknownDrawable(Drawable drawable, int color) {
        if (drawable instanceof DrawableWrapper || drawable instanceof android.support.v7.graphics.drawable.DrawableWrapper) {
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, color);
            DrawableCompat.setTintMode(drawable, SRC_ATOP);
            drawable = DrawableCompat.unwrap(drawable);
            return drawable;
        } else {
            try {
                Drawable copy = drawable.getConstantState().newDrawable();
                copy.mutate();
                copy.setColorFilter(color, SRC_ATOP);
                return copy;
            } catch (Exception e) {
                return drawable;
            }
        }
    }

    /**
     * Colors the given drawable to a specified color. Uses mode SRC_ATOP.<br>
     * Automatically loads a good quality bitmap from the {@code resourceId} if it is valid.
     *
     * @param context    Which context to use
     * @param resourceId Which drawable resource to load
     * @param color      Which color to use
     * @return A colored {@link Drawable} ready for use
     */
    public Drawable colorDrawable(Context context, int resourceId, int color) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inDither = false; // disable dithering
        //noinspection deprecation
        opts.inPurgeable = true; // allocate pixels that could be freed by the system
        //noinspection deprecation
        opts.inInputShareable = true; // see javadoc
        opts.inTempStorage = new byte[32 * 1024]; // temp storage - advice is to use 16K
        opts.inPreferQualityOverSpeed = false;

        Bitmap original = BitmapFactory.decodeResource(context.getResources(), resourceId, opts);
        return colorDrawable(context, new BitmapDrawable(context.getResources(), original), color);
    }

    /**
     * Creates a new {@code StateListDrawable} drawable. States that should be provided are "normal",<br>
     * "clicked" (pressed) and "checked" (selected). All states are actually integer colors.<br>
     * Optionally, {@code shouldFade} can be set to false to avoid the fading effect.<br>
     * <br>
     * Note: <i>{@link Color#TRANSPARENT} can be used to supply a transparent state.</i>
     *
     * @param normal     Color for the idle state
     * @param clicked    Color for the clicked/pressed state
     * @param checked    Color for the checked/selected state
     * @param shouldFade Set to true to enable the fading effect, false otherwise
     * @return A {@link StateListDrawable} drawable object ready for use
     */
    @SuppressLint({
            "InlinedApi", "NewApi"
    })
    public Drawable createStateDrawable(int normal, int clicked, int checked, boolean shouldFade, Drawable original) {
        // init state arrays
        int[] selectedState = new int[]{
                android.R.attr.state_selected
        };
        int[] pressedState = new int[]{
                android.R.attr.state_pressed
        };
        int[] checkedState = new int[]{
                android.R.attr.state_checked
        };
        int[] focusedState = new int[]{
                android.R.attr.state_focused
        };
        int[] activatedState = new int[]{
                android.R.attr.state_activated
        };


        // init normal state drawable
//        Drawable normalDrawable = new GradientDrawable(Orientation.BOTTOM_TOP, new int[]{
//                normal, normal
//        }).mutate();
//        if (normal == Color.TRANSPARENT)
//            normalDrawable.setAlpha(0);
//        else
//            normalDrawable.setBounds(BOUNDS, BOUNDS, BOUNDS, BOUNDS);

        // init normal state drawable
        Drawable normalDrawable = original.mutate();

        // init clicked state drawable
        Drawable clickedDrawable = new GradientDrawable(Orientation.BOTTOM_TOP, new int[]{
                clicked, clicked
        }).mutate();
        if (clicked == Color.TRANSPARENT)
            clickedDrawable.setAlpha(0);
        else
            clickedDrawable.setBounds(BOUNDS, BOUNDS, BOUNDS, BOUNDS);

        // init checked state drawable
        Drawable checkedDrawable = new GradientDrawable(Orientation.BOTTOM_TOP, new int[]{
                checked, checked
        }).mutate();
        if (checked == Color.TRANSPARENT)
            checkedDrawable.setAlpha(0);
        else
            checkedDrawable.setBounds(BOUNDS, BOUNDS, BOUNDS, BOUNDS);

        // init focused state drawable (use normal color)
        Drawable focusedDrawable = new GradientDrawable(Orientation.BOTTOM_TOP, new int[]{
                normal, normal
        }).mutate();
        if (normal == Color.TRANSPARENT)
            focusedDrawable.setAlpha(0);
        else
            focusedDrawable.setBounds(BOUNDS, BOUNDS, BOUNDS, BOUNDS);

        //设置带有Corners属性的drawable
        if (normalDrawable instanceof GradientDrawable){
//           float rad = ((GradientDrawable) normalDrawable).getCornerRadius();
            ((GradientDrawable)clickedDrawable).setCornerRadius(rad);
            ((GradientDrawable)focusedDrawable).setCornerRadius(rad);
            ((GradientDrawable)focusedDrawable).setCornerRadius(rad);
        }
        // prepare state list (order of adding states is important!)
        StateListDrawable states = new StateListDrawable();
        states.addState(pressedState, clickedDrawable);
        if (!shouldFade) {
            states.addState(selectedState, clickedDrawable);
            states.addState(focusedState, focusedDrawable);
            states.addState(checkedState, checkedDrawable);
        }

        // add fade effect if applicable

        if (shouldFade) {
            states.addState(new int[]{}, normalDrawable);
            states.setEnterFadeDuration(0);
            states.setExitFadeDuration(FADE_DURATION);
        } else {
            states.addState(activatedState, clickedDrawable);
            states.addState(new int[]{}, normalDrawable);
        }


        return states;
    }

    /**
     * Creates a new {@code RippleDrawable} used in Lollipop's ListView.
     *
     * @param context     Which context to use
     * @param rippleColor Color for the clicked, pressed and focused ripple states
     * @return A fully colored RippleDrawable instance
     */
//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    public Drawable createListViewRipple(Context context, int rippleColor) {
//        RippleDrawable ripple;
//        ripple = (RippleDrawable) context.getResources().getDrawable(R.drawable.ripple_test, null);
//        if (ripple != null) {
//            ripple.setColor(ColorStateList.valueOf(rippleColor));
//        }
//        return ripple;
//    }

    /**
     * Creates a new {@code RippleDrawable} used in Lollipop and later.
     *
     * @param normalColor Color for the idle ripple state
     * @param rippleColor Color for the clicked, pressed and focused ripple states
     * @param bounds      Clip/mask drawable to these rectangle bounds
     * @return A fully colored RippleDrawable instance
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Drawable createRippleDrawable(int normalColor, int rippleColor, Rect bounds, Drawable content) {
        return new RippleDrawable(ColorStateList.valueOf(rippleColor), content, content);
//        ColorDrawable maskDrawable = null;
//        if (bounds != null) {
//            maskDrawable = new ColorDrawable(Color.WHITE);
//            maskDrawable.setBounds(bounds);
//        }
//
//        if (normalColor == Color.TRANSPARENT) {
//            return new RippleDrawable(ColorStateList.valueOf(rippleColor), content, maskDrawable);
//        } else {
//            return new RippleDrawable(ColorStateList.valueOf(rippleColor), content, maskDrawable);
//        }
    }

    /**
     * Creates a new drawable using given parameters. States that should be provided are "normal",<br>
     * "clicked" (pressed) and "checked" (selected). All states are actually integer colors.<br>
     * Optionally, {@code shouldFade} can be set to false to avoid the fading effect.<br>
     * Depending on API level, Drawable instance will be a Ripple drawable (Lollipop) or StateListDrawable.<br>
     * <br>
     * Note: <i>{@link Color#TRANSPARENT} can be used to supply a transparent state.</i>
     *
     * @param normal     Color for the idle state
     * @param clicked    Color for the clicked/pressed state
     * @param checked    Color for the checked/selected state
     * @param shouldFade Set to true to enable the fading effect, false otherwise
     * @return A {@link StateListDrawable} drawable object ready for use
     */
    public Drawable createBackgroundDrawable(int normal, int clicked, int checked, boolean shouldFade) {
        return createBackgroundDrawable(normal, clicked, checked, shouldFade, null, null);
    }

    /**
     * Very similar to {@link #createBackgroundDrawable(int, int, int, boolean)}, adding only one more parameter.
     *
     * @param bounds Clip/mask drawable to these rectangle bounds
     * @return Clipped/masked drawable instance
     */
    public Drawable createBackgroundDrawable(int normal, int clicked, int checked, boolean shouldFade, Rect bounds, Drawable original) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return createRippleDrawable(normal, clicked, bounds, original);
        } else {
            return createStateDrawable(normal, clicked, checked, shouldFade, original);
        }
    }

    /**
     * Similar to {@link #createContrastStateDrawable(Context, int, int, boolean, Drawable)} but using colors
     * only, no drawables.
     *
     * @param normal            Color normal state to this color
     * @param clickedBackground Background color of the View that will show when view is clicked
     * @return The color state list that is in contrast with the on-click background color
     */
    @SuppressLint({
            "InlinedApi", "NewApi"
    })
    public ColorStateList createContrastStateColors(int normal, int clickedBackground) {
        // init state arrays
        int[] normalState = new int[]{};
        int[] selectedState = new int[]{
                android.R.attr.state_selected
        };
        int[] pressedState = new int[]{
                android.R.attr.state_pressed
        };
        int[] checkedState = new int[]{
                android.R.attr.state_checked
        };
        int[] activatedState = new int[]{};

        activatedState = new int[]{
                android.R.attr.state_activated
        };


        // initialize identifiers
        int[] stateColors;
        int[][] stateIdentifiers;
        int contrastColor = getContrastColor(clickedBackground);


        stateIdentifiers = new int[][]{
                selectedState, pressedState, checkedState, activatedState, normalState
        };
        stateColors = new int[]{
                contrastColor, contrastColor, contrastColor, contrastColor, normal
        };


        return new ColorStateList(stateIdentifiers, stateColors);
    }

    /**
     * Similar to {@link #createBackgroundDrawable(int, int, int, boolean)} but with additional {@code original} drawable parameter.
     *
     * @param context           Which context to use
     * @param normal            Color normal state of the drawable to this color
     * @param clickedBackground Background color of the View that will show when view is clicked
     * @param shouldFade        Set to true if the state list should have a fading effect
     * @param original          This drawable will be contrasted to the {@code clickedBackground} color on press
     * @return The state list drawable that is in contrast with the on-click background color
     */
    @SuppressLint({
            "InlinedApi", "NewApi"
    })
    public Drawable createContrastStateDrawable(Context context, int normal, int clickedBackground, boolean shouldFade, Drawable original) {
        if (original == null || original instanceof StateListDrawable) {
            if (original != null) {
                original = original.getCurrent();
            }

            // overridden in previous if clause, so check again
            if (original == null) {
                return null;
            }
        }

        // init state arrays
        int[] selectedState = new int[]{
                android.R.attr.state_selected
        };
        int[] pressedState = new int[]{
                android.R.attr.state_pressed
        };
        int[] checkedState = new int[]{
                android.R.attr.state_checked
        };
        int[] activatedState = new int[]{};

        activatedState = new int[]{
                android.R.attr.state_activated
        };


        Drawable normalStateDrawable = colorDrawable(context, original, normal);
        Drawable clickedStateDrawable = colorDrawable(context, original, getContrastColor(clickedBackground));
        Drawable checkedStateDrawable = colorDrawable(context, original, getContrastColor(clickedBackground));

        // prepare state list (order of adding states is important!)
        StateListDrawable states = new StateListDrawable();
        states.addState(pressedState, clickedStateDrawable);
        if (!shouldFade) {
            states.addState(selectedState, clickedStateDrawable);
            states.addState(checkedState, checkedStateDrawable);
        }

        // add fade effect if applicable

        if (shouldFade) {
            states.addState(new int[]{}, normalStateDrawable);
            states.setEnterFadeDuration(0);
            states.setExitFadeDuration(FADE_DURATION);
        } else {
            states.addState(activatedState, clickedStateDrawable);
            states.addState(new int[]{}, normalStateDrawable);
        }


        return states;
    }

    /**
     * Very similar to {@link #createContrastStateDrawable(Context context, int, int, boolean, Drawable)} but
     * creates a Ripple drawable available in Lollipop.
     *
     * @param normal            Color normal state of the drawable to this color
     * @param clickedBackground Background color of the View that will show when view is clicked
     * @param original          This drawable will be contrasted to the {@code clickedBackground} color on press
     * @return The Ripple drawable that is in contrast with the on-click background color
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Drawable createContrastRippleDrawable(int normal, int clickedBackground, Drawable original) {
        if (original == null) {
            return createRippleDrawable(normal, clickedBackground, null, original);
        }

        return new RippleDrawable(ColorStateList.valueOf(clickedBackground), original, new ColorDrawable(clickedBackground));
    }

    /**
     * This basically chooses between {@link #createContrastStateDrawable(Context, int, int, boolean, Drawable)}
     * and {@link #createContrastRippleDrawable(int, int, Drawable)} depending on the available API level.
     *
     * @param context           Which context to use
     * @param normal            Color normal state of the drawable to this color
     * @param clickedBackground Background color of the View that will show when view is clicked
     * @param shouldFade        Set to true if the state list (pre-API 21) should have a fading effect
     * @param original          This drawable will be contrasted to the {@code clickedBackground} color on press (pre-API 21) or used for masking in
     *                          ripples on post-API 21
     * @return The state list drawable (< API21) or a ripple drawable (>= API21) that is in contrast with the on-click background color
     */
    public Drawable createContrastBackgroundDrawable(Context context, int normal, int clickedBackground, boolean shouldFade,
                                                     Drawable original) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return createContrastRippleDrawable(normal, clickedBackground, original);
        } else {
            return createContrastStateDrawable(context, normal, clickedBackground, shouldFade, original);
        }
    }

    /**
     * Calculates the contrasted color from the given one. If the color darkness is under<br>
     * the {@link #BRIGHTNESS_THRESHOLD}, contrasted color is white. If the color darkness is<br>
     * over the {@link #BRIGHTNESS_THRESHOLD}, contrasted color is black.
     *
     * @param color Calculating contrasted color to this one
     * @return White or black, depending on the provided color's brightness
     */
    public int getContrastColor(int color) {
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        // human eye is least sensitive to blue, then to red, then green; calculating:
        int brightness = (b + r + r + g + g + g) / 6;

        if (brightness < BRIGHTNESS_THRESHOLD)
            return Color.WHITE;
        else
            return Color.BLACK;
    }

    public void setViewRipple(View... Views) {
        for (View view : Views) {
            setViewRipple(view);
        }
    }
    public void setViewRipple(View View) {
        setViewRipple(View,0);
    }

    public void setViewRipple(View View,float cornerRadius) {
        //先做初始化，如果可以获取到
        rad = cornerRadius;
        int nowColor = 0;
        //不同布局可能产生不同drawable，original代表原始drawable
        Drawable original = new ColorDrawable(nowColor);
//        if (View.getBackground()!=null){
        try {
            Drawable bg = View.getBackground().mutate();
            if (bg instanceof ColorDrawable) {
                nowColor = ((ColorDrawable) bg).getColor();
                original = bg;
            } else if (bg instanceof GradientDrawable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                nowColor = ((GradientDrawable) bg).getColor().getDefaultColor();
                rad = ((GradientDrawable) bg).getCornerRadius();
                original = bg;
            } else {
                nowColor = Color.TRANSPARENT;
                original = bg;
            }
        } catch (Exception e) {
            e.printStackTrace();
            nowColor = Color.TRANSPARENT;
        }
        if (nowColor != 0) {
            setViewRipple(View, nowColor, original);
        } else {
            //防止出现点击无涟漪反应的现象
            View.setClickable(true);
            setViewRipple(View, Color.TRANSPARENT, original);
        }
    }

    /**
     * Fetch the button color for you and create drawable
     * If transparent, then set ripple or clicked state color to grey
     */
    public void setViewRipple(View viewRipple, int color, Drawable original) {
        if (viewRipple != null) {
            viewRipple.setBackgroundDrawable(getColorDrawable(viewRipple, color, original));
        }
    }

    public void setViewRipple(View viewRipple, String colorString, Drawable original) {
//        int color = Color.parseColor((colorString);
        int color = decodeColor(colorString);
        if (viewRipple != null) {
            viewRipple.setBackgroundDrawable(getColorDrawable(viewRipple, color, original));
        }
    }

    private Drawable getColorDrawable(View viewGroup, int color, Drawable original) {
        Drawable drawable;
        if (color == Color.TRANSPARENT) {
            drawable = this.createBackgroundDrawable(color, Color.parseColor("#FFD8D8D8"), Color.parseColor("#FFD8D8D8"), true, getRect(viewGroup), original);
        } else {
            drawable = this.createBackgroundDrawable(color, this.darkenColor(color), this.darkenColor(color), true, getRect(viewGroup), original);
        }
        return drawable;
    }

    private Rect getRect(View view) {
        int[] l = new int[2];
        view.getLocationOnScreen(l);
        return new Rect(l[0], l[1], l[0] + view.getWidth(), l[1] + view.getHeight());
    }
}
