package SunShineGroup.simpleFallbacks.Commands;

import SunShineGroup.simpleFallbacks.Fallback.FallbackManager;
import SunShineGroup.simpleFallbacks.SimpleFallbacks;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.List;

public class FallbacksCommand implements SimpleCommand {
    private final SimpleFallbacks plugin;

    public FallbacksCommand(SimpleFallbacks plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource commandSource = invocation.source();
        //String[] args = invocation.arguments();

        List<String> fallbacks = FallbackManager.fallbackManager().getFallbacks();
        List<String> avaibleFallbacks = FallbackManager.fallbackManager().getAvaibleFallbacks();

        commandSource.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>=== FALLBACKS ===</yellow>"));

        for (String fallback : fallbacks) {
            if (avaibleFallbacks.contains(fallback))
                commandSource.sendMessage(MiniMessage.miniMessage().deserialize("<gray>" + fallback + "</gray> <dark_gray>-</dark_gray> <green>online</green>"));
            else
                commandSource.sendMessage(MiniMessage.miniMessage().deserialize("<gray>" + fallback + "</gray> <dark_gray>-</dark_gray> <red>offline</red>"));
        }

        commandSource.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Total: </yellow><gray>" + avaibleFallbacks.size() + " <dark_gray>/</dark_gray> " + fallbacks.size() + "</gray>"));
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("simplefallbacks.fallbacks.use");
    }
}
