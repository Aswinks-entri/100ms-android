package live.hms.app2.ui.meeting.participants

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.MainThread
import androidx.recyclerview.widget.RecyclerView
import live.hms.app2.databinding.ListItemPeerListBinding
import live.hms.video.media.tracks.HMSTrack
import live.hms.video.media.tracks.HMSTrackType
import live.hms.video.sdk.models.HMSPeer
import live.hms.video.sdk.models.HMSRemotePeer

class ParticipantsAdapter(
  val isAllowedToChangeRole: Boolean,
  val isAllowedToKickPeer : Boolean,
  val isAllowedToMutePeer : Boolean,
  val isAllowedToAskUnmutePeer : Boolean,
  private val showSheet : (HMSPeer) -> Unit,
  private val requestPeerLeave : (HMSRemotePeer, String) -> Unit,
  private val togglePeerMute : (HMSRemotePeer, HMSTrackType) -> Unit,
) : RecyclerView.Adapter<ParticipantsAdapter.PeerViewHolder>() {

  companion object {
    private const val TAG = "ParticipantsAdapter"
  }

  private fun v(value: Boolean) = if (value) View.VISIBLE else View.GONE

  inner class PeerViewHolder(
    private val binding: ListItemPeerListBinding
  ) : RecyclerView.ViewHolder(binding.root) {
    init {
      with(binding) {
        roleChange.setOnClickListener { showSheet(items[adapterPosition]) }
        removePeer.setOnClickListener { requestPeerLeave(items[adapterPosition] as HMSRemotePeer, "Bye") }
        muteUnmuteAudio.setOnClickListener { togglePeerMute(items[adapterPosition] as HMSRemotePeer, HMSTrackType.AUDIO) }
        muteUnmuteVideo.setOnClickListener { togglePeerMute(items[adapterPosition] as HMSRemotePeer, HMSTrackType.VIDEO) }
      }
    }

    fun bind(item: HMSPeer) {
      with(binding) {
        name.text = item.name
        iconScreenShare.visibility = v(item.auxiliaryTracks.isNotEmpty())
        iconAudioOff.visibility = v(item.audioTrack?.isMute != false)
        iconVideoOff.visibility = v(item.videoTrack?.isMute != false)
        roleChange.text = item.hmsRole.name
        // Show change role option only if the role of the local peer allows
        //  and if it's not the local peer itself.
        roleChange.isEnabled = isAllowedToChangeRole && !item.isLocal
        removePeer.isEnabled = !item.isLocal && isAllowedToKickPeer
        item.audioTrack?.let {
          setTrackMuteButtonVisibility(it, item, muteUnmuteAudio)
        }

        item.videoTrack?.let {
          setTrackMuteButtonVisibility(it, item, muteUnmuteVideo)
        }
      }
    }

  }

  fun setTrackMuteButtonVisibility(it: HMSTrack, item: HMSPeer, button : Button) {
    val isMute = it.isMute
    button.visibility = v(!item.isLocal &&
            (
                    ( isAllowedToMutePeer && !isMute) ||
                            (isAllowedToAskUnmutePeer && isMute)
                    )
    )
    button.text = if(isMute) "Unmute" else "Mute"
  }

  private val items = ArrayList<HMSPeer>()

  @MainThread
  fun setItems(newItems: Array<HMSPeer>) {
    items.clear()
    items.addAll(newItems)
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeerViewHolder {
    val binding = ListItemPeerListBinding.inflate(
      LayoutInflater.from(parent.context),
      parent,
      false
    )
    return PeerViewHolder(binding)
  }

  override fun onBindViewHolder(holder: PeerViewHolder, position: Int) {
    holder.bind(items[position])
  }

  override fun getItemCount(): Int = items.size
}