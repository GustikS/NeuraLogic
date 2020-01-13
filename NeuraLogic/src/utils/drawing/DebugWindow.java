package utils.drawing;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.logging.Logger;

public class DebugWindow extends JFrame {

    private static final Logger LOG = Logger.getLogger(DebugWindow.class.getName());

    private static final GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    private static final int screenWidth = gd.getDisplayMode().getWidth();
    private static final int screenHeight = gd.getDisplayMode().getHeight();

    BufferedImage img;

    private static boolean centerImage = false; //is nicely centered but causes flickering in combination with the JcrollPanel:( //todo try to remove the JScrollPanel

    public DebugWindow(byte[] imageBytes, String name, Object lock) {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle(name + " (close this window to continue debugging...)");
//        setLocationRelativeTo(null);
        if (centerImage) {
            setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
//        setLayout(new BorderLayout());
//        createBufferStrategy(2);
        }

        try {
            img = ImageIO.read(new ByteArrayInputStream(imageBytes));
            if (img == null) {
                LOG.severe("The graph's image could not have been drawn!");
                return;
            }
        } catch (IOException e) {
            LOG.severe(e.getMessage());
        }

        ImagePanel imagePanel = new ImagePanel(img);
        JComponent scroll = makeScrollingPanel(imagePanel);
        add(scroll, BorderLayout.CENTER);

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


        pack();
        setVisible(true);
        imagePanel.revalidate();
        imagePanel.repaint();
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


    public JComponent makeScrollingPanel(ImagePanel imagePanel) {
        JScrollPane scroll = new JScrollPane(imagePanel);
//        scroll.setPreferredSize(new Dimension(screenWidth, screenHeight));
        JViewport vport = scroll.getViewport();
        vport.addMouseMotionListener(imagePanel.mouseListener);
        vport.addMouseListener(imagePanel.mouseListener);
        vport.addMouseWheelListener(imagePanel.mouseListener);
        return scroll;
    }


    static class ImagePanel extends JPanel {
        private final static RenderingHints textRenderHints = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        private final static RenderingHints imageRenderHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        private final static RenderingHints renderHints = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        BufferedImage img;
        double scale = 1;
        Point position;

        MouseAdapter mouseListener;

//        boolean flip = true;

        public ImagePanel(BufferedImage img) {
            this.img = img;
            setBackground(Color.BLACK);
            setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));
            mouseListener = new MouseListener();
            position = new Point(0, 0);
            scale = Math.min((double) (screenWidth - 4) / img.getWidth(null), (double) (screenHeight - 15) / img.getHeight(null));
            setDoubleBuffered(true);
        }

        public static void applyRenderHints(Graphics2D g2d) {
            g2d.setRenderingHints(textRenderHints);
            g2d.setRenderingHints(imageRenderHints);
            g2d.setRenderingHints(renderHints);
        }

        @Override
        public Dimension getPreferredSize() {
//            int w = (int) Math.max(Math.round(img.getWidth() * scale), screenWidth - 15);
//            int h = (int) Math.max(Math.round(img.getHeight() * scale), screenHeight - 15);
//            Dimension size = new Dimension(w, h);
            Dimension size = new Dimension((int) (img.getWidth() * scale), (int) (img.getHeight() * scale));
            return size;
        }

        @Override
        protected void paintComponent(Graphics g) {
//            flip = ! flip;
//            if (flip){
//                g.dispose();
//                return;
//            }

            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D) g.create();
            applyRenderHints(g2d);  //turn on some nice effects

            if (centerImage) {
//                g2d.translate(this.getWidth() / 2 - (img.getWidth() * scale) / 2, this.getHeight() / 2 - (img.getHeight() * scale) / 2);
                g2d.translate(this.getWidth() / 2, (this.getHeight() + 15) / 2);
                g2d.translate(-(img.getWidth() * scale) / 2, -(img.getHeight() * scale) / 2);
            }

            g2d.scale(scale, scale);
            g2d.drawImage(img, 0, 0, this);
            g2d.dispose();

            invalidate();
            revalidate();
        }

        class MouseListener extends MouseAdapter {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                double delta = 1 - 0.05 * e.getPreciseWheelRotation();
                scale *= delta;
                zoomShift(e, delta);
                invalidate();
                revalidate();
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                JViewport vport = (JViewport) e.getSource();
                JComponent label = (JComponent) vport.getView();
                Point cp = e.getPoint();
                Point vp = vport.getViewPosition();
                vp.translate(position.x - cp.x, position.y - cp.y);
                label.scrollRectToVisible(new Rectangle(vp, vport.getSize()));
                position.setLocation(cp);
                invalidate();
                revalidate();
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                position.setLocation(e.getPoint());
            }

            private void zoomShift(MouseEvent e, double scaleDelta) {
                JViewport vport = (JViewport) e.getSource();
                JComponent label = (JComponent) vport.getView();
                Point cp = e.getPoint();
                Point vp = vport.getViewPosition();

                int newX = (int) (cp.x * (scaleDelta - 1.0) + scaleDelta * vp.x);
                int newY = (int) (cp.y * (scaleDelta - 1.0) + scaleDelta * vp.y);

                Point moved = new Point(newX, newY);
                label.scrollRectToVisible(new Rectangle(moved, vport.getSize()));
                position.setLocation(cp);
            }
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
        private Image getScaledInstance(int w, int h) {
            return img.getScaledInstance(w, h, Image.SCALE_FAST);
        }

    }
}
