package com.udacity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)


        file_name_value.text = intent.extras?.getString("name")
        status_value.text = intent.extras?.getString("status")


        done_button.setOnClickListener {
            startActivity(Intent(this@DetailActivity,MainActivity::class.java))
            finish()
        }


    }

}
