package gg.moonflower.etched.gametest;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import org.spongepowered.asm.mixin.MixinEnvironment;

@SuppressWarnings("UnstableApiUsage")
public final class EtchedClientGameTests implements FabricClientGameTest {
	@Override
	public void runTest(ClientGameTestContext context) {
		MixinEnvironment.getCurrentEnvironment().audit();
		try (TestSingleplayerContext singleplayer = context.worldBuilder().create()) {
			context.waitFor(client -> client.level != null);
			context.waitTicks(40);
			context.takeScreenshot("etched-client-smoke-test");
		}
	}
}
