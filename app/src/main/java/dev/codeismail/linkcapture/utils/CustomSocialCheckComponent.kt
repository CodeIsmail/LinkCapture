package dev.codeismail.linkcapture.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.CompoundButton
import androidx.constraintlayout.widget.ConstraintLayout
import dev.codeismail.linkcapture.R
import kotlinx.android.synthetic.main.layout_social_check_component.view.*


class CustomSocialCheckComponent @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val socialName: String
    private var isSocialSelected: Boolean
    private val socialImage: Drawable

    init {
        val inflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        inflater.inflate(R.layout.layout_social_check_component, this)
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CustomSocialCheckComponent,
            0, 0).apply {

            try {
                isSocialSelected = getBoolean(R.styleable.CustomSocialCheckComponent_isSocialSelected, false)
                socialImage = getDrawable(R.styleable.CustomSocialCheckComponent_socialImage)!!
                socialName = getString(R.styleable.CustomSocialCheckComponent_socialName)!!
            } finally {
                recycle()
            }
        }
        socialText.text = socialName
        socialIcon.setImageDrawable(socialImage)
        socialCheck.isChecked = isSocialSelected
    }

    fun setCheckState(state : Boolean){
        socialCheck.isChecked = state
    }

    fun getCheckState(): Boolean = socialCheck.isChecked
}