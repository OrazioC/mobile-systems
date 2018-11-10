package mobilesystems.wifidirect.shopforyou.peerlist;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import mobilesystems.wifidirect.shopforyou.R;

public class PeerViewHolder extends RecyclerView.ViewHolder {

    private PeerListener listener;

    private final TextView peerNameView;
    private final TextView peerAddressView;
    private final TextView peerTypeView;
    private final TextView peerStatusView;
    private final TextView connectView;

    PeerViewHolder(View itemView, PeerListener listener) {
        super(itemView);

        this.listener = listener;
        peerNameView = itemView.findViewById(R.id.peer_name);
        peerAddressView  = itemView.findViewById(R.id.peer_address);
        peerTypeView  = itemView.findViewById(R.id.peer_type);
        peerStatusView = itemView.findViewById(R.id.peer_status);
        connectView = itemView.findViewById(R.id.connect_to_peer);
    }

    void bind(final PeerModel model) {
        peerNameView.setText(model.name);
        peerAddressView.setText(model.address);
        peerTypeView.setText(model.type);
        peerStatusView.setText(model.status);

        connectView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(model);
            }
        });
    }
}
