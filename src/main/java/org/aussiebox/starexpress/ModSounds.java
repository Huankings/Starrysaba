package org.aussiebox.starexpress;

import dev.doctor4t.ratatouille.util.registrar.SoundEventRegistrar;
import net.minecraft.sounds.SoundEvent;

public interface ModSounds {

    SoundEventRegistrar registrar = new SoundEventRegistrar(StarryExpress.MOD_ID);

    SoundEvent BLOCK_CIRCUITWEAVER_PLUSH_HONK = registrar.create("block.circuitweaver_plush.honk");

    static void init() {
        registrar.registerEntries();
    }

}
