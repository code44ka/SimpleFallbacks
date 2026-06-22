package SunShineGroup.simpleFallbacks.Commands;

import SunShineGroup.simpleFallbacks.Fallback.FallbackManager;
import SunShineGroup.simpleFallbacks.SimpleFallbacks;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.dejvokep.boostedyaml.YamlDocument;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class HubCommand implements SimpleCommand {
    private final SimpleFallbacks plugin;
    private final ProxyServer proxy;
    private final YamlDocument messagesConfig;
    private final YamlDocument localization;

    public HubCommand(SimpleFallbacks plugin) {
        this.plugin = plugin;
        this.proxy = this.plugin.getServer();
        this.messagesConfig = this.plugin.getMessagesConfig();
        this.localization = this.plugin.getLocalization();
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource commandSource = invocation.source();
        String[] args = invocation.arguments();

        if (!(commandSource instanceof Player player)) {
            commandSource.sendMessage(MiniMessage.miniMessage().deserialize("<red>This command must be user by player</red>"));
            return;
        }

        player.getCurrentServer().ifPresent(present -> {
            String currentServer = present.getServer().getServerInfo().getName();

            if (FallbackManager.fallbackManager().isFallbackServer(currentServer)) {
                if (messagesConfig.getBoolean("messages.hub.already"))
                    player.sendMessage(MiniMessage.miniMessage().deserialize(localization.getString("messages.hub.already").trim()));
                return;
            }

            RegisteredServer registeredServer = FallbackManager.fallbackManager().getFallbackServer();

            player.createConnectionRequest(registeredServer).connect().thenAccept(result -> {
                if (messagesConfig.getBoolean("messages.hub.move"))
                    player.sendMessage(MiniMessage.miniMessage().deserialize(localization.getString("messages.hub.move").trim()));
            });
        });
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("simplefallbacks.hub.use");
    }
}
