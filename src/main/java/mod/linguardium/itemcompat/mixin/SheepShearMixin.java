package mod.linguardium.itemcompat.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static mod.linguardium.itemcompat.ShearsTag.SHEARS_ITEM;


@Mixin(SheepEntity.class)
public abstract class SheepShearMixin extends AnimalEntity {
	@Shadow
	abstract public boolean isSheared();
	@Shadow
	abstract public void dropItems();

	public SheepShearMixin(EntityType<? extends AnimalEntity> type, World world) {
		super(type, world);
	}

	@Inject(at = @At("HEAD"), method = "interactMob", cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	private void interactMob(final PlayerEntity player, final Hand hand,final CallbackInfoReturnable<Boolean> info) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (itemStack.getItem() instanceof ShearsItem || itemStack.getItem().isIn(SHEARS_ITEM)) {
			if (!this.isSheared() && !this.isBaby()) {
				this.dropItems();
				if (!this.world.isClient) {
					itemStack.damage(1, player, (playerEntity) -> {
						playerEntity.sendToolBreakStatus(hand);
					});
				}
				info.setReturnValue(true);
			}
		}
	}
}

