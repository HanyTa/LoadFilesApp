package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private lateinit var selectedApp: String
    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setState(ButtonState.Completed)
        custom_button.setOnClickListener {
            download()
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadID == id) {
                if (intent.action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                    val query = DownloadManager.Query()
                    query.setFilterById(intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0));
                    val manager =
                        context!!.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    val cursor: Cursor = manager.query(query)
                    if (cursor.moveToFirst()) {
                        if (cursor.count > 0) {
                            val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                notificationManager.sendNotification(
                                    selectedApp,
                                    applicationContext,
                                    "Success"
                                )
                            } else {
                                notificationManager.sendNotification(
                                    selectedApp,
                                    applicationContext,
                                    "Failed"
                                )
                            }
                            custom_button.setState(ButtonState.Completed)
                        }
                    }
                }
            }
        }
    }

    private fun download() {
        val selectedOption = download_group!!.checkedRadioButtonId
        if (selectedOption == -1) {
            Toast.makeText(
                this@MainActivity,
                getString(R.string.empty_selection_message),
                Toast.LENGTH_LONG
            ).show()
        } else {
            custom_button.setState(ButtonState.Loading)
            notificationManager = ContextCompat.getSystemService(applicationContext, NotificationManager::class.java) as NotificationManager
            createChannel(getString(R.string.load_app_channel_id), getString(R.string.notification_channel_name))
            selectedApp = when (selectedOption) {
                R.id.glide_button -> {
                    "https://github.com/bumptech/glide/archive/master.zip"
                }
                R.id.load_app -> {
                    "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
                }
                else -> {
                    "https://github.com/square/retrofit/archive/master.zip"
                }
            }
            val request =
                DownloadManager.Request(Uri.parse(selectedApp))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        }

    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
                lightColor = Color.GRAY
                enableVibration(true)
                enableLights(true)
            }
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    companion object {
        private var URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

}
