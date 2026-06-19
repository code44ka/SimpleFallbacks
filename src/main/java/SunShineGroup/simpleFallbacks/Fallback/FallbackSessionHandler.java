package SunShineGroup.simpleFallbacks.Fallback;

import SunShineGroup.simpleFallbacks.SimpleFallbacks;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.elytrium.limboapi.api.Limbo;
import net.elytrium.limboapi.api.LimboSessionHandler;
import net.elytrium.limboapi.api.player.LimboPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;

public class FallbackSessionHandler implements LimboSessionHandler {
    private final SimpleFallbacks plugin;
    private final RegisteredServer server;
    private Player player;

    public FallbackSessionHandler(SimpleFallbacks plugin, RegisteredServer server) {
        this.plugin = plugin;
        this.server = server;
    }

    @Override
    public void onSpawn(Limbo server, LimboPlayer limboPlayer) {
        LimboSessionHandler.super.onSpawn(server, limboPlayer);
        limboPlayer.disableFalling();

        Player player = limboPlayer.getProxyPlayer();
        this.player = player;

        this.player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Не удалось подключиться к " + this.server.getServerInfo().getName() + ".</red>"));
        this.player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Вы отправлены в лимбо."));
        this.player.showTitle(Title.title(MiniMessage.miniMessage().deserialize("<red>Не удалось подключиться</red>"), Component.empty()));

        limboPlayer.flushPackets();
    }

    @Override
    public void onDisconnect() {
        LimboSessionHandler.super.onDisconnect();

        plugin.getLogger().warn(player.getUsername() + " покинул лимбо, не дождавшись подключения к серверу.");
    }
}
