package com.half.wowsca.listener

import android.content.Context
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import com.half.wowsca.R
import com.half.wowsca.managers.CaptainManager
import com.half.wowsca.model.Captain

class AddRemoveListener(
    private val captain: Captain,
    private val ctx: Context,
    private val box: CheckBox,
    private val onCaptainChanged: (Captain, Boolean) -> Unit,
) : View.OnClickListener {
    override fun onClick(view: View) {
        val captains = CaptainManager.getCaptains(ctx)
        val isRemove = captains?.get(CaptainManager.getCapIdStr(captain)) != null
        if (isRemove) {
            box.isChecked = false
            Toast.makeText(
                ctx,
                captain.name + " " + ctx.getString(R.string.list_clan_removed_message),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            box.isChecked = true
            Toast.makeText(
                ctx,
                captain.name + " " + ctx.getString(R.string.list_clan_added_message),
                Toast.LENGTH_SHORT
            ).show()
        }
        onCaptainChanged(captain, isRemove)
    }
}
