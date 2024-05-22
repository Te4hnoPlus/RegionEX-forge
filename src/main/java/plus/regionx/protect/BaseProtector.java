package plus.regionx.protect;

import net.minecraftforge.common.MinecraftForge;
import plus.regionx.access.WorldFieldAccess;


public class BaseProtector {
    protected final WorldFieldAccess access;

    public BaseProtector(WorldFieldAccess access) {
        this.access = access;
    }


    public final void init(){
        MinecraftForge.EVENT_BUS.register(this);
    }
}