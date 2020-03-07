package mod.linguardium.itemcompat.mixin;


import jdk.internal.jline.internal.Nullable;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.List;

import static mod.linguardium.itemcompat.ShearsTag.SHEARS_ITEM;
import static net.minecraft.block.BeehiveBlock.dropHoneycomb;


@Mixin(BeehiveBlock.class)
public abstract class HiveShearMixin extends BlockWithEntity {
	public boolean itemCompat_hasBees(World world, BlockPos pos) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof BeehiveBlockEntity) {
			BeehiveBlockEntity beehiveBlockEntity = (BeehiveBlockEntity)blockEntity;
			return !beehiveBlockEntity.hasNoBees();
		} else {
			return false;
		}
	}
	public void itemCompat_angerNearbyBees(World world, BlockPos pos) {
		List<BeeEntity> list = world.getNonSpectatingEntities(BeeEntity.class, (new Box(pos)).expand(8.0D, 6.0D, 8.0D));
		if (!list.isEmpty()) {
			List<PlayerEntity> list2 = world.getNonSpectatingEntities(PlayerEntity.class, (new Box(pos)).expand(8.0D, 6.0D, 8.0D));
			int i = list2.size();
			Iterator var6 = list.iterator();

			while(var6.hasNext()) {
				BeeEntity beeEntity = (BeeEntity)var6.next();
				if (beeEntity.getTarget() == null) {
					beeEntity.setBeeAttacker((Entity)list2.get(world.random.nextInt(i)));
				}
			}
		}
	}
	public void itemCompat_takeHoney(World world, BlockState state, BlockPos pos, @Nullable PlayerEntity player, BeehiveBlockEntity.BeeState beeState) {
		this.itemCompat_takeHoney(world, state, pos);
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof BeehiveBlockEntity) {
			BeehiveBlockEntity beehiveBlockEntity = (BeehiveBlockEntity)blockEntity;
			beehiveBlockEntity.angerBees(player, state, beeState);
		}

	}

	public void itemCompat_takeHoney(World world, BlockState state, BlockPos pos) {
		world.setBlockState(pos, (BlockState)state.with(HONEY_LEVEL, 0), 3);
	}

	@Shadow
	public static IntProperty HONEY_LEVEL;

	public HiveShearMixin(Settings settings) {
		super(settings);
	}

	@Inject(at = @At("HEAD"), method = "onUse", cancellable = true)
	public void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable info) {
		ItemStack itemStack = player.getStackInHand(hand);
		ItemStack itemStack2 = itemStack.copy();
		int i = (Integer)state.get(HONEY_LEVEL);
		if (i >= 5) {
			if (itemStack.getItem() instanceof ShearsItem || itemStack.getItem().isIn(SHEARS_ITEM)) {
				world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BLOCK_BEEHIVE_SHEAR, SoundCategory.NEUTRAL, 1.0F, 1.0F);
				dropHoneycomb(world, pos);
				itemStack.damage(1, player, (playerx) -> {
					playerx.sendToolBreakStatus(hand);
				});
				if (!CampfireBlock.isLitCampfireInRange(world, pos, 5)) {
					if (this.itemCompat_hasBees(world, pos)) {
						this.itemCompat_angerNearbyBees(world, pos);
					}

					this.itemCompat_takeHoney(world, state, pos, player, BeehiveBlockEntity.BeeState.EMERGENCY);
				} else {
					this.itemCompat_takeHoney(world, state, pos);
					if (player instanceof ServerPlayerEntity) {
						Criterions.SAFELY_HARVEST_HONEY.test((ServerPlayerEntity)player, pos, itemStack2);
					}
				}
				info.setReturnValue(ActionResult.SUCCESS);
			}
		}
	}
}

