package gg.moonflower.etched.test;

import gg.moonflower.etched.core.mixin.VillagerGoalPackagesMixin;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.behavior.VillagerGoalPackages;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public final class MixinTargetParityCheck {
    private MixinTargetParityCheck() {
    }

    public static void run() {
        Method target = Arrays.stream(VillagerGoalPackages.class.getDeclaredMethods())
                .filter(method -> method.getName().equals("getWorkPackage"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Minecraft no longer has VillagerGoalPackages.getWorkPackage"));

        require(Modifier.isStatic(target.getModifiers()), "Villager work-package target is no longer static");
        require(Arrays.equals(target.getParameterTypes(), new Class<?>[]{Holder.class, float.class}),
                "Villager work-package target signature changed: " + Arrays.toString(target.getParameterTypes()));

        assertCaptureHandler("capture");
        assertCaptureHandler("clear");
    }

    private static void assertCaptureHandler(String name) {
        try {
            VillagerGoalPackagesMixin.class.getDeclaredMethod(
                    name, Holder.class, float.class, CallbackInfoReturnable.class);
        } catch (NoSuchMethodException exception) {
            throw new AssertionError("Villager mixin " + name + " handler does not match Minecraft's Holder-based signature", exception);
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
