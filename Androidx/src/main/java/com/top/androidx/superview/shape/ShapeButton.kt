package com.top.androidx.superview.shape

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton

import com.top.androidx.superview.data.AttributeSetData
import com.top.androidx.superview.helper.AttributeSetHelper
import com.top.androidx.superview.helper.ShapeBuilder


class ShapeButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AppCompatButton(context, attrs, defStyleAttr) {
    var shapeBuilder: ShapeBuilder? = null
    var attributeSetData: AttributeSetData = AttributeSetData()

    init {
        attributeSetData = AttributeSetHelper().loadFromAttributeSet(context, attrs)
        shapeBuilder = ShapeBuilder()
        shapeBuilder?.init(this, attributeSetData)
    }
}