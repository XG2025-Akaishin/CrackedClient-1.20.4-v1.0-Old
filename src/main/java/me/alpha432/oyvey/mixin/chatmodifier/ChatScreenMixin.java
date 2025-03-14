package me.alpha432.oyvey.mixin.chatmodifier;

import me.alpha432.oyvey.CrackedClient;
import me.alpha432.oyvey.features.modules.chat.chatmodifier.ChatModifier;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ChatScreen.class, priority = 1001)
public abstract class ChatScreenMixin {
    @Shadow protected TextFieldWidget chatField;

    @Inject(method = "init", at = @At(value = "RETURN"))
    private void onInit(CallbackInfo info) {
        if (ChatModifier.getInstance().isInfiniteChatBox()) chatField.setMaxLength(Integer.MAX_VALUE);
    }
}
