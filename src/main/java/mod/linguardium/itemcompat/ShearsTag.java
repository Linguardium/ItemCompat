package mod.linguardium.itemcompat;


import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;


public class ShearsTag {
    public static final Tag<Item> SHEARS_ITEM = TagRegistry.item(new Identifier("c","shears"));
    public static void init() {

    }
}
