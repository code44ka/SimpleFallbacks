package SunShineGroup.simpleFallbacks;

import SunShineGroup.simpleFallbacks.Listener.FallbackListener;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
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

    private LimboFactory limboFactory;

    private FallbackListener fallbackListener;

    // CONFIGS
    private YamlDocument config;

    @Inject
    public SimpleFallbacks(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;

        loadConfiguration();

        logger.info("Plugin initialized");
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent e) {
        limboFactory = (LimboFactory) server.getPluginManager().getPlugin("limboapi").flatMap(PluginContainer::getInstance).orElseThrow();
        fallbackListener = new FallbackListener(this);
        fallbackListener.CreateLimbo();
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

        } catch (IOException e) {
            logger.error("Cannot create config file");
        }
    }

    public YamlDocument getConfig() {
        return config;
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