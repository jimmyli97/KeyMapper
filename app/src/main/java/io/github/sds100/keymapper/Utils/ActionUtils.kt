package io.github.sds100.keymapper.Utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.view.KeyEvent
import io.github.sds100.keymapper.*
import io.github.sds100.keymapper.Services.MyIMEService
import io.github.sds100.keymapper.Utils.ErrorCodeUtils.ERROR_CODE_ACTION_IS_NULL
import io.github.sds100.keymapper.Utils.ErrorCodeUtils.ERROR_CODE_APP_DISABLED
import io.github.sds100.keymapper.Utils.ErrorCodeUtils.ERROR_CODE_APP_UNINSTALLED
import io.github.sds100.keymapper.Utils.ErrorCodeUtils.ERROR_CODE_IME_SERVICE_NOT_CHOSEN
import io.github.sds100.keymapper.Utils.ErrorCodeUtils.ERROR_CODE_NO_ACTION_DATA
import io.github.sds100.keymapper.Utils.ErrorCodeUtils.ERROR_CODE_PERMISSION_DENIED
import io.github.sds100.keymapper.Utils.ErrorCodeUtils.ERROR_CODE_SHORTCUT_NOT_FOUND
import io.github.sds100.keymapper.Utils.PermissionUtils.isPermissionGranted

/**
 * Created by sds100 on 03/09/2018.
 */

/**
 * Provides functions commonly used with [Action]s
 */
object ActionUtils {

    /**
     * Get a description for an action. E.g if the user chose an app, then the description will
     * be 'Open <app>'
     */
    fun getDescription(ctx: Context, action: Action?): ActionDescription {

        val errorCodeResult = getPotentialErrorCode(ctx, action)

        val errorMessage = if (errorCodeResult == null) {
            null
        } else {
            ErrorCodeUtils.getErrorCodeResultDescription(ctx, errorCodeResult)
        }

        val title = getTitle(ctx, action)
        val icon = getIcon(ctx, action)

        return ActionDescription(
                icon, title, errorMessage, errorCodeResult
        )
    }

    private fun getTitle(ctx: Context, action: Action?): String? {
        action ?: return null

        when (action.type) {
            ActionType.APP -> {
                try {
                    val applicationInfo = ctx.packageManager.getApplicationInfo(
                            action.data,
                            PackageManager.GET_META_DATA
                    )

                    val applicationLabel = ctx.packageManager.getApplicationLabel(applicationInfo)

                    return ctx.getString(R.string.description_open_app, applicationLabel.toString())
                } catch (e: PackageManager.NameNotFoundException) {
                    //the app isn't installed
                    return null
                }
            }

            ActionType.APP_SHORTCUT -> {
                val intent = Intent.parseUri(action.data, 0)

                //get the title for the shortcut
                if (intent.extras != null &&
                        intent.extras!!.containsKey(AppShortcutUtils.EXTRA_SHORTCUT_TITLE)) {
                    return intent.extras!!.getString(AppShortcutUtils.EXTRA_SHORTCUT_TITLE)
                }

                return null
            }

            ActionType.SYSTEM_ACTION -> {
                //convert the string representation into an enum
                val systemActionId = action.data
                return ctx.str(SystemActionUtils.getSystemActionDef(systemActionId).descriptionRes)
            }

            ActionType.KEYCODE -> {
                val key = KeyEvent.keyCodeToString(action.data.toInt())
                return ctx.str(R.string.description_keycode, key)
            }

            ActionType.KEY -> {
                val keyCode = action.data.toInt()
                val key = KeycodeUtils.keycodeToString(keyCode)

                return ctx.str(R.string.description_key, key)
            }

            ActionType.TEXT_BLOCK -> {
                val text = action.data
                return ctx.str(R.string.description_text_block, text)
            }
        }
    }

    /**
     * Get the icon for any Action
     */
    private fun getIcon(ctx: Context, action: Action?): Drawable? {
        action ?: return null

        return when (action.type) {
            ActionType.APP -> {
                try {
                    return ctx.packageManager.getApplicationIcon(action.data)
                } catch (e: PackageManager.NameNotFoundException) {
                    //if the app isn't installed, it can't find the icon for it
                    return null
                }
            }

            ActionType.SYSTEM_ACTION -> {
                //convert the string representation of the enum entry into an enum object
                val systemActionId = action.data
                val resId = SystemActionUtils.getSystemActionDef(systemActionId).iconRes ?: return null

                ctx.drawable(resId)
            }

            //return null if no icon should be used
            else -> null
        }
    }

    /**
     * if the action requires a permission, which needs user approval, this function
     * returns the permission required. Null is returned if the action doesn't need any permission
     */
    private fun getRequiredPermissionForAction(action: Action): Array<String>? {
        if (action.type == ActionType.SYSTEM_ACTION) {
            return SystemActionUtils.getSystemActionDef(action.data).permissions
        }

        return null
    }

    /**
     * @return if the action can't be performed, it returns an error code.
     * returns null if their if the action can be performed.
     */
    fun getPotentialErrorCode(ctx: Context, action: Action?): ErrorCodeResult? {
        //action is null
        action ?: return ErrorCodeResult(ERROR_CODE_ACTION_IS_NULL)

        //the action has not data
        if (action.data.isEmpty()) return ErrorCodeResult(ERROR_CODE_NO_ACTION_DATA)

        //action requires the IME service but it isn't chosen
        if (action.requiresIME && !MyIMEService.isInputMethodChosen(ctx)) {
            return ErrorCodeResult(ERROR_CODE_IME_SERVICE_NOT_CHOSEN)
        }

        //a required permission isn't granted
        getRequiredPermissionForAction(action)?.forEach { permission ->
            if (!isPermissionGranted(ctx, permission)) {
                return ErrorCodeResult(ERROR_CODE_PERMISSION_DENIED, permission)
            }
        }

        when (action.type) {
            ActionType.APP -> {
                try {
                    val appInfo = ctx.packageManager.getApplicationInfo(action.data, 0)

                    //if the app is disabled, show an error message because it won't open
                    if (!appInfo.enabled) {
                        return ErrorCodeResult(ERROR_CODE_APP_DISABLED, appInfo.packageName)
                    }

                    return null
                } catch (e: Exception) {
                    return ErrorCodeResult(ERROR_CODE_APP_UNINSTALLED, action.data)
                }
            }

            ActionType.APP_SHORTCUT -> {
                val intent = Intent.parseUri(action.data, 0)
                val activityExists = intent.resolveActivityInfo(ctx.packageManager, 0) != null

                if (!activityExists) {
                    return ErrorCodeResult(ERROR_CODE_SHORTCUT_NOT_FOUND, action.data)
                }
            }

            else -> return null
        }

        return null
    }
}