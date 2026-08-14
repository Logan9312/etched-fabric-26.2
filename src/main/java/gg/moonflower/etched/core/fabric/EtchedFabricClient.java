package gg.moonflower.etched.core.fabric;

import gg.moonflower.etched.core.EtchedClient;
import net.fabricmc.api.ClientModInitializer;

public final class EtchedFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EtchedClient.initialize();
    }
}
