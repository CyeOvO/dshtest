package io.github.cyeovo.dshtest;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import io.github.cyeovo.dshtest.network.WeatherTimeHandler;
import io.github.cyeovo.dshtest.network.WeatherTimePayload;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

// The value here should match an entry in the META-INF/neoforge.toml file
@Mod(DSHTest.MODID)
public class DSHTest {
    // Define mod id in a common place for everything to reference
    // Must be lowercase and must match mod_id in gradle.properties
    public static final String MODID = "dshtest";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public DSHTest(IEventBus modEventBus, ModContainer modContainer) {
        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        // Register the network payload used by the weather/time screen
        modEventBus.addListener(this::registerPayloads);
    }

    /**
     * Registers the client -&gt; server payload the weather/time screen sends when
     * the player changes the weather or the time.
     */
    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToServer(
                WeatherTimePayload.TYPE,
                WeatherTimePayload.STREAM_CODEC,
                WeatherTimeHandler::handle);
    }
}
