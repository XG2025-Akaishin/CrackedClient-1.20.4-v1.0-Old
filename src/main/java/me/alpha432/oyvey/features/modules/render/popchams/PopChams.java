package me.alpha432.oyvey.features.modules.render.popchams;

import com.google.common.eventbus.Subscribe;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import me.alpha432.oyvey.event.impl.TotemEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.render.popchams.utils.AnimateUtil;
import me.alpha432.oyvey.features.modules.render.popchams.utils.MathUtil;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;

public final class PopChams extends Module {

    public Setting<Double> alphaSpeed = this.register(new Setting<>("AlphaSpeed", 0.2D, 0D, 1D));

    public Setting<Integer> colorR = this.register(new Setting<>("Red", 240, 0, 255));
    public Setting<Integer> colorG = this.register(new Setting<>("Green", 240, 0, 255));
    public Setting<Integer> colorB = this.register(new Setting<>("Blue", 240, 0, 255));
    public Setting<Integer> colorA = this.register(new Setting<>("Alpha", 240, 0, 255));

    private final CopyOnWriteArrayList<Person> popList = new CopyOnWriteArrayList<>();
    public static PopChams INSTANCE;

    public PopChams() {
        super("PopChams", "PopChams", Module.Category.RENDER, true, false, false);
        INSTANCE = this;
    }

    //public Color color = new Color(colorR.getValue(),colorG.getValue(),colorB.getValue(),colorA.getValue());

    @Override
    public void onUpdate() {
        popList.forEach(person -> person.update(popList));
    }

    @Override
    public void onPreRender3D(MatrixStack matrixStack) {
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(770, 771, 0, 1);

        popList.forEach(person -> {
            person.modelPlayer.leftPants.visible = false;
            person.modelPlayer.rightPants.visible = false;
            person.modelPlayer.leftSleeve.visible = false;
            person.modelPlayer.rightSleeve.visible = false;
            person.modelPlayer.jacket.visible = false;
            person.modelPlayer.hat.visible = false;
            renderEntity(matrixStack, person.player, person.modelPlayer, person.getAlpha());
        });

        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
    }


    @Subscribe
    private void onTotemPop(TotemEvent e) {
        if (e.getPlayer().equals(mc.player) || mc.world == null) return;

        PlayerEntity entity = new PlayerEntity(mc.world, BlockPos.ORIGIN, e.getPlayer().bodyYaw, new GameProfile(e.getPlayer().getUuid(), e.getPlayer().getName().getString())) {
            @Override
            public boolean isSpectator() {
                return false;
            }

            @Override
            public boolean isCreative() {
                return false;
            }
        };

        entity.copyPositionAndRotation(e.getPlayer());
        entity.bodyYaw = e.getPlayer().bodyYaw;
        entity.headYaw = e.getPlayer().headYaw;
        entity.handSwingProgress = e.getPlayer().handSwingProgress;
        entity.handSwingTicks = e.getPlayer().handSwingTicks;
        entity.setSneaking(e.getPlayer().isSneaking());
        entity.limbAnimator.setSpeed(e.getPlayer().limbAnimator.getSpeed());
        entity.limbAnimator.pos = e.getPlayer().limbAnimator.getPos();
        popList.add(new Person(entity));
    }

    private void renderEntity(MatrixStack matrices, LivingEntity entity, BipedEntityModel<PlayerEntity> modelBase, int alpha) {
        Color color = new Color(colorR.getValue(),colorG.getValue(),colorB.getValue(),colorA.getValue());

        double x = entity.getX() - mc.getEntityRenderDispatcher().camera.getPos().getX();
        double y = entity.getY() - mc.getEntityRenderDispatcher().camera.getPos().getY();
        double z = entity.getZ() - mc.getEntityRenderDispatcher().camera.getPos().getZ();

        matrices.push();
        matrices.translate((float) x, (float) y, (float) z);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotation(MathUtil.rad(180 - entity.bodyYaw)));
        prepareScale(matrices);

        modelBase.animateModel((PlayerEntity) entity, entity.limbAnimator.getPos(), entity.limbAnimator.getSpeed(), mc.getTickDelta());
        modelBase.setAngles((PlayerEntity) entity, entity.limbAnimator.getPos(), entity.limbAnimator.getSpeed(), entity.age, entity.headYaw - entity.bodyYaw, entity.getPitch());

        RenderSystem.enableBlend();
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        modelBase.render(matrices, buffer, 10, 0, color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alpha / 255f);
        tessellator.draw();
        RenderSystem.disableBlend();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        matrices.pop();
    }

    private static void prepareScale(MatrixStack matrixStack) {
        matrixStack.scale(-1.0F, -1.0F, 1.0F);
        matrixStack.scale(1.6f, 1.8f, 1.6f);
        matrixStack.translate(0.0F, -1.501F, 0.0F);
    }

    private class Person {
        private final PlayerEntity player;
        private final PlayerEntityModel<PlayerEntity> modelPlayer;
        private int alpha;

        public Person(PlayerEntity player) {
            this.player = player;
            modelPlayer = new PlayerEntityModel<>(new EntityRendererFactory.Context(mc.getEntityRenderDispatcher(), mc.getItemRenderer(), mc.getBlockRenderManager(), mc.getEntityRenderDispatcher().getHeldItemRenderer(), mc.getResourceManager(), mc.getEntityModelLoader(), mc.textRenderer).getPart(EntityModelLayers.PLAYER), false);
            modelPlayer.getHead().scale(new Vector3f(-0.3f, -0.3f, -0.3f));
            Color color = new Color(colorR.getValue(),colorG.getValue(),colorB.getValue(),colorA.getValue());
            alpha = color.getAlpha();
        }

        public void update(CopyOnWriteArrayList<Person> arrayList) {
            if (alpha <= 0) {
                arrayList.remove(this);
                player.kill();
                player.remove(Entity.RemovalReason.KILLED);
                player.onRemoved();
                return;
            }
            alpha = (int) (AnimateUtil.animate(alpha, 0, alphaSpeed.getValue()) - 0.2);
        }

        public int getAlpha() {
            return (int) MathUtil.clamp(alpha, 0, 255);
        }
    }
}