package live.hms.roomkit.ui

import android.app.Activity
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import live.hms.roomkit.ui.meeting.MeetingActivity
import live.hms.roomkit.util.ROOM_CODE
import live.hms.roomkit.util.ROOM_PREBUILT
import live.hms.roomkit.util.TOKEN

object HMSRoomKit {


    fun launchPrebuilt(roomCode: String? = null, token: String?= null, activity: Activity, options: HMSPrebuiltOptions? = null) {
        Intent(activity, MeetingActivity::class.java).apply {
            putExtra(ROOM_CODE, roomCode)
            putExtra(TOKEN, token)
            putExtra(ROOM_PREBUILT, options)
            startActivity(activity, this, null)
        }

    }

}

