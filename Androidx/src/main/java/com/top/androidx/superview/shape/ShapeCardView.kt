package com.top.androidx.superview.shape

import android.content.Context
import android.util.AttributeSet
import androidx.cardview.widget.CardView

import com.top.androidx.superview.data.AttributeSetData
import com.top.androidx.superview.helper.AttributeSetHelper
import com.top.androidx.superview.helper.ShapeBuilder


class ShapeCardView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : CardView(context, attrs, defStyleAttr) {
    var shapeBuilder: ShapeBuilder? = null
    var attributeSetData: AttributeSetData = AttributeSetData()

    init {
        attributeSetData = AttributeSetHelper().loadFromAttributeSet(context, attrs)
        shapeBuilder = ShapeBuilder()
        shapeBuilder?.init(this, attributeSetData)
    }
}