package SunShineGroup.simpleFallbacks;

import SunShineGroup.simpleFallbacks.Commands.FallbacksCommand;
import SunShineGroup.simpleFallbacks.Commands.HubCommand;
import SunShineGroup.simpleFallbacks.Fallback.FallbackManager;
import SunShineGroup.simpleFallbacks.Listener.FallbackListener;
import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import net.elytrium.limboapi.api.LimboFactory;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Plugin(
        id = "simplefallbacks",
        name = "SimpleFallbacks",
        version = "1.0",
        dependencies = {
                @Dependency(id = "limboapi")
        }
)
public class SimpleFallbacks {
    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    private final CommandManager commandManager;

    private LimboFactory limboFactory;

    private FallbackListener fallbackListener;
    private FallbackManager fallbackManager;

    // CONFIGS
    private YamlDocument config;
    private YamlDocument messagesConfig;

    @Inject
    public SimpleFallbacks(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory, CommandManager commandManager) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.commandManager = commandManager;

        loadConfiguration();

        logger.info("Plugin initialized");
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent e) {
        limboFactory = (LimboFactory) server.getPluginManager().getPlugin("limboapi").flatMap(PluginContainer::getInstance).orElseThrow();

        FallbackManager.fallbackManager().init(this);

        fallbackListener = new FallbackListener(this);

        registerEvents();
        registerCommands();
    }

    private void registerEvents() {
        server.getEventManager().register(this, fallbackListener);

        logger.info("Events are registered");
    }

    private void registerCommands() {
        commandManager.register(
                commandManager.metaBuilder("hub")
                        .aliases("lobby")
                        .plugin(this)
                        .build(),
                new HubCommand(this)
        );

        commandManager.register(
                commandManager.metaBuilder("fallbacks")
                        .plugin(this)
                        .build(),
                new FallbacksCommand(this)

        );

        logger.info("Commands are registered");
    }

    private void loadConfiguration() {
        try {
            config = YamlDocument.create(new File(dataDirectory.toFile(), "config.yml"),
                    getClass().getResourceAsStream("/config.yml"),
                    GeneralSettings.DEFAULT,
                    LoaderSettings.builder().setAutoUpdate(true).build(),
                    DumperSettings.DEFAULT);

            config.update();
            config.save();

            messagesConfig = YamlDocument.create(new File(dataDirectory.toFile(), "messages.yml"),
                    getClass().getResourceAsStream("/messages.yml"),
                    GeneralSettings.DEFAULT,
                    LoaderSettings.builder().setAutoUpdate(true).build(),
                    DumperSettings.DEFAULT);

            messagesConfig.update();
            messagesConfig.save();
        } catch (IOException e) {
            logger.error("Cannot create config file");
        }
    }

    public YamlDocument getConfig() {
        return config;
    }

    public YamlDocument getMessagesConfig() {
        return  messagesConfig;
    }

    public Logger getLogger() {
        return logger;
    }

    public LimboFactory getLimboFactory() {
        return limboFactory;
    }

    public ProxyServer getServer() {
        return server;
    }
}