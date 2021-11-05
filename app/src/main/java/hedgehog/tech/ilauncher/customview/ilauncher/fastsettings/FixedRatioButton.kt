package hedgehog.tech.ilauncher.customview.ilauncher.fastsettings

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import hedgehog.tech.ilauncher.R

class FixedRatioButton: androidx.appcompat.widget.AppCompatCheckBox {

    private var iconStateEnabled: Drawable? = null
    private var iconStateDisabled: Drawable? = null
    private var iconSize: Float = 0.33F

    // Background is required

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int): super(context, attrs, defStyle) {
        initIconDrawable(attrs)
    }
    constructor(context: Context, attrs: AttributeSet?): super(context, attrs) {
        initIconDrawable(attrs)
    }

    private fun initIconDrawable(attrs: AttributeSet?) {
        if(attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.FixedRatioButton)

            if(a.hasValue(R.styleable.FixedRatioButton_buttonIcon)) {
                iconStateEnabled = a.getDrawable(R.styleable.FixedRatioButton_buttonIcon)!!
            }

            if(a.hasValue(R.styleable.FixedRatioButton_buttonIconDisabled)) {
                iconStateDisabled = a.getDrawable(R.styleable.FixedRatioButton_buttonIconDisabled)!!
            }

            if(a.hasValue(R.styleable.FixedRatioButton_buttonIconDisabled)) {
                iconSize = a.getFloat(R.styleable.FixedRatioButton_buttonIconSize, 0.33F)
            }

            a.recycle()
        }

        buttonDrawable = null
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val iconToSet = (
                     if(isChecked) iconStateEnabled
                     else iconStateDisabled
                ) ?: iconStateEnabled

        iconToSet?.also { icon ->
            if(canvas != null) {

                // if button is disabled -> make icon 40% transparent
                icon.alpha = if(isEnabled) 255 else 102

                val padding = ((width - width*iconSize)/2).toInt()

                // For saving initial icon ratio
                val initialImageRatio = icon.intrinsicHeight/(icon.intrinsicWidth).toFloat()
                val paddingVertical = ((width - width*iconSize*initialImageRatio)/2).toInt()

                icon.setBounds(0 + padding,
                        0 + paddingVertical,
                        width - padding,
                        width - paddingVertical)

                icon.draw(canvas)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }

    override fun performClick(): Boolean {
        isChecked = !isChecked
        return super.performClick()
    }
}