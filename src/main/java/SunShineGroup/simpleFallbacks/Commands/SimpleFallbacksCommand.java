package SunShineGroup.simpleFallbacks.Commands;

import SunShineGroup.simpleFallbacks.SimpleFallbacks;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class SimpleFallbacksCommand implements SimpleCommand {
    private final SimpleFallbacks plugin;

    public SimpleFallbacksCommand(SimpleFallbacks plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource commandSource = invocation.source();
        String[] args = invocation.arguments();

        if (args.length == 0)
            return;

        switch (args[0]) {
            case "reload":
                plugin.reload();

                commandSource.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Plugin reloaded</yellow>"));
                break;
        }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("simplefallbacks.admin");
    }
}
