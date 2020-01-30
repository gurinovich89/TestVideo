package com.example.testvideo

import android.animation.ValueAnimator
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    /*val ALPHA = object : Property<Float, View>(Float::class.java, "alpha") {
        override fun setValue(`object`: View, value: Float) {
            `object`.alpha = value
        }

        override fun get(`object`: View): Float {
            return `object`.alpha
        }
    };*/

    var progressValue: Int = 100
    lateinit var progressValueAnimator: ValueAnimator

    val progressRunnable = Runnable {
        progressBar?.progress = progressValue
        progressValue += 5
        //progressBar.postDelayed(this.progressRunnable, 50)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnStart.setOnClickListener { showVideo() }
        videoView.setZOrderOnTop(true)
        //videoView.setBackgroundColor(Color.WHITE)
        //progressBar.postDelayed(progressRunnable, 50L)

        //ObjectAnimator.ofFloat(progressBar, View.ALPHA, 0, 1).start();


        progressValueAnimator = ValueAnimator.ofInt(0, 1000)
        progressValueAnimator.duration = 30000
        progressValueAnimator.interpolator = LinearInterpolator()
        progressValueAnimator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            progressBar.setProgress(value)
            Log.i("west", "value=$value")
        }
        btnProgressPause.setOnClickListener { pauseOrResumeProgress() }

        /*val progressAnimator = ObjectAnimator.ofFloat(progressBar, "progress", 0.0f, 1000.0f);
        progressAnimator.setDuration(7000)
        progressAnimator.start()*/
    }

    private fun pauseOrResumeProgress() {
        if (!progressValueAnimator.isStarted) {
            progressValueAnimator.start()
            return
        }
        if (progressValueAnimator.isPaused) {
            progressValueAnimator.resume()
        } else {
            progressValueAnimator.pause()
        }
    }

    private fun showVideo() {
        //videoView.setVideoURI(Uri.parse("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"))
        //videoView.setVideoURI(Uri.parse("http://techslides.com/demos/sample-videos/small.mp4"))
        //videoView.setVideoURI(Uri.parse("https://docs.google.com/uc?export=download&id=1F96l6NPOcWyku23IEQ0WmfTV2XYIMYvy"))
        videoView.setVideoURI(Uri.parse("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4"))
        //videoView.setVideoURI(Uri.parse("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4"))
        videoView.setOnPreparedListener(object : MediaPlayer.OnPreparedListener {
            override fun onPrepared(mp: MediaPlayer?) {
                //videoView.setBackgroundColor(Color.TRANSPARENT)
            }
        })
        videoView.setOnCompletionListener(object : MediaPlayer.OnCompletionListener {
            override fun onCompletion(mp: MediaPlayer?) {
                Toast.makeText(applicationContext, "Video completed", Toast.LENGTH_LONG).show()
                Log.i("west_onCompleted", "Video completed currentPosition=" + mp?.currentPosition)
            }
        })
        videoView.setOnInfoListener(object : MediaPlayer.OnInfoListener {
            override fun onInfo(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
                val infoMessage = when (what) {
                    MediaPlayer.MEDIA_INFO_BUFFERING_START -> "MEDIA_INFO_BUFFERING_START"
                    MediaPlayer.MEDIA_INFO_BUFFERING_END -> "MEDIA_INFO_BUFFERING_END"
                    else -> what.toString()
                }

                Log.i(
                    "west_onInfo",
                    "what=$what,extra=$extra,duration=${mp?.duration},currentPosition=${mp?.currentPosition},info=$infoMessage"
                )
                return false
            }
        })
        videoView.setOnErrorListener { mp, what, extra ->
            Log.i("westSetOnErrorListener", "Can't play this video")
            true
        }
        videoView.start()
    }
}
