package mod.linguardium.itemcompat.mixin;


import jdk.internal.jline.internal.Nullable;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.*;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


import static mod.linguardium.itemcompat.ShearsTag.SHEARS_ITEM;


@Mixin(BeehiveBlock.class)
public abstract class HiveShearMixin extends BlockWithEntity {
	@Shadow abstract public boolean hasBees(World world, BlockPos pos);
	@Shadow abstract public void angerNearbyBees(World world, BlockPos pos);
	@Shadow abstract public void takeHoney(World world, BlockState state, BlockPos pos);
	@Shadow abstract public void takeHoney(World world, BlockState state, BlockPos pos, @Nullable PlayerEntity player, BeehiveBlockEntity.BeeState beeState);

	public HiveShearMixin(Settings settings) {
		super(settings);
	}

	@Inject(at = @At("RETURN"), method = "onUse", cancellable = true)
	public void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> info) {
		if (info.getReturnValue().equals(ActionResult.PASS)) {
			ItemStack itemStack = player.getStackInHand(hand);
			ItemStack itemStack2 = itemStack.copy();
			int i = BeehiveBlockEntity.getHoneyLevel(state);
			if (i >= 5) {
				if (itemStack.getItem() instanceof ShearsItem || itemStack.getItem().isIn(SHEARS_ITEM)) {
					world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BLOCK_BEEHIVE_SHEAR, SoundCategory.NEUTRAL, 1.0F, 1.0F);
					BeehiveBlock.dropHoneycomb(world, pos);
					itemStack.damage(1, player, (playerx) -> {
						playerx.sendToolBreakStatus(hand);
					});
					if (!CampfireBlock.isLitCampfireInRange(world, pos, 5)) {
						if (this.hasBees(world, pos)) {
							this.angerNearbyBees(world, pos);
						}

						this.takeHoney(world, state, pos, player, BeehiveBlockEntity.BeeState.EMERGENCY);
					} else {
						this.takeHoney(world, state, pos);
						if (player instanceof ServerPlayerEntity) {
							Criterions.SAFELY_HARVEST_HONEY.test((ServerPlayerEntity) player, pos, itemStack2);
						}
					}
					info.setReturnValue(ActionResult.SUCCESS);
				}
			}
		}
	}
}

