package mod.linguardium.itemcompat.mixin;


import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShearsItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

import static mod.linguardium.itemcompat.ShearsTag.SHEARS_ITEM;


@Mixin(DispenserBlock.class)
public abstract class DispenserMixin extends BlockWithEntity {

	@Shadow
	private static Map<Item, DispenserBehavior> BEHAVIORS;

	public DispenserMixin(Settings settings) {
		super(settings);
	}

	@Inject(at = @At("RETURN"), method = "getBehaviorForItem", cancellable = true)
	public void ItemCompat_getBehaviorForItem(ItemStack itemStack, CallbackInfoReturnable<DispenserBehavior> info) {
		// I dont like this but its the easiest way i found to get the default value of the Object to Object map
		// This allows other mods to mixin without this one overriding unless they provide the default value of the map
		if (info.getReturnValue().equals(BEHAVIORS.get(Items.AIR.asItem()))) {
			if (itemStack.getItem() instanceof ShearsItem || itemStack.getItem().isIn(SHEARS_ITEM)) {
				info.setReturnValue(BEHAVIORS.get(Items.SHEARS.asItem()));
			}
		}
	}
}

