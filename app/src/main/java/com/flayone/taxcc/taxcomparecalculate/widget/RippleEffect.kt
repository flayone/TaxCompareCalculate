package com.flayone.taxcc.taxcomparecalculate.widget

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.*
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.graphics.drawable.DrawableCompat
import android.view.View
import org.jetbrains.anko.backgroundDrawable

class RippleEffect {
    private val BOUNDS = 1500
    private val BRIGHTNESS_THRESHOLD = 180
    private val FADE_DURATION = 200

    private var rad = 0f

    private var mInstance: RippleEffect? = null

    /**
     * Destroys everything related to coloring.<br></br>
     */
    @Synchronized
    fun destroy() {
        mInstance = null
    }

    companion object {
        fun newInstance():RippleEffect = RippleEffect()
    }
    /**
     * Returns the singleton factory object.
     *
     * @return The only available `Coloring`
     */
    @Synchronized
    fun get(): RippleEffect {
        if (mInstance == null) {
            mInstance = RippleEffect()
        }
        return mInstance as RippleEffect
    }

    //自定义设置 GradientDrawable 中 CornerRadius属性的默认值
    fun getRad(): Float {
        return rad
    }

    fun setRad(rad: Float) {
        this.rad = rad
    }

    /* **********  Factory methods go below this line  ********** */

    /**
     * Converts a String hex color value to an Integer color value.<br></br>
     * <br></br>
     * **Supported formats:**<br></br>
     *
     *  * #aaRRggBb
     *  * 0xaaRRggBb
     *  * 0XaaRRggBb
     *  * #RRggBb
     *  * 0xRRggBb
     *  * 0XRRggBb
     *
     * @param colorString String value of the desired color
     * @return Integer value for the color, or gray if something goes wrong
     */
    private fun decodeColor(colorString: String?): Int {
        var cs = colorString
        if (cs == null || cs.trim { it <= ' ' }.isEmpty())
            return Color.GRAY

        if (cs.startsWith("#"))
            cs = cs.replace("#", "")

        if (cs.startsWith("0x"))
            cs = cs.replace("0x", "")

        if (cs.startsWith("0X"))
            cs = cs.replace("0X", "")

        val alpha: Int
        val red: Int
        val green: Int
        val blue: Int

        try {
            when {
                cs.length == 8 -> {
                    alpha = Integer.parseInt(cs.substring(0, 2), 16)
                    red = Integer.parseInt(cs.substring(2, 4), 16)
                    green = Integer.parseInt(cs.substring(4, 6), 16)
                    blue = Integer.parseInt(cs.substring(6, 8), 16)
                }
                cs.length == 6 -> {
                    alpha = 255
                    red = Integer.parseInt(cs.substring(0, 2), 16)
                    green = Integer.parseInt(cs.substring(2, 4), 16)
                    blue = Integer.parseInt(cs.substring(4, 6), 16)
                }
                else -> return Color.GRAY
            }
            return Color.argb(alpha, red, green, blue)
        } catch (e: NumberFormatException) {
            return Color.GRAY
        }

    }


    /**
     * Makes the given color a little bit darker.
     *
     * @param color Original color that needs to be darker
     * @return Darkened original color
     */
    private fun darkenColor(color: Int): Int {
        //颜色加深深度值
        val amount = 60

        var r = Color.red(color)
        var g = Color.green(color)
        var b = Color.blue(color)
        val a = Color.alpha(color)

        if (r - amount >= 0) {
            r -= amount
        } else {
            r = 0
        }

        if (g - amount >= 0) {
            g -= amount
        } else {
            g = 0
        }

        if (b - amount >= 0) {
            b -= amount
        } else {
            b = 0
        }

        return Color.argb(a, r, g, b)
    }

    /**
     * Colors the given drawable to a specified color. Uses mode SRC_ATOP.
     *
     * @param context  Which context to use
     * @param drawable Which drawable to color
     * @param color    Which color to use
     * @return A colored drawable ready for use
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun colorDrawable(context: Context, drawable: Drawable, color: Int): Drawable {
        if (drawable !is BitmapDrawable) {
            return colorUnknownDrawable(drawable, color)
        }

        val original = drawable.bitmap
        val copy = Bitmap.createBitmap(original.width, original.height, original.config)

        val paint = Paint()
        val c = Canvas(copy)
        paint.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        c.drawBitmap(original, 0f, 0f, paint)

        return BitmapDrawable(context.resources, copy)
    }


    /**
     * Tries to clone and just color filter the drawable. Uses mode SRC_ATOP.
     *
     * @param drawable Which drawable to color
     * @param color    Which color to use
     * @return A colored drawable ready for use
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun colorUnknownDrawable(drawable: Drawable, color: Int): Drawable {
        var d = drawable
        if (d is DrawableWrapper || d is android.support.v7.graphics.drawable.DrawableWrapper) {
            d = DrawableCompat.wrap(d)
            DrawableCompat.setTint(d, color)
            DrawableCompat.setTintMode(d, PorterDuff.Mode.SRC_ATOP)
            d = DrawableCompat.unwrap(d)
            return d
        } else {
            return try {
                val copy = d.constantState!!.newDrawable()
                copy.mutate()
                copy.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
                copy
            } catch (e: Exception) {
                d
            }

        }
    }


    /**
     * Creates a new `StateListDrawable` drawable. States that should be provided are "normal",<br></br>
     * "clicked" (pressed) and "checked" (selected). All states are actually integer colors.<br></br>
     * Optionally, `shouldFade` can be set to false to avoid the fading effect.<br></br>
     * <br></br>
     * Note: *[Color.TRANSPARENT] can be used to supply a transparent state.*
     *
     * @param normal     Color for the idle state
     * @param clicked    Color for the clicked/pressed state
     * @param checked    Color for the checked/selected state
     * @param shouldFade Set to true to enable the fading effect, false otherwise
     * @return A [StateListDrawable] drawable object ready for use
     */
    @SuppressLint("InlinedApi", "NewApi")
    private fun createStateDrawable(normal: Int, clicked: Int, checked: Int, shouldFade: Boolean, original: Drawable?): Drawable {
        // init state arrays
        val selectedState = intArrayOf(android.R.attr.state_selected)
        val pressedState = intArrayOf(android.R.attr.state_pressed)
        val checkedState = intArrayOf(android.R.attr.state_checked)
        val focusedState = intArrayOf(android.R.attr.state_focused)
        val activatedState = intArrayOf(android.R.attr.state_activated)


        // init normal state drawable
        //        Drawable normalDrawable = new GradientDrawable(Orientation.BOTTOM_TOP, new int[]{
        //                normal, normal
        //        }).mutate();
        //        if (normal == Color.TRANSPARENT)
        //            normalDrawable.setAlpha(0);
        //        else
        //            normalDrawable.setBounds(BOUNDS, BOUNDS, BOUNDS, BOUNDS);

        // init normal state drawable
        val normalDrawable = original!!.mutate()

        // init clicked state drawable
        val clickedDrawable = GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, intArrayOf(clicked, clicked)).mutate()
        if (clicked == Color.TRANSPARENT)
            clickedDrawable.alpha = 0
        else
            clickedDrawable.setBounds(BOUNDS, BOUNDS, BOUNDS, BOUNDS)

        // init checked state drawable
        val checkedDrawable = GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, intArrayOf(checked, checked)).mutate()
        if (checked == Color.TRANSPARENT)
            checkedDrawable.alpha = 0
        else
            checkedDrawable.setBounds(BOUNDS, BOUNDS, BOUNDS, BOUNDS)

        // init focused state drawable (use normal color)
        val focusedDrawable = GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, intArrayOf(normal, normal)).mutate()
        if (normal == Color.TRANSPARENT)
            focusedDrawable.alpha = 0
        else
            focusedDrawable.setBounds(BOUNDS, BOUNDS, BOUNDS, BOUNDS)

        //设置带有Corners属性的drawable
        if (normalDrawable is GradientDrawable) {
            //           float rad = ((GradientDrawable) normalDrawable).getCornerRadius();
            (clickedDrawable as GradientDrawable).cornerRadius = rad
            (focusedDrawable as GradientDrawable).cornerRadius = rad
            focusedDrawable.cornerRadius = rad
        }
        // prepare state list (order of adding states is important!)
        val states = StateListDrawable()
        states.addState(pressedState, clickedDrawable)
        if (!shouldFade) {
            states.addState(selectedState, clickedDrawable)
            states.addState(focusedState, focusedDrawable)
            states.addState(checkedState, checkedDrawable)
        }

        // add fade effect if applicable

        if (shouldFade) {
            states.addState(intArrayOf(), normalDrawable)
            states.setEnterFadeDuration(0)
            states.setExitFadeDuration(FADE_DURATION)
        } else {
            states.addState(activatedState, clickedDrawable)
            states.addState(intArrayOf(), normalDrawable)
        }


        return states
    }

    /**
     * Creates a new `RippleDrawable` used in Lollipop and later.
     *
     * @param rippleColor Color for the clicked, pressed and focused ripple states
     * @return A fully colored RippleDrawable instance
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun createRippleDrawable(rippleColor: Int, content: Drawable?): Drawable {
        return RippleDrawable(ColorStateList.valueOf(rippleColor), content, content)
    }


    /**
     * Very similar to [.createBackgroundDrawable], adding only one more parameter.
     *
     * @return Clipped/masked drawable instance
     */
    private fun createBackgroundDrawable(normal: Int, clicked: Int, checked: Int, shouldFade: Boolean, original: Drawable?): Drawable {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            createRippleDrawable(clicked, original)
        } else {
            createStateDrawable(normal, clicked, checked, shouldFade, original)
        }
    }

    /**
     * Similar to [.createBackgroundDrawable] but with additional `original` drawable parameter.
     *
     * @param context           Which context to use
     * @param normal            Color normal state of the drawable to this color
     * @param clickedBackground Background color of the View that will show when view is clicked
     * @param shouldFade        Set to true if the state list should have a fading effect
     * @param original          This drawable will be contrasted to the `clickedBackground` color on press
     * @return The state list drawable that is in contrast with the on-click background color
     */
    @SuppressLint("InlinedApi", "NewApi")
    private fun createContrastStateDrawable(context: Context, normal: Int, clickedBackground: Int, shouldFade: Boolean, original: Drawable?): Drawable? {
        var o = original
        if (o == null || o is StateListDrawable) {
            if (o != null) {
                o = o.current
            }

            // overridden in previous if clause, so check again
            if (o == null) {
                return null
            }
        }

        // init state arrays
        val selectedState = intArrayOf(android.R.attr.state_selected)
        val pressedState = intArrayOf(android.R.attr.state_pressed)
        val checkedState = intArrayOf(android.R.attr.state_checked)
        val activatedState = intArrayOf(android.R.attr.state_activated)


        val normalStateDrawable = colorDrawable(context, o, normal)
        val clickedStateDrawable = colorDrawable(context, o, getContrastColor(clickedBackground))
        val checkedStateDrawable = colorDrawable(context, o, getContrastColor(clickedBackground))

        // prepare state list (order of adding states is important!)
        val states = StateListDrawable()
        states.addState(pressedState, clickedStateDrawable)
        if (!shouldFade) {
            states.addState(selectedState, clickedStateDrawable)
            states.addState(checkedState, checkedStateDrawable)
        }

        // add fade effect if applicable

        if (shouldFade) {
            states.addState(intArrayOf(), normalStateDrawable)
            states.setEnterFadeDuration(0)
            states.setExitFadeDuration(FADE_DURATION)
        } else {
            states.addState(activatedState, clickedStateDrawable)
            states.addState(intArrayOf(), normalStateDrawable)
        }

        return states
    }

    /**
     * Very similar to [.createContrastStateDrawable] but
     * creates a Ripple drawable available in Lollipop.
     *
     * @param clickedBackground Background color of the View that will show when view is clicked
     * @param original          This drawable will be contrasted to the `clickedBackground` color on press
     * @return The Ripple drawable that is in contrast with the on-click background color
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun createContrastRippleDrawable(clickedBackground: Int, original: Drawable?): Drawable {
        return if (original == null) {
            createRippleDrawable(clickedBackground, original)
        } else RippleDrawable(ColorStateList.valueOf(clickedBackground), original, ColorDrawable(clickedBackground))

    }

    /**
     * Calculates the contrasted color from the given one. If the color darkness is under<br></br>
     * the [.BRIGHTNESS_THRESHOLD], contrasted color is white. If the color darkness is<br></br>
     * over the [.BRIGHTNESS_THRESHOLD], contrasted color is black.
     *
     * @param color Calculating contrasted color to this one
     * @return White or black, depending on the provided color's brightness
     */
    private fun getContrastColor(color: Int): Int {
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)

        // human eye is least sensitive to blue, then to red, then green; calculating:
        val brightness = (b + r + r + g + g + g) / 6

        return if (brightness < BRIGHTNESS_THRESHOLD)
            Color.WHITE
        else
            Color.BLACK
    }

    fun setViewRipple(vararg Views: View) {
        for (view in Views) {
            setViewRipple(view)
        }
    }

    fun setViewRipple(View: View) {
        setViewRipple(View, 0f)
    }

    fun setViewRipple(View: View, cornerRadius: Float) {
        //先做初始化，如果可以获取到
        rad = cornerRadius
        var nowColor = Color.TRANSPARENT
        //不同布局可能产生不同drawable，original代表原始drawable
        var original: Drawable = ColorDrawable(nowColor)
        //        if (View.getBackground()!=null){
        try {
            if (View.background == null){
                nowColor = Color.WHITE
                original = ColorDrawable(nowColor)
            }else {
                val bg = View.background.mutate()
                if (bg is ColorDrawable) {
                    nowColor = if (bg.color == Color.TRANSPARENT) {
                        Color.WHITE
                    } else {
                        bg.color
                    }
                    original = ColorDrawable(nowColor)
                } else if (bg is GradientDrawable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    nowColor = bg.color!!.defaultColor
                    rad = bg.cornerRadius
                    original = bg
                } else {
                    nowColor = Color.TRANSPARENT
                    original = bg
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (nowColor != Color.TRANSPARENT) {
            setViewRipple(View, nowColor, original)
        } else {
            //防止出现点击无涟漪反应的现象
            View.isClickable = true
            setViewRipple(View, Color.TRANSPARENT, ColorDrawable(Color.TRANSPARENT))
        }
    }

    /**
     * Fetch the button color for you and create drawable
     * If transparent, then set ripple or clicked state color to grey
     */
    fun setViewRipple(viewRipple: View, color: Int, original: Drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            viewRipple.background = getColorDrawable(viewRipple, color, original)
        } else {
            viewRipple.backgroundDrawable = getColorDrawable(viewRipple, color, original)
        }
    }

    fun setViewRipple(viewRipple: View?, colorString: String, original: Drawable) {
        //        int color = Color.parseColor((colorString);
        val color = decodeColor(colorString)
        viewRipple?.setBackgroundDrawable(getColorDrawable(viewRipple, color, original))
    }

    private fun getColorDrawable(viewGroup: View, color: Int, original: Drawable): Drawable {
        return if (color == Color.TRANSPARENT) {
            createBackgroundDrawable(color, Color.parseColor("#FFD8D8D8"), Color.parseColor("#FFD8D8D8"), true, original)
        } else {
            createBackgroundDrawable(color, darkenColor(color), darkenColor(color), true, original)
        }
    }

    private fun getRect(view: View): Rect {
        val l = IntArray(2)
        view.getLocationOnScreen(l)
        return Rect(l[0], l[1], l[0] + view.width, l[1] + view.height)
    }
}