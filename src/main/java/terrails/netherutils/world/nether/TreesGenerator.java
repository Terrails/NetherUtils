package terrails.netherutils.world.nether;

import biomesoplenty.api.block.BOPBlocks;
import biomesoplenty.common.block.BlockBOPGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.Loader;
import terrails.netherutils.config.ConfigHandler;
import terrails.netherutils.world.nether.trees.WorldGeneratorHellwood;
import terrails.netherutils.world.nether.trees.WorldGeneratorAshwood;
import terrails.netherutils.world.nether.trees.WorldGeneratorSoulwood;

import java.util.Random;

public class TreesGenerator implements IWorldGenerator {

    WorldGeneratorHellwood HELL_WOOD;
    WorldGeneratorAshwood ASH_WOOD;
    WorldGeneratorSoulwood SOUL_WOOD;

    public static IBlockState[] blockStates;

    public TreesGenerator() {
        if (Loader.isModLoaded("biomesoplenty")) {
            blockStates = new IBlockState[]{
                    Blocks.NETHERRACK.getDefaultState(),
                    BOPBlocks.grass.getDefaultState().withProperty(BlockBOPGrass.VARIANT, BlockBOPGrass.BOPGrassType.MYCELIAL_NETHERRACK),
                    BOPBlocks.grass.getDefaultState().withProperty(BlockBOPGrass.VARIANT, BlockBOPGrass.BOPGrassType.OVERGROWN_NETHERRACK)};
            HELL_WOOD = new WorldGeneratorHellwood(false, blockStates);
            ASH_WOOD = new WorldGeneratorAshwood(false, blockStates);
            SOUL_WOOD = new WorldGeneratorSoulwood(false, blockStates);
        } else {
            this.HELL_WOOD = new WorldGeneratorHellwood(false);
            this.ASH_WOOD = new WorldGeneratorAshwood(false);
            this.SOUL_WOOD = new WorldGeneratorSoulwood(false);
        }
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        this.generateTree(random, chunkX, chunkZ, world);
    }

    @SuppressWarnings("unchecked")
    public void generateTree(Random random, int chunkX, int chunkZ, World world) {
        int xSpawn, ySpawn, zSpawn;

        int xPos = chunkX * 16 + 8;
        int zPos = chunkZ * 16 + 8;

        BlockPos chunkPos = new BlockPos(xPos, 0, zPos);

        BlockPos position;

        Biome biome = world.getChunkFromBlockCoords(chunkPos).getBiome(chunkPos, world.getBiomeProvider());

        if (biome == null)
        {
            return;
        }

        if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.NETHER) || world.provider.getDimension() == -1) {
            if (ConfigHandler.generateHellWood && random.nextInt(1) == 0) {
                for (int iter = 0; iter < 5; iter++) {
                    xSpawn = xPos + random.nextInt(16);
                    ySpawn = random.nextInt(80) + 16;
                    zSpawn = zPos + random.nextInt(16);
                    position = new BlockPos(xSpawn, ySpawn, zSpawn);

                    this.HELL_WOOD.generate(world, random, position);
                }
            }
            if (ConfigHandler.generateAshWood && random.nextInt(2) == 0) {
                for (int iter = 0; iter < 5; iter++) {
                    xSpawn = xPos + random.nextInt(16);
                    ySpawn = random.nextInt(80) + 16;
                    zSpawn = zPos + random.nextInt(16);
                    position = new BlockPos(xSpawn, ySpawn, zSpawn);

                    this.ASH_WOOD.generate(world, random, position);
                }
            }
            if (ConfigHandler.generateSoulWood && random.nextInt(2) == 0) {
                for (int iter = 0; iter < 5; iter++) {
                    xSpawn = xPos + random.nextInt(16);
                    ySpawn = random.nextInt(80) + 16;
                    zSpawn = zPos + random.nextInt(16);
                    position = new BlockPos(xSpawn, ySpawn, zSpawn);

                    this.SOUL_WOOD.generate(world, random, position);
                }
            }
        }
    }
}
