import java.awt.image.*;
import java.awt.*;
import java.awt.color.ColorSpace;

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
public class HoughTransformation {

    private int width, height;

    public BufferedImage houghTransformCircle(BufferedImage edgImg,
                                              BufferedImage roiImg,
                                              BufferedImage cenImg) {

        width = edgImg.getWidth();
        height = edgImg.getHeight();
        int[] imgPixels = new int[width * height];
        int[] imgCenters = new int[width * height];
        byte[][][] acc = new byte[width][height][25-3];

        PixelGrabber pixGrab = new PixelGrabber(edgImg, 0, 0, width, height,
                                                imgPixels, 0, width);
        try {
            pixGrab.grabPixels();
        } catch (InterruptedException ex) {
            System.out.println(ex.getMessage());
        }

        pixGrab = new PixelGrabber(cenImg, 0, 0, width, height,
                                   imgCenters, 0, width);
        try {
            pixGrab.grabPixels();
        } catch (InterruptedException ex) {
            System.out.println(ex.getMessage());
        }


        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // if point match
                if (ImageUtils.getRed(imgPixels[y * width + x]) != 0) {
                    for (int r = 3; r < 25; r++) {
                        drawCircle(x, y, r, acc);
                    }
                }
            }
        }
        int maxVal = 0;
        int rx, ry, rr, lx, ly, lr, rcount, lcount;
        rx = ry = rr = lx = ly = lr = rcount = lcount = 0;
        for (int r = 3; r < 25-3; r++) {
            for (int x = 0; x < width/2; x++) {
                for (int y = 0; y < height; y++) {
                    if (acc[x][y][r] > 15 &&
                        ImageUtils.getRed(imgCenters[y * width + x]) != 0 ) {
                        ++rcount;
                        rx += x;
                        ry += y;
                        rr += r;
                    }
                }
            }
        }
        maxVal = 0;
        for (int r = 3; r < 25-3; r++) {
            for (int x = width/2; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    if (acc[x][y][r] > 15 &&
                        ImageUtils.getRed(imgCenters[y * width + x]) != 0 ) {
                        ++lcount;
                        lx += x;
                        ly += y;
                        lr += r;
                    }
                }
            }
        }
        if( lcount > 0 && rcount > 0 ) {
            Graphics g2d = (Graphics2D) roiImg.getGraphics();
            g2d.setColor(Color.YELLOW);
            g2d.drawOval((lx / lcount) - (lr / lcount),
                         (ly / lcount) - (lr / lcount), (lr / lcount) * 2,
                         (lr / lcount) * 2);
            g2d.drawOval((rx / rcount) - (rr / rcount),
                         (ry / rcount) - (rr / rcount), (rr / rcount) * 2,
                         (rr / rcount) * 2);
        }
        return roiImg;
    }


    public void drawCircle(int xCenter, int yCenter, int radius, byte[][][] acc) {
        int x, y, r2;

        r2 = radius * radius;

        if ((yCenter + radius) < acc[0].length) {
            acc[xCenter][yCenter + radius][radius - 3] += 1;
        }
        if ((yCenter - radius) >= 0) {
            acc[xCenter][yCenter - radius][radius - 3] += 1;
        }
        if ((xCenter + radius) < acc.length) {
            acc[xCenter + radius][yCenter][radius - 3] += 1;
        }
        if ((xCenter - radius) >= 0) {
            acc[xCenter - radius][yCenter][radius - 3] += 1;
        }
        x = 1;
        y = (int) (Math.sqrt(r2 - 1) + 0.5);
        while (x < y) {
            if ((yCenter + y) < acc[0].length && (xCenter + x) < acc.length) {
                acc[xCenter + x][yCenter + y][radius - 3] += 1;
            }
            if ((xCenter + x) < acc.length && (yCenter - y) >= 0) {
                acc[xCenter + x][yCenter - y][radius - 3]+= 1;
            }
            if ((xCenter - x) >= 0 && (yCenter + y) < acc[0].length) {
                acc[xCenter - x][yCenter + y][radius - 3]+= 1;
            }
            if ((xCenter - x) >= 0 && (yCenter - y) >= 0) {
                acc[xCenter - x][yCenter - y][radius - 3]+= 1;
            }
            if ((yCenter + x) < acc[0].length && (xCenter + y) < acc.length) {
                acc[xCenter + y][yCenter + x][radius - 3]+= 1;
            }
            if ((xCenter + y) < acc.length && (yCenter - x) >= 0) {
                acc[xCenter + y][yCenter - x][radius - 3]+= 1;
            }
            if ((xCenter - y) >= 0 && (yCenter + x) < acc[0].length) {
                acc[xCenter - y][yCenter + x][radius - 3]+= 1;
            }
            if ((xCenter - y) >= 0 && (yCenter - x) >= 0) {
                acc[xCenter - y][yCenter - x][radius - 3]+= 1;
            }
            x += 1;
            y = (int) (Math.sqrt(r2 - x * x) + 0.5);
        }
        if (x == y) {
            if ((xCenter + x) < acc[0].length && (yCenter + y) < acc[0].length) {
                acc[xCenter + x][yCenter + y][radius - 3] += 1;
            }
            if ((xCenter + x) < acc[0].length && (yCenter - y) >= 0) {
                acc[xCenter + x][yCenter - y][radius - 3]+= 1;
            }
            if ((xCenter - x) >= 0 && (yCenter + y) < acc[0].length) {
                acc[xCenter - x][yCenter + y][radius - 3]+= 1;
            }
            if ((xCenter - x) >= 0 && (yCenter - y) >= 0) {
                acc[xCenter - x][yCenter - y][radius - 3]+= 1;
            }
        }
    }

}
