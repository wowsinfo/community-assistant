package com.half.wowsca.listener

import android.content.Context
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import com.half.wowsca.CAApp.Companion.eventBus
import com.half.wowsca.R
import com.half.wowsca.managers.CaptainManager
import com.half.wowsca.model.Captain
import com.half.wowsca.model.events.AddRemoveEvent

/**
 * Created by slai4 on 9/19/2015.
 */
class AddRemoveListener(
    private val captain: Captain,
    private val ctx: Context,
    private val box: CheckBox
) : View.OnClickListener {
    override fun onClick(view: View) {
        val captains = CaptainManager.getCaptains(
            ctx
        )
        val event = AddRemoveEvent()
        event.captain = captain
        if (captains?.get(CaptainManager.getCapIdStr(captain)) == null) {
            event.isRemove = false
            box.isChecked = true
            Toast.makeText(
                ctx,
                captain.name + " " + ctx.getString(R.string.list_clan_added_message),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            event.isRemove = true
            box.isChecked = false
            Toast.makeText(
                ctx,
                captain.name + " " + ctx.getString(R.string.list_clan_removed_message),
                Toast.LENGTH_SHORT
            ).show()
        }
        eventBus.post(event)
    }
}
