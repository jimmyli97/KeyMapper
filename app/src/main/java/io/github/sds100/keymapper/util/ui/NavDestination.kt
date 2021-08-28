package io.github.sds100.keymapper.util.ui

import io.github.sds100.keymapper.actions.ActionData
import io.github.sds100.keymapper.actions.KeyEventAction
import io.github.sds100.keymapper.actions.sound.ChooseSoundFileResult
import io.github.sds100.keymapper.actions.swipegesture.PickSwipeResult
import io.github.sds100.keymapper.actions.tapscreen.PickCoordinateResult
import io.github.sds100.keymapper.constraints.ChooseConstraintType
import io.github.sds100.keymapper.constraints.Constraint
import io.github.sds100.keymapper.system.apps.ActivityInfo
import io.github.sds100.keymapper.system.apps.ChooseAppShortcutResult
import io.github.sds100.keymapper.system.bluetooth.BluetoothDeviceInfo
import io.github.sds100.keymapper.system.intents.ConfigIntentResult

/**
 * Created by sds100 on 25/07/2021.
 */
sealed class NavDestination<R> {
    companion object {
        const val ID_CHOOSE_APP = "choose_app"
        const val ID_CHOOSE_APP_SHORTCUT = "choose_app_shortcut"
        const val ID_KEY_CODE = "key_code"
        const val ID_KEY_EVENT = "key_event"
        const val ID_PICK_TAP_COORDINATE = "pick_tap_coordinate"
        const val ID_PICK_SWIPE_GESTURE = "pick_swipe_gesture"
        const val ID_CONFIG_INTENT = "config_intent"
        const val ID_CHOOSE_ACTIVITY = "choose_activity"
        const val ID_CHOOSE_SOUND = "choose_sound"
        const val ID_CHOOSE_ACTION = "choose_action"
        const val ID_CHOOSE_CONSTRAINT = "choose_constraint"
        const val ID_CHOOSE_BLUETOOTH_DEVICE = "choose_bluetooth_device"

        fun NavDestination<*>.getId(): String {
            return when (this) {
                ChooseApp -> ID_CHOOSE_APP
                ChooseAppShortcut -> ID_CHOOSE_APP_SHORTCUT
                ChooseKeyCode -> ID_KEY_CODE
                is ConfigKeyEventAction -> ID_KEY_EVENT
                is PickTapCoordinate -> ID_PICK_TAP_COORDINATE
                is PickSwipeGesture -> ID_PICK_SWIPE_GESTURE
                is ConfigIntent -> ID_CONFIG_INTENT
                ChooseActivity -> ID_CHOOSE_ACTIVITY
                ChooseSound -> ID_CHOOSE_SOUND
                ChooseAction -> ID_CHOOSE_ACTION
                is ChooseConstraint -> ID_CHOOSE_CONSTRAINT
                ChooseBluetoothDevice -> ID_CHOOSE_BLUETOOTH_DEVICE
            }
        }
    }

    object ChooseApp : NavDestination<String>()
    object ChooseAppShortcut : NavDestination<ChooseAppShortcutResult>()
    object ChooseKeyCode : NavDestination<Int>()
    data class ConfigKeyEventAction(val action: KeyEventAction? = null) :
        NavDestination<KeyEventAction>()

    data class PickTapCoordinate(val result: PickCoordinateResult? = null) :
        NavDestination<PickCoordinateResult>()

    data class PickSwipeGesture(val result: PickSwipeResult? = null) :
        NavDestination<PickSwipeResult>()

    data class ConfigIntent(val result: ConfigIntentResult? = null) :
        NavDestination<ConfigIntentResult>()

    object ChooseActivity : NavDestination<ActivityInfo>()
    object ChooseSound : NavDestination<ChooseSoundFileResult>()
    object ChooseAction : NavDestination<ActionData>()
    data class ChooseConstraint(val supportedConstraints: List<ChooseConstraintType>) :
        NavDestination<Constraint>()

    object ChooseBluetoothDevice : NavDestination<BluetoothDeviceInfo>()
}