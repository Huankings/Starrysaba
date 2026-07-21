package org.aussiebox.starexpress.config;

import blue.endless.jankson.Comment;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.*;

@Sync(Option.SyncMode.OVERRIDE_CLIENT)
@Config(name = "starexpress-server", wrapperName = "StarryExpressServerConfig")
public class ServerConfig {

    @SectionHeader("role_config")

    @Comment("Config options related to the Allergic modifier.")
    @Nest public AllergicConfig allergicConfig = new AllergicConfig();

    public static class AllergicConfig {

        @Comment("The chance of Allergic players receiving no effects upon their allergy triggering. Set to 0 to disable.")
        public int nothingChance = 3;

        @Comment("The chance of Allergic players receiving instinct upon their allergy triggering. Set to 0 to disable.")
        public int instinctChance = 1;

        @Comment("The chance of Allergic players receiving armor upon their allergy triggering. Set to 0 to disable.")
        public int armorChance = 1;

        @Comment("The chance of Allergic players being poisoned upon their allergy triggering. Set to 0 to disable.")
        public int poisonChance = 1;

        @Comment("The duration, in seconds, of the Allergic's instinct effect.")
        public int instinctDuration = 3;

    }
}
