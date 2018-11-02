package mobilesystems.wifidirect.shopforyou;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment implements HomeFragmentContract.View {

    private HomeFragmentContract.Presenter presenter;
    private PeerListAdapter adapter;


    public @Nullable
    View onCreateView(@NonNull LayoutInflater inflater,
                      @Nullable ViewGroup container,
                      @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.home_fragment, container, false);

        RecyclerView peerList = rootView.findViewById(R.id.peer_list);
        adapter = new PeerListAdapter();
        peerList.setAdapter(adapter);
        peerList.setLayoutManager(new LinearLayoutManager(getContext()));

        presenter = new HomeFragmentPresenter(adapter);

        View button = rootView.findViewById(R.id.request_peers);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.populateList();
            }
        });


        return rootView;
    }


}
