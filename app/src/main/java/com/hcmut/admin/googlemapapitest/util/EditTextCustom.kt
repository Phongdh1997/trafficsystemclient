package com.hcmut.admin.googlemapapitest.util

import android.annotation.SuppressLint
import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.hcmut.admin.googlemapapitest.R
import kotlinx.android.synthetic.main.layout_custom_edit_text.view.*

class EditTextCustom : RelativeLayout {

    constructor(context: Context) : super(context) {
        initView(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        initView(context, attrs)
    }

    @SuppressLint("NewApi")
    private fun initView(context: Context, attrs: AttributeSet?) {
        View.inflate(context, R.layout.layout_custom_edit_text, this)

        context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.EditTextCustom,
                0, 0
        ).apply {
            try {
                edtMess.inputType = getInt(R.styleable.EditTextCustom_android_inputType, InputType.TYPE_CLASS_TEXT)

                edtMess.hint = getString(R.styleable.EditTextCustom_hintContent)

                edtMess.isFocusable = getBoolean(R.styleable.EditTextCustom_forcus, true)

                if (getString(R.styleable.EditTextCustom_textUnit).isNullOrEmpty()) {
                    tvUnit.visibility = View.GONE
                } else {
                    tvUnit.visibility = View.VISIBLE
                    tvUnit.hint = getString(R.styleable.EditTextCustom_textUnit)
                }
                ic_start.setImageResource(getResourceId(R.styleable.EditTextCustom_drawableStart, 0))

                ic_end.visibility = if (getResourceId(R.styleable.EditTextCustom_drawableEnd, 0) != 0) View.VISIBLE else View.GONE

                ic_end.setImageResource(getResourceId(R.styleable.EditTextCustom_drawableEnd, 0))

            } finally {
                recycle()
            }
        }
    }

    fun getText(): String {
        return edtMess?.text.toString()
    }

    fun setText(string: String){
        edtMess?.setText(string)
    }

    fun setHint(string: String) {
        edtMess?.hint = string
    }
}