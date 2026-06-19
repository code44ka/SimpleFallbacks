package SunShineGroup.simpleFallbacks.Listener;

import SunShineGroup.simpleFallbacks.Fallback.FallbackSessionHandler;
import SunShineGroup.simpleFallbacks.SimpleFallbacks;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.dejvokep.boostedyaml.YamlDocument;
import net.elytrium.limboapi.api.Limbo;
import net.elytrium.limboapi.api.LimboFactory;
import net.elytrium.limboapi.api.chunk.Dimension;
import net.elytrium.limboapi.api.chunk.VirtualWorld;
import net.elytrium.limboapi.api.event.LoginLimboRegisterEvent;
import net.elytrium.limboapi.api.player.GameMode;

import java.util.ArrayList;
import java.util.List;

public class FallbackListener {
    private final SimpleFallbacks plugin;
    private final ProxyServer proxy;
    private final YamlDocument config;
    private final LimboFactory limboFactory;
    private Limbo limbo;

    public FallbackListener(SimpleFallbacks plugin) {
        this.plugin = plugin;
        proxy = plugin.getServer();
        config = plugin.getConfig();
        limboFactory = this.plugin.getLimboFactory();
    }

    @Subscribe
    public void onKickEvent(KickedFromServerEvent e) {
        Player player = e.getPlayer();
        RegisteredServer server = e.getServer();

        String serverName = server.getServerInfo().getName();

        if (serverName.equals(config.getString("settings.limbo.name")))
            return;

        plugin.getLogger().warn("Player kicked from {} server", serverName);

        List<String> fallbacks = config.getStringList("settings.fallback-servers");
        List<String> avaibleFallbacks = new ArrayList<>();

        for (String fallback : fallbacks) {
            if (fallback.equals(serverName)) {
                sendToLimbo(player, server);

                return;
            }

            avaibleFallbacks.add(fallback);
        }

        e.setResult(KickedFromServerEvent.RedirectPlayer.create(proxy.getServer(avaibleFallbacks.get(0)).get()));
    }

    //@Subscribe
    public void onLimboLogin(LoginLimboRegisterEvent e) {
        e.setOnKickCallback(kickEvent -> {
            Player player = kickEvent.getPlayer();
            RegisteredServer server = kickEvent.getServer();

            String serverName = server.getServerInfo().getName();

            if (serverName.equals(config.getString("settings.limbo.name")))
                return false;

            plugin.getLogger().warn("Player kicked from {} server", serverName);

            List<String> fallbacks = config.getStringList("settings.fallback-servers");
            List<String> avaibleFallbacks = new ArrayList<>();

            for (String fallback : fallbacks) {
                if (fallback.equals(serverName)) {
                     sendToLimbo(player, server);

                     return false;
                }

                avaibleFallbacks.add(fallback);
            }

            kickEvent.setResult(KickedFromServerEvent.RedirectPlayer.create(proxy.getServer(avaibleFallbacks.get(0)).get()));

            return false;
        });
    }

    public void CreateLimbo() {
        VirtualWorld virtualWorld = limboFactory.createVirtualWorld(
                Dimension.valueOf(config.getString("settings.limbo.world", "THE_END")),
                config.getDouble("settings.limbo.x", 0d),
                config.getDouble("settings.limbo.y", 500d),
                config.getDouble("settings.limbo.z", 0d),
                config.getFloat("settings.limbo.yaw", 0f),
                config.getFloat("settings.limbo.pitch", 0f)
        );

        limbo = limboFactory.createLimbo(virtualWorld)
                .setGameMode(GameMode.valueOf(config.getString("settings.limbo.gamemode")))
                .setName(config.getString("settings.limbo.name"))
                .setShouldRespawn(true);

        plugin.getLogger().info("Limbo created");
    }

    private void sendToLimbo(Player player, RegisteredServer server) {
        limbo.spawnPlayer(player, new FallbackSessionHandler(plugin, server));
    }
}