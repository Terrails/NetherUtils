package terrails.netherutils.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import terrails.netherutils.Constants;
import terrails.netherutils.NetherUtils;
import terrails.netherutils.api.portal.IPortalMaster;
import terrails.netherutils.client.render.TESRPortal;
import terrails.netherutils.tileentity.TileEntityTank;
import terrails.netherutils.tileentity.portal.PortalId;
import terrails.netherutils.tileentity.portal.TileEntityPortalMaster;
import terrails.terracore.block.BlockBase;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Random;

public class BlockPortal extends BlockBase {

    public static final int GUI_ID = 1;

    public BlockPortal(String name) {
        super(Material.ROCK, name);
        setCreativeTab(Constants.CreativeTab.NetherUtils);
        setHardness(4.0F);
        setLightLevel(1.5F);
        setHarvestLevel("pickaxe", 2);
        GameRegistry.registerTileEntity(TileEntityPortalMaster.class, "portal");
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPortalMaster.class, new TESRPortal());
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity != null && tileEntity instanceof TileEntityPortalMaster) {
            TileEntityPortalMaster te = (TileEntityPortalMaster) tileEntity;

            if (stack.hasTagCompound()) {
                NBTTagCompound compound = stack.getTagCompound();
                if (compound != null) {

                    te.setFuel(new FluidStack(FluidRegistry.getFluid(compound.getString("Fuel")), compound.getInteger("FuelAmount")));

                    if (compound.getInteger("ySlave") != 0)
                        te.setSlavePos(new BlockPos(compound.getInteger("xSlave"), compound.getInteger("ySlave"), compound.getInteger("zSlave")));

                    if (compound.hasKey("Inventory")) {
                        try {
                            te.inventory.deserializeNBT((NBTTagCompound) compound.getTag("Inventory"));
                        } catch (NullPointerException e) {
                            e.getCause();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof TileEntityPortalMaster) {
            TileEntityPortalMaster te = (TileEntityPortalMaster) tile;

            ItemStack stack = new ItemStack(state.getBlock());
            if (!te.isInvEmpty() || !te.getSlavePos().equals(BlockPos.ORIGIN) || te.hasFuel()) {
                if (!stack.hasTagCompound()) {
                    stack.setTagCompound(new NBTTagCompound());
                }
            }

            NBTTagCompound compound = stack.getTagCompound();
            assert compound != null;

            if (!te.getSlavePos().equals(BlockPos.ORIGIN)) {
                compound.setInteger("xSlave", te.getSlavePos().getX());
                compound.setInteger("ySlave", te.getSlavePos().getY());
                compound.setInteger("zSlave", te.getSlavePos().getZ());
            }
            if (te.hasFuel()) {
                compound.setString("Fuel", te.getFuel().getFluid().getName());
                compound.setInteger("FuelAmount", te.getFuel().amount);
            }
            if (!te.isInvEmpty()) {
                compound.setTag("Inventory", te.inventory.serializeNBT());
            }
            drops.add(stack);
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity != null && tileEntity instanceof TileEntityPortalMaster) {
            PortalId.removePortal((TileEntityPortalMaster) tileEntity);
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof TileEntityPortalMaster)) {
            return false;
        } else if (player.isSneaking()) {

            TileEntityPortalMaster tile = (TileEntityPortalMaster) te;
            if (!tile.isActive()) {
                if (!tile.hasFuel())
                    player.sendMessage(new TextComponentString("Missing fuel!"));
                if (!tile.hasRequiredBlocks())
                    player.sendMessage(new TextComponentString("Missing required blocks!"));
                if (!tile.isInvFull())
                    player.sendMessage(new TextComponentString("Missing ingredients!"));
            }
        }
        if (!player.isSneaking()) {
            player.openGui(NetherUtils.INSTANCE, GUI_ID, world, pos.getX(), pos.getY(), pos.getZ());
        }


        return true;
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        return willHarvest || super.removedByPlayer(state, world, pos, player, false);
    }
    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack tool) {
        super.harvestBlock(world, player, pos, state, te, tool);
        world.setBlockToAir(pos);
    }

    @Override
    public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {}

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return null;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityPortalMaster();
    }
}
