package io.github.sds100.keymapper.system.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.FingerprintGestureController
import android.accessibilityservice.GestureDescription
import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Path
import android.os.Build
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import androidx.core.content.getSystemService
import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import io.github.sds100.keymapper.actions.swipegesture.SerializablePath
import io.github.sds100.keymapper.api.Api
import io.github.sds100.keymapper.mappings.fingerprintmaps.FingerprintMapId
import io.github.sds100.keymapper.system.devices.InputDeviceInfo
import io.github.sds100.keymapper.system.devices.isExternalCompat
import io.github.sds100.keymapper.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber


/**
 * Created by sds100 on 05/04/2020.
 */

class MyAccessibilityService : AccessibilityService(), LifecycleOwner, IAccessibilityService {

    /**
     * Broadcast receiver for all intents sent from within the app.
     */
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Api.ACTION_TRIGGER_KEYMAP_BY_UID -> {
                    intent.getStringExtra(Api.EXTRA_KEYMAP_UID)?.let {
                        controller.triggerKeyMapFromIntent(it)
                    }
                }
            }
        }
    }

    private lateinit var lifecycleRegistry: LifecycleRegistry

    private var fingerprintGestureCallback:
        FingerprintGestureController.FingerprintGestureCallback? = null

    override val rootNode: AccessibilityNodeModel?
        get() {
            return rootInActiveWindow?.toModel()
        }

    override val isFingerprintGestureDetectionAvailable: Boolean
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            fingerprintGestureController.isGestureDetectionAvailable
        } else {
            false
        }

    private val _isKeyboardHidden by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            MutableStateFlow(softKeyboardController.showMode == SHOW_MODE_HIDDEN)
        } else {
            MutableStateFlow(false)
        }
    }

    override val isKeyboardHidden: Flow<Boolean>
        get() = _isKeyboardHidden

    override var serviceFlags: Int?
        get() = serviceInfo?.flags
        set(value) {
            if (serviceInfo != null && value != null) {
                serviceInfo = serviceInfo.apply {
                    flags = value
                }
            }
        }

    override var serviceFeedbackType: Int?
        get() = serviceInfo?.feedbackType
        set(value) {
            if (serviceInfo != null && value != null) {
                serviceInfo = serviceInfo.apply {
                    feedbackType = value
                }
            }
        }

    override var serviceEventTypes: Int?
        get() = serviceInfo?.eventTypes
        set(value) {
            if (serviceInfo != null && value != null) {
                serviceInfo = serviceInfo.apply {
                    eventTypes = value
                }
            }
        }

    private lateinit var controller: AccessibilityServiceController

    override fun onCreate() {
        super.onCreate()
        Timber.i("Accessibility service: onCreate")

        lifecycleRegistry = LifecycleRegistry(this)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED

        controller = Inject.accessibilityServiceController(this)

        IntentFilter().apply {
            addAction(Api.ACTION_TRIGGER_KEYMAP_BY_UID)
            addAction(Intent.ACTION_INPUT_METHOD_CHANGED)

            registerReceiver(broadcastReceiver, this)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            softKeyboardController.addOnShowModeChangedListener { _, showMode ->
                when (showMode) {
                    SHOW_MODE_AUTO -> _isKeyboardHidden.value = false
                    SHOW_MODE_HIDDEN -> _isKeyboardHidden.value = true
                }
            }
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()

        Timber.i("Accessibility service: onServiceConnected")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            fingerprintGestureCallback =
                object : FingerprintGestureController.FingerprintGestureCallback() {

                    override fun onGestureDetected(gesture: Int) {
                        super.onGestureDetected(gesture)

                        val id: FingerprintMapId = when (gesture) {
                            FingerprintGestureController.FINGERPRINT_GESTURE_SWIPE_DOWN ->
                                FingerprintMapId.SWIPE_DOWN

                            FingerprintGestureController.FINGERPRINT_GESTURE_SWIPE_UP ->
                                FingerprintMapId.SWIPE_UP

                            FingerprintGestureController.FINGERPRINT_GESTURE_SWIPE_LEFT ->
                                FingerprintMapId.SWIPE_LEFT

                            FingerprintGestureController.FINGERPRINT_GESTURE_SWIPE_RIGHT ->
                                FingerprintMapId.SWIPE_RIGHT

                            else -> return
                        }
                        controller.onFingerprintGesture(id)
                    }
                }

            fingerprintGestureCallback?.let {
                fingerprintGestureController.registerFingerprintGestureCallback(it, null)
            }
        }

        controller.onServiceConnected()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Timber.i("Accessibility service: onUnbind")
        return super.onUnbind(intent)
    }

    override fun onInterrupt() {}

    override fun onDestroy() {

        if (::lifecycleRegistry.isInitialized) {
            lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        }

        unregisterReceiver(broadcastReceiver)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            fingerprintGestureController
                .unregisterFingerprintGestureCallback(fingerprintGestureCallback)
        }

        Timber.i("Accessibility service: onDestroy")

        super.onDestroy()
    }

    override fun onLowMemory() {

        val memoryInfo = ActivityManager.MemoryInfo()
        getSystemService<ActivityManager>()?.getMemoryInfo(memoryInfo)

        Timber.i("Accessibility service: onLowMemory, total: ${memoryInfo.totalMem}, available: ${memoryInfo.availMem}, is low memory: ${memoryInfo.lowMemory}, threshold: ${memoryInfo.threshold}")

        super.onLowMemory()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        controller.onAccessibilityEvent(event?.toModel())
    }

    override fun onKeyEvent(event: KeyEvent?): Boolean {
        event ?: return super.onKeyEvent(event)

        val device = if (event.device == null) {
            null
        } else {
            InputDeviceInfo(
                descriptor = event.device.descriptor,
                name = event.device.name,
                id = event.deviceId,
                isExternal = event.device.isExternalCompat
            )
        }

        return controller.onKeyEvent(
            event.keyCode,
            event.action,
            device,
            event.metaState,
            event.scanCode,
            event.eventTime
        )
    }

    override fun getLifecycle() = lifecycleRegistry

    override fun hideKeyboard() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            softKeyboardController.showMode = SHOW_MODE_HIDDEN
        }
    }

    override fun showKeyboard() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            softKeyboardController.showMode = SHOW_MODE_AUTO
        }
    }

    override fun switchIme(imeId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            softKeyboardController.switchToInputMethod(imeId)
        }
    }

    override fun performActionOnNode(
        findNode: (node: AccessibilityNodeModel) -> Boolean,
        performAction: (node: AccessibilityNodeModel) -> AccessibilityNodeAction?
    ): Result<*> {
        val node = rootInActiveWindow.findNodeRecursively {
            findNode(it.toModel())
        }

        if (node == null) {
            return Error.FailedToFindAccessibilityNode
        }

        val (action, extras) = performAction(node.toModel()) ?: return Success(Unit)

        node.performAction(action, bundleOf(*extras.toList().toTypedArray()))
        node.recycle()

        return Success(Unit)
    }

    override fun doGlobalAction(action: Int): Result<*> {
        val success = performGlobalAction(action)

        if (success) {
            return Success(Unit)
        } else {
            return Error.FailedToPerformAccessibilityGlobalAction(action)
        }
    }

    private fun dispatchPath(path: Path, duration: Long, inputEventType: InputEventType): Result<*> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val strokeDescription =
                when {
                    inputEventType == InputEventType.DOWN && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ->
                        GestureDescription.StrokeDescription(
                            path,
                            0,
                            duration,
                            true
                        )

                    inputEventType == InputEventType.UP && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ->
                        GestureDescription.StrokeDescription(
                            path,
                            59999,
                            duration,
                            false
                        )

                    else -> GestureDescription.StrokeDescription(path, 0, duration)
                }

            strokeDescription.let {
                val gestureDescription = GestureDescription.Builder().apply {
                    addStroke(it)
                }.build()

                val success = dispatchGesture(gestureDescription, null, null)

                if (success) {
                    return Success(Unit)
                } else {
                    Error.FailedToDispatchGesture
                }
            }
        }

        return Error.SdkVersionTooLow(Build.VERSION_CODES.N)
    }

    override fun tapScreen(x: Int, y: Int, inputEventType: InputEventType): Result<*> {
        val duration = 1L //ms
        val path = Path().apply {
            moveTo(x.toFloat(), y.toFloat())
        }
        return dispatchPath(path, duration, inputEventType)
    }

    override fun swipeScreen(path: SerializablePath, inputEventType: InputEventType): Result<*> {
        val duration = 200L //ms

        return dispatchPath(path, duration, inputEventType)
    }

    override fun findFocussedNode(focus: Int): AccessibilityNodeModel? {
        return findFocus(focus)?.toModel()
    }
}