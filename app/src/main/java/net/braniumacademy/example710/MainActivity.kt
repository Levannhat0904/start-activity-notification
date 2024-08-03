package net.braniumacademy.example710

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private var notificationId: Int = 1
    private lateinit var containerLayout: View
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            setupNotification()
        } else {
            Snackbar.make(
                containerLayout, R.string.txt_permission_denied,
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }
    private lateinit var editNotificationContent: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createNotificationChannel()
        editNotificationContent = findViewById(R.id.edit_notification_content)
        val btnPostNotification = findViewById<Button>(R.id.btn_post_notification)
        btnPostNotification.setOnClickListener {
            checkPostNotificationPermission()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                setShowBadge(true)
            }
            // đăng ký channel với hệ thống
            val notificationManager: NotificationManager =
                getSystemService(NotificationManager::class.java) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun checkPostNotificationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
        ) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            return
        }
        setupNotification()
    }

    @SuppressLint("MissingPermission")
    private fun setupNotification() {
        val resultIntent = Intent(this, DetailActivity::class.java).apply {
            putExtra(EXTRA_KEY_IMAGE, R.drawable.cat1)
            putExtra(EXTRA_KEY_MESSAGE, editNotificationContent.text.toString())
        }
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(resultIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        val nullIcon: Icon? = null
        val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.ic_cat)
        val largePicture = BitmapFactory.decodeResource(resources, R.drawable.cat1)
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setLargeIcon(largeIcon)
            .setContentTitle(getString(R.string.text_notification_title))
            .setContentText(editNotificationContent.text.toString())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(largePicture)
                    .bigLargeIcon(nullIcon)
            )
            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            .setContentIntent(resultPendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            notify(notificationId++, builder.build())
        }
        editNotificationContent.text.clear()
    }

    companion object {
        const val CHANNEL_ID = "net.braniumacademy.example710.CHANNEL_1"
        const val EXTRA_KEY_IMAGE = "net.braniumacademy.example710.KEY_IMAGE"
        const val EXTRA_KEY_MESSAGE = "net.braniumacademy.example710.KEY_MESSAGE"
    }
}