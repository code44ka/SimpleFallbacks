package SunShineGroup.simpleFallbacks.Fallback;

import SunShineGroup.simpleFallbacks.SimpleFallbacks;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.dejvokep.boostedyaml.YamlDocument;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class FallbackManager {
    private static Random rnd = new Random();

    private List<String> fallbacks;

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
        fallbacks = config.getStringList("settings.fallback-servers");
    }

    public List<String> getAvaibleFallbacks() {
        List<String> avaibleFallbacks = new ArrayList<>();

        for (String fallback : fallbacks) {
            Optional<RegisteredServer> server = proxyServer.getServer(fallback);

            if (server.isPresent()){
                if (isServerOnline(server.get())) {
                    avaibleFallbacks.add(server.get().getServerInfo().getName());
                }
            }
        }

        return avaibleFallbacks;
    }

    public List<String> getFallbacks() {
        return fallbacks;
    }

    public RegisteredServer getFallbackServer() {
        List<String> avaibleFallbacks = getAvaibleFallbacks();

        if (avaibleFallbacks.isEmpty())
            return null;

        RegisteredServer fallbackServer = proxyServer.getServer(avaibleFallbacks.get(rnd.nextInt(0, avaibleFallbacks.size()))).get();

        return fallbackServer;
    }

    private Boolean isServerOnline(RegisteredServer registeredServer) {
        try {
            registeredServer.ping().get(100, TimeUnit.MILLISECONDS);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean isFallbackServer(String server) {
        if (fallbacks.contains(server))
            return true;

        return false;
    }
}
