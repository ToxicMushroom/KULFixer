package me.melijn.kulfixer

import android.Manifest
import android.app.job.JobScheduler
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import kotlinx.coroutines.*


class StatusFragment : Fragment() {
    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_status, container, false)

        // buttons
        view.findViewById<Button>(R.id.button).setOnClickListener {
            view.findNavController().navigate(R.id.action_statusFragment_to_settingsFragment)
        }
        view.findViewById<Button>(R.id.grant_permissions).setOnClickListener {
            val notificationInterceptAccess: Boolean = ContextCompat.checkSelfPermission(it.context, Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS) == PackageManager.PERMISSION_GRANTED
            val accessibilityAccess: Boolean = ContextCompat.checkSelfPermission(it.context, Settings.ACTION_ACCESSIBILITY_SETTINGS) == PackageManager.PERMISSION_GRANTED

            if (!notificationInterceptAccess) {
                Toast.makeText(context, "Please grant notification access", Toast.LENGTH_SHORT)
                    .show()
                resultLauncher.launch(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
            }
            if (!accessibilityAccess) {
                Toast.makeText(context, "Please grant accessibility access", Toast.LENGTH_SHORT)
                    .show()
                resultLauncher.launch(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
        }

        return view
    }
}