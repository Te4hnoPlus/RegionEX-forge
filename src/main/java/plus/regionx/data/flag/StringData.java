package plus.regionx.data.flag;

import plus.region.data.IoUtils;
import plus.region.utl.FastExitException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class StringData implements ExtendedFlagData{
    private String value;

    public StringData(){}

    public StringData(String value){
        this.value = value;
    }


    @Override
    public void writeTo(OutputStream stream) throws IOException {
        IoUtils.writeShortString(stream, value);
    }


    @Override
    public void readFrom(InputStream stream) throws IOException, FastExitException {
        value = IoUtils.readShortString(stream);
    }


    public String getValue() {
        return value;
    }


    public void setValue(String value) {
        this.value = value;
    }


    @Override
    public String toString() {
        return value;
    }
}