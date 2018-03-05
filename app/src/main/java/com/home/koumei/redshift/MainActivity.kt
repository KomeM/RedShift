package com.home.koumei.redshift

import android.app.Activity
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : Activity() {

    // FilterServiceと共有する変数
    companion object {
        var red = 80
        var brown = 80
        var filterKey = ":filter"
        var redKey = ":red"
        var brownKey = ":brown"
    }

    // filterがついてるかどうか
    private var filter = false

    //保存領域へのアクセス
    var prefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // FilterServiceへのIntent
        val intent by lazy { Intent(this, FilterService::class.java) }

        // このアプリケーションへのIntent
        val myIntent by lazy { Intent(applicationContext, MainActivity::class.java) }

        // 通知をクリックした時にアプリを起動するように設定
        val contentIntent by lazy { PendingIntent.getActivity(
                                applicationContext,
                                0,
                                 myIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
        ) }
        val builder by lazy { NotificationCompat.Builder(applicationContext)
                                .setSmallIcon(R.mipmap.redshift)
                                .setContentTitle("Red Shift は動作中です")
                                .setContentText("タップして表示")
                                .setContentIntent(contentIntent)}
        val myNotificationManager by lazy { NotificationManagerCompat.from(applicationContext) }

        setContentView(R.layout.activity_main)

        // 前回の終了時の変数を取り出す
        prefs = getSharedPreferences("DataSave", Context.MODE_PRIVATE)
        red = prefs!!.getInt(redKey, 80)
        brown = prefs!!.getInt(brownKey, 80)
        filter = prefs!!.getBoolean(filterKey, false)


        /*フィルターがかかってるなら、かかってる時の状態にする*/
        if (filter) {
            filter_button.setImageDrawable(getDrawable(R.mipmap.redshift_short2))
            filter_name.text = "Red Shift"
            filter_name.isChecked = true

            // 通知の開始、通知はキルされない
            val notification = builder.build()
            notification.flags = Notification.FLAG_ONGOING_EVENT
            myNotificationManager.notify(0, notification)
        }

        filter_name.setOnClickListener {
            /*On -> Off*/
            if (filter) {
                // フィルターを停止させ、停止した時の動作
                filter_button.setImageDrawable(getDrawable(R.mipmap.redshift_short))
                filter_name.text = "No Filter"
                stopService(intent)
                filter = false

                // 通知を止める
                myNotificationManager.cancel(0)
            }
            /*Off -> On*/
            else {
                // フィルターを貼り、フィルター時の動作
                filter_button.setImageDrawable(getDrawable(R.mipmap.redshift_short2))
                filter_name.text = "Red Shift"
                startService(intent)
                filter = true

                // 通知の開始、通知はキルされない
                val notification = builder.build()
                notification.flags = Notification.FLAG_ONGOING_EVENT
                myNotificationManager.notify(0, notification)
            }
        }

        // Seekbarの動作
        red_seek.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(p0: SeekBar?)
                = Unit

            override fun onStopTrackingTouch(p0: SeekBar?)
                = Unit

            /*SeekBarの値が変更された時*/
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

                // 変更値を取得して保存
                red_value.text = String.format(Locale.US, "%d", p1*100/255) + "%"
                red = p1
                prefs!!.edit().putInt(redKey, red).apply()

                // フィルターが動いてるなら反映
                if (filter) {
                    startService(intent)
                }
            }
        })

        // Seekbarの動作
        brown_seek.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(p0: SeekBar?)
                = Unit

            override fun onStopTrackingTouch(p0: SeekBar?)
                = Unit

            /*SeekBarの値が変更された時*/
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

                // 変更値を取得して保存
                brown_value.text = String.format(Locale.US, "%d", p1*100/255) + "%"
                brown = p1
                prefs!!.edit().putInt(brownKey, brown).apply()

                // フィルターが動いてるなら反映
                if (filter){
                    startService(intent)
                }
            }
        })

        // seekbarの値を元に戻すことで、自動的にフィルターの値が変更される
        red_seek.progress = red
        brown_seek.progress = brown

        /*デフォルトボタンが押された時の動作*/
        red_default.setOnClickListener {
            red_seek.progress = 80
        }
        brown_default.setOnClickListener {
            brown_seek.progress = 80
        }

    }

    /*アプリが閉じる前に、現在の状態が保存される*/
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        // 赤とalpha値は保存してあるため、filterの状態のみ保存
        prefs!!.edit().putBoolean(filterKey, filter).apply()

    }


}

