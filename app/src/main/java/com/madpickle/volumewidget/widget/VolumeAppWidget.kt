package com.madpickle.volumewidget.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Build
import android.widget.RemoteViews
import com.madpickle.volumewidget.ActionTypes
import com.madpickle.volumewidget.R
import com.madpickle.volumewidget.broadcastReceiver.VolumeChangeListener
import com.madpickle.volumewidget.broadcastReceiver.VolumeChangeReceiver


/**
 * Implementation of App Widget functionality.
 */
class VolumeAppWidget : AppWidgetProvider() {
    private var volumeReceiver: VolumeChangeReceiver? = null
    private val mediaFilter = IntentFilter(Intent.ACTION_MEDIA_BUTTON)
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {}

    override fun onEnabled(context: Context) {
        mediaFilter.addAction("android.media.VOLUME_CHANGED_ACTION")
        mediaFilter.addAction("android.intent.action.MEDIA_BUTTON")
        val appWidget = ComponentName(context, VolumeAppWidget::class.java)
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val updateManager = UpdateManagerWidget(context, appWidgetManager, appWidget)
        volumeReceiver = VolumeChangeReceiver(context, updateManager)
        volumeReceiver?.let {
            context.applicationContext.registerReceiver(volumeReceiver, mediaFilter)
        }
    }

    override fun onDisabled(context: Context) {
        volumeReceiver?.let {
            context.applicationContext.unregisterReceiver(volumeReceiver)
        }
    }

    override fun onReceive(context: Context, intent: Intent?) {
        super.onReceive(context, intent)
        if(intent != null) {
            val appWidget = ComponentName(context, VolumeAppWidget::class.java)
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val views = RemoteViews(context.packageName, R.layout.volume_app_widget)
            val audio = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            fun updateAddStreamValue(streamType: Int, idView: Int){
                val curValueStream = audio.getStreamVolume(streamType)
                val maxValue = audio.getStreamMaxVolume(streamType)
                audio.setStreamVolume(streamType,
                    getNewValueForAdd(curValueStream, maxValue),
                    AudioManager.FLAG_PLAY_SOUND)
                views.setTextViewText(idView,
                    getNewValueForAdd(curValueStream, maxValue).toString())
                appWidgetManager.updateAppWidget(appWidget, views)
            }
            fun updateTurnStreamValue(streamType: Int, idView: Int){
                val curValueStream = audio.getStreamVolume(streamType)
                audio.setStreamVolume(streamType,
                    getVewValueForTurn(curValueStream),
                    AudioManager.FLAG_PLAY_SOUND)
                views.setTextViewText(idView,
                    getVewValueForTurn(curValueStream).toString())
                appWidgetManager.updateAppWidget(appWidget, views)
            }
            when(intent.action){
                ActionTypes.ACTION_ADD_ALARM.name -> updateAddStreamValue(AudioManager.STREAM_ALARM, R.id.alarm_text_counter)
                ActionTypes.ACTION_TURN_ALARM.name -> updateTurnStreamValue(AudioManager.STREAM_ALARM, R.id.alarm_text_counter)
                ActionTypes.ACTION_ADD_MUSIC.name -> updateAddStreamValue(AudioManager.STREAM_MUSIC, R.id.music_text_counter)
                ActionTypes.ACTION_TURN_MUSIC.name -> updateTurnStreamValue(AudioManager.STREAM_MUSIC, R.id.music_text_counter)
                ActionTypes.ACTION_ADD_SYSTEM.name -> updateAddStreamValue(AudioManager.STREAM_SYSTEM, R.id.system_text_counter)
                ActionTypes.ACTION_TURN_SYSTEM.name -> updateTurnStreamValue(AudioManager.STREAM_SYSTEM, R.id.system_text_counter)
                ActionTypes.ACTION_ADD_RING.name -> updateAddStreamValue(AudioManager.STREAM_RING, R.id.voice_text_counter)
                ActionTypes.ACTION_TURN_RING.name -> updateTurnStreamValue(AudioManager.STREAM_RING, R.id.voice_text_counter)
            }
        }
    }

    /**
     * need new calculate method for updating add counter range 10
     * */
    private fun getNewValueForAdd(curValue: Int, maxValue: Int): Int {
        return if(curValue < maxValue){
            curValue + 1
        }else{
            maxValue
        }
    }

    private fun getVewValueForTurn(curValue: Int): Int {
        return if(curValue  > 0){
            curValue - 1
        } else {
            0
        }
    }
}

class UpdateManagerWidget(
    private val ctx: Context,
    private val appWidgetManager: AppWidgetManager,
    private val appWidgetName: ComponentName
): VolumeChangeListener{

    init {
        initButtonsEvents()
    }

    private fun initButtonsEvents() {
        val views = RemoteViews(ctx.packageName, R.layout.volume_app_widget)
        views.setOnClickPendingIntent(R.id.ic_add_alarm_button, getPendingIntent(ActionTypes.ACTION_ADD_ALARM.name))
        views.setOnClickPendingIntent(R.id.ic_turn_alarm_button, getPendingIntent(ActionTypes.ACTION_TURN_ALARM.name))

        views.setOnClickPendingIntent(R.id.ic_add_music_button, getPendingIntent(ActionTypes.ACTION_ADD_MUSIC.name))
        views.setOnClickPendingIntent(R.id.ic_turn_music_button, getPendingIntent(ActionTypes.ACTION_TURN_MUSIC.name))

        views.setOnClickPendingIntent(R.id.ic_add_system_button, getPendingIntent(ActionTypes.ACTION_ADD_SYSTEM.name))
        views.setOnClickPendingIntent(R.id.ic_turn_system_button, getPendingIntent(ActionTypes.ACTION_TURN_SYSTEM.name))

        views.setOnClickPendingIntent(R.id.ic_add_voice_button, getPendingIntent(ActionTypes.ACTION_ADD_RING.name))
        views.setOnClickPendingIntent(R.id.ic_turn_voice_button, getPendingIntent(ActionTypes.ACTION_TURN_RING.name))
        appWidgetManager.updateAppWidget(appWidgetName, views)
    }


    private fun getPendingIntent(action: String): PendingIntent {
        return  Intent(ctx, VolumeAppWidget::class.java).let { intent ->
            intent.action = action
            return@let if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getBroadcast(ctx, 0, intent, PendingIntent.FLAG_MUTABLE)
            } else {
                PendingIntent.getBroadcast(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            }
        }
    }

    override fun onInitAllValues(alarm: Int, music: Int, system: Int, voice: Int) {
        val views = RemoteViews(ctx.packageName, R.layout.volume_app_widget)
        views.setTextViewText(R.id.alarm_text_counter, alarm.toString())
        views.setTextViewText(R.id.music_text_counter, music.toString())
        views.setTextViewText(R.id.system_text_counter, system.toString())
        views.setTextViewText(R.id.voice_text_counter, voice.toString())
        val appWidget = ComponentName(ctx, VolumeAppWidget::class.java)
        appWidgetManager.updateAppWidget(appWidget, views)
    }

    override fun onChangeAlarm(value: Int?) {
        value?.let { updateTextViewValue(it, R.id.alarm_text_counter) }
    }

    override fun onChangeMusic(value: Int?) {
        value?.let { updateTextViewValue(it, R.id.music_text_counter) }
    }

    override fun onChangeSystem(value: Int?) {
        value?.let { updateTextViewValue(it, R.id.system_text_counter) }
    }

    override fun onChangeVoice(value: Int?) {
        value?.let { updateTextViewValue(it, R.id.voice_text_counter) }
    }

    private fun updateTextViewValue(value: Int, textViewId: Int) {
        val views = RemoteViews(ctx.packageName, R.layout.volume_app_widget)
        views.setTextViewText(textViewId, value.toString())
        appWidgetManager.updateAppWidget(appWidgetName, views)
    }

}