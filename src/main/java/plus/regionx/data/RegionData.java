package plus.regionx.data;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.player.EntityPlayer;
import plus.region.utl.FastExitException;
import plus.regionx.MainRegionEX;
import plus.regionx.data.flag.ExtendedFlagData;
import plus.regionx.data.flag.UserData;
import plus.tson.utl.Te4HashSet;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import static plus.region.data.IoUtils.*;


public class RegionData implements ExtendedFlagData{
    ImmutableMap<UUID, UserData> data = ImmutableMap.of();
    long createTime;
    int flags;

    public UserData getOrAddEntry(EntityPlayer player){
        UserData userData = data.get(player.getUniqueID());
        if(userData != null) return userData;

        userData = new UserData(player);

        addEntry(userData);
        return userData;
    }


    public ImmutableCollection<UserData> getEntries() {
        return data.values();
    }


    public UserData getEntry(UUID uuid) {
        return data.get(uuid);
    }


    public boolean getFlag(int i) {
        return (flags & i) == i;
    }


    public void setFlag(int i, boolean b) {
        if(b)  flags |= i;
        else   flags &= ~i;
    }


    public void setFlag(RegionFlag.Base flag, boolean b) {
        setFlag(flag.getId(), b);
    }


    public boolean getFlag(RegionFlag.Base flag) {
        return getFlag(flag.getId());
    }


    public ExtendedFlagData getFlagExtended(RegionFlag.Advanced flag) {
        return null;
    }


    public RegionData setFlagExtended(RegionFlag.Advanced flag, ExtendedFlagData value) {
        RegionDataEx data = new RegionDataEx();
        data.data = this.data;
        data.createTime = this.createTime;
        data.flags = this.flags;
        data.setFlagExtended(flag, value);
        return data;
    }


    public boolean isExtended() {
        return false;
    }


    protected void addEntry(UserData userData) {
        if(data.size() >= 65535) {
            MainRegionEX.log.warn("Too many users in region");
            return;
        }
        HashMap<UUID, UserData> copy = new HashMap<>(data);
        copy.put(userData.getUUID(), userData);
        data = ImmutableMap.copyOf(copy);
    }


    public void removeEntry(UUID uuid) {
        if(!data.containsKey(uuid)){
            MainRegionEX.log.warn("User [{}] not member is this region", uuid.toString());
            return;
        }
        HashMap<UUID, UserData> copy = new HashMap<>(data);
        copy.remove(uuid);
        data = ImmutableMap.copyOf(copy);
    }


    public ImmutableCollection<UserData> getRawData() {
        return data.values();
    }


    public Set<UserData> getPlayers() {
        Te4HashSet<UserData> set = new Te4HashSet<>();
        for (UserData userData : data.values()) {
            set.add(userData);
        }
        return set;
    }


    @Override
    public void writeTo(OutputStream stream) throws IOException {
        writeShort(stream, data.size());

        for(Map.Entry<UUID, UserData> entry : data.entrySet()) {
            entry.getValue().writeTo(stream);
        }
    }


    @Override
    public void readFrom(InputStream stream) throws IOException, FastExitException {
        int size = readShort(stream);
        HashMap<UUID, UserData> temp = new HashMap<>(size);
        for(int i = 0; i < size; i++) {
            UserData userData = new UserData();
            userData.readFrom(stream);
            temp.put(userData.getUUID(), userData);
        }
        data = ImmutableMap.copyOf(temp);
    }


    public static void writeTo(RegionData data, OutputStream stream) throws IOException {
        long bits = data.createTime;
        if(data.isExtended()) {
            bits |= 1;
        } else {
            bits &= ~1;
        }
        writeLong(stream, bits);

        data.writeTo(stream);
    }


    public static RegionData readRegionDataFrom(InputStream stream) throws IOException, FastExitException {
        long bits = readLong(stream);
        RegionData data;
        if((bits & 1) == 1) {
            data = new RegionDataEx();
            bits &= ~1;
        } else {
            data = new RegionData();
        }
        data.createTime = bits;

        data.readFrom(stream);
        return data;
    }
}