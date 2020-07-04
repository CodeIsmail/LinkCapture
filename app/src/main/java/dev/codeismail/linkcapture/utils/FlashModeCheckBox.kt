package dev.codeismail.linkcapture.utils

import android.content.Context
import android.util.AttributeSet
import android.widget.CheckBox
import dev.codeismail.linkcapture.R

class FlashModeCheckBox @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CheckBox(context, attrs, defStyleAttr) {

    override fun setChecked(checked: Boolean) {
        super.setChecked(checked)
        if(checked){
            this.background = resources.getDrawable(R.drawable.ic_flash, null)
        }else{
            this.background = resources.getDrawable(R.drawable.ic_flash_off, null)
        }
    }
}