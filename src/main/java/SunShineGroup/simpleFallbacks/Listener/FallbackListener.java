package SunShineGroup.simpleFallbacks.Listener;

import SunShineGroup.simpleFallbacks.Fallback.FallbackSessionHandler;
import SunShineGroup.simpleFallbacks.SimpleFallbacks;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.dejvokep.boostedyaml.YamlDocument;
import net.elytrium.limboapi.api.Limbo;
import net.elytrium.limboapi.api.LimboFactory;
import net.elytrium.limboapi.api.chunk.Dimension;
import net.elytrium.limboapi.api.chunk.VirtualWorld;
import net.elytrium.limboapi.api.event.LoginLimboRegisterEvent;
import net.elytrium.limboapi.api.player.GameMode;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class FallbackListener {
    private final SimpleFallbacks plugin;
    private final YamlDocument config;
    private final LimboFactory limboFactory;
    private Limbo limbo;

    public FallbackListener(SimpleFallbacks plugin) {
        this.plugin = plugin;
        config = plugin.getConfig();
        limboFactory = this.plugin.getLimboFactory();
    }

    @Subscribe
    public void onLimboLogin(LoginLimboRegisterEvent e) {
        e.setOnKickCallback(kickEvent -> {
            plugin.getLogger().warn("Player kicked");

            Player player = kickEvent.getPlayer();
            RegisteredServer server = kickEvent.getServer();

            String serverName = server.getServerInfo().getName();
            List<String> fallbacks = config.getStringList("settings.fallback-servers");

            for (String fallback : fallbacks) {
                if (serverName.equals(fallback)) {
                    plugin.getLogger().info("Fallback server has been off");
                    return false;
                }
            }

            plugin.getLogger().warn("ЫЫЫЫЫ");
            sendToLimbo(player, server);

            return false;
        });
    }

    public void CreateLimbo() {
        VirtualWorld virtualWorld = limboFactory.createVirtualWorld(
                Dimension.valueOf(config.getString("settings.limbo.world", "THE_END")),
                config.getDouble("settings.limbo.x", 0d),
                config.getDouble("settings.limbo.y", 100d),
                config.getDouble("settings.limbo.z", 0d),
                config.getFloat("settings.limbo.yaw", 0f),
                config.getFloat("settings.limbo.pitch", 0f)
        );

        limbo = limboFactory.createLimbo(virtualWorld)
                .setGameMode(GameMode.valueOf(config.getString("settings.limbo.gamemode")))
                .setName(config.getString("settings.limbo.name"));

        plugin.getLogger().info("Limbo created");
    }

    private void sendToLimbo(Player player, RegisteredServer server) {
        limbo.spawnPlayer(player, new FallbackSessionHandler(plugin, server));
    }
}