package io.github.cyeovo.dshtest.client;

import io.github.cyeovo.dshtest.network.WeatherTimePayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * In-game screen for changing the world time and weather.
 * <p>
 * Opened with the {@code X} key (rebindable in Options &gt; Controls).
 * The screen itself never changes the world directly: it sends a
 * {@link WeatherTimePayload} to the server, which performs the change.
 */
@OnlyIn(Dist.CLIENT)
public class WeatherTimeScreen extends Screen {

    /** Minecraft daytime runs from 0 to 23999 ticks (one full day). */
    private static final int TICKS_PER_DAY = 24000;

    /** Default duration (in ticks) used by the weather buttons: 10 minutes. */
    private static final int DEFAULT_WEATHER_DURATION = 12000;

    private static final int WIDGET_WIDTH = 200;

    /** Where the time slider currently sits, in ticks. */
    private int sliderTicks;

    public WeatherTimeScreen() {
        super(Component.translatable("dshtest.screen.weather_time.title"));
        // Start the slider at the current time so it does not jump on open.
        this.sliderTicks = currentDayTime();
    }

    private static int currentDayTime() {
        var level = net.minecraft.client.Minecraft.getInstance().level;
        if (level == null) {
            return 0;
        }
        // getDayTime() grows forever; take the position within the current day.
        return (int) Math.floorMod(level.getDayTime(), TICKS_PER_DAY);
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int left = centerX - WIDGET_WIDTH / 2;
        int y = this.height / 4;

        // ---- Time ----
        y += 24;
        this.addRenderableWidget(new TimeSlider(left, y, WIDGET_WIDTH, 20, this.sliderTicks));

        y += 24;
        this.addRenderableWidget(Button.builder(
                        Component.translatable("dshtest.button.set_time"),
                        button -> send(WeatherTimePayload.Action.SET_TIME, this.sliderTicks))
                .bounds(left, y, WIDGET_WIDTH, 20)
                .build());

        // ---- Weather ----
        y += 32;
        int halfWidth = (WIDGET_WIDTH - 4) / 2;

        this.addRenderableWidget(Button.builder(
                        Component.translatable("dshtest.button.weather_clear"),
                        button -> send(WeatherTimePayload.Action.WEATHER_CLEAR, DEFAULT_WEATHER_DURATION))
                .bounds(left, y, halfWidth, 20)
                .build());

        this.addRenderableWidget(Button.builder(
                        Component.translatable("dshtest.button.weather_rain"),
                        button -> send(WeatherTimePayload.Action.WEATHER_RAIN, DEFAULT_WEATHER_DURATION))
                .bounds(left + halfWidth + 4, y, halfWidth, 20)
                .build());

        y += 24;
        this.addRenderableWidget(Button.builder(
                        Component.translatable("dshtest.button.weather_thunder"),
                        button -> send(WeatherTimePayload.Action.WEATHER_THUNDER, DEFAULT_WEATHER_DURATION))
                .bounds(left, y, WIDGET_WIDTH, 20)
                .build());

        // ---- Close ----
        y += 32;
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> this.onClose())
                .bounds(left, y, WIDGET_WIDTH, 20)
                .build());
    }

    private void send(WeatherTimePayload.Action action, int value) {
        PacketDistributor.sendToServer(new WeatherTimePayload(action, value));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 4 - 20, 0xFFFFFF);

        // Show what the slider currently points at, in human terms.
        guiGraphics.drawCenteredString(this.font,
                Component.translatable("dshtest.screen.current_time", describeTime(this.sliderTicks)),
                this.width / 2, this.height / 4 + 6, 0xA0A0A0);
    }

    /** Turns a tick value into a readable in-game clock time (e.g. "06:00"). */
    private static Component describeTime(int ticks) {
        // Minecraft tick 0 is 06:00, so shift by 6000 ticks before converting.
        // 24000 ticks == 24 hours, i.e. 1000 ticks per in-game hour.
        int totalMinutes = (int) (((ticks + 6000L) % TICKS_PER_DAY) * 24L * 60L / TICKS_PER_DAY);
        int hours = (totalMinutes / 60) % 24;
        int minutes = totalMinutes % 60;
        return Component.literal(String.format("%02d:%02d", hours, minutes));
    }

    @Override
    public boolean isPauseScreen() {
        // Do not pause the game, so the player can watch the weather change.
        return false;
    }

    /** Slider that selects a time of day, in ticks. */
    @OnlyIn(Dist.CLIENT)
    private class TimeSlider extends AbstractSliderButton {

        TimeSlider(int x, int y, int width, int height, int initialTicks) {
            super(x, y, width, height, CommonComponents.EMPTY,
                    (double) initialTicks / (TICKS_PER_DAY - 1));
            this.updateMessage();
        }

        private int ticks() {
            return (int) Math.round(this.value * (TICKS_PER_DAY - 1));
        }

        @Override
        protected void updateMessage() {
            this.setMessage(Component.translatable("dshtest.slider.time", describeTime(this.ticks())));
        }

        @Override
        protected void applyValue() {
            // Track the value so the "Set time" button knows what to send.
            WeatherTimeScreen.this.sliderTicks = this.ticks();
        }
    }
}
