package plus.regionx.data;

import plus.region.utl.FastExitException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public interface ExtendedFlagData {
    void writeTo(OutputStream stream) throws IOException;
    void readFrom(InputStream stream) throws IOException, FastExitException;
}