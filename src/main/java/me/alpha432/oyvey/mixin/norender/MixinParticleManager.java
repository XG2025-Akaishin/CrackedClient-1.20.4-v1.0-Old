package me.alpha432.oyvey.mixin.norender;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.alpha432.oyvey.event.impl.norender.ParticleEvent;
import static me.alpha432.oyvey.util.traits.Util.EVENT_BUS;

@Mixin(ParticleManager.class)
public class MixinParticleManager {
    @Inject(at = @At("HEAD"), method = "addParticle(Lnet/minecraft/client/particle/Particle;)V", cancellable = true)
    public void onAddParticle(Particle particle, CallbackInfo ci) {
        ParticleEvent.AddParticle event = new ParticleEvent.AddParticle(particle);
        EVENT_BUS.post(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "addEmitter(Lnet/minecraft/entity/Entity;Lnet/minecraft/particle/ParticleEffect;)V", cancellable = true)
    public void onAddEmmiter(Entity entity, ParticleEffect particleEffect, CallbackInfo ci) {
        ParticleEvent.AddEmmiter event = new ParticleEvent.AddEmmiter(particleEffect);
        EVENT_BUS.post(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "addEmitter(Lnet/minecraft/entity/Entity;Lnet/minecraft/particle/ParticleEffect;I)V", cancellable = true)
    public void onAddEmmiterAged(Entity entity, ParticleEffect particleEffect, int maxAge, CallbackInfo ci) {
        ParticleEvent.AddEmmiter event = new ParticleEvent.AddEmmiter(particleEffect);
        EVENT_BUS.post(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}