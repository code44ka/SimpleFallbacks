package SunShineGroup.simpleFallbacks.Fallback;

import SunShineGroup.simpleFallbacks.SimpleFallbacks;
import com.velocitypowered.api.proxy.ConnectionRequestBuilder;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import dev.dejvokep.boostedyaml.YamlDocument;
import net.elytrium.limboapi.api.Limbo;
import net.elytrium.limboapi.api.LimboSessionHandler;
import net.elytrium.limboapi.api.player.LimboPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;

import java.util.concurrent.TimeUnit;

public class FallbackSessionHandler implements LimboSessionHandler {
    private final SimpleFallbacks plugin;
    private final ProxyServer proxy;
    private final RegisteredServer fromServer;
    private final YamlDocument config;
    private final YamlDocument messageConfig;
    private final YamlDocument localization;
    private final int reconnectDelay;
    private final int maxAttempts;
    private int attempts = 0;
    private LimboPlayer limboPlayer;
    private Player player;
    private ScheduledTask reconnectTask;

    public FallbackSessionHandler(SimpleFallbacks plugin, RegisteredServer server) {
        this.plugin = plugin;
        this.fromServer = server;
        this.proxy = this.plugin.getServer();
        this.config = this.plugin.getConfig();
        this.messageConfig = this.plugin.getMessagesConfig();
        this.localization = this.plugin.getLocalization();

        maxAttempts = config.getInt("settings.limbo.max-attempts", 5);
        reconnectDelay = config.getInt("settings.limbo.reconnect-delay", 10000);
    }

    @Override
    public void onSpawn(Limbo server, LimboPlayer limboPlayer) {
        this.limboPlayer = limboPlayer;
        LimboSessionHandler.super.onSpawn(server, this.limboPlayer);
        this.limboPlayer.disableFalling();

        this.player = limboPlayer.getProxyPlayer();

        String serverName = this.fromServer.getServerInfo().getName();

        if (messageConfig.getBoolean("messages.limbo.join"))
            this.player.sendMessage(MiniMessage.miniMessage().deserialize(localization.getString("messages.limbo.join").replace("{server}", serverName)));

        if (messageConfig.getBoolean("messages.limbo.title") && messageConfig.getBoolean("messages.limbo.subtitle"))
        {
            this.player.showTitle(Title.title(
                    MiniMessage.miniMessage().deserialize(localization.getString("messages.limbo.title")),
                    MiniMessage.miniMessage().deserialize(localization.getString("messages.limbo.subtitle").replace("{server}", serverName))
            ));
        }

        else if (!messageConfig.getBoolean("messages.limbo.title") && messageConfig.getBoolean("messages.limbo.subtitle")) {
            this.player.showTitle(Title.title(
                    Component.empty(),
                    MiniMessage.miniMessage().deserialize(localization.getString("messages.limbo.subtitle").replace("{server}", serverName))
            ));
        }

        else if (messageConfig.getBoolean("messages.limbo.title") && !messageConfig.getBoolean("messages.limbo.subtitle")) {
            this.player.showTitle(Title.title(
                    MiniMessage.miniMessage().deserialize(localization.getString("messages.limbo.title")),
                    Component.empty()
            ));
        }

        //startReconnectTask();
    }

    @Override
    public void onDisconnect() {
        LimboSessionHandler.super.onDisconnect();

        plugin.getLogger().warn(player.getUsername() + " покинул лимбо, не дождавшись подключения к серверу.");
    }

    private void startReconnectTask() {
        reconnectTask = proxy.getScheduler().buildTask(plugin, () -> {
            plugin.getLogger().info("Началось попытка");
            if (attempts >= maxAttempts)
            {
                plugin.getLogger().info("Закончились попытки, бб");
                reconnectTask.cancel();
                return;
            }

            attempts++;

            RegisteredServer fallbackServer = FallbackManager.fallbackManager().getFallbackServer();

            if (fallbackServer != null) {
                player.createConnectionRequest(fallbackServer).connect().thenAccept(result -> {
                    plugin.getLogger().info("Попытка реконекта");

                    if (result.getStatus() == ConnectionRequestBuilder.Status.SUCCESS) {
                        plugin.getLogger().info("Удачно");
                        limboPlayer.disconnect();
                        reconnectTask.cancel();

                        return;
                    }

                    plugin.getLogger().info("Неудачно");
                });
            }
        })
                .delay(reconnectDelay, TimeUnit.MILLISECONDS)
                .repeat(reconnectDelay, TimeUnit.MILLISECONDS)
                .schedule();



//        reconnectTask = proxy.getScheduler().buildTask(plugin, () -> {
//            attempts++;
//            plugin.getLogger().info("Попытка реконекта");
//            try {
//                fromServer.ping().get(2, TimeUnit.SECONDS);
//
//                player.createConnectionRequest(fromServer).connect().thenAccept(result -> {
//                        if (result.getStatus() == ConnectionRequestBuilder.Status.SUCCESS) {
//                            limboPlayer.disconnect();
//                            reconnectTask.cancel();
//
//                            return;
//                        }
//
//                        player.sendMessage(MiniMessage.miniMessage().deserialize("Try reconnect"));
//                });
//
//                if (maxAttempts != 0 && attempts >= maxAttempts) {
//                    player.sendMessage(MiniMessage.miniMessage().deserialize("Cannot connect to server"));
//                    reconnectTask.cancel();
//                    return;
//                }
//            } catch (Exception e) {
//
//            }
//        })
//                .delay(reconnectDelay, TimeUnit.MILLISECONDS)
//                .repeat(reconnectDelay, TimeUnit.MILLISECONDS).schedule();
    }
}
