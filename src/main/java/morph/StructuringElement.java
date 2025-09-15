package morph;

public class StructuringElement {
    private boolean[][] mask;
    private int centerX, centerY;
    private String name;

    public StructuringElement(boolean[][] mask, int centerX, int centerY, String name) {
        this.mask = mask;
        this.centerX = centerX;
        this.centerY = centerY;
        this.name = name;
    }

    // Elemento 1: Cruz 3x3
    public static StructuringElement createCross3() {
        boolean[][] mask = {
            {false, true,  false},
            {true,  true,  true },
            {false, true,  false}
        };
        return new StructuringElement(mask, 1, 1, "Cruz 3x3");
    }

    // Elemento 2: Cuadrado 3x3
    public static StructuringElement createSquare3() {
        boolean[][] mask = {
            {true, true, true},
            {true, true, true},
            {true, true, true}
        };
        return new StructuringElement(mask, 1, 1, "Cuadrado 3x3");
    }

    // Elemento 3: Diamante 5x5
    public static StructuringElement createDiamond5() {
        boolean[][] mask = {
            {false, false, true,  false, false},
            {false, true,  true,  true,  false},
            {true,  true,  true,  true,  true },
            {false, true,  true,  true,  false},
            {false, false, true,  false, false}
        };
        return new StructuringElement(mask, 2, 2, "Diamante 5x5");
    }

    // Elemento 4: Línea horizontal 5x1
    public static StructuringElement createHorizontalLine5() {
        boolean[][] mask = {
            {true, true, true, true, true}
        };
        return new StructuringElement(mask, 2, 0, "Línea Horizontal 5x1");
    }

    // Elemento 5: Línea vertical 1x5
    public static StructuringElement createVerticalLine5() {
        boolean[][] mask = {
            {true},
            {true},
            {true},
            {true},
            {true}
        };
        return new StructuringElement(mask, 0, 2, "Línea Vertical 1x5");
    }

    // Fábrica de elementos por nombre
    public static StructuringElement create(String elementName) {
        switch(elementName.toLowerCase()) {
            case "cruz": case "cross":
                return createCross3();
            case "cuadrado": case "square":
                return createSquare3();
            case "diamante": case "diamond":
                return createDiamond5();
            case "horizontal": case "hline":
                return createHorizontalLine5();
            case "vertical": case "vline":
                return createVerticalLine5();
            default:
                throw new IllegalArgumentException("Elemento desconocido: " + elementName);
        }
    }

    // Gets
    public boolean[][] getMask() { return mask; }
    public int getCenterX() { return centerX; }
    public int getCenterY() { return centerY; }
    public String getName() { return name; }
    public int getWidth() { return mask[0].length; }
    public int getHeight() { return mask.length; }

    public void printMask() {
        System.out.println("Elemento estructurante: " + name);
        for (int y = 0; y < mask.length; y++) {
            for (int x = 0; x < mask[y].length; x++) {
                System.out.print(mask[y][x] ? " " : "·");
            }
            if (y == centerY) System.out.print(" <- centro");
            System.out.println();
        }
    }
}