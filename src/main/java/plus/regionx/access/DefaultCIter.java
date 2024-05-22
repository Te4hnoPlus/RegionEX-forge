package plus.regionx.access;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldServer;
import java.util.Iterator;


public class DefaultCIter implements Iterable<Long>{
    private final LongOpenHashSet set = new LongOpenHashSet();

    public DefaultCIter(WorldServer world) {
        for (EntityPlayer player : world.playerEntities) {
            int px = (int) player.posX;
            int pz = (int) player.posZ;

            set.add(ChunkPos.asLong(px-512, pz-512));
            set.add(ChunkPos.asLong(px+512, pz+512));

            set.add(ChunkPos.asLong(px-512, pz+512));
            set.add(ChunkPos.asLong(px+512, pz-512));

            set.add(ChunkPos.asLong(px, pz));
        }
    }


    @Override
    public Iterator<Long> iterator() {
        return set.iterator();
    }
}