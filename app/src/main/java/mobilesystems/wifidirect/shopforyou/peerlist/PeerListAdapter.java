package mobilesystems.wifidirect.shopforyou.peerlist;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import mobilesystems.wifidirect.shopforyou.R;

public class PeerListAdapter extends RecyclerView.Adapter<PeerViewHolder> implements PeerListAdapterContract.View {

    private @NonNull List<PeerModel> peerList = Collections.emptyList();
    private @Nullable PeerListener peerListener;

    @Override
    public void setPeerList(@NonNull List<PeerModel> peerList) {
        this.peerList = Collections.unmodifiableList(peerList);
        notifyDataSetChanged();
    }

    @Override
    public PeerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.peer_card, parent, false);
        return new PeerViewHolder(v, peerListener);
    }

    @Override
    public void onBindViewHolder(PeerViewHolder viewHolder, int position) {
        viewHolder.bind(peerList.get(position));
    }

    @Override
    public int getItemCount() {
        return peerList.size();
    }

    @Override
    public void setListener(@NonNull PeerListener listener) {
        this.peerListener = listener;
    }
}
