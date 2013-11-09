import java.awt.image.*;
import java.awt.color.*;
import java.awt.*;

/**
 * <p>Title: PupilDetector</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ImageUtils {

    public static BufferedImage makeImage(byte[] data, int width, int height,
                                          int numBands,
                                          int pixelStride, int scanlineStride,
                                          ColorSpace cs,
                                          boolean hasAlpha,
                                          boolean isAlphaPremultiplied) {
        DataBufferByte db = new DataBufferByte(data, data.length);

        int[] offsets = new int[numBands];
        for (int i = 0; i < numBands; i++) {
            offsets[i] = i;
        }
        SampleModel sm =
                new PixelInterleavedSampleModel(DataBuffer.TYPE_BYTE,
                                                width, height,
                                                pixelStride,
                                                scanlineStride,
                                                offsets);
        WritableRaster wr =
                Raster.createInterleavedRaster(db, width, height,
                                               scanlineStride, pixelStride,
                                               offsets, new Point(0, 0));

        int[] bits = new int[numBands];
        for (int i = 0; i < numBands; i++) {
            bits[i] = 8;
        }
        int transparency =
                hasAlpha ? Transparency.TRANSLUCENT : Transparency.OPAQUE;
        ColorModel cm = new ComponentColorModel(cs,
                                                bits,
                                                hasAlpha,
                                                isAlphaPremultiplied,
                                                transparency,
                                                DataBuffer.TYPE_BYTE);

        BufferedImage bi = new BufferedImage(cm, wr,
                                             isAlphaPremultiplied,
                                             null);

        return bi;
    }

    public static int getRed(int pixel) {
        return (pixel & 0x00ff0000) >> 16;
    }

    public static int getGreen(int pixel) {
        return (pixel & 0x0000ff00) >> 8;
    }

    public static int getBlue(int pixel) {
        return pixel & 0x000000ff;
    }

    public static int makePixel(int red, int green, int blue) {
        return (255 << 24) | (red << 16) | (green << 8) | blue;
    }

}
