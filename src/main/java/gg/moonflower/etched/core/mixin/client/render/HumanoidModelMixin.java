package gg.moonflower.etched.core.mixin.client.render;

import gg.moonflower.etched.common.item.BoomboxItem;
import gg.moonflower.etched.core.registry.EtchedComponents;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public class HumanoidModelMixin<T extends HumanoidRenderState> {

    @Final
    @Shadow
    public ModelPart leftArm;

    @Final
    @Shadow
    public ModelPart rightArm;

    @Unique
    private static @Nullable HumanoidArm etched$getPlayingArm(HumanoidRenderState state) {
        if (BoomboxItem.hasRecord(state.rightHandItemStack) && !state.rightHandItemStack.has(EtchedComponents.PAUSED.get())) {
            return HumanoidArm.RIGHT;
        }
        if (BoomboxItem.hasRecord(state.leftHandItemStack) && !state.leftHandItemStack.has(EtchedComponents.PAUSED.get())) {
            return HumanoidArm.LEFT;
        }
        return null;
    }


    @Inject(method = "poseRightArm", at = @At("HEAD"), cancellable = true)
    public void poseRightArm(T state, CallbackInfo ci) {
        if (etched$getPlayingArm(state) == HumanoidArm.RIGHT) {
            this.rightArm.xRot = (float) Math.PI;
            this.rightArm.yRot = 0.0F;
            this.rightArm.zRot = -0.610865F;
            ci.cancel();
        }
    }

    @Inject(method = "poseLeftArm", at = @At("HEAD"), cancellable = true)
    public void poseLeftArm(T state, CallbackInfo ci) {
        if (etched$getPlayingArm(state) == HumanoidArm.LEFT) {
            this.leftArm.xRot = (float) Math.PI;
            this.leftArm.yRot = 0.0F;
            this.leftArm.zRot = 0.610865F;
            ci.cancel();
        }
    }

    @Inject(method = "setupAttackAnimation", at = @At("RETURN"))
    public void etched$restoreBoomboxArmAfterAttack(T state, CallbackInfo ci) {
        HumanoidArm arm = etched$getPlayingArm(state);
        if (arm == HumanoidArm.RIGHT) {
            this.rightArm.xRot = (float) Math.PI;
            this.rightArm.yRot = 0.0F;
            this.rightArm.zRot = -0.610865F;
        } else if (arm == HumanoidArm.LEFT) {
            this.leftArm.xRot = (float) Math.PI;
            this.leftArm.yRot = 0.0F;
            this.leftArm.zRot = 0.610865F;
        }
    }

}
