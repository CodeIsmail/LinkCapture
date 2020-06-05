package dev.codeismail.linkcapture.utils

import android.content.Context
import android.util.AttributeSet
import android.widget.CheckBox
import dev.codeismail.linkcapture.R

class CustomCheckBox @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CheckBox(context, attrs, defStyleAttr) {

    override fun setChecked(checked: Boolean) {
        if(checked){
            this.background = resources.getDrawable(R.drawable.check_bg, null)
        }else{
            this.background = resources.getDrawable(R.drawable.uncheck_bg, null)
        }
        super.setChecked(checked)
    }
}