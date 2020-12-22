package io.github.sds100.keymapper.data.model.options

import io.github.sds100.keymapper.data.model.Trigger
import io.github.sds100.keymapper.data.model.options.BoolOption.Companion.saveBoolOption
import kotlinx.android.parcel.Parcelize
import splitties.bitflags.hasFlag

@Parcelize
class TriggerKeyOptions(
    override val id: String,
    val clickType: IntOption,
    private val doNotConsumeKeyEvents: BoolOption
) : BaseOptions<Trigger.Key> {

    companion object {
        const val ID_DO_NOT_CONSUME_KEY_EVENT = "do_not_consume_key_event"
        const val ID_CLICK_TYPE = "click_type"
    }

    constructor(key: Trigger.Key, @Trigger.Mode mode: Int) : this(
        id = key.uniqueId,

        clickType = IntOption(
            id = ID_CLICK_TYPE,
            value = key.clickType,
            isAllowed = mode == Trigger.SEQUENCE || mode == Trigger.UNDEFINED
        ),

        doNotConsumeKeyEvents = BoolOption(
            id = ID_DO_NOT_CONSUME_KEY_EVENT,
            value = key.flags.hasFlag(Trigger.Key.FLAG_DO_NOT_CONSUME_KEY_EVENT),
            isAllowed = true
        )
    )

    override fun setValue(id: String, value: Boolean): TriggerKeyOptions {
        when (id) {
            ID_DO_NOT_CONSUME_KEY_EVENT -> doNotConsumeKeyEvents.value = value
        }

        return this
    }

    override fun setValue(id: String, value: Int): TriggerKeyOptions {
        when (id) {
            ID_CLICK_TYPE -> clickType.value = value
        }

        return this
    }

    override val intOptions: List<IntOption>
        get() = listOf()

    override val boolOptions: List<BoolOption>
        get() = listOf(doNotConsumeKeyEvents)

    override fun apply(old: Trigger.Key): Trigger.Key {
        val newFlags = old.flags.saveBoolOption(
            doNotConsumeKeyEvents,
            Trigger.Key.FLAG_DO_NOT_CONSUME_KEY_EVENT
        )

        val newClickType = clickType.value

        return old.copy(flags = newFlags, clickType = newClickType)
    }
}