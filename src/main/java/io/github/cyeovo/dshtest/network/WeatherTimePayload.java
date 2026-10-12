package io.github.cyeovo.dshtest.network;

import io.github.cyeovo.dshtest.DSHTest;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Sent from the client to the server when the player changes the weather or the
 * time in the control screen.
 * <p>
 * Weather and time are server-authoritative, so the client cannot apply them
 * locally; it has to ask the server to do it.
 *
 * @param action which value to change
 * @param value  the new value; its meaning depends on {@link Action}
 */
public record WeatherTimePayload(Action action, int value) implements CustomPacketPayload {

    /** The actions the control screen can request. */
    public enum Action {
        /** Set daytime to {@code value} ticks (0-23999). */
        SET_TIME,
        /** Advance daytime by {@code value} ticks. */
        ADD_TIME,
        /** Clear weather for {@code value} ticks. */
        WEATHER_CLEAR,
        /** Rain for {@code value} ticks. */
        WEATHER_RAIN,
        /** Thunder for {@code value} ticks. */
        WEATHER_THUNDER;

        private static final Action[] VALUES = values();

        /**
         * Looks up an action by its ordinal.
         * <p>
         * A client could send an out-of-range value, so this validates rather
         * than indexing the array blindly and crashing the server.
         */
        public static Action byId(int id) {
            if (id < 0 || id >= VALUES.length) {
                throw new IllegalArgumentException("Unknown weather/time action id: " + id);
            }
            return VALUES[id];
        }
    }

    public static final CustomPacketPayload.Type<WeatherTimePayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(DSHTest.MODID, "weather_time"));

    public static final StreamCodec<RegistryFriendlyByteBuf, WeatherTimePayload> STREAM_CODEC = StreamCodec.composite(
            // Send the action as its ordinal; validated on decode below.
            ByteBufCodecs.VAR_INT.map(Action::byId, Action::ordinal),
            WeatherTimePayload::action,
            ByteBufCodecs.VAR_INT,
            WeatherTimePayload::value,
            WeatherTimePayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
