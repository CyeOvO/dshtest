package io.github.cyeovo.dshtest.network;

import io.github.cyeovo.dshtest.Config;
import io.github.cyeovo.dshtest.DSHTest;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Applies weather / time changes requested by the client.
 * <p>
 * This runs on the logical server, because weather and time are
 * server-authoritative. It mirrors what the vanilla {@code /weather} and
 * {@code /time} commands do, including their permission requirement (level 2),
 * so a normal player on a multiplayer server cannot use this to change the
 * weather of someone else's world.
 */
public final class WeatherTimeHandler {

    /** Same permission level the vanilla /weather and /time commands require. */
    private static final int REQUIRED_PERMISSION_LEVEL = 2;

    private WeatherTimeHandler() {}

    public static void handle(WeatherTimePayload payload, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) {
            return;
        }

        if (!hasPermission(player)) {
            player.displayClientMessage(
                    Component.translatable("dshtest.message.no_permission"), false);
            return;
        }

        ServerLevel level = player.server.overworld();
        int duration = clampWeatherDuration(payload.value());

        switch (payload.action()) {
            case SET_TIME -> setTime(level, payload.value());
            case ADD_TIME -> addTime(level, payload.value());
            case WEATHER_CLEAR -> level.setWeatherParameters(duration, 0, false, false);
            case WEATHER_RAIN -> level.setWeatherParameters(0, duration, true, false);
            case WEATHER_THUNDER -> level.setWeatherParameters(0, duration, true, true);
        }

        if (Config.LOG_CHANGES.getAsBoolean()) {
            DSHTest.LOGGER.info("dshtest: {} applied by {} (value {})",
                    payload.action(), player.getName().getString(), payload.value());
        }
    }

    /**
     * Turns a requested weather duration into the value actually applied.
     * <p>
     * The client sends a nominal duration; the server clamps it to the range
     * configured by the operator, so a modified client cannot set an absurd
     * weather duration.
     */
    private static int clampWeatherDuration(int requested) {
        int configured = Config.WEATHER_DURATION_TICKS.getAsInt();
        // Negative values would mean "keep the current weather" to vanilla, which
        // is not something this screen offers, so treat anything <= 0 as the default.
        return requested <= 0 ? configured : Math.min(requested, configured);
    }

    /**
     * Checks the player's command permission level.
     * <p>
     * Uses the vanilla permission check rather than {@code ServerPlayer}'s
     * protected {@code getPermissionLevel()}.
     */
    private static boolean hasPermission(ServerPlayer player) {
        return player.hasPermissions(REQUIRED_PERMISSION_LEVEL);
    }

    /**
     * Sets the daytime on every dimension, like {@code /time set} does.
     */
    private static void setTime(ServerLevel level, int time) {
        for (ServerLevel serverLevel : level.getServer().getAllLevels()) {
            serverLevel.setDayTime(time);
        }
    }

    /** Advances the daytime on every dimension, like {@code /time add} does. */
    private static void addTime(ServerLevel level, int amount) {
        for (ServerLevel serverLevel : level.getServer().getAllLevels()) {
            serverLevel.setDayTime(serverLevel.getDayTime() + amount);
        }
    }
}
