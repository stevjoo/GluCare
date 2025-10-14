package com.example.mapmidtermproject.settings

import android.os.Bundle
import android.view.View
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mapmidtermproject.R

class FAQActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faq)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        setupAccordion(
            findViewById(R.id.header1),
            findViewById(R.id.answer1),
            findViewById(R.id.arrow1)
        )
        setupAccordion(
            findViewById(R.id.header2),
            findViewById(R.id.answer2),
            findViewById(R.id.arrow2)
        )
        setupAccordion(
            findViewById(R.id.header3),
            findViewById(R.id.answer3),
            findViewById(R.id.arrow3)
        )
    }

    private fun setupAccordion(header: LinearLayout, answer: TextView, arrow: ImageView) {
        header.setOnClickListener {
            val visible = answer.visibility == View.VISIBLE
            answer.visibility = if (visible) View.GONE else View.VISIBLE

            val fromDeg = if (visible) 180f else 0f
            val toDeg = if (visible) 0f else 180f
            val rotate = RotateAnimation(
                fromDeg, toDeg,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f
            )
            rotate.duration = 200
            rotate.fillAfter = true
            arrow.startAnimation(rotate)
        }
    }
}
