package gg.moonflower.etched.gametest;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import org.spongepowered.asm.mixin.MixinEnvironment;
import gg.moonflower.etched.api.sound.AbstractOnlineSoundInstance;
import gg.moonflower.etched.api.sound.StopListeningSound;
import gg.moonflower.etched.api.sound.source.AudioSource;
import net.minecraft.sounds.SoundSource;

@SuppressWarnings("UnstableApiUsage")
public final class EtchedClientGameTests implements FabricClientGameTest {
	@Override
	public void runTest(ClientGameTestContext context) {
		MixinEnvironment.getCurrentEnvironment().audit();
		checkOnlineSoundResolution();
		try (TestSingleplayerContext singleplayer = context.worldBuilder().create()) {
			context.waitFor(client -> client.level != null);
			context.waitTicks(40);
			context.takeScreenshot("etched-client-smoke-test");
		}
	}

	private static void checkOnlineSoundResolution() {
		// Resolution must retain our synthetic event without a resource-pack lookup or download.
		var sound = new AbstractOnlineSoundInstance("https://example.invalid/test.ogg", "Test track",
				16, SoundSource.RECORDS, null, AudioSource.AudioFileType.FILE, false);
		int[] stopped = {0};
		var wrapper = StopListeningSound.create(sound, () -> stopped[0]++);
		var event = wrapper.getOrResolve(null);
		if (event == null || sound.getSoundEvent() != event || wrapper.getSoundEvent() != event
				|| !(wrapper.getSound() instanceof AbstractOnlineSoundInstance.OnlineSound)) {
			throw new AssertionError("Online sound resolution lost its event or stream source");
		}
		wrapper.onStop();
		wrapper.stopListening();
		wrapper.onStop();
		if (stopped[0] != 1) throw new AssertionError("Sound stop listener was not forwarded/suppressed correctly");
	}
}
