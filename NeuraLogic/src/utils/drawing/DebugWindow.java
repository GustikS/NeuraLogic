package utils.drawing;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.logging.Logger;

public class DebugWindow extends JFrame {

    private static final Logger LOG = Logger.getLogger(DebugWindow.class.getName());

    BufferedImage img;
    ImageIcon imageIcon;

    public DebugWindow(byte[] imageBytes, String name, Object lock) {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle(name + " (close this window to continue debugging...)");
        setVisible(true);
        setupImage(imageBytes);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent arg0) {
                synchronized (lock) {
                    setVisible(false);
                    dispose();
                    lock.notify();
                }
            }
        });
    }

    public void pauseUntilWindowCloses(Object lock) {
        Thread t = new Thread(() -> {
            synchronized (lock) {
                while (isVisible())
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        LOG.severe(e.getMessage());
                    }
                LOG.finer("Image window closed");
            }
        });
        t.start();

        try {
            t.join();
        } catch (InterruptedException e) {
            LOG.severe(e.getMessage());
        }
    }

    private void setupImage(byte[] imageBytes) {
        try {
            img = ImageIO.read(new ByteArrayInputStream(imageBytes));
            if (img == null) {
                LOG.severe("The graph's image could not have been drawn!");
            }
        } catch (IOException e) {
            LOG.severe(e.getMessage());
        }
        imageIcon = new ImageIcon(img);
        JLabel label = new JLabel(imageIcon);

        label.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                repaintImage(label);
            }
        });
        add(label);
        pack();
    }

    void repaintImage(JLabel label) {
        Image newimg = getScaledImage(img, getContentPane().getWidth(), getContentPane().getHeight());
        imageIcon = new ImageIcon(newimg);  // transform it back
        label.setIcon(imageIcon);
    }

    /**
     * Fast image scaling
     *
     * @param srcImg
     * @param w
     * @param h
     * @return
     */
    private Image getScaledImage(Image srcImg, int w, int h) {
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();

        return resizedImg;
    }

    /**
     * This is somewhat slower
     *
     * @return
     */
    private Image getScaledInstance() {
        return img.getScaledInstance(getContentPane().getWidth(), getContentPane().getHeight(), Image.SCALE_FAST);
    }
}
