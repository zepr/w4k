# W4K
Un worms-like réalisé pour le Java 4K Game Programming Contest 2006 (JAVA 4K)

## Jouer
Le jeu se présente sous la forme d'une archive Java (Java ARchive, JAR), et nécessite donc une [machine virtuelle Java](https://www.java.com/fr/download/) pour s'exécuter. 
Si le double-clic ne fonctionne pas, le programme peut être lancé avec la commande
```
/chemin/vers/java -jar wk4.jar
```
## Construire
Pour reconstruire une archive de 4Ko à partir du code source de ce repository

Plusieurs outils sont nécessaires 
* [JoGa](https://web.archive.org/web/20070701185344/http://www.nq4.de/index.php?option=com_content&task=view&id=12&Itemid=27) - Outil d'optimisation de bytecode
* [7-zip](http://www.7-zip.org/) - Le célèbre outil de compression

Ce tutoriel a été écrit en 2006. Le site de JoGa a disparu tout comme son binaire (Le lien plus haut pointe sur une copie de [The Wayback Machine](https://archive.org/web/). Pour référence, une copie du jar a été ajoutée dans le répertoire lib de ce projet. JoGa est un outil freeware (c)2007 - nq4 / A.Ebbert, A. Houben. 
Des outils d'optimisation récents devraient permettre d'obtenir le même résultat (Peut-être même un fichier plus petit).
* [ProGuard](https://www.guardsquare.com/en/proguard) 
* [yGuard](https://www.yworks.com/products/yguard)


Les opérations à réaliser sont les suivantes

* Compiler le code
```
javac -source 1.4 -target 1.4 W.java
```
* Générer le jar intermédiaire
```
jar cvf W4K.jar W.class
```
* Optimiser le bytecode
```
java -jar lib/JoGa.jar W4K.jar
```
* Récupérer le fichier W.class de l'archive JoGaDIR/W4K_JOGA.jar
```
7za x JoGaDIR/W4K_JOGA.jar W.class
```
* Générer l'archive finale
```
7za a -tzip -mx=9 W4K_final.jar W.class META-INF/MANIFEST.MF
```

Le fichier W4K_final.jar devrait faire exactement 4096 bytes, soit précisément 4Ko
