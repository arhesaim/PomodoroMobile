package com.example.myapplication

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.*

class MainActivity : AppCompatActivity() {

    private var timeSelected: Int = 0
    private var timeCountDown: CountDownTimer? = null
    private var timeProgress = 0
    private var pauseOffSet: Long = 0
    private var isStart = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val addBtn: TextView = findViewById(R.id.btnAdd)
        addBtn.setOnClickListener {
            setTimeFunction()
        }
        val startBtn: Button = findViewById(R.id.btnPlayPause)
        startBtn.setOnClickListener {
            startTimerSetup()
        }

        val resetBtn: TextView = findViewById(R.id.ib_reset)
        resetBtn.setOnClickListener {
            resetTime()
        }

        val addTimeTv: TextView = findViewById(R.id.tv_addTime)
        addTimeTv.setOnClickListener {
            addExtraTime()
        }
    }

    private fun addExtraTime() {
        val progressBar: ProgressBar = findViewById(R.id.pbTimer)
        if (timeSelected != 0) {
            timeSelected += 5 // Add 5 minutes
            progressBar.max = timeSelected * 60
            timePause()
            startTimer(pauseOffSet)
            Toast.makeText(this, "5 minutes added", Toast.LENGTH_SHORT).show()
        }
    }

    private fun resetTime() {
        if (timeCountDown != null) {
            timeCountDown!!.cancel()
            timeProgress = 0
            timeSelected = 0
            pauseOffSet = 0
            timeCountDown = null
            val startBtn: Button = findViewById(R.id.btnPlayPause)
            startBtn.text = "Start"
            isStart = true
            val progressBar = findViewById<ProgressBar>(R.id.pbTimer)
            progressBar.progress = 0
            val timeLeftTv: TextView = findViewById(R.id.tvTimeLeft)
            timeLeftTv.text = "0 min"
        }
    }

    private fun timePause() {
        if (timeCountDown != null) {
            timeCountDown!!.cancel()
        }
    }

    private fun startTimerSetup() {
        val startBtn: Button = findViewById(R.id.btnPlayPause)
        if (timeSelected * 60 > timeProgress) {
            if (isStart) {
                startBtn.text = "Pause"
                startTimer(pauseOffSet)
                isStart = false
            } else {
                isStart = true
                startBtn.text = "Resume"
                timePause()
            }
        } else {
            Toast.makeText(this, "Enter Time", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startTimer(pauseOffSetL: Long) {
        val progressBar = findViewById<ProgressBar>(R.id.pbTimer)
        progressBar.progress = timeProgress
        timeCountDown = object : CountDownTimer(
            (timeSelected * 60 * 1000).toLong() - pauseOffSetL * 1000, 1000
        ) {
            override fun onTick(p0: Long) {
                timeProgress++
                pauseOffSet = timeSelected.toLong() * 60 - p0 / 1000
                progressBar.progress = timeSelected * 60 - timeProgress
                val timeLeftTv: TextView = findViewById(R.id.tvTimeLeft)
                val minutesLeft = (timeSelected * 60 - timeProgress) / 60
                timeLeftTv.text = "$minutesLeft min"
            }

            override fun onFinish() {
                resetTime()
                Toast.makeText(this@MainActivity, "Times Up!", Toast.LENGTH_SHORT).show()
            }

        }.start()
    }

    private fun setTimeFunction() {
        val timeDialog = Dialog(this)
        timeDialog.setContentView(R.layout.add_dialog)
        val timeSet = timeDialog.findViewById<EditText>(R.id.etGetTime)
        val timeLeftTv: TextView = findViewById(R.id.tvTimeLeft)
        val btnStart: Button = findViewById(R.id.btnPlayPause)
        val progressBar = findViewById<ProgressBar>(R.id.pbTimer)
        timeDialog.findViewById<Button>(R.id.btnOk).setOnClickListener {
            if (timeSet.text.isEmpty()) {
                Toast.makeText(this, "Enter Time Duration", Toast.LENGTH_SHORT).show()
            } else {
                resetTime()
                val minutes = timeSet.text.toString().toInt()
                timeLeftTv.text = "$minutes min"
                btnStart.text = "Start"
                timeSelected = minutes
                progressBar.max = timeSelected * 60
            }
            timeDialog.dismiss()
        }
        timeDialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (timeCountDown != null) {
            timeCountDown?.cancel()
            timeProgress = 0
        }
    }
}