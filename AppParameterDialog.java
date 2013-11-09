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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class AppParameterDialog extends JDialog {

    private JLabel lblCannyHighThreshold = new JLabel();
    private JSlider sldCannyHighThreshold = new JSlider();
    private JSlider sldMeanKernel = new JSlider();
    private JLabel lblCannyHighThresholdValue = new JLabel();
    private JLabel lblCannyGaussian = new JLabel();
    private JLabel lblCannyGaussianValue = new JLabel();
    private JSlider sldCannyGaussian = new JSlider();
    private JLabel lblCannyLowThreshold = new JLabel();
    private JLabel lblCannyLowThresholdValue = new JLabel();
    private JSlider sldCannyLowThreshold = new JSlider();
    private JCheckBox chkCanny = new JCheckBox();
    private JCheckBox chkMexicanHat = new JCheckBox();
    private JCheckBox chkMeanKernel = new JCheckBox();
    private JButton btnClose = new JButton();
    private ApplicationParameter _appParam;
    public AppParameterDialog() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public AppParameterDialog(ApplicationParameter appParam) {
        try {
            _appParam = appParam;
            jbInit();
            configure();
            setSize(260, 390);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void configure() {
        if (_appParam != null) {
            // canny
            chkCanny.setSelected(_appParam._canny_enabled);
            sldCannyGaussian.setEnabled(_appParam._canny_enabled);
            sldCannyGaussian.setValue((int)(_appParam._gaussian_sigma*10));
            lblCannyGaussianValue.setText(String.valueOf(_appParam._gaussian_sigma));
            sldCannyLowThreshold.setEnabled(_appParam._canny_enabled);
            sldCannyLowThreshold.setValue(_appParam._canny_threshold_low);
            lblCannyLowThresholdValue.setText(String.valueOf(_appParam.
                    _canny_threshold_low));
            sldCannyHighThreshold.setEnabled(_appParam._canny_enabled);
            sldCannyHighThreshold.setValue(_appParam._canny_threshold_high);
            lblCannyHighThresholdValue.setText( String.valueOf(_appParam.
                    _canny_threshold_high));
            // mean kernel
            chkMeanKernel.setSelected(_appParam._mean_kernel_enabled);
            sldMeanKernel.setEnabled(_appParam._mean_kernel_enabled);
            sldMeanKernel.setValue(_appParam._mean_kernel_size);
            // mexican hat
            chkMexicanHat.setSelected(_appParam._mexican_hat_enabled);
        }
    }

    private void jbInit() throws Exception {
        this.getContentPane().setLayout(null);
        this.setDefaultCloseOperation(3);
        this.setResizable(false);
        this.setTitle("Application Parameter");
        lblCannyHighThreshold.setForeground(Color.gray);
        lblCannyHighThreshold.setText("High Threshold");
        lblCannyHighThreshold.setBounds(new Rectangle(22, 212, 205, 24));
        sldCannyHighThreshold.setMaximum(255);
        sldCannyHighThreshold.setMinimum(0);
        sldCannyHighThreshold.setMinorTickSpacing(1);
        sldCannyHighThreshold.setPaintLabels(false);
        sldCannyHighThreshold.setPaintTicks(false);
        sldCannyHighThreshold.setBounds(new Rectangle(15, 236, 187, 34));
        sldCannyHighThreshold.addChangeListener(new
                AppParameterDialog_sldCannyHighThreshold_changeAdapter(this));
        sldMeanKernel.setMaximum(15);
        sldMeanKernel.setMinimum(3);
        sldMeanKernel.setMinorTickSpacing(2);
        sldMeanKernel.setPaintLabels(true);
        sldMeanKernel.setPaintTicks(true);
        sldMeanKernel.setBounds(new Rectangle(15, 34, 214, 34));
        sldMeanKernel.addChangeListener(new
                AppParameterDialog_sldMeanKernel_changeAdapter(this));
        lblCannyHighThresholdValue.setForeground(Color.gray);
        lblCannyHighThresholdValue.setHorizontalAlignment(SwingConstants.RIGHT);
        lblCannyHighThresholdValue.setHorizontalTextPosition(SwingConstants.
                RIGHT);
        lblCannyHighThresholdValue.setText("72");
        lblCannyHighThresholdValue.setBounds(new Rectangle(200, 241, 30, 24));
        lblCannyGaussian.setForeground(Color.gray);
        lblCannyGaussian.setText("Gaussian Sigma");
        lblCannyGaussian.setBounds(new Rectangle(22, 101, 205, 24));
        lblCannyGaussianValue.setForeground(Color.gray);
        lblCannyGaussianValue.setHorizontalAlignment(SwingConstants.RIGHT);
        lblCannyGaussianValue.setHorizontalTextPosition(SwingConstants.RIGHT);
        lblCannyGaussianValue.setText("1.1");
        lblCannyGaussianValue.setBounds(new Rectangle(200, 127, 30, 24));
        sldCannyGaussian.setMajorTickSpacing(1);
        sldCannyGaussian.setMaximum(50);
        sldCannyGaussian.setMinorTickSpacing(1);
        sldCannyGaussian.setPaintTicks(false);
        sldCannyGaussian.setBounds(new Rectangle(15, 122, 187, 34));
        sldCannyGaussian.addChangeListener(new
                AppParameterDialog_sldCannyGaussian_changeAdapter(this));
        lblCannyLowThreshold.setForeground(Color.gray);
        lblCannyLowThreshold.setText("Low Threshold");
        lblCannyLowThreshold.setBounds(new Rectangle(22, 153, 205, 24));
        lblCannyLowThresholdValue.setForeground(Color.gray);
        lblCannyLowThresholdValue.setHorizontalAlignment(SwingConstants.RIGHT);
        lblCannyLowThresholdValue.setHorizontalTextPosition(SwingConstants.
                RIGHT);
        lblCannyLowThresholdValue.setText("72");
        lblCannyLowThresholdValue.setBounds(new Rectangle(200, 182, 30, 24));
        sldCannyLowThreshold.setMaximum(255);
        sldCannyLowThreshold.setMinorTickSpacing(1);
        sldCannyLowThreshold.setBounds(new Rectangle(15, 177, 187, 34));
        sldCannyLowThreshold.addChangeListener(new
                AppParameterDialog_sldCannyLowThreshold_changeAdapter(this));
        chkCanny.setText("Canny Edge Detector");
        chkCanny.setBounds(new Rectangle(6, 74, 207, 25));
        chkCanny.addActionListener(new
                                   AppParameterDialog_chkCanny_actionAdapter(this));
        chkMexicanHat.setText("Mexican Hat Filter");
        chkMexicanHat.setBounds(new Rectangle(6, 273, 207, 25));
        chkMexicanHat.addActionListener(new
                AppParameterDialog_chkMexicanHat_actionAdapter(this));
        chkMeanKernel.setText("Mean Kernel");
        chkMeanKernel.setBounds(new Rectangle(6, 4, 207, 25));
        chkMeanKernel.addActionListener(new
                                        AppParameterDialog_chkMeanKernel_actionAdapter(this));
        btnClose.setBounds(new Rectangle(153, 318, 89, 30));
        btnClose.setText("Close");
        btnClose.addActionListener(new
                                   AppParameterDialog_btnClose_actionAdapter(this));
        this.getContentPane().add(sldMeanKernel);
        this.getContentPane().add(lblCannyGaussian);
        this.getContentPane().add(lblCannyGaussianValue);
        this.getContentPane().add(sldCannyGaussian);
        this.getContentPane().add(lblCannyLowThreshold);
        this.getContentPane().add(lblCannyLowThresholdValue);
        this.getContentPane().add(sldCannyLowThreshold);
        this.getContentPane().add(lblCannyHighThresholdValue);
        this.getContentPane().add(lblCannyHighThreshold);
        this.getContentPane().add(sldCannyHighThreshold);
        this.getContentPane().add(chkCanny);
        this.getContentPane().add(chkMeanKernel);
        this.getContentPane().add(chkMexicanHat);
        this.getContentPane().add(btnClose);
    }

    public void chkMeanKernel_actionPerformed(ActionEvent e) {
        _appParam._mean_kernel_enabled = chkMeanKernel.isSelected();
        configure();
    }

    public void chkCanny_actionPerformed(ActionEvent e) {
        _appParam._canny_enabled = chkCanny.isSelected();
        configure();
    }

    public void chkMexicanHat_actionPerformed(ActionEvent e) {
        _appParam._mexican_hat_enabled = chkMexicanHat.isSelected();
        configure();
    }

    public void btnClose_actionPerformed(ActionEvent e) {
        dispose();
    }

    public void sldCannyHighThreshold_stateChanged(ChangeEvent e) {
        if( sldCannyHighThreshold.getValue() < _appParam._canny_threshold_low ) {
            _appParam._canny_threshold_high = _appParam._canny_threshold_low;
        } else {
            _appParam._canny_threshold_high = sldCannyHighThreshold.getValue();
        }
        configure();
    }

    public void sldCannyLowThreshold_stateChanged(ChangeEvent e) {
        _appParam._canny_threshold_low = sldCannyLowThreshold.getValue();
        configure();
    }

    public void sldMeanKernel_stateChanged(ChangeEvent e) {
        _appParam._mean_kernel_size = sldMeanKernel.getValue();
        configure();
    }

    public void sldCannyGaussian_stateChanged(ChangeEvent e) {
        _appParam._gaussian_sigma = (float)(sldCannyGaussian.getValue()/10.0);
        configure();
    }


}


class AppParameterDialog_btnClose_actionAdapter implements ActionListener {
    private AppParameterDialog adaptee;
    AppParameterDialog_btnClose_actionAdapter(AppParameterDialog adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.btnClose_actionPerformed(e);
    }
}


class AppParameterDialog_chkMexicanHat_actionAdapter implements ActionListener {
    private AppParameterDialog adaptee;
    AppParameterDialog_chkMexicanHat_actionAdapter(AppParameterDialog adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.chkMexicanHat_actionPerformed(e);
    }
}



class AppParameterDialog_sldCannyHighThreshold_changeAdapter implements
        ChangeListener {
    private AppParameterDialog adaptee;
    AppParameterDialog_sldCannyHighThreshold_changeAdapter(AppParameterDialog
            adaptee) {
        this.adaptee = adaptee;
    }

    public void stateChanged(ChangeEvent e) {
        adaptee.sldCannyHighThreshold_stateChanged(e);
    }
}




class AppParameterDialog_sldCannyLowThreshold_changeAdapter implements
        ChangeListener {
    private AppParameterDialog adaptee;
    AppParameterDialog_sldCannyLowThreshold_changeAdapter(AppParameterDialog
            adaptee) {
        this.adaptee = adaptee;
    }

    public void stateChanged(ChangeEvent e) {
        adaptee.sldCannyLowThreshold_stateChanged(e);
    }
}

class AppParameterDialog_sldCannyGaussian_changeAdapter implements
        ChangeListener {
    private AppParameterDialog adaptee;
    AppParameterDialog_sldCannyGaussian_changeAdapter(AppParameterDialog
            adaptee) {
        this.adaptee = adaptee;
    }

    public void stateChanged(ChangeEvent e) {
        adaptee.sldCannyGaussian_stateChanged(e);
    }
}


class AppParameterDialog_chkCanny_actionAdapter implements ActionListener {
    private AppParameterDialog adaptee;
    AppParameterDialog_chkCanny_actionAdapter(AppParameterDialog adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.chkCanny_actionPerformed(e);
    }
}


class AppParameterDialog_sldMeanKernel_changeAdapter implements ChangeListener {
    private AppParameterDialog adaptee;
    AppParameterDialog_sldMeanKernel_changeAdapter(AppParameterDialog adaptee) {
        this.adaptee = adaptee;
    }

    public void stateChanged(ChangeEvent e) {
        adaptee.sldMeanKernel_stateChanged(e);
    }
}


class AppParameterDialog_chkMeanKernel_actionAdapter implements ActionListener {
    private AppParameterDialog adaptee;
    AppParameterDialog_chkMeanKernel_actionAdapter(AppParameterDialog adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.chkMeanKernel_actionPerformed(e);
    }
}
