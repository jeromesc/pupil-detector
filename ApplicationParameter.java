

import java.awt.Rectangle;

/**
 * <p>Title: </p>
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
public class ApplicationParameter {

    public static String VIDEO_DEVICE =
            "vfw:Microsoft WDM Image Capture (Win32):0";

    public static boolean _debug_mode = false;

    public boolean _mean_kernel_enabled = true;
    public int _mean_kernel_size = 5;

    public boolean _canny_enabled = true;
    public float _gaussian_sigma = 2.0f;
    public int _canny_threshold_low = 50;
    public int _canny_threshold_high = 160;

    public final float[] _sobel_x_kernel = { -1.0f, 0.0f, 1.0f, -2.0f, 0.0f,
                                           2.0f, -1.0f, 0.0f, 1.0f};
    public final float[] _sobel_y_kernel = { -1.0f, -2.0f, -1.0f, 0.0f, 0.0f,
                                           0.0f, 1.0f, 2.0f, 1.0f};

    public boolean _mexican_hat_enabled = true;
    public final float[] _mexican_hat_kernel = { 0.0f,  0.0f, -1.0f,  0.0f,  0.0f,
                                                 0.0f, -1.0f, -2.0f, -1.0f,  0.0f,
                                                -1.0f, -2.0f, 16.0f, -2.0f, -1.0f,
                                                 0.0f, -1.0f, -2.0f, -1.0f,  0.0f,
                                                 0.0f,  0.0f, -1.0f,  0.0f,  0.0f};
    public float _threshold_factor = 0.3f;
    public Rectangle _roi = new Rectangle(10, 10, 210, 210);
}
