package mobilesystems.wifidirect.shopforyou;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

class PeerListAdapter extends RecyclerView.Adapter<PeerListAdapter.PeerViewHolder> implements PeerListAdapterContract.View {

    private @NonNull List<PeerModel> peerList = new ArrayList<>();

    @Override
    public void setPeerList(@NonNull List<PeerModel> peerList) {
        this.peerList.clear();
        this.peerList.addAll(peerList);
        notifyDataSetChanged();
    }

    @Override
    public PeerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.peer_card, parent, false);
        return new PeerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PeerViewHolder viewHolder, int position) {
        final PeerModel peer = peerList.get(position);
        viewHolder.getPeerNameView().setText(peer.name);
        viewHolder.getPeerAddressView().setText(peer.address);
        viewHolder.getPeerTypeView().setText(peer.type);
        viewHolder.getPeerStatusView().setText(peer.status);
        viewHolder.getConnectView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                peer.connectTask.run();
            }
        });
    }

    @Override
    public int getItemCount() {
        return peerList.size();
    }

    public static class PeerViewHolder extends RecyclerView.ViewHolder {

        private final TextView peerNameView;
        private final TextView peerAddressView;
        private final TextView peerTypeView;
        private final TextView peerStatusView;
        private final TextView connectView;

        public PeerViewHolder(View itemView) {
            super(itemView);

            peerNameView = itemView.findViewById(R.id.peer_name);
            peerAddressView  = itemView.findViewById(R.id.peer_address);
            peerTypeView  = itemView.findViewById(R.id.peer_type);
            peerStatusView = itemView.findViewById(R.id.peer_status);
            connectView = itemView.findViewById(R.id.connect_to_peer);

        }

        public TextView getPeerNameView() {
            return peerNameView;
        }
        public TextView getPeerAddressView() { return peerAddressView; }
        public TextView getPeerTypeView() { return peerTypeView; }
        public TextView getPeerStatusView() { return peerStatusView; }
        public TextView getConnectView() { return connectView; }
    }
}
