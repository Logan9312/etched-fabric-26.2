package gg.moonflower.etched.gametest;

import java.lang.reflect.Method;
import java.util.ArrayList;

import net.fabricmc.fabric.api.gametest.v1.CustomTestMethodInvoker;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.behavior.VillagerGoalPackages;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.MixinEnvironment;

import gg.moonflower.etched.common.blockentity.AlbumJukeboxBlockEntity;
import gg.moonflower.etched.common.blockentity.RadioBlockEntity;
import gg.moonflower.etched.common.entity.MinecartJukebox;
import gg.moonflower.etched.core.registry.EtchedBlocks;
import gg.moonflower.etched.core.registry.EtchedEntities;
import gg.moonflower.etched.core.registry.EtchedVillagers;

public final class EtchedGameTests implements CustomTestMethodInvoker {
	@GameTest
	public void auditServerMixins(GameTestHelper helper) {
		MixinEnvironment.getCurrentEnvironment().audit();
		helper.succeed();
	}

	@GameTest
	public void vanillaVillagerLoadsAndTicks(GameTestHelper helper) {
		Villager villager = helper.spawn(EntityTypes.VILLAGER, 1, 1, 1);
		helper.runAfterDelay(5, () -> {
			helper.assertEntityPresent(EntityTypes.VILLAGER);
			if (!villager.isAlive()) {
				throw helper.assertionException("Villager was removed while its AI was ticking");
			}
			helper.succeed();
		});
	}

	@GameTest(maxTicks = 160)
	public void villagerPopulationSurvivesExtendedAiTicks(GameTestHelper helper) {
		var villagers = new ArrayList<Villager>(32);
		for (int index = 0; index < 32; index++) {
			villagers.add(helper.spawn(EntityTypes.VILLAGER, 1 + index % 8, 1, 1 + index / 8));
		}
		helper.runAfterDelay(100, () -> {
			long survivors = villagers.stream().filter(Villager::isAlive).count();
			helper.assertTrue(survivors == villagers.size(),
					"Expected all 32 villagers to survive 100 AI ticks, but only " + survivors + " remained");
			helper.succeed();
		});
	}

	@GameTest
	public void bardWorkPackageBuildsSuccessfully(GameTestHelper helper) {
		var bardKey = ResourceKey.create(Registries.VILLAGER_PROFESSION, EtchedVillagers.BARD.getId());
		var bard = helper.getLevel().registryAccess()
				.lookupOrThrow(Registries.VILLAGER_PROFESSION)
				.getOrThrow(bardKey);
		var workPackage = VillagerGoalPackages.getWorkPackage(bard, 0.5F);
		helper.assertTrue(!workPackage.isEmpty(), "Bard work package was empty");
		helper.succeed();
	}

	@GameTest
	public void etchedBlocksLoadWithExpectedBlockEntities(GameTestHelper helper) {
		helper.setBlock(1, 1, 1, EtchedBlocks.ETCHING_TABLE.get());
		helper.setBlock(2, 1, 1, EtchedBlocks.ALBUM_JUKEBOX.get());
		helper.setBlock(3, 1, 1, EtchedBlocks.RADIO.get());
		helper.assertBlockPresent(EtchedBlocks.ETCHING_TABLE.get(), 1, 1, 1);
		helper.assertBlockPresent(EtchedBlocks.ALBUM_JUKEBOX.get(), 2, 1, 1);
		helper.assertBlockPresent(EtchedBlocks.RADIO.get(), 3, 1, 1);
		helper.getBlockEntity(new BlockPos(2, 1, 1), AlbumJukeboxBlockEntity.class);
		helper.getBlockEntity(new BlockPos(3, 1, 1), RadioBlockEntity.class);
		helper.succeed();
	}

	@GameTest
	public void jukeboxMinecartSpawnsAndTicks(GameTestHelper helper) {
		MinecartJukebox minecart = helper.spawn(EtchedEntities.JUKEBOX_MINECART.get(), 1, 1, 1);
		helper.runAfterDelay(5, () -> {
			helper.assertTrue(minecart.isAlive(), "Etched jukebox minecart did not survive its first ticks");
			helper.assertEntityPresent(EtchedEntities.JUKEBOX_MINECART.get());
		helper.succeed();
		});
	}

	@Override
	public void invokeTestMethod(GameTestHelper helper, Method method) throws ReflectiveOperationException {
		helper.setBlock(0, 0, 0, Blocks.AIR);
		method.invoke(this, helper);
	}
}
