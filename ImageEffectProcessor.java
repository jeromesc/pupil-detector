import java.awt.color.*;
import java.awt.image.*;
import java.util.*;

import com.pearsoneduc.ip.op.*; // library for thresholding, canny

/**
 * <p>Title: PupilDetector</p>
 *
 * <p>Description: This class provide all necessary functions to process
 * an image provided by a CameraImageGrabber object which captures images
 * from a WebCam. </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author Jerome Schmaltz
 * @version 1.0
 */
public class ImageEffectProcessor extends Observable implements Observer {

    private CameraImageGrabber _cig;
    private boolean _running = false;
    private Thread _tcig;
    private ApplicationParameter _appParam;

    /**
     * Default constructor. Must provide the CameraImageGrabber
     * class which feeds this class with images captured from a
     * web cam. The other parameter contains configuration for
     * image processing.
     * @param cig CameraImageGrabber Provide the images captured
     * from a web cam.
     * @param appParam ApplicationParameter Parameter configuration
     * for image processing.
     */
    public ImageEffectProcessor(CameraImageGrabber cig,
                                ApplicationParameter appParam) {
        _cig = cig;
        _cig.addObserver(this);
        _appParam = appParam;
    }

    /**
     * This function starts a Thread to
     * process images captured from the
     * CameraImageGrabber.
     */
    public void start() {
        if (!_running) {
            _running = true;
            _tcig = new Thread(_cig);
            _tcig.start();
        }
    }

    /**
     * Stop the image processing
     * steps and tell CameraImageGrabber
     * to stop sending images.
     */
    public void stop() {
        if (_running) {
            _running = false;
            _tcig.destroy();
        }
    }

    /**
     * This function process images provided by the
     * CameraImageGrabber. This function is called
     * by the CIG when notifies that an image has been
     * captured and ready to be processed.
     * @param o Observable The CIG
     * @param arg Object The buffered image to be
     * processed.
     */
    public void update(Observable o, Object arg) {

        double timeStart, timeEnd;
        BufferedImage bufImg = (BufferedImage) arg;

        setChanged();
        notifyObservers(bufImg);

        // work only with ROI
        BufferedImage roiImg = bufImg.getSubimage((int) _appParam._roi.getX(),
                                                  (int) _appParam._roi.getY(),
                                                  (int) _appParam._roi.getWidth(),
                                                  (int) _appParam._roi.
                                                  getHeight());

        PixelGrabber pixGrab;
        int[] imgPixels;
        int width = roiImg.getWidth();
        int height = roiImg.getHeight();
        BufferedImage gsImg = new BufferedImage(width, height,
                                                BufferedImage.TYPE_BYTE_GRAY);
        BufferedImage cannyImg = new BufferedImage(width, height,
                BufferedImage.TYPE_BYTE_GRAY);

        timeStart = System.currentTimeMillis();

        // convert to grey level
        ColorConvertOp ccop = new ColorConvertOp(ColorSpace.getInstance(
                ColorSpace.CS_GRAY), null);
        ccop.filter(roiImg, gsImg);

        // execute the list of effect

        // 1. gray level adjustment
        LinearOp glop = new LinearOp(0, 255);
        gsImg = glop.filter(gsImg, null);

        // 2. blur a little bit
        if (_appParam._mean_kernel_enabled) {
            MeanKernel mk = new MeanKernel(_appParam._mean_kernel_size,
                                           _appParam._mean_kernel_size);
            ConvolveOp cop = new ConvolveOp(mk);
            gsImg = cop.filter(gsImg, null);
        }

        // 3. canny detector
        if (_appParam._canny_enabled) {
            CannyEdgeOp ceo = new CannyEdgeOp(_appParam._gaussian_sigma,
                                              _appParam._canny_threshold_low,
                                              _appParam._canny_threshold_high);
            cannyImg = ceo.filter(gsImg, null);
        }

        // 4. find edges directions
        Kernel sxk = new Kernel(3, 3, _appParam._sobel_x_kernel);
        Kernel syk = new Kernel(3, 3, _appParam._sobel_y_kernel);

        ConvolutionOp sobx = new ConvolutionOp(sxk);
        float[] sxd = sobx.convolve(gsImg);

        ConvolutionOp soby = new ConvolutionOp(syk);
        float[] syd = soby.convolve(gsImg);

        float[][] ed = new float[width][height];
        float[][] sxd2 = ArrayUtils.convert(sxd, width);
        float[][] syd2 = ArrayUtils.convert(syd, width);

        // compute edge directions (ed) [angles are in rads]
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                ed[i][j] = (float) Math.atan(syd2[i][j] / sxd2[i][j]);
            }
        }

        // 5. center detection
        // accumulator
        if (_appParam._canny_enabled) {
            byte[][] acc = new byte[width][height];
            // grab pixels from canny image
            imgPixels = new int[width * height];
            pixGrab = new PixelGrabber(cannyImg, 0, 0, width, height,
                                       imgPixels, 0, width);
            try {
                pixGrab.grabPixels();
            } catch (InterruptedException ex) {
                System.out.println(ex.getMessage());
            }
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    if (ImageUtils.getRed(imgPixels[y * width + x]) != 0) {
                        double j = 0.0;
                        for (int i = x; i < width; i++) {
                            j = Math.abs((i - x) * Math.tan(ed[x][y]));
                            if ((y + (int) j) < height && (y + (int) j) >= 0) {
                                acc[i][y + (int) j] += 5;
                            }
                        }
                        for (int i = x; i >= 0; i--) {
                            j = Math.abs((x - i) * Math.tan(ed[x][y]));
                            if ((y + (int) j) < height && (y - (int) j) >= 0) {
                                acc[i][y - (int) j] += 5;
                            }
                        }
                    }
                }
            }

            // convert array into image
            BufferedImage cImg = ImageUtils.makeImage(ArrayUtils.convert(acc,
                    width), width, height, 1, 1, width,
                    ColorSpace.getInstance(
                            ColorSpace.CS_GRAY), false, false);

            // 6. Threshold application
            int maxPixValue = 0;
            imgPixels = new int[width * height];
            pixGrab = new PixelGrabber(cImg, 0, 0, width, height,
                                       imgPixels, 0, width);
            try {
                pixGrab.grabPixels();
            } catch (InterruptedException ex) {
                System.out.println(ex.getMessage());
            }

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    if (maxPixValue < ImageUtils.getRed(imgPixels[y * width + x])) {
                        maxPixValue = ImageUtils.getRed(imgPixels[y * width + x]);
                    }
                }
            }
            maxPixValue *= _appParam._threshold_factor;
            ThresholdOp thop = new ThresholdOp(maxPixValue);
            cImg = thop.filter(cImg, null);

            // 7. Mexican hat
            if (_appParam._mexican_hat_enabled) {
                Kernel mhk = new Kernel(5, 5, _appParam._mexican_hat_kernel);
                ConvolveOp cmhop = new ConvolveOp(mhk);
                cImg = cmhop.filter(cImg, null);
                gsImg = cImg;
            }

            // 8. Hough transform
            HoughTransformation ht = new HoughTransformation();
            roiImg = ht.houghTransformCircle(cannyImg, roiImg, cImg);
            gsImg = roiImg;
        }

        timeEnd = System.currentTimeMillis();
        System.out.println("Filtering done in : " + (timeEnd - timeStart) +
                           " milliseconds.");

        setChanged();
        notifyObservers(gsImg);
    }
}
