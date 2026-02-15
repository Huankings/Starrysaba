package org.aussiebox.starexpress.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class StarstruckSparkleParticle extends SimpleAnimatedParticle {

    StarstruckSparkleParticle(ClientLevel clientLevel, double d, double e, double f, double g, double h, double i, SpriteSet spriteSet) {
        super(clientLevel, d, e, f, spriteSet, 0.0125F);
        this.friction = 0.0F;
        this.gravity = 0.0F;
        this.xd = g;
        this.yd = h;
        this.zd = i;
        this.quadSize *= 0.75F;
        this.lifetime = 30 + this.random.nextInt(12);
        this.hasPhysics = true;
        this.setFadeColor(Color.WHITE.getRGB());
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public void tick() {
        this.setSprite(this.sprites.get(this.age, this.lifetime));
        if (this.age++ >= this.lifetime)
            this.remove();
    }

    @Override
    public void move(double d, double e, double f) {
        this.setBoundingBox(this.getBoundingBox().move(d, e, f));
        this.setLocationFromBoundingbox();
    }

    @Environment(EnvType.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(@NotNull SimpleParticleType simpleParticleType, @NotNull ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
            return new StarstruckSparkleParticle(clientLevel, d, e, f, g, h, i, this.sprites);
        }
    }
}