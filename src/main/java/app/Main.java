package app;

import io.ImageIOUtil;
import morph.MorphOps;
import morph.StructuringElement;

import java.awt.image.BufferedImage;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length == 0 || contains(args, "--help")) {
            System.out.println("Uso:");
            System.out.println("  java app.Main --op erosion|dilation --kernel cruz|cuadrado|diamante|horizontal|vertical --in entrada.png --out salida.png");
            return;
        }

        String op = valueOf(args, "--op", "erosion");
        String kernel = valueOf(args, "--kernel", "cuadrado");
        String inPath = valueOf(args, "--in", null);
        String outPath = valueOf(args, "--out", null);

        if (inPath == null || outPath == null) {
            System.err.println("Faltan --in o --out");
            return;
        }

        BufferedImage img = ImageIOUtil.readPNG(inPath);
        StructuringElement se = StructuringElement.create(kernel);

        long t0 = System.nanoTime();
        BufferedImage out;
        if (op.equalsIgnoreCase("erosion")) {
            out = MorphOps.erosionRGB(img, se);
        } else {
            out = MorphOps.dilationRGB(img, se);
        }
        long t1 = System.nanoTime();
        System.out.println("Tiempo (solo algoritmo): " + ((t1 - t0) / 1_000_000) + " ms");

        ImageIOUtil.writePNG(out, outPath);
        System.out.println("Listo.");
    }

    private static boolean contains(String[] a, String k) {
        for (String s : a) if (s.equalsIgnoreCase(k)) return true;
        return false;
    }
    private static String valueOf(String[] a, String key, String def) {
        for (int i = 0; i < a.length - 1; i++) {
            if (a[i].equalsIgnoreCase(key)) return a[i + 1];
        }
        return def;
    }
}
