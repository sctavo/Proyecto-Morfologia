package morph;

import java.awt.image.BufferedImage;

public class MorphOps {

    // Aplica erosion sobre RGB por canal con un elemento estructurante binario
    public static BufferedImage erosionRGB(BufferedImage input, StructuringElement se) {
        int w = input.getWidth();
        int h = input.getHeight();
        int[] src = input.getRGB(0, 0, w, h, null, 0, w);
        int[] dst = new int[src.length];

        boolean[][] mask = se.getMask();
        int mh = se.getHeight();
        int mw = se.getWidth();
        int cx = se.getCenterX();
        int cy = se.getCenterY();

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int minR = 255, minG = 255, minB = 255;
                int a = (src[y * w + x] >>> 24) & 0xff; // preserva alfa

                for (int j = 0; j < mh; j++) {
                    int yy = y + (j - cy);
                    if (yy < 0 || yy >= h) continue;

                    for (int i = 0; i < mw; i++) {
                        if (!mask[j][i]) continue;
                        int xx = x + (i - cx);
                        if (xx < 0 || xx >= w) continue;

                        int p = src[yy * w + xx];
                        int r = (p >> 16) & 0xff;
                        int g = (p >> 8) & 0xff;
                        int b = (p) & 0xff;

                        if (r < minR) minR = r;
                        if (g < minG) minG = g;
                        if (b < minB) minB = b;
                    }
                }
                dst[y * w + x] = (a << 24) | (minR << 16) | (minG << 8) | (minB);
            }
        }

        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        out.setRGB(0, 0, w, h, dst, 0, w);
        return out;
    }

    // Aplica dilatacion sobre RGB por canal con un elemento estructurante binario
    public static BufferedImage dilationRGB(BufferedImage input, StructuringElement se) {
        int w = input.getWidth();
        int h = input.getHeight();
        int[] src = input.getRGB(0, 0, w, h, null, 0, w);
        int[] dst = new int[src.length];

        boolean[][] mask = se.getMask();
        int mh = se.getHeight();
        int mw = se.getWidth();
        int cx = se.getCenterX();
        int cy = se.getCenterY();

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int maxR = 0, maxG = 0, maxB = 0;
                int a = (src[y * w + x] >>> 24) & 0xff;

                for (int j = 0; j < mh; j++) {
                    int yy = y + (j - cy);
                    if (yy < 0 || yy >= h) continue;

                    for (int i = 0; i < mw; i++) {
                        if (!mask[j][i]) continue;
                        int xx = x + (i - cx);
                        if (xx < 0 || xx >= w) continue;

                        int p = src[yy * w + xx];
                        int r = (p >> 16) & 0xff;
                        int g = (p >> 8) & 0xff;
                        int b = (p) & 0xff;

                        if (r > maxR) maxR = r;
                        if (g > maxG) maxG = g;
                        if (b > maxB) maxB = b;
                    }
                }
                dst[y * w + x] = (a << 24) | (maxR << 16) | (maxG << 8) | (maxB);
            }
        }

        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        out.setRGB(0, 0, w, h, dst, 0, w);
        return out;
    }
}
