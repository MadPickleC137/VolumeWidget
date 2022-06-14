package com.madpickle.volumewidget.broadcastReceiver

/**
 * Created by David Madilyan on 19.02.2022.
 */
interface VolumeChangeListener {
    fun onInitAllValues(alarm: Int, music: Int, system: Int, voice: Int)
    fun onChangeAlarm(value: Int?)
    fun onChangeMusic(value: Int?)
    fun onChangeSystem(value: Int?)
    fun onChangeVoice(value: Int?)
}