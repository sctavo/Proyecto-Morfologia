package parallel;

import java.awt.image.BufferedImage;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import morph.StructuringElement;

public class ParallelMorph {

    private static final int DEFAULT_THRESHOLD = 512 * 512; // tiles de 512x512

    public static BufferedImage erosionRGB(BufferedImage input, StructuringElement se) {
        return processParallel(input, se, true, DEFAULT_THRESHOLD);
    }

    public static BufferedImage dilationRGB(BufferedImage input, StructuringElement se) {
        return processParallel(input, se, false, DEFAULT_THRESHOLD);
    }

    private static BufferedImage processParallel(BufferedImage input, StructuringElement se, boolean isErosion, int threshold) {
        int w = input.getWidth();
        int h = input.getHeight();

        // Extraer pixels de entrada
        int[] src = input.getRGB(0, 0, w, h, null, 0, w);
        int[] dst = new int[src.length];

        // Crear pool ForkJoin y ejecutar
        ForkJoinPool pool = new ForkJoinPool();
        try {
            MorphTask task = new MorphTask(src, dst, w, h, se, isErosion, 0, 0, w, h, threshold);
            pool.invoke(task);
        } finally {
            pool.shutdown();
        }

        // Crear imagen resultado
        BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        result.setRGB(0, 0, w, h, dst, 0, w);
        return result;
    }

    // Tarea recursiva ForkJoin
    private static class MorphTask extends RecursiveAction {
        private final int[] src, dst;
        private final int imgWidth, imgHeight;
        private final StructuringElement se;
        private final boolean isErosion;
        private final int x0, y0, x1, y1; // rectangulo a procesar
        private final int threshold;

        MorphTask(int[] src, int[] dst, int imgWidth, int imgHeight, 
                 StructuringElement se, boolean isErosion,
                 int x0, int y0, int x1, int y1, int threshold) {
            this.src = src;
            this.dst = dst;
            this.imgWidth = imgWidth;
            this.imgHeight = imgHeight;
            this.se = se;
            this.isErosion = isErosion;
            this.x0 = x0;
            this.y0 = y0;
            this.x1 = x1;
            this.y1 = y1;
            this.threshold = threshold;
        }

        @Override
        protected void compute() {
            int area = (x1 - x0) * (y1 - y0);

            if (area <= threshold) {
                // Procesar directamente
                processBlock();
            } else {
                // Dividir en 4 cuadrantes
                int midX = (x0 + x1) / 2;
                int midY = (y0 + y1) / 2;

                invokeAll(
                    new MorphTask(src, dst, imgWidth, imgHeight, se, isErosion, x0, y0, midX, midY, threshold),
                    new MorphTask(src, dst, imgWidth, imgHeight, se, isErosion, midX, y0, x1, midY, threshold),
                    new MorphTask(src, dst, imgWidth, imgHeight, se, isErosion, x0, midY, midX, y1, threshold),
                    new MorphTask(src, dst, imgWidth, imgHeight, se, isErosion, midX, midY, x1, y1, threshold)
                );
            }
        }

        private void processBlock() {
            boolean[][] mask = se.getMask();
            int mh = se.getHeight();
            int mw = se.getWidth();
            int cx = se.getCenterX();
            int cy = se.getCenterY();

            for (int y = y0; y < y1; y++) {
                for (int x = x0; x < x1; x++) {
                    int resultR, resultG, resultB;
                    int a = (src[y * imgWidth + x] >>> 24) & 0xff;

                    if (isErosion) {
                        resultR = resultG = resultB = 255; // minimo inicial
                    } else {
                        resultR = resultG = resultB = 0;   // maximo inicial
                    }

                    for (int j = 0; j < mh; j++) {
                        int yy = y + (j - cy);
                        if (yy < 0 || yy >= imgHeight) continue;

                        for (int i = 0; i < mw; i++) {
                            if (!mask[j][i]) continue;
                            int xx = x + (i - cx);
                            if (xx < 0 || xx >= imgWidth) continue;

                            int p = src[yy * imgWidth + xx];
                            int r = (p >> 16) & 0xff;
                            int g = (p >> 8) & 0xff;
                            int b = (p) & 0xff;

                            if (isErosion) {
                                if (r < resultR) resultR = r;
                                if (g < resultG) resultG = g;
                                if (b < resultB) resultB = b;
                            } else {
                                if (r > resultR) resultR = r;
                                if (g > resultG) resultG = g;
                                if (b > resultB) resultB = b;
                            }
                        }
                    }
                    dst[y * imgWidth + x] = (a << 24) | (resultR << 16) | (resultG << 8) | (resultB);
                }
            }
        }
    }
}