package com.madpickle.volumewidget

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Bundle
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import com.madpickle.volumewidget.broadcastReceiver.VolumeChangeListener
import com.madpickle.volumewidget.broadcastReceiver.VolumeChangeReceiver
import com.madpickle.volumewidget.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), VolumeChangeListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var volumeReceiver: VolumeChangeReceiver
    private val mediaFilter = IntentFilter(Intent.ACTION_MEDIA_BUTTON)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mediaFilter.addAction("android.media.VOLUME_CHANGED_ACTION")
        mediaFilter.addAction("android.intent.action.MEDIA_BUTTON")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        volumeReceiver = VolumeChangeReceiver(this, this)
        registerReceiver(volumeReceiver, mediaFilter)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(volumeReceiver)
    }

    private fun initViews() {
        val mAudio = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        initControlsType(binding.alarm, AudioManager.STREAM_ALARM, mAudio)
        initControlsType(binding.music, AudioManager.STREAM_MUSIC, mAudio)
        initControlsType(binding.system, AudioManager.STREAM_SYSTEM, mAudio)
        initControlsType(binding.voice, AudioManager.STREAM_VOICE_CALL, mAudio)
    }
    private fun initControlsType(seek: SeekBar, stream: Int, mAudio: AudioManager) {
        seek.max = mAudio.getStreamMaxVolume(stream)
        seek.progress = mAudio.getStreamVolume(stream)
        seek.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(bar: SeekBar, progress: Int, fromUser: Boolean) {
                mAudio.setStreamVolume(stream, progress, AudioManager.FLAG_PLAY_SOUND)
            }

            override fun onStartTrackingTouch(bar: SeekBar) {}
            override fun onStopTrackingTouch(bar: SeekBar) {}
        })
    }

    override fun onInitAllValues(alarm: Int, music: Int, system: Int, voice: Int) {
        binding.alarm.progress = alarm
        binding.music.progress = music
        binding.system.progress = system
        binding.voice.progress = voice
    }

    override fun onChangeAlarm(value: Int?) {
        binding.alarm.post {
            if (value != null) {
                binding.alarm.progress = value
            }
        }
    }

    override fun onChangeMusic(value: Int?) {
        binding.music.post {
            if (value != null) {
                binding.music.progress = value
            }
        }
    }

    override fun onChangeSystem(value: Int?) {
        binding.system.post {
            if (value != null) {
                binding.system.progress = value
            }
        }
    }

    override fun onChangeVoice(value: Int?) {
        binding.voice.post {
            if (value != null) {
                binding.voice.progress = value
            }
        }
    }
}