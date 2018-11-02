package mobilesystems.wifidirect.shopforyou;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

class PeerListAdapter extends RecyclerView.Adapter<PeerListAdapter.ViewHolder> implements PeerListAdapterContract.View {

    private @NonNull
    List<String> peerList = new ArrayList<>();

    @Override
    public void setPeerList(@NonNull List<String> peerList) {
        this.peerList.clear();
        this.peerList.addAll(peerList);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.peer_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.getTextView().setText(peerList.get(position));
    }

    @Override
    public int getItemCount() {
        return peerList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.peer_name);
        }

        public TextView getTextView() {
            return textView;
        }
    }
}
