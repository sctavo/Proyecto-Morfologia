package io;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageIOUtil {

    public static BufferedImage readPNG(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("Archivo no encontrado: " + filePath);
        }

        BufferedImage image = ImageIO.read(file);
        if (image == null) {
            throw new IOException("No se pudo leer la imagen: " + filePath);
        }

        System.out.println("Imagen cargada: " + image.getWidth() + "x" + image.getHeight() + " píxeles");
        return image;
    }

    public static void writePNG(BufferedImage image, String filePath) throws IOException {
        File file = new File(filePath);

        // Crear directorios si no existen
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        boolean success = ImageIO.write(image, "PNG", file);
        if (!success) {
            throw new IOException("No se pudo escribir la imagen: " + filePath);
        }

        System.out.println("Imagen guardada: " + filePath);
    }

    public static int[] toIntArray(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        return image.getRGB(0, 0, width, height, null, 0, width);
    }

    public static BufferedImage fromIntArray(int[] rgbData, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, width, height, rgbData, 0, width);
        return image;
    }

    public static void printImageInfo(BufferedImage image) {
        System.out.println("Información de imagen:");
        System.out.println("  Ancho: " + image.getWidth());
        System.out.println("  Alto: " + image.getHeight());
        System.out.println("  Tipo: " + image.getType());
        System.out.println("  Canales: " + (image.getColorModel().hasAlpha() ? "RGBA" : "RGB"));
    }
}