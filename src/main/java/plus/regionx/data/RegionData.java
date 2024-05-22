package plus.regionx.data;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.player.EntityPlayer;
import plus.region.utl.FastExitException;
import plus.regionx.MainRegionEX;
import plus.tson.utl.Te4HashSet;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import static plus.region.data.IoUtils.*;


public class RegionData {
    ImmutableMap<UUID, Entry> data = ImmutableMap.of();
    long createTime;
    int flags;

    public Entry getOrAddEntry(EntityPlayer player){
        Entry entry = data.get(player.getUniqueID());
        if(entry != null) return entry;

        entry = new Entry(player);

        addEntry(entry);
        return entry;
    }


    public ImmutableCollection<Entry> getEntries() {
        return data.values();
    }


    public Entry getEntry(UUID uuid) {
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


    protected void addEntry(Entry entry) {
        if(data.size() >= 65535) {
            MainRegionEX.log.warn("Too many users in region");
            return;
        }
        HashMap<UUID, Entry> copy = new HashMap<>(data);
        copy.put(entry.getUUID(), entry);
        data = ImmutableMap.copyOf(copy);
    }


    public void removeEntry(UUID uuid) {
        if(!data.containsKey(uuid)){
            MainRegionEX.log.warn("User [{}] not member is this region", uuid.toString());
            return;
        }
        HashMap<UUID, Entry> copy = new HashMap<>(data);
        copy.remove(uuid);
        data = ImmutableMap.copyOf(copy);
    }


    public ImmutableCollection<Entry> getRawData() {
        return data.values();
    }


    public Set<Entry> getPlayers() {
        Te4HashSet<Entry> set = new Te4HashSet<>();
        for (Entry entry : data.values()) {
            set.add(entry);
        }
        return set;
    }


    public static class Entry {
        private final UUID uuid;
        private String name;
        private long bits;
        private byte flags;
        // 1 - user is creator
        // 2 - user is member
        // 4 - user is manager

        public Entry(byte flags, UUID uuid, String name, long lastLogin) {
            this.flags = flags;
            this.uuid = uuid;
            this.name = name;
            this.bits = lastLogin;
        }


        public Entry(UUID uuid, String name, long lastLogin) {
            this((byte) 2, uuid, name, lastLogin);
        }


        public Entry(EntityPlayer player) {
            this(player.getUniqueID(), player.getName(), System.currentTimeMillis());
        }


        public UUID getUUID() {
            return uuid;
        }


        public String getName() {
            return name;
        }


        public boolean isCreator(){
            return (flags & 1) == 1;
        }


        public boolean isMember(){
            return (flags & 2) == 2;
        }


        public boolean isManager(){
            return (flags & 4) == 4;
        }


        public void setCreator(boolean b){
            if(b)  flags |= 7;
            else   flags &= ~1;
        }


        public boolean setManager(boolean b){
            if(b)  flags |= 6;
            else   flags &= ~4;
            return true;
        }


        public byte getFlags() {
            return flags;
        }


        public void setFlags(byte userFlags) {
            this.flags = userFlags;
        }


        public long lastLoginMillis(){
            return bits;
        }
    }



    public static void writeTo(RegionData data, OutputStream stream) throws IOException {
        long bits = data.createTime;
        if(data.isExtended()) {
            bits |= 1;
        } else {
            bits &= ~1;
        }
        writeLong(stream, bits);
        writeShort(stream, data.data.size());

        for(Map.Entry<UUID, Entry> entry : data.data.entrySet()) {
            writeTo(entry.getValue(), stream);
        }
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

        int size = readShort(stream);
        HashMap<UUID, Entry> temp = new HashMap<>(size);
        for(int i = 0; i < size; i++) {
            Entry entry = readUserFrom(stream);
            temp.put(entry.getUUID(), entry);
        }
        data.data = ImmutableMap.copyOf(temp);
        return data;
    }


    public static void writeTo(Entry entry, OutputStream stream) throws IOException {
        stream.write(entry.flags);
        writeLong(stream, entry.bits);
        writeUUID(stream, entry.uuid);
        writeShortString(stream, entry.name);
    }


    public static Entry readUserFrom(InputStream stream) throws IOException, FastExitException {
        int userFlags = stream.read();
        if(userFlags == -1) throw FastExitException.INSTANCE;

        long joinTime = readLong(stream);
        UUID uuid = readUUID(stream);
        String lastName = readShortString(stream);

        return new Entry((byte) userFlags, uuid, lastName, joinTime);
    }
}