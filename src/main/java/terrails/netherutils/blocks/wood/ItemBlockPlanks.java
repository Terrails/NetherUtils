package terrails.netherutils.blocks.wood;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import terrails.netherutils.Constants;
import terrails.netherutils.blocks.wood.WoodType;

public class ItemBlockPlanks extends ItemBlock {

    public ItemBlockPlanks(Block block) {
        super(block);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return "tile." + Constants.MOD_ID + ".planks_" + WoodType.byMetadata(stack.getMetadata());
    }
}
