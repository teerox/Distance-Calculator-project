package com.example.riby_distance_calculator.screen

import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ProgressBar
import com.example.riby_distance_calculator.R
import com.example.riby_distance_calculator.utils.Permissions.getLocationPermission


class SplashScreenActivity : AppCompatActivity() {

    private var SPLASH_TIME = 3000 //This is 3 seconds
    private var splashProgress: ProgressBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        getLocationPermission(this)
        splashProgress = findViewById(R.id.splashProgress)
        playProgress()
        //imageView1.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this,R.anim.anim))
        Handler().postDelayed({
            //Do any action here. Now we are moving to next page
            val mySuperIntent = Intent(this, MainActivity::class.java)
            startActivity(mySuperIntent)
            //This 'finish()' is for exiting the app when back button pressed from Home page which is ActivityHome
            finish()
        }, SPLASH_TIME.toLong())
    }

    //Method to run progress bar for 5 seconds
    private fun playProgress() {
        ObjectAnimator.ofInt(splashProgress, "progress", 100)
            .setDuration(3000)
            .start()
    }
}
