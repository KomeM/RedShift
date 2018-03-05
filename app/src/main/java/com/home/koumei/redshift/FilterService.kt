package com.home.koumei.redshift

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.*
import com.home.koumei.redshift.MainActivity.Companion.brown
import com.home.koumei.redshift.MainActivity.Companion.brownKey
import com.home.koumei.redshift.MainActivity.Companion.red
import com.home.koumei.redshift.MainActivity.Companion.redKey


/**
 * Created by koumei on 18/02/26.
 */

class FilterService: Service(){

    // オーバーレイヤ
    private val overlayView: ViewGroup by lazy { LayoutInflater.from(this).inflate(R.layout.filter, null) as ViewGroup }

    // 保存領域にアクセスする変数
    var prefs: SharedPreferences? = null

    // WindowManager
    private val windowManager: WindowManager by lazy { applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager }

    // window parameter
    val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    or WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            PixelFormat.TRANSLUCENT
    )

    /*何も作成されていない時に呼び出される*/
    override fun onCreate() {
        super.onCreate()

        //保存領域にアクセスし変数を取得
        prefs = getSharedPreferences("DataSave", Context.MODE_PRIVATE)
        red = prefs!!.getInt(redKey, 80)
        brown = prefs!!.getInt(brownKey, 80)

        // Viewの色合いの変更をおこない、Viewを加える
        overlayView.setBackgroundColor(Color.argb(brown, red, 0, 0))
        windowManager.addView(overlayView, params)
    }

    /*StartServiceの実行時に呼び出される*/
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        // Viewの変更の更新のみ行う
        overlayView.setBackgroundColor(Color.argb(brown, red, 0, 0))
        windowManager.updateViewLayout(overlayView, params)

        return START_STICKY_COMPATIBILITY
    }


    /*stopServiceの実行時に呼び出される*/
    override fun onDestroy() {
        super.onDestroy()

        // Viewの破棄と赤とalpha値の保存
        windowManager.removeView(overlayView)
        prefs!!.edit().putInt(redKey, red).apply()
        prefs!!.edit().putInt(brownKey, brown).apply()
    }

    override fun onBind(p0: Intent?): IBinder? = null



}