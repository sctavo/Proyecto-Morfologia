# Compilar todo
javac -encoding UTF-8 -d . src\main\java\io\ImageIOUtil.java src\main\java\morph\StructuringElement.java src\main\java\morph\MorphOps.java src\main\java\parallel\ParallelMorph.java src\main\java\app\Main.java

# Ejecutar version secuencial
java app.Main --op erosion --kernel cuadrado --mode seq --in input\imagen.png --out output\seq_erosion.png

# Ejecutar version paralela
java app.Main --op dilation --kernel cruz --mode par --in input\imagen.png --out output\par_dilation.png

# Comparar ambas versiones
java app.Main --op erosion --kernel diamante --compare --in input\imagen.png --out output\comparacion.png