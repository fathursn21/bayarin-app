package id.ac.pnm.bayarin_app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import id.ac.pnm.bayarin_app.data.model.Notifications
import id.ac.pnm.bayarin_app.ui.navigation.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        listenForPushNotifications(this)

        setContent {
            AppNavigation()
        }
    }

    private fun listenForPushNotifications(context: Context) {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val dbRef = FirebaseDatabase.getInstance().getReference("users/${currentUser.uid}/notifications")

        //jika ada data baru yang ditambahkan ke Firebase
        dbRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val notif = snapshot.getValue(Notifications::class.java)

                // Cek notifikasi belum dibaca atau belum, agar tidak memunculkan notifikasi lama
                if (notif != null && !notif.isRead) {
                    showAndroidNotification(context, notif.title, notif.message)
                    //menandai notifikasi yang sudah dibaca di Firebase agar tidak muncul lagi saat login ulang
                    dbRef.child(notif.id).child("isRead").setValue(true)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // pop-up notifikasi di bar atas
    private fun showAndroidNotification(context: Context, title: String, message: String) {
        val channelId = "BAYARIN_NOTIF_CHANNEL"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // untuk Android 8.0 Oreo ke atas
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Notifikasi Tagihan", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        // Memunculkan notifikasi
        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}