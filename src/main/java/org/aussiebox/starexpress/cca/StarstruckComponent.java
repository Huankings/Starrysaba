package org.aussiebox.starexpress.cca;

import dev.doctor4t.wathe.record.GameRecordManager;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.aussiebox.starexpress.StarryExpress;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

public class StarstruckComponent implements AutoSyncedComponent, ServerTickingComponent {
    public static final ComponentKey<StarstruckComponent> KEY = ComponentRegistry.getOrCreate(StarryExpress.id("starstruck"), StarstruckComponent.class);
    private final Player player;
    public int ticks = 0;

    public StarstruckComponent(Player player) {
        this.player = player;
    }

    @Override
    public void serverTick() {
        if (this.ticks > 0) {
            --this.ticks;
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.serverLevel().sendParticles(StarryExpress.STARSTRUCK_SPARKLE, serverPlayer.getX(), serverPlayer.getY()+0.2, serverPlayer.getZ(), player.getRandom().nextIntBetweenInclusive(1, 2), 0.2, 0, 0.2, 0);
                if (this.ticks == 0) {
                    // 星界能力自然结束时在服务端统一记录，确保实时回放和总结算都能稳定出现。
                    GameRecordManager.recordGlobalEvent(serverPlayer.serverLevel(), StarryExpress.id("starstruck_ability_end"), serverPlayer, null);
                }
            }
            this.sync();
        }
    }

    public void sync() {
        KEY.sync(this.player);
    }

    public void reset() {
        this.ticks = 0;
        sync();
    }

    public void setTicks(int ticks) {
        this.ticks = ticks;
        this.sync();
    }

    public void writeToNbt(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registryLookup) {
        tag.putInt("ticks", this.ticks);
    }

    public void readFromNbt(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registryLookup) {
        this.ticks = tag.contains("ticks") ? tag.getInt("ticks") : 0;
    }
}
