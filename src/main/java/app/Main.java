package app;

import io.ImageIOUtil;
import morph.MorphOps;
import morph.StructuringElement;
import parallel.ParallelMorph;

import java.awt.image.BufferedImage;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length == 0 || contains(args, "--help")) {
            System.out.println("=== PROYECTO MORFOLOGIA MATEMATICA ===");
            System.out.println("Uso:");
            System.out.println("  java app.Main --op erosion|dilation --kernel cruz|cuadrado|diamante|horizontal|vertical --mode seq|par --in entrada.png --out salida.png");
            System.out.println();
            System.out.println("Parametros:");
            System.out.println("  --op       : erosion o dilation");
            System.out.println("  --kernel   : cruz, cuadrado, diamante, horizontal, vertical");
            System.out.println("  --mode     : seq (secuencial) o par (paralelo)");
            System.out.println("  --in       : archivo PNG de entrada");
            System.out.println("  --out      : archivo PNG de salida");
            System.out.println("  --compare  : comparar secuencial vs paralelo");
            System.out.println();
            System.out.println("Ejemplos:");
            System.out.println("  java app.Main --op erosion --kernel cuadrado --mode seq --in input.png --out output_seq.png");
            System.out.println("  java app.Main --op dilation --kernel cruz --mode par --in input.png --out output_par.png");
            System.out.println("  java app.Main --op erosion --kernel diamante --compare --in input.png --out output.png");
            return;
        }

        String op = valueOf(args, "--op", "erosion");
        String kernel = valueOf(args, "--kernel", "cuadrado");
        String mode = valueOf(args, "--mode", "seq");
        String inPath = valueOf(args, "--in", null);
        String outPath = valueOf(args, "--out", null);
        boolean compare = contains(args, "--compare");

        if (inPath == null || outPath == null) {
            System.err.println("Error: Faltan --in o --out");
            return;
        }

        System.out.println("=== EJECUTANDO MORFOLOGIA ===");
        System.out.println("Operacion: " + op);
        System.out.println("Elemento estructurante: " + kernel);
        System.out.println("Modo: " + (compare ? "comparacion" : mode));
        System.out.println("Entrada: " + inPath);
        System.out.println("Salida: " + outPath);
        System.out.println();

        // Cargar imagen
        BufferedImage img = ImageIOUtil.readPNG(inPath);
        StructuringElement se = StructuringElement.create(kernel);

        // Mostrar info del elemento estructurante
        se.printMask();
        System.out.println();

        if (compare) {
            runComparison(img, se, op, outPath);
        } else {
            runSingle(img, se, op, mode, outPath);
        }
    }

    private static void runSingle(BufferedImage img, StructuringElement se, String op, String mode, String outPath) throws Exception {
        System.out.println("Ejecutando " + mode.toUpperCase() + "...");

        long t0 = System.nanoTime();
        BufferedImage result;

        if (mode.equalsIgnoreCase("par")) {
            if (op.equalsIgnoreCase("erosion")) {
                result = ParallelMorph.erosionRGB(img, se);
            } else {
                result = ParallelMorph.dilationRGB(img, se);
            }
        } else {
            if (op.equalsIgnoreCase("erosion")) {
                result = MorphOps.erosionRGB(img, se);
            } else {
                result = MorphOps.dilationRGB(img, se);
            }
        }

        long t1 = System.nanoTime();
        double timeMs = (t1 - t0) / 1_000_000.0;

        ImageIOUtil.writePNG(result, outPath);

        System.out.println("Tiempo de procesamiento: " + String.format("%.2f", timeMs) + " ms");
        System.out.println("Listo.");
    }

    private static void runComparison(BufferedImage img, StructuringElement se, String op, String outPath) throws Exception {
        System.out.println("=== COMPARACION SECUENCIAL vs PARALELO ===");

        // Ejecutar secuencial
        System.out.println("Ejecutando version SECUENCIAL...");
        long t0 = System.nanoTime();
        BufferedImage seqResult;
        if (op.equalsIgnoreCase("erosion")) {
            seqResult = MorphOps.erosionRGB(img, se);
        } else {
            seqResult = MorphOps.dilationRGB(img, se);
        }
        long t1 = System.nanoTime();
        double seqTime = (t1 - t0) / 1_000_000.0;

        // Ejecutar paralelo
        System.out.println("Ejecutando version PARALELO...");
        long t2 = System.nanoTime();
        BufferedImage parResult;
        if (op.equalsIgnoreCase("erosion")) {
            parResult = ParallelMorph.erosionRGB(img, se);
        } else {
            parResult = ParallelMorph.dilationRGB(img, se);
        }
        long t3 = System.nanoTime();
        double parTime = (t3 - t2) / 1_000_000.0;

        // Comparar resultados
        boolean identical = compareImages(seqResult, parResult);

        // Guardar resultado (del paralelo)
        ImageIOUtil.writePNG(parResult, outPath);

        // Mostrar resultados
        System.out.println();
        System.out.println("=== RESULTADOS ===");
        System.out.println("Tiempo secuencial: " + String.format("%.2f", seqTime) + " ms");
        System.out.println("Tiempo paralelo: " + String.format("%.2f", parTime) + " ms");
        System.out.println("Speedup: " + String.format("%.2fx", seqTime / parTime));
        System.out.println("Resultados identicos: " + (identical ? "SI" : "NO"));
        System.out.println("Archivo guardado: " + outPath);
    }

    private static boolean compareImages(BufferedImage img1, BufferedImage img2) {
        if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) {
            return false;
        }

        int w = img1.getWidth();
        int h = img1.getHeight();
        int[] data1 = img1.getRGB(0, 0, w, h, null, 0, w);
        int[] data2 = img2.getRGB(0, 0, w, h, null, 0, w);

        return Arrays.equals(data1, data2);
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