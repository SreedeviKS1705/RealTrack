package com.qaptive.core.ui.customviews

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class IconView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    AppCompatTextView(context, attrs, defStyleAttr)
{
    private val iconTypeface : Typeface by lazy {
        Typeface.createFromAsset(context.assets,"lqticon.ttf")
    }

    init {
        this.typeface = iconTypeface
    }
}