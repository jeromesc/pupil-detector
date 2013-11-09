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
public class ArrayUtils {

    public static float[][] convert(float[] src, int width) {
        float[][] dest = new float[width][src.length/width];
        for(int i = 0; i < width; i++ ) {
            for(int j = 0; j < src.length/width; j++ ) {
                dest[i][j] = src[j*width+i];
            }
        }
        return dest;
    }

    public static byte[] convert(byte[][] src, int width) {
        byte[] dest = new byte[src[0].length*width];
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < src[0].length; j++ ) {
                dest[j*width+i] = src[i][j];
            }
        }
        return dest;
    }


    public static byte[] reduceDimension( byte[][] src ) {
      int arrSize  = src[0].length * src.length;
      int rowLen   = src.length;
      byte[] dest = new byte[arrSize];

      for( int i = 0; i < arrSize; i++ ) {
        dest[i] = src[i%rowLen][i/rowLen];
      }
      return dest;
  }


  public static  float[][] increaseDimension( float[] src, int awidth, int aheight ) {
     float[][] dest = new float[awidth][aheight];

     for( int i = 0; i < src.length; i++ ) {
       dest[i%awidth][i/awidth] = src[i];
     }
     return dest;
  }

}
