package com.builtbroken.ai.improvements.util;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.LongHashMap;
import net.minecraft.world.*;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.List;

/**
 * These classes were copied from JUnit, hence why they are compressed inside of this single class
 */
public class CheckFakeWorld extends World
{
    public boolean debugInfo = false;
    public Logger logger;

    public CheckFakeWorld(ISaveHandler p_i45369_1_, WorldSettings p_i45369_3_, WorldProvider p_i45369_4_)
    {
        super(p_i45369_1_, "FakeWorld", p_i45369_3_, p_i45369_4_, new Profiler());
        logger = LogManager.getLogger("FW-" + "FakeWorld");
    }

    public static CheckFakeWorld newWorld(String name)
    {
        WorldSettings settings = new WorldSettings(0, WorldSettings.GameType.SURVIVAL, false, false, WorldType.FLAT);
        WorldInfo worldInfo = new WorldInfo(settings, name);
        CheckWorldSaveHandler handler = new CheckWorldSaveHandler(worldInfo);
        return new CheckFakeWorld(handler, settings, new CheckWorldProvider());
    }

    @Override
    protected IChunkProvider createChunkProvider()
    {
        return new ChunkProviderCheckServer(this, new ChunkProviderCheck(this));
    }

    @Override
    protected int func_152379_p()
    {
        return 0;
    }

    @Override
    public Entity getEntityByID(int p_73045_1_)
    {
        return null;
    }

    @Override
    public boolean setBlock(int x, int y, int z, Block block, int meta, int notify)
    {
        debug("");
        debug("setBlock(" + x + ", " + y + ", " + z + ", " + block + ", " + meta + ", " + notify);
        if (x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000)
        {
            if (y < 0)
            {
                debug("setBlock() y level is too low");
                return false;
            } else if (y >= 256)
            {
                debug("setBlock() y level is too high");
                return false;
            } else
            {
                Chunk chunk = this.getChunkFromChunkCoords(x >> 4, z >> 4);
                debug("setBlock() chunk = " + chunk);
                Block block1 = null;
                net.minecraftforge.common.util.BlockSnapshot blockSnapshot = null;

                if ((notify & 1) != 0)
                {
                    block1 = chunk.getBlock(x & 15, y, z & 15);
                }

                if (this.captureBlockSnapshots && !this.isRemote)
                {
                    blockSnapshot = net.minecraftforge.common.util.BlockSnapshot.getBlockSnapshot(this, x, y, z, notify);
                    this.capturedBlockSnapshots.add(blockSnapshot);
                }

                boolean flag = chunk.func_150807_a(x & 15, y, z & 15, block, meta);
                debug("setBlock() flag = " + flag + " BlockSnapshot = " + blockSnapshot);

                if (!flag && blockSnapshot != null)
                {
                    this.capturedBlockSnapshots.remove(blockSnapshot);
                    blockSnapshot = null;
                }

                this.theProfiler.startSection("checkLight");
                this.func_147451_t(x, y, z);
                this.theProfiler.endSection();

                if (flag && blockSnapshot == null) // Don't notify clients or update physics while capturing blockstates
                {
                    // Modularize client and physic updates
                    this.markAndNotifyBlock(x, y, z, chunk, block1, block, notify);
                }

                return flag;
            }
        } else
        {
            debug("setBlock() too far from zero zero");
            return false;
        }
    }

    protected void debug(String msg)
    {
        if (debugInfo)
            logger.info(msg);
    }

    public static class CheckWorldProvider extends WorldProvider
    {
        public CheckWorldProvider()
        {
            this.terrainType = WorldType.FLAT;
        }

        @Override
        public String getDimensionName()
        {
            return "FakeWorld";
        }
    }

    public static class CheckWorldSaveHandler implements ISaveHandler
    {
        WorldInfo info;
        File dataDir;

        public CheckWorldSaveHandler(WorldInfo info)
        {
            this.info = info;
            dataDir = new File(new File("."), "data");
            dataDir.mkdirs();
        }

        @Override
        public WorldInfo loadWorldInfo()
        {
            return info;
        }

        @Override
        public void checkSessionLock() throws MinecraftException
        {

        }

        @Override
        public IChunkLoader getChunkLoader(WorldProvider provider)
        {
            return null;
        }

        @Override
        public void saveWorldInfoWithPlayer(WorldInfo p_75755_1_, NBTTagCompound p_75755_2_)
        {

        }

        @Override
        public void saveWorldInfo(WorldInfo p_75761_1_)
        {

        }

        @Override
        public IPlayerFileData getSaveHandler()
        {
            return null;
        }

        @Override
        public void flush()
        {

        }

        @Override
        public File getWorldDirectory()
        {
            return dataDir;
        }

        @Override
        public File getMapFileFromName(String s)
        {
            return new File(dataDir, s);
        }

        @Override
        public String getWorldDirectoryName()
        {
            return "MinecraftUnitTesting";
        }
    }

    public static class ChunkProviderCheckServer implements IChunkProvider
    {
        public IChunkProvider currentChunkProvider;
        public LongHashMap loadedChunkHashMap = new LongHashMap();
        public World worldObj;

        public ChunkProviderCheckServer(World p_i1520_1_, IChunkProvider p_i1520_3_)
        {
            this.worldObj = p_i1520_1_;
            this.currentChunkProvider = p_i1520_3_;
        }

        @Override
        public boolean chunkExists(int p_73149_1_, int p_73149_2_)
        {
            return this.loadedChunkHashMap.containsItem(ChunkCoordIntPair.chunkXZ2Int(p_73149_1_, p_73149_2_));
        }

        @Override
        public Chunk loadChunk(int par1, int par2)
        {
            Chunk chunk = new Chunk(this.worldObj, par1, par2);
            chunk.isChunkLoaded = true;
            this.loadedChunkHashMap.add(ChunkCoordIntPair.chunkXZ2Int(par1, par2), chunk);
            return (Chunk) this.loadedChunkHashMap.getValueByKey(ChunkCoordIntPair.chunkXZ2Int(par1, par2));
        }

        @Override
        public Chunk provideChunk(int p_73154_1_, int p_73154_2_)
        {
            Chunk chunk = (Chunk) this.loadedChunkHashMap.getValueByKey(ChunkCoordIntPair.chunkXZ2Int(p_73154_1_, p_73154_2_));
            return chunk == null ? this.loadChunk(p_73154_1_, p_73154_2_) : chunk;
        }

        @Override
        public void populate(IChunkProvider p_73153_1_, int p_73153_2_, int p_73153_3_)
        {
            Chunk chunk = this.provideChunk(p_73153_2_, p_73153_3_);

            if (!chunk.isTerrainPopulated)
            {
                chunk.func_150809_p();

                if (this.currentChunkProvider != null)
                {
                    this.currentChunkProvider.populate(p_73153_1_, p_73153_2_, p_73153_3_);
                    //TODO GameRegistry.generateWorld(p_73153_2_, p_73153_3_, worldObj, currentChunkProvider, p_73153_1_);
                    chunk.setChunkModified();
                }
            }
        }

        @Override
        public boolean saveChunks(boolean p_73151_1_, IProgressUpdate p_73151_2_)
        {
            return true;
        }

        @Override
        public boolean unloadQueuedChunks()
        {
            return false;
        }

        @Override
        public boolean canSave()
        {
            return false;
        }

        @Override
        public String makeString()
        {
            return "ServerChunkCache: " + this.loadedChunkHashMap.getNumHashElements();
        }

        @Override
        public List getPossibleCreatures(EnumCreatureType p_73155_1_, int p_73155_2_, int p_73155_3_, int p_73155_4_)
        {
            return this.currentChunkProvider.getPossibleCreatures(p_73155_1_, p_73155_2_, p_73155_3_, p_73155_4_);
        }

        @Override
        public ChunkPosition func_147416_a(World p_147416_1_, String p_147416_2_, int p_147416_3_, int p_147416_4_, int p_147416_5_)
        {
            return this.currentChunkProvider.func_147416_a(p_147416_1_, p_147416_2_, p_147416_3_, p_147416_4_, p_147416_5_);
        }

        @Override
        public int getLoadedChunkCount()
        {
            return this.loadedChunkHashMap.getNumHashElements();
        }

        @Override
        public void recreateStructures(int p_82695_1_, int p_82695_2_) {}

        @Override
        public void saveExtraData()
        {

        }
    }

    public static class ChunkProviderCheck implements IChunkProvider
    {
        private World worldObj;

        public ChunkProviderCheck(World p_i2004_1_)
        {
            this.worldObj = p_i2004_1_;
        }

        /**
         * loads or generates the chunk at the chunk location specified
         */
        @Override
        public Chunk loadChunk(int x, int z)
        {
            return this.provideChunk(x, z);
        }

        /**
         * Will return back a chunk, if it doesn't exist and its not a MP client it will generates all the blocks for the
         * specified chunk from the map seed and chunk seed
         */
        @Override
        public Chunk provideChunk(int chunkX, int chunkZ)
        {
            Chunk chunk = new Chunk(this.worldObj, chunkX, chunkZ);


            //Not sure if we need to call this before biome
            chunk.generateSkylightMap();

            //Set biome ID for chunk
            for (int l = 0; l < chunk.getBiomeArray().length; ++l)
            {
                chunk.getBiomeArray()[l] = (byte) 1;
            }

            chunk.generateSkylightMap();
            return chunk;
        }

        /**
         * Checks to see if a chunk exists at x, y
         */
        @Override
        public boolean chunkExists(int p_73149_1_, int p_73149_2_)
        {
            return true;
        }

        /**
         * Populates chunk with ores etc etc
         */
        @Override
        public void populate(IChunkProvider p_73153_1_, int p_73153_2_, int p_73153_3_)
        {

        }

        /**
         * Two modes of operation: if passed true, save all Chunks in one go.  If passed false, save up to two chunks.
         * Return true if all chunks have been saved.
         */
        @Override
        public boolean saveChunks(boolean p_73151_1_, IProgressUpdate p_73151_2_)
        {
            return true;
        }

        /**
         * Save extra data not associated with any Chunk.  Not saved during autosave, only during world unload.  Currently
         * unimplemented.
         */
        @Override
        public void saveExtraData() {}

        /**
         * Unloads chunks that are marked to be unloaded. This is not guaranteed to unload every such chunk.
         */
        @Override
        public boolean unloadQueuedChunks()
        {
            return false;
        }

        /**
         * Returns if the IChunkProvider supports saving.
         */
        @Override
        public boolean canSave()
        {
            return true;
        }

        /**
         * Converts the instance data to a readable string.
         */
        @Override
        public String makeString()
        {
            return "sigVoidSource";
        }

        /**
         * Returns a list of creatures of the specified type that can spawn at the given location.
         */
        @Override
        public List getPossibleCreatures(EnumCreatureType p_73155_1_, int p_73155_2_, int p_73155_3_, int p_73155_4_)
        {
            BiomeGenBase biomegenbase = this.worldObj.getBiomeGenForCoords(p_73155_2_, p_73155_4_);
            return biomegenbase.getSpawnableList(p_73155_1_);
        }

        @Override
        public ChunkPosition func_147416_a(World p_147416_1_, String p_147416_2_, int p_147416_3_, int p_147416_4_, int p_147416_5_)
        {
            return null;
        }

        @Override
        public int getLoadedChunkCount()
        {
            return 0;
        }

        @Override
        public void recreateStructures(int p_82695_1_, int p_82695_2_)
        {

        }
    }
}
