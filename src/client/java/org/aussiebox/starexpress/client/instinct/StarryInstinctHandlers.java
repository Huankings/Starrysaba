package org.aussiebox.starexpress.client.instinct;

import org.aussiebox.starexpress.client.instinct.allergic.AllergicInstinctHandler;

public final class StarryInstinctHandlers {
    public static final int PRIORITY_STATUS_INSTINCT_COLOR = 100;

    private StarryInstinctHandlers() {
    }

    public static void register() {
        AllergicInstinctHandler.register();
    }
}
