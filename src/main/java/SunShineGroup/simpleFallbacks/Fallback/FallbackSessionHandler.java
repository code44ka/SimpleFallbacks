package SunShineGroup.simpleFallbacks.Fallback;

import SunShineGroup.simpleFallbacks.SimpleFallbacks;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.dejvokep.boostedyaml.YamlDocument;
import net.elytrium.limboapi.api.Limbo;
import net.elytrium.limboapi.api.LimboSessionHandler;
import net.elytrium.limboapi.api.player.LimboPlayer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;

public class FallbackSessionHandler implements LimboSessionHandler {
    private final SimpleFallbacks plugin;
    private final RegisteredServer server;
    private final YamlDocument messageConfig;
    private Player player;

    public FallbackSessionHandler(SimpleFallbacks plugin, RegisteredServer server) {
        this.plugin = plugin;
        this.server = server;
        this.messageConfig = plugin.getMessagesConfig();
    }

    @Override
    public void onSpawn(Limbo server, LimboPlayer limboPlayer) {
        LimboSessionHandler.super.onSpawn(server, limboPlayer);
        limboPlayer.disableFalling();

        this.player = limboPlayer.getProxyPlayer();

        String serverName = this.server.getServerInfo().getName();

        this.player.sendMessage(MiniMessage.miniMessage().deserialize(messageConfig.getString("messages.limbo.join").replace("{server}", serverName)));
        this.player.showTitle(Title.title(
                MiniMessage.miniMessage().deserialize(messageConfig.getString("messages.limbo.title")),
                MiniMessage.miniMessage().deserialize(messageConfig.getString("messages.limbo.subtitle").replace("{server}", serverName))
        ));
    }

    @Override
    public void onDisconnect() {
        LimboSessionHandler.super.onDisconnect();

        plugin.getLogger().warn(player.getUsername() + " покинул лимбо, не дождавшись подключения к серверу.");
    }
}
