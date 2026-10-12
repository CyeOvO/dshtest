package io.github.cyeovo.dshtest;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Configuration for DSH Test.
 * <p>
 * These options are edited through the in-game config screen: Mods &gt; DSH Test
 * &gt; Config.
 */
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    /** How long the weather buttons make the weather last, in ticks. */
    public static final ModConfigSpec.IntValue WEATHER_DURATION_TICKS = BUILDER
            .comment("How long the Clear / Rain / Thunder buttons make the weather last, in ticks.",
                    "20 ticks = 1 second, so 12000 ticks = 10 minutes.")
            .defineInRange("weatherDurationTicks", 12000, 1, Integer.MAX_VALUE);

    /** Whether to log each weather/time change to the server log. */
    public static final ModConfigSpec.BooleanValue LOG_CHANGES = BUILDER
            .comment("Whether to log every weather/time change to the server log.")
            .define("logChanges", true);

    static final ModConfigSpec SPEC = BUILDER.build();
}
