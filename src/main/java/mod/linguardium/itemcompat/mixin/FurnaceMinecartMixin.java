package mod.linguardium.itemcompat.mixin;


import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(FurnaceMinecartEntity.class)
public abstract class FurnaceMinecartMixin extends AbstractMinecartEntity {
	@Shadow private int fuel;
	@Shadow public double pushX;
	@Shadow public double pushZ;

	protected FurnaceMinecartMixin(EntityType<?> type, World world) {
		super(type, world);
	}


	//public boolean interact(PlayerEntity player, Hand hand) {
	// copy procedure, replace check with fuel registry.
	@Inject(at = @At("HEAD"), method = "interact", cancellable = true)
	public void interact(PlayerEntity player, Hand hand, CallbackInfoReturnable info) {
		ItemStack item = player.getStackInHand(hand);
		Integer itemfuel = FuelRegistry.INSTANCE.get(item.getItem());
		if (itemfuel != null && (this.fuel + itemfuel <= 32000)) {
			this.fuel += itemfuel;
			if (!player.abilities.creativeMode) {
				item.decrement(1);
			}
		}
		if (this.fuel > 0) {
			this.pushX = this.getX() - player.getX();
			this.pushZ = this.getZ() - player.getZ();
		}

		if (this.fuel > 0) {
			this.pushX = this.getX() - player.getX();
			this.pushZ = this.getZ() - player.getZ();
		}

		info.setReturnValue(true);
	}
}

