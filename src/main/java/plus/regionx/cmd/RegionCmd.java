package plus.regionx.cmd;

import com.google.common.collect.ImmutableCollection;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import plus.region.Region;
import plus.region.RegionQuery;
import plus.regionx.access.WorldFieldAccess;
import plus.regionx.access.WorldLocal;
import plus.regionx.data.RegionData;
import plus.regionx.data.flag.UserData;


public class RegionCmd extends CommandBase {
    protected final WorldFieldAccess access;

    public RegionCmd(WorldFieldAccess access) {
        this.access = access;
    }


    @Override
    public String getName() {
        return "region";
    }


    @Override
    public String getUsage(ICommandSender sender) {
        return "/region <args>";
    }


    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        l1: if(sender instanceof Entity){
            Entity entity = (Entity)sender;
            if(entity.world == null)break l1;

            WorldLocal local = access.get(entity.world);
            BlockPos pos = entity.getPosition();

            if(args.length == 0){
                reply(sender, "RegionEX based on https://github.com/Te4hnoPlus/RegionEX");
                return;
            }
            String cmd = args[0];
            switch (cmd){
                case "info":{
                    RegionQuery regions = local.context().getRegions(pos.getX(), pos.getY(), pos.getZ());
                    if(regions.isEmpty()){
                        reply(sender, "This place not contains in any regions");
                        return;
                    } else {
                        for (Region region : regions) {
                            RegionData data = region.getData(local.getDataManager());
                            if(data != null){
                                ImmutableCollection<UserData> users = data.getEntries();
                                if(!users.isEmpty()){
                                    StringBuilder builder = new StringBuilder();

                                    for (UserData userData : users){
                                        if(userData.isCreator()){
                                            builder.append("C:");
                                        } else if(userData.isManager()){
                                            builder.append("M");
                                        } else if(!userData.isMember()){
                                            continue;
                                        }
                                        builder.append(userData.getName()).append(",");
                                    }

                                    if(builder.length() > 0) {
                                        builder.setCharAt(builder.length()-1,']');
                                        reply(sender, "Region: "+regFromTo(region)+", users: ["+builder);
                                        continue;
                                    }
                                }
                            }
                            reply(sender, "Region: " + regFromTo(region));
                        }
                    }
                    break;
                }
                case "create":{
                    if(!(entity instanceof EntityPlayer)){
                        reply(sender, "Only players can create regions");
                        return;
                    }
                    if(args.length != 4){
                        reply(sender, "Usage: /region create <name> <from X,Y,Z> <to X,Y,Z>");
                        break;
                    }
                    String name = args[1];
                    BlockPos from = fromArg(args[2]);
                    BlockPos to = fromArg(args[3]);

                    if(from == null || to == null){
                        reply(sender, "Invalid coordinates");
                        break;
                    }

                    RegionQuery regions = local.context().getRegions(new Region(from.getX(), from.getY(), from.getZ(), to.getX(), to.getY(), to.getZ()));
                    if(!regions.isEmpty()){
                        l2: for (Region region : regions) {
                            RegionData data = region.getData(local.getDataManager());
                            if(data != null){
                                UserData userData = data.getEntry(entity.getUniqueID());
                                if(userData != null && userData.isCreator()){
                                    continue l2;
                                }
                            }
                            reply(sender, "Your region can`t be intersect with: "+regFromTo(region));
                            return;
                        }
                    }

                    Region region = local.context().createRegion(from.getX(), from.getY(), from.getZ(), to.getX(), to.getY(), to.getZ());

                    RegionData data = region.getData(local.getDataManager());
                    if(data == null){
                        data = new RegionData();
                    }
                    data.getOrAddEntry((EntityPlayer) entity).setCreator(true);

                    region.setData(local.getDataManager(), data);

                    //region create test 0,0,0 10,10,10
                    reply(sender, "Region created: "+regFromTo(region));

                    break;
                }
                default:{
                    reply(sender, "Unknown RegionEX command: "+cmd);
                    break;
                }
            }
            return;
        }

        reply(sender, "This command can only be used in-game");
    }


    private BlockPos fromArg(String arg){
        String[] split = arg.split(",");
        if(split.length != 3){
            return null;
        }
        int x = Integer.parseInt(split[0]);
        int y = Integer.parseInt(split[1]);
        int z = Integer.parseInt(split[2]);
        return new BlockPos(x, y, z);
    }


    private static String regFromTo(Region r){
        return "[" + r.minX + ", " + r.minY + ", " + r.minZ + "] -> [" + r.maxX + ", " + r.maxY + ", " + r.maxZ + "]";
    }


    private void reply(ICommandSender sender, String msg) {
        sender.sendMessage(new TextComponentString(msg));
    }
}


