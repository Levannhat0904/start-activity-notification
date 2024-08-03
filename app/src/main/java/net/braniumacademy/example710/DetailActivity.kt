package net.braniumacademy.example710

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        val imageResult = findViewById<ImageView>(R.id.image_result)
        val textMessage = findViewById<TextView>(R.id.text_message)
        val message = intent?.extras?.getString(MainActivity.EXTRA_KEY_MESSAGE)
        val imageId =
            intent?.extras?.getInt(MainActivity.EXTRA_KEY_IMAGE, R.drawable.ic_notification)!!
        imageResult.setImageResource(imageId)
        textMessage.text = message
    }
}