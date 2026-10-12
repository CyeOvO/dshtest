package io.github.cyeovo.dshtest.client;

import com.mojang.blaze3d.platform.InputConstants;

import io.github.cyeovo.dshtest.DSHTest;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

/**
 * Client-side key handling: pressing {@code X} opens the weather/time screen.
 * <p>
 * Both handlers sit behind the same {@code @EventBusSubscriber}; FML inspects the
 * event type of each {@code @SubscribeEvent} method and routes it to the mod bus
 * or the game bus automatically ({@code RegisterKeyMappingsEvent} is an
 * {@code IModBusEvent}, so it goes to the mod bus).
 * <p>
 * The key shows up in Options &gt; Controls where the player can rebind it.
 */
@EventBusSubscriber(modid = DSHTest.MODID, value = Dist.CLIENT)
public final class DSHTestKeybinds {

    /** Translation key for the key's category, shown in the Controls screen. */
    public static final String KEY_CATEGORY = "key.categories." + DSHTest.MODID;
    private static final String KEY_OPEN_SCREEN = "key." + DSHTest.MODID + ".open_weather_time";

    public static final KeyMapping OPEN_WEATHER_TIME_SCREEN = new KeyMapping(
            KEY_OPEN_SCREEN,
            // Only active while in-game, so X does not fire in menus.
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_X,
            KEY_CATEGORY);

    private DSHTestKeybinds() {}

    /** Registers the key mapping so it appears in the Controls screen. */
    @SubscribeEvent
    static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_WEATHER_TIME_SCREEN);
    }

    /** Opens the screen when the key is pressed. */
    @SubscribeEvent
    static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();

        // consumeClick() returns true once per press.
        while (OPEN_WEATHER_TIME_SCREEN.consumeClick()) {
            // Only open in-world, and not on top of another screen.
            if (minecraft.screen == null && minecraft.level != null && minecraft.player != null) {
                minecraft.setScreen(new WeatherTimeScreen());
            }
        }
    }
}
