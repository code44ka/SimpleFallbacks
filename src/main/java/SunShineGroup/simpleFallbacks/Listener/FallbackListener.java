package SunShineGroup.simpleFallbacks.Listener;

import SunShineGroup.simpleFallbacks.Fallback.FallbackManager;
import SunShineGroup.simpleFallbacks.Fallback.FallbackSessionHandler;
import SunShineGroup.simpleFallbacks.SimpleFallbacks;
import com.velocitypowered.api.event.Subscribe;
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
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;

public class FallbackListener {
    private final SimpleFallbacks plugin;
    private final ProxyServer proxy;
    private final YamlDocument config;
    private final YamlDocument messagesConfig;
    private final LimboFactory limboFactory;
    private Limbo limbo;

    public FallbackListener(SimpleFallbacks plugin) {
        this.plugin = plugin;
        proxy = plugin.getServer();
        config = plugin.getConfig();
        messagesConfig = plugin.getMessagesConfig();
        limboFactory = this.plugin.getLimboFactory();
    }

    @Subscribe
    public void onLimboLogin(LoginLimboRegisterEvent e) {
        Player player = e.getPlayer();

        e.setOnKickCallback(kickEvent -> {
            RegisteredServer fromServer = kickEvent.getServer();
            String serverName = fromServer.getServerInfo().getName();

            if (serverName.equals(config.getString("settings.limbo.name")))
                return false;

            plugin.getLogger().warn("Player kicked from {} server", serverName);

            RegisteredServer fallbackServer = FallbackManager.fallbackManager().getFallbackServer();

            if (fallbackServer == null) {
                plugin.getLogger().warn("Игрок отправлен в лимбо");
                sendToLimbo(player, fromServer);

                return true;
            }

            plugin.getLogger().warn("Игрок отправлен на фаллбэк сервер");
            sendToFallbackServer(player, fromServer, fallbackServer);

            return true;
        });
    }

    public void sendToFallbackServer(Player player, RegisteredServer fromServer, RegisteredServer toServer) {
        player.createConnectionRequest(toServer).connect().thenAccept(result -> {
            player.sendMessage(MiniMessage.miniMessage().deserialize(messagesConfig.getString("messages.fallback.join").replace("{server}", toServer.getServerInfo().getName())));
            player.showTitle(Title.title(
                    MiniMessage.miniMessage().deserialize(messagesConfig.getString("messages.fallback.title").replace("{server}", toServer.getServerInfo().getName())),
                    MiniMessage.miniMessage().deserialize(messagesConfig.getString("messages.fallback.subtitle").replace("{server}", toServer.getServerInfo().getName())
            )));
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