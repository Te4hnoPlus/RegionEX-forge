package plus.regionx.protect;

import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import plus.regionx.access.WorldFieldAccess;


public class EntityProtector extends BaseProtector {
    public EntityProtector(WorldFieldAccess access) {
        super(access);
    }


    @SubscribeEvent
    public void onEntityAttack(LivingAttackEvent event) {

    }
}
