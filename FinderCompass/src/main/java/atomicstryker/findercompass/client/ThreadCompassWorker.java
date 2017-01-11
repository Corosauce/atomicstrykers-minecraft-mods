package atomicstryker.findercompass.client;import java.util.ArrayList;import java.util.List;import net.minecraft.block.Block;import net.minecraft.block.state.IBlockState;import net.minecraft.client.Minecraft;import net.minecraft.util.math.BlockPos;/** * Runnable worker class for finding Blocks * * @author AtomicStryker */public class ThreadCompassWorker extends Thread{    private Minecraft mcinstance;    private boolean isRunning = false;    public ThreadCompassWorker(Minecraft mc)    {        mcinstance = mc;    }    private Block block;    private int[] intArray;    public void setupValues(Block b, int[] configInts)    {        block = b;        intArray = configInts;    }    public boolean isWorking()    {        return isRunning;    }    @Override    public void run()    {        isRunning = true;        // search!        BlockPos result = findNearestBlockChunkOfIDInRange(block, intArray[0], intArray[1], intArray[2], intArray[3], intArray[4], intArray[5], intArray[6], intArray[7]);        if (result != null)        {            FinderCompassClientTicker.instance.onFoundChunkCoordinates(result, block, intArray[0]);        }        isRunning = false;    }    private BlockPos findNearestBlockChunkOfIDInRange(Block blockID, int meta, int playerX, int playerY, int playerZ, int xzRange, int yRange, int minY, int maxY)    {        List<BlockPos> blocksInRange = this.findBlocksOfIDInRange(blockID, meta, playerX, playerY, playerZ, xzRange, yRange, minY, maxY);        BlockPos playerCoords = new BlockPos(playerX, playerY, playerZ);        BlockPos resultCoords = new BlockPos(0, 0, 0);        double minDist = 9999.0D;        for (BlockPos coords : blocksInRange)        {            double localDist = playerCoords.distanceSq(coords);            if (localDist < minDist)            {                resultCoords = coords;                minDist = localDist;            }        }        //System.out.printf("Compassworker found stuff of id %s at [%d|%d|%d]\n", blockID, resultCoords.posX, resultCoords.posY, resultCoords.posZ);        return resultCoords;    }    private List<BlockPos> findBlocksOfIDInRange(Block blockID, int meta, int playerX, int playerY, int playerZ, int xzRange, int yRange, int minY, int maxY)    {        ArrayList<BlockPos> resultList = new ArrayList<>();        for (int yIter = playerY - yRange; yIter <= playerY + yRange + 1; ++yIter)        {            if (yIter >= minY && yIter <= maxY)            {                for (int zIter = playerZ - xzRange; zIter <= playerZ + xzRange; ++zIter)                {                    for (int xIter = playerX - xzRange; xIter <= playerX + xzRange; ++xIter)                    {                        IBlockState state = mcinstance.world.getBlockState(new BlockPos(xIter, yIter, zIter));                        if (state.getBlock() == blockID)                        {                            if (meta >= 0 && state.getBlock().getMetaFromState(state) != meta)                            {                                continue;                            }                            BlockPos var13 = new BlockPos(xIter, yIter, zIter);                            resultList.add(var13);                        }                        Thread.yield();                    }                }            }        }        return resultList;    }}