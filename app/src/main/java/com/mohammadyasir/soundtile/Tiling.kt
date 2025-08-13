package com.mohammadyasir.soundtile

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Icon
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS
import android.service.quicksettings.Tile.STATE_ACTIVE
import android.service.quicksettings.Tile.STATE_INACTIVE
import android.service.quicksettings.TileService

class Tiling : TileService() {

    private val receiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateTile()
        }
    }

    override fun onStartListening() {
        super.onStartListening()
        registerReceiver(receiver, IntentFilter(AudioManager.RINGER_MODE_CHANGED_ACTION))
        updateTile()
    }

    override fun onStopListening() {
        super.onStopListening()
        unregisterReceiver(receiver)
    }

    override fun onClick() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        // Check if the app has permission to modify DND
        if (!notificationManager.isNotificationPolicyAccessGranted) {
            Handler(Looper.getMainLooper()).post {showPermissionRequestDialog()}
            return
        }

        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        when (audioManager.ringerMode) {
            AudioManager.RINGER_MODE_NORMAL -> {
                audioManager.ringerMode = AudioManager.RINGER_MODE_VIBRATE
            }
            else -> {
                audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
            }
        }
    }

    private fun updateTile() {
        val tile = qsTile?: return
        tile.subtitle = getString(R.string.app_name)

        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager

        when (audioManager.ringerMode) {
            AudioManager.RINGER_MODE_NORMAL -> {
                tile.state = STATE_ACTIVE
                tile.label = getString(R.string.normal)
                tile.icon = Icon.createWithResource(this, R.drawable.normal)
            }

            AudioManager.RINGER_MODE_VIBRATE -> {
                tile.state = STATE_INACTIVE
                tile.label = getString(R.string.vibrate)
                tile.icon = Icon.createWithResource(this, R.drawable.vibrate)
            }

            AudioManager.RINGER_MODE_SILENT -> {
                tile.state = STATE_INACTIVE
                tile.label = getString(R.string.silent)
                tile.icon = Icon.createWithResource(this, R.drawable.dnd_on)
            }
        }

        tile.updateTile()
    }

    private fun showPermissionRequestDialog() {
        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle(getString(R.string.permission_required))
            .setMessage(getString(R.string.permission_description))
            .setPositiveButton(getString(R.string.grant_permission)) { _, _ ->
                PendingIntent.getActivity(this,0,
                    Intent(ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                ).send()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        showDialog(dialog)
    }
}