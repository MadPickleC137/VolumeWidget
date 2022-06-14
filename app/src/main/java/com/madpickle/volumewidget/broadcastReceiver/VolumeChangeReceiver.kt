package com.madpickle.volumewidget.broadcastReceiver

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Handler
import android.os.Looper


/**
 * Created by David Madilyan on 19.02.2022.
 *
 */
class VolumeChangeReceiver(ctx: Context, private val listener: VolumeChangeListener): BroadcastReceiver() {

    private var alarmPreValue = 0
    private var musicPreValue = 0
    private var systemPreValue = 0
    private var voicePreValue = 0

    private val audio = ctx.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    init {
        alarmPreValue = audio.getStreamVolume(AudioManager.STREAM_ALARM)
        musicPreValue = audio.getStreamVolume(AudioManager.STREAM_MUSIC)
        systemPreValue = audio.getStreamVolume(AudioManager.STREAM_SYSTEM)
        voicePreValue = audio.getStreamVolume(AudioManager.STREAM_RING)
        listener.onInitAllValues(alarmPreValue, musicPreValue, systemPreValue, voicePreValue)
    }

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        val alarmCurValue = audio.getStreamVolume(AudioManager.STREAM_ALARM)
        val musicCurValue = audio.getStreamVolume(AudioManager.STREAM_MUSIC)
        val systemCurValue = audio.getStreamVolume(AudioManager.STREAM_SYSTEM)
        val voiceCurValue = audio.getStreamVolume(AudioManager.STREAM_RING)
        if(alarmCurValue - alarmPreValue != 0){
            alarmPreValue = checkOnChange(alarmCurValue) { listener.onChangeAlarm(alarmCurValue) }
        }
        if(musicCurValue - musicPreValue != 0){
            musicPreValue = checkOnChange(musicCurValue) { listener.onChangeMusic(musicCurValue) }
        }
        if(systemCurValue - systemPreValue != 0){
            systemPreValue = checkOnChange(systemCurValue) { listener.onChangeSystem(systemCurValue) }
        }
        if(voiceCurValue - voicePreValue != 0){
            voicePreValue = checkOnChange(voiceCurValue) { listener.onChangeVoice(voiceCurValue) }
        }
    }

    private fun checkOnChange(alarmCurValue: Int, function: () -> Unit): Int {
        Handler(Looper.getMainLooper()).post {
            function()
        }
        return  alarmCurValue
    }
}