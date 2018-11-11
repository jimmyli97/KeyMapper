package io.github.sds100.keymapper.Utils

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import io.github.sds100.keymapper.R
import io.github.sds100.keymapper.SystemAction
import io.github.sds100.keymapper.SystemActionListItem

/**
 * Created by sds100 on 01/08/2018.
 */

object SystemActionUtils {
    fun getSystemActionListItems(): List<SystemActionListItem> {
        //must be in the order the items should be shown to the user
        return sequence {
            yield(SystemActionListItem(SystemAction.GO_BACK))
            yield(SystemActionListItem(SystemAction.GO_HOME))
            yield(SystemActionListItem(SystemAction.OPEN_RECENTS))
            yield(SystemActionListItem(SystemAction.OPEN_MENU))

            yield(SystemActionListItem(SystemAction.TOGGLE_WIFI))
            yield(SystemActionListItem(SystemAction.ENABLE_WIFI))
            yield(SystemActionListItem(SystemAction.DISABLE_WIFI))

            yield(SystemActionListItem(SystemAction.TOGGLE_MOBILE_DATA))
            yield(SystemActionListItem(SystemAction.ENABLE_MOBILE_DATA))
            yield(SystemActionListItem(SystemAction.DISABLE_MOBILE_DATA))

            yield(SystemActionListItem(SystemAction.TOGGLE_BLUETOOTH))
            yield(SystemActionListItem(SystemAction.ENABLE_BLUETOOTH))
            yield(SystemActionListItem(SystemAction.DISABLE_BLUETOOTH))

            yield(SystemActionListItem(SystemAction.VOLUME_UP))
            yield(SystemActionListItem(SystemAction.VOLUME_DOWN))
            yield(SystemActionListItem(SystemAction.VOLUME_SHOW_DIALOG))

            yield(SystemActionListItem(SystemAction.TOGGLE_AUTO_ROTATE))
            yield(SystemActionListItem(SystemAction.ENABLE_AUTO_ROTATE))
            yield(SystemActionListItem(SystemAction.DISABLE_AUTO_ROTATE))
            yield(SystemActionListItem(SystemAction.PORTRAIT_MODE))
            yield(SystemActionListItem(SystemAction.LANDSCAPE_MODE))

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                yield(SystemActionListItem(SystemAction.VOLUME_MUTE))
                yield(SystemActionListItem(SystemAction.VOLUME_UNMUTE))
                yield(SystemActionListItem(SystemAction.VOLUME_TOGGLE_MUTE))
            }

            yield(SystemActionListItem(SystemAction.TOGGLE_AUTO_BRIGHTNESS))
            yield(SystemActionListItem(SystemAction.ENABLE_AUTO_BRIGHTNESS))
            yield(SystemActionListItem(SystemAction.DISABLE_AUTO_BRIGHTNESS))
            yield(SystemActionListItem(SystemAction.INCREASE_BRIGHTNESS))
            yield(SystemActionListItem(SystemAction.DECREASE_BRIGHTNESS))

            yield(SystemActionListItem(SystemAction.EXPAND_NOTIFICATION_DRAWER))
            yield(SystemActionListItem(SystemAction.EXPAND_QUICK_SETTINGS))
            yield(SystemActionListItem(SystemAction.COLLAPSE_STATUS_BAR))

            yield(SystemActionListItem(SystemAction.PAUSE_MEDIA))
            yield(SystemActionListItem(SystemAction.PLAY_MEDIA))
            yield(SystemActionListItem(SystemAction.PLAY_PAUSE_MEDIA))
            yield(SystemActionListItem(SystemAction.NEXT_TRACK))
            yield(SystemActionListItem(SystemAction.PREVIOUS_TRACK))
        }.toList()
    }

    /**
     * Get a string resource id for a string which describes a specified [SystemAction].
     */
    @SuppressLint("NewApi")
    @StringRes
    fun getDescription(@SystemAction.SystemActionId systemAction: String): Int {
        return when (systemAction) {
            SystemAction.ENABLE_WIFI -> R.string.action_enable_wifi
            SystemAction.DISABLE_WIFI -> R.string.action_disable_wifi
            SystemAction.TOGGLE_WIFI -> R.string.action_toggle_wifi

            SystemAction.ENABLE_BLUETOOTH -> R.string.action_enable_bluetooth
            SystemAction.DISABLE_BLUETOOTH -> R.string.action_disable_bluetooth
            SystemAction.TOGGLE_BLUETOOTH -> R.string.action_toggle_bluetooth

            SystemAction.VOLUME_UP -> R.string.action_volume_up
            SystemAction.VOLUME_DOWN -> R.string.action_volume_down
            SystemAction.VOLUME_SHOW_DIALOG -> R.string.action_volume_show_dialog

            SystemAction.ENABLE_AUTO_ROTATE -> R.string.action_enable_auto_rotate
            SystemAction.DISABLE_AUTO_ROTATE -> R.string.action_disable_auto_rotate
            SystemAction.TOGGLE_AUTO_ROTATE -> R.string.action_toggle_auto_rotate
            SystemAction.PORTRAIT_MODE -> R.string.action_portrait_mode
            SystemAction.LANDSCAPE_MODE -> R.string.action_landscape_mode

            SystemAction.VOLUME_MUTE -> return R.string.action_volume_mute
            SystemAction.VOLUME_TOGGLE_MUTE -> return R.string.action_toggle_mute
            SystemAction.VOLUME_UNMUTE -> return R.string.action_volume_unmute

            SystemAction.TOGGLE_MOBILE_DATA -> return R.string.action_toggle_mobile_data
            SystemAction.ENABLE_MOBILE_DATA -> return R.string.action_enable_mobile_data
            SystemAction.DISABLE_MOBILE_DATA -> return R.string.action_disable_mobile_data

            SystemAction.TOGGLE_AUTO_BRIGHTNESS -> return R.string.action_toggle_auto_brightness
            SystemAction.DISABLE_AUTO_BRIGHTNESS -> return R.string.action_disable_auto_brightness
            SystemAction.ENABLE_AUTO_BRIGHTNESS -> return R.string.action_enable_auto_brightness
            SystemAction.INCREASE_BRIGHTNESS -> return R.string.action_increase_brightness
            SystemAction.DECREASE_BRIGHTNESS -> return R.string.action_decrease_brightness

            SystemAction.EXPAND_NOTIFICATION_DRAWER -> return R.string.action_expand_notification_drawer
            SystemAction.EXPAND_QUICK_SETTINGS -> return R.string.action_expand_quick_settings
            SystemAction.COLLAPSE_STATUS_BAR -> return R.string.action_collapse_status_bar

            SystemAction.PAUSE_MEDIA -> return R.string.action_pause_media
            SystemAction.PLAY_MEDIA -> return R.string.action_play_media
            SystemAction.PLAY_PAUSE_MEDIA -> return R.string.action_play_pause_media
            SystemAction.NEXT_TRACK -> return R.string.action_next_track
            SystemAction.PREVIOUS_TRACK -> return R.string.action_previous_track
            SystemAction.GO_BACK -> return R.string.action_go_back
            SystemAction.GO_HOME -> return R.string.action_go_home
            SystemAction.OPEN_RECENTS -> return R.string.action_open_recents
            SystemAction.OPEN_MENU -> return R.string.action_open_menu

            else -> throw Exception("Can't find a description for $systemAction")
        }
    }

    /**
     * Get a drawable resource id for a specified [SystemAction]
     */
    @SuppressLint("NewApi")
    @DrawableRes
    fun getIconResource(@SystemAction.SystemActionId systemAction: String): Int? {
        return when (systemAction) {
            SystemAction.TOGGLE_WIFI -> R.drawable.ic_network_wifi_black_24dp
            SystemAction.ENABLE_WIFI -> R.drawable.ic_network_wifi_black_24dp
            SystemAction.DISABLE_WIFI -> R.drawable.ic_signal_wifi_off_black_24dp

            SystemAction.TOGGLE_BLUETOOTH -> R.drawable.ic_bluetooth_black_24dp
            SystemAction.ENABLE_BLUETOOTH -> R.drawable.ic_bluetooth_black_24dp
            SystemAction.DISABLE_BLUETOOTH -> R.drawable.ic_bluetooth_disabled_black_24dp

            SystemAction.VOLUME_UP -> R.drawable.ic_volume_up_black_24dp
            SystemAction.VOLUME_DOWN -> R.drawable.ic_volume_down_black_24dp
            SystemAction.VOLUME_MUTE -> R.drawable.ic_volume_mute_black_24dp
            SystemAction.VOLUME_TOGGLE_MUTE -> R.drawable.ic_volume_mute_black_24dp
            SystemAction.VOLUME_UNMUTE -> R.drawable.ic_volume_up_black_24dp

            SystemAction.ENABLE_AUTO_ROTATE -> R.drawable.ic_screen_rotation_black_24dp
            SystemAction.DISABLE_AUTO_ROTATE -> R.drawable.ic_screen_lock_rotation_black_24dp
            SystemAction.TOGGLE_AUTO_ROTATE -> R.drawable.ic_screen_rotation_black_24dp
            SystemAction.PORTRAIT_MODE -> R.drawable.ic_stay_current_portrait_black_24dp
            SystemAction.LANDSCAPE_MODE -> R.drawable.ic_stay_current_landscape_black_24dp

            SystemAction.TOGGLE_MOBILE_DATA -> R.drawable.ic_signal
            SystemAction.ENABLE_MOBILE_DATA -> R.drawable.ic_signal
            SystemAction.DISABLE_MOBILE_DATA -> R.drawable.ic_signal_off

            SystemAction.TOGGLE_AUTO_BRIGHTNESS -> R.drawable.ic_brightness_auto_black_24dp
            SystemAction.DISABLE_AUTO_BRIGHTNESS -> R.drawable.ic_disable_brightness_auto_24dp
            SystemAction.ENABLE_AUTO_BRIGHTNESS -> R.drawable.ic_brightness_auto_black_24dp
            SystemAction.INCREASE_BRIGHTNESS -> R.drawable.ic_brightness_high_black_24dp
            SystemAction.DECREASE_BRIGHTNESS -> R.drawable.ic_brightness_low_black_24dp

            SystemAction.PAUSE_MEDIA -> R.drawable.ic_pause_black_24dp
            SystemAction.PLAY_MEDIA -> R.drawable.ic_play_arrow_black_24dp
            SystemAction.PLAY_PAUSE_MEDIA -> R.drawable.ic_play_pause_24dp
            SystemAction.NEXT_TRACK -> R.drawable.ic_skip_next_black_24dp
            SystemAction.PREVIOUS_TRACK -> R.drawable.ic_skip_previous_black_24dp

            SystemAction.GO_BACK -> R.drawable.ic_arrow_back_black_24dp
            SystemAction.GO_HOME -> R.drawable.ic_home_black_24dp
            SystemAction.OPEN_RECENTS -> R.drawable.crop_square
            SystemAction.OPEN_MENU -> R.drawable.ic_more_vert_black_24dp

            else -> null
        }
    }
}