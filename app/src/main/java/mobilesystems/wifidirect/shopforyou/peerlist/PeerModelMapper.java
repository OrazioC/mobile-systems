package mobilesystems.wifidirect.shopforyou.peerlist;

import android.net.wifi.p2p.WifiP2pDevice;
import android.support.annotation.NonNull;

public class PeerModelMapper {

    private @NonNull
    PeerConnectionStatusMapper peerConnectionStatusMapper;

    public PeerModelMapper(@NonNull PeerConnectionStatusMapper peerConnectionStatusMapper) {
        this.peerConnectionStatusMapper = peerConnectionStatusMapper;
    }

    public PeerModel map(@NonNull WifiP2pDevice device) {
        return new PeerModel(
                device.deviceName,
                device.deviceAddress,
                device.primaryDeviceType,
                peerConnectionStatusMapper.map(device.status)
        );
    }
}
