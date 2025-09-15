#  Proyecto-Morfologia

javac -d . src\main\java\app\Main.java

java app.Main

javac -encoding UTF-8 -d . src\main\java\io\ImageIOUtil.java src\main\java\morph\StructuringElement.java src\main\java\morph\MorphOps.java src\main\java\app\Main.java

java app.Main --op dilation --kernel cruz --in "C:\Users\gsgsn\Desktop\imagen.png" --out "C:\Users\gsgsn\Desktop\salida.png"