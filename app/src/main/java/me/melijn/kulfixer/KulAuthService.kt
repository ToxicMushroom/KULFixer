package me.melijn.kulfixer


import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.SharedPreferences
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.core.text.isDigitsOnly
import androidx.preference.PreferenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class KulAuthService : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()
        // Set up your accessibility service here
        // Enable the service and configure event types to listen for
        val info = AccessibilityServiceInfo()
        info.eventTypes =
            AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED or AccessibilityEvent.TYPE_VIEW_FOCUSED or AccessibilityEvent.TYPE_WINDOWS_CHANGED or
                    AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
        serviceInfo = info
    }

    private val mutex = Mutex()
    private var lastExec = 0L
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val eventType = event?.eventType ?: return
        if (event.packageName != "be.kuleuven.icts.authenticator") return
        info(event.text.joinToString())

        when (eventType) {
            AccessibilityEvent.TYPE_VIEW_FOCUSED -> {
                info("view focused " + event.packageName)
            }

            AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED -> {
                info("view accessibility focused " + event.packageName)
            }

            AccessibilityEvent.TYPE_WINDOWS_CHANGED -> {
                info("window changed " + event.packageName)
            }

            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                info("window state changed " + event.packageName)
            }
        }

        val preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val startupDelay = preferences.getLongB("edit_text_launch_delay")
        val tapDelay = preferences.getLongB("edit_text_tap_delay")
        val buttonTaps = List(4) { i -> preferences.getLongB("edit_text_preference_${i + 1}") }

        CoroutineScope(Dispatchers.Default).launch {
            if (mutex.isLocked) return@launch
            mutex.withLock {
                if (System.currentTimeMillis() - lastExec < 1000) return@withLock
                delay(startupDelay)
                lastExec = System.currentTimeMillis()
                val rootNode = rootInActiveWindow
                if (rootNode.packageName == "be.kuleuven.icts.authenticator") {
                    val children = mutableListOf<AccessibilityNodeInfo>()
                    for (i in 0 until rootNode.childCount) {
                        val child = rootNode.getChild(i)
                        info(child.text?.toString() ?: "")
                        children.add(child)
                    }
                    val buttons = children.filter { it.className == "android.widget.Button" }
                    val numericButtons = buttons
                        .filter { it.text?.isDigitsOnly() ?: false }
                        .sortedBy { it.text.toString().toInt() }
                    if (numericButtons.size != 10) return@withLock

                    val buttonsToTapList =
                        buttonTaps.map { buttonNr -> numericButtons[(buttonNr).toInt()] }

                    for (button in buttonsToTapList) {
                        delay(tapDelay)
                        button.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    }
                }
                lastExec = System.currentTimeMillis()
            }
        }
    }

    private fun SharedPreferences.getLongB(key: String, default: Long = 200): Long =
        this.getString(key, null)?.toLong() ?: default

    private fun info(text: String) {
        Log.i("Accessibility", text)
    }

    override fun onInterrupt() {
    }

}