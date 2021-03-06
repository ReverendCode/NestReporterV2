package com.vaporware.nestreporterv2

import android.content.Context
import android.content.res.TypedArray
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.CheckBox
import android.os.CountDownTimer
import kotlin.reflect.KMutableProperty1


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

class EditWatcher(private val tag: KMutableProperty1<Info, String>, val function: () -> Unit): TextWatcher {
    private var timer: CountDownTimer? = null

    override fun afterTextChanged(change: Editable?) {
        timer = object : CountDownTimer(600, 100) {

            override fun onTick(millisUntilFinished: Long) {
                //nothing to do here.
            }

            override fun onFinish() {
                function
            }
        }.start()
    }
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        if (timer != null) timer!!.cancel()
    }

}
