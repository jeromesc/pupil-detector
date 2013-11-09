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

import javax.media.*;
import javax.media.control.*;
import javax.media.util.*;
import javax.media.format.*;
import javax.media.protocol.*;
import java.io.IOException;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Observable;

public class CameraImageGrabber
        extends Observable implements Runnable, ControllerListener

{
    private CaptureDeviceInfo _device;
    private YUVFormat _videoFormat = new YUVFormat(YUVFormat.YUV_420);
    private MediaLocator _locator;
    private DataSource _source;
    private Processor _processor;
    private PushBufferStream _cameraStream;
    private boolean _waitingForRealize = true;
    private ApplicationParameter _appParam;

////////////////////////////////////////////////////////////////////////////////

    /**
     * This function initialize the web camera by setting
     * the output video format and create a processor.
     * @throws ExceptionInInitializerError
     * @throws NoDataSourceException
     * @throws IOException
     * @throws NoProcessorException
     */
    public CameraImageGrabber(ApplicationParameter appParam)
            throws ExceptionInInitializerError, NoDataSourceException,
            IOException, NoProcessorException
    {
        // set the application's parameters
        _appParam = appParam;

        // create the device
        _device = CaptureDeviceManager.getDevice(ApplicationParameter.
                                                 VIDEO_DEVICE);
        // list formats
        Format[] formats = _device.getFormats();

        // set format
        if (formats != null)
        {
            for (int index = 0; index < formats.length; index++)
            {
                // cast
                _videoFormat = (YUVFormat) formats[index];
            }
        }
        else
        {
            throw new ExceptionInInitializerError(
                    "Current device has no supported formats.");
        }

        // get locator
        _locator = _device.getLocator();

        // get the corresponding data source
        _source = Manager.createDataSource(_locator);

        // get format control
        FormatControl[] fmtc = ((CaptureDevice) _source).getFormatControls();

        // try the set the chosen format...
        for (int i = 0; i < fmtc.length; i++)
        {
            if (fmtc[i].setFormat(_videoFormat) != null)
            {
                break;
            }
        }

        // we're are going to process images from
        // web cam, we then choose the processor
        _processor = Manager.createProcessor(_source);
        _processor.addControllerListener(this);
        _processor.realize();

        // pooling
        while (_waitingForRealize)
        {
            // not much
        }
    }

////////////////////////////////////////////////////////////////////////////////

    /**
     * This function will be called to start the
     * current class delivering continous images.
     */
    public void run()
    {
        // start the processor
        _processor.start();

        // Get access to push buffer data source
        PushBufferDataSource pbSrc = (PushBufferDataSource) _processor.
                                     getDataOutput();

        /* Can now retrieve the PushBufferStream that will enable us
         * to access the data from the camera */
        PushBufferStream[] strms = pbSrc.getStreams();

        /* Should test format - in terms of previously selected
         * parameters */
        _cameraStream = strms[0];
        YUVFormat yuv = (YUVFormat) _cameraStream.getFormat();

        while (true)
        {
            // Set up for conversion below
            BufferToImage conv = new BufferToImage(yuv);

            // Grab image from webcam
            Buffer cameraBuffer = new Buffer();
            try
            {
                _cameraStream.read(cameraBuffer);
            }
            catch (IOException ex)
            {
                System.out.println("CameraImageGrabber error : " +
                                   ex.getMessage());
            }

            // Convert to an AWT image
            BufferedImage source = (BufferedImage) conv.createImage(
                    cameraBuffer);
            setChanged();
            notifyObservers(source);
        }
    }

    public void controllerUpdate(ControllerEvent controllerEvent)
    {
        if (controllerEvent instanceof RealizeCompleteEvent)
        {
            _waitingForRealize = false;
        }
    }

}
