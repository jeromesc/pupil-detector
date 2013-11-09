import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;

import javax.swing.*;

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
public class JPDInterface extends JDialog implements Observer {

    private ImageEffectProcessor _iep;
    private CameraImageGrabber _cig;
    private BufferedImage _currentFrame;
    private BufferedImage _currentROI;
    private ApplicationParameter _appParam;
    private Dimension _dimension;
    private int _ROI_x1, _ROI_y1, _ROI_x2, _ROI_y2;
    private boolean _tempROISetup = false;

    /**
     * Default constructor. It initializes all variables and
     * setting all application parameters.
     */
    public JPDInterface() {

        // set application and images dimensions
        _dimension = new Dimension(640, 480);

        _appParam = new ApplicationParameter();
        // defining application settings
        _appParam._mean_kernel_size = 3;
        _appParam._gaussian_sigma = 1.2f;
        _appParam._canny_threshold_low = 18;
        _appParam._canny_threshold_high = 41;
        _appParam._threshold_factor = 0.4f;

        try {
            _cig = new CameraImageGrabber(_appParam);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
                                          JOptionPane.ERROR_MESSAGE);
        }

        _iep = new ImageEffectProcessor(_cig, _appParam);
        _iep.addObserver(this);

        if (!ApplicationParameter._debug_mode) {
            _iep.start();
        } else {
            // load an image for test puposes
            Image testImage = Toolkit.getDefaultToolkit().
                              createImage("hibou.jpg");
            MediaTracker mt = new MediaTracker(this);
            mt.addImage(testImage, 0);
            try {
                mt.waitForAll();
            } catch (InterruptedException ex1) {
            }
            _currentFrame = new BufferedImage(testImage.getWidth(this),
                                              testImage.getHeight(this),
                                              BufferedImage.TYPE_INT_RGB);
            _currentFrame.getGraphics().drawImage(testImage, 0, 0,
                                                  testImage.getWidth(this),
                                                  testImage.getHeight(this), this);
            _iep.update(null, _currentFrame);
        }
        setDefaultCloseOperation(3);
        setTitle("JPupilDetector");
        setSize(_dimension);
        show();
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Method overrided to ensure images, ROI and temp ROI (as
     * dynamically defined by the user) is well painted on the
     * frame.
     * @param g Graphics Everybody knows that object.
     */
    public void paint(Graphics g) {
        // Paint the back image
        if (_currentFrame != null) {
            g.drawImage(_currentFrame, 0, 0, (int) _dimension.getWidth(),
                        (int) _dimension.getHeight(), this);
        }
        // Paint the ROI
        if (_currentROI != null) {
            g.drawImage(_currentROI, (int) _appParam._roi.getLocation().getX(),
                        (int) _appParam._roi.getLocation().getY(),
                        (int) _appParam._roi.getWidth(),
                        (int) _appParam._roi.getHeight(), this);
        }
        // Paint the user's ROI
        if (!_tempROISetup) {
            g.setColor(Color.RED);
            g.drawRect((int) _appParam._roi.getLocation().getX(),
                       (int) _appParam._roi.getLocation().getY(),
                       (int) _appParam._roi.getWidth(),
                       (int) _appParam._roi.getHeight());
        } else {
            g.setColor(Color.GREEN);
            g.drawRect(_ROI_x1 < _ROI_x2 ? _ROI_x1 : _ROI_x2,
                       _ROI_y1 < _ROI_y2 ? _ROI_y1 : _ROI_y2,
                       _ROI_x2 > _ROI_x1 ? (_ROI_x2 - _ROI_x1) :
                       (_ROI_x1 - _ROI_x2),
                       _ROI_y2 > _ROI_y1 ? (_ROI_y2 - _ROI_y1) :
                       (_ROI_y1 - _ROI_y2));
        }
    }

    /**
     * Update the application's images.
     * @param o Observable The guys who's observing us,
     * in this case, the ImageEffectProcessor.
     * @param arg Object The image sent by the observer.
     * Comparaison is made by its size. Large size is
     * synonym of frame image not ROI image.
     */
    public void update(Observable o, Object arg) {
        BufferedImage img = (BufferedImage) arg;
        if (img != null) {
            if (img.getWidth() == _dimension.getWidth() &&
                img.getHeight() == _dimension.getHeight()) {
                _currentFrame = img;
            } else {
                _currentROI = img;
            }
        }
        repaint();
    }

    /**
     * Application entry point.
     * @param args String[]
     */
    public static void main(String[] args) {
        JPDInterface jpdinterface = new JPDInterface();
    }

    /**
     * Entry point for JBuilder design utility.
     * @throws Exception
     */
    private void jbInit() throws Exception {
        this.addKeyListener(new JPDInterface_this_keyAdapter(this));
        this.addMouseListener(new JPDInterface_this_mouseAdapter(this));
        this.addMouseMotionListener(new JPDInterface_this_mouseMotionAdapter(this));
    }

    /**
     * Listens for a key typed by user.
     * @param e KeyEvent
     */
    public void this_keyTyped(KeyEvent e) {
        // c is for configuration
        // it displays the application parameter
        // dialog to play with parameters.
        if (e.getKeyChar() == 'c') {
            AppParameterDialog appParamDlg = new AppParameterDialog(_appParam);
            appParamDlg.show();
        }
        // u is for update.
        // update is made by telling the ImageEffectProcessor
        // to process the ROI again.
        if (e.getKeyChar() == 'u') {
            _iep.update(null, _currentFrame);
            repaint();
        }
    }

    /**
     * Once the mouse is released from the user. We're
     * computing the coordinates to make a new ROI.
     * @param e MouseEvent
     */
    public void this_mouseReleased(MouseEvent e) {
        _ROI_x2 = e.getX();
        _ROI_y2 = e.getY();
        _tempROISetup = false;

        int x, y, w, h;
        if (_ROI_x1 < _ROI_x2) {
            x = _ROI_x1;
            w = _ROI_x2 - _ROI_x1;
        } else {
            x = _ROI_x2;
            w = _ROI_x1 - _ROI_x2;
        }
        if (_ROI_y1 < _ROI_y2) {
            y = _ROI_y1;
            h = _ROI_y2 - _ROI_y1;
        } else {
            y = _ROI_y2;
            h = _ROI_y1 - _ROI_y2;
        }
        _appParam._roi.setBounds(x, y, w, h);
        if( _appParam._debug_mode ) {
            _iep.update(null, _currentFrame);
        }
        repaint();
    }

    /**
     * Replacing old coordinates.
     * @param e MouseEvent
     */
    public void this_mouseDragged(MouseEvent e) {
        _ROI_x2 = e.getX();
        _ROI_y2 = e.getY();
        repaint();
    }

    /**
     * Getting the first coordinates of the
     * user's defined ROI.
     * @param e MouseEvent
     */
    public void this_mousePressed(MouseEvent e) {
        _ROI_x1 = e.getX();
        _ROI_y1 = e.getY();
        _ROI_x2 = e.getX() + 10;
        _ROI_y2 = e.getY() + 10;
        _tempROISetup = true;
        repaint();
    }


}


class JPDInterface_this_keyAdapter extends KeyAdapter {
    private JPDInterface adaptee;
    JPDInterface_this_keyAdapter(JPDInterface adaptee) {
        this.adaptee = adaptee;
    }

    public void keyTyped(KeyEvent e) {
        adaptee.this_keyTyped(e);
    }
}


class JPDInterface_this_mouseAdapter extends MouseAdapter {
    private JPDInterface adaptee;
    JPDInterface_this_mouseAdapter(JPDInterface adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseReleased(MouseEvent e) {
        adaptee.this_mouseReleased(e);
    }

    public void mousePressed(MouseEvent e) {
        adaptee.this_mousePressed(e);
    }

}


class JPDInterface_this_mouseMotionAdapter extends MouseMotionAdapter {
    private JPDInterface adaptee;
    JPDInterface_this_mouseMotionAdapter(JPDInterface adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseDragged(MouseEvent e) {
        adaptee.this_mouseDragged(e);
    }
}
