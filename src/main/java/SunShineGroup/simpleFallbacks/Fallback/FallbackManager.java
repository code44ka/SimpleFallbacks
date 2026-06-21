package SunShineGroup.simpleFallbacks.Fallback;

import SunShineGroup.simpleFallbacks.SimpleFallbacks;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.dejvokep.boostedyaml.YamlDocument;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class FallbackManager {
    private static Random rnd = new Random();

    private static FallbackManager fallbackManager;

    private SimpleFallbacks plugin;
    private ProxyServer proxyServer;
    private YamlDocument config;

    private FallbackManager() {}

    public static FallbackManager fallbackManager() {
        if (fallbackManager == null)
            fallbackManager = new FallbackManager();

        return fallbackManager;
    }

    public void init(SimpleFallbacks plugin) {
        this.plugin = plugin;
        this.proxyServer = plugin.getServer();
        this.config = plugin.getConfig();
    }

    public List<String> getAvaibleFallbacks() {
        List<String> fallbacks = config.getStringList("settings.fallback-servers");
        List<String> avaibleFallbacks = new ArrayList<>();

        for (String fallback : fallbacks) {
            Optional<RegisteredServer> server = proxyServer.getServer(fallback);

            if (server.isPresent()){
                if (isServerOnline(server.get())) {
                    avaibleFallbacks.add(server.get().getServerInfo().getName());
                }
            }
        }

        if (avaibleFallbacks == null || avaibleFallbacks.isEmpty())
            return null;

        return avaibleFallbacks;
    }

    public RegisteredServer getFallbackServer() {
        List<String> avaibleFallbacks = getAvaibleFallbacks();

        if (avaibleFallbacks == null)
           return null;

        RegisteredServer fallbackServer = proxyServer.getServer(avaibleFallbacks.get(rnd.nextInt(0, avaibleFallbacks.size()))).get();

        return fallbackServer;
    }

    private Boolean isServerOnline(RegisteredServer registeredServer) {
        try {
            registeredServer.ping().join();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
