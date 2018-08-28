package com.vaporware.nestreporterv2

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.widget.CheckBox
import android.widget.RadioGroup

//extending these classes allow for custom xml elements, allowing us to keep Db column info in one place
class SCheckBox(context: Context, attributeSet: AttributeSet): CheckBox(context, attributeSet) {
    lateinit var column: CharSequence
    init {
        val array: TypedArray = context.obtainStyledAttributes(attributeSet,R.styleable.SCheckBox)
        val n = array.indexCount
        for (i in 0..n) {
            val attr = array.getIndex(i)
            if (attr == R.styleable.SCheckBox_column) column = array.getString(i)
        }
        array.recycle()
    }
}

class SRadioGroup(context: Context, attributeSet: AttributeSet): RadioGroup(context, attributeSet)