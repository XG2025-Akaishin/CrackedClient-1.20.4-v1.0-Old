package me.alpha432.oyvey.manager.shader.utilis;

import net.minecraft.client.gl.Framebuffer;

public interface IShaderEffect {
    void addFakeTargetHook(String name, Framebuffer buffer);
}
