package org.aussiebox.starexpress.util;

import net.minecraft.util.Mth;

public class StarryExpressUtil {

    public static int lerpColor(int color1, int color2, float delta) {
        delta = Mth.clamp(delta, 0.0F, 1.0F);

        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;

        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        int r = (int) Mth.lerp(delta, (float) r1, (float) r2);
        int g = (int) Mth.lerp(delta, (float) g1, (float) g2);
        int b = (int) Mth.lerp(delta, (float) b1, (float) b2);

        return (r << 16) | (g << 8) | b;
    }

}
