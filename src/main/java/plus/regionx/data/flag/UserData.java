package plus.regionx.data.flag;

import net.minecraft.entity.player.EntityPlayer;
import plus.region.utl.FastExitException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import static plus.region.data.IoUtils.*;

public class UserData implements ExtendedFlagData {
    private UUID uuid;
    private String name;
    private long bits;
    private byte flags;
    // 1 - user is creator
    // 2 - user is member
    // 4 - user is manager

    public UserData() {
    }


    public UserData(byte flags, UUID uuid, String name, long lastLogin) {
        this.flags = flags;
        this.uuid = uuid;
        this.name = name;
        this.bits = lastLogin;
    }


    public UserData(UUID uuid, String name, long lastLogin) {
        this((byte) 2, uuid, name, lastLogin);
    }


    public UserData(EntityPlayer player) {
        this(player.getUniqueID(), player.getName(), System.currentTimeMillis());
    }


    public UUID getUUID() {
        return uuid;
    }


    public String getName() {
        return name;
    }


    public boolean isCreator() {
        return (flags & 1) == 1;
    }


    public boolean isMember() {
        return (flags & 2) == 2;
    }


    public boolean isManager() {
        return (flags & 4) == 4;
    }


    public void setCreator(boolean b) {
        if (b) flags |= 7;
        else flags &= ~1;
    }


    public boolean setManager(boolean b) {
        if (b) flags |= 6;
        else flags &= ~4;
        return true;
    }


    public byte getFlags() {
        return flags;
    }


    public void setFlags(byte userFlags) {
        this.flags = userFlags;
    }


    public long lastLoginMillis() {
        return bits;
    }


    @Override
    public void writeTo(OutputStream stream) throws IOException {
        stream.write(flags);
        writeLong(stream, bits);
        writeUUID(stream, uuid);
        writeShortString(stream, name);
    }


    @Override
    public void readFrom(InputStream stream) throws IOException, FastExitException {
        int userFlags = stream.read();
        if (userFlags == -1) throw FastExitException.INSTANCE;
        flags = (byte) userFlags;

        bits = readLong(stream);
        uuid = readUUID(stream);
        name = readShortString(stream);
    }
}
