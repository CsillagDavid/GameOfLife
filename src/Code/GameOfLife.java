/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Code;

import java.awt.Point;
import java.util.LinkedList;

/**
 *
 * @author csdvi
 */
public class GameOfLife {

    /*a játéktér 2 dimenziós boolean típusú láncolt listában van tárolva
    a false értékek jelentik az üres, míg a true a sejttel ellátott cellákat*/
    private LinkedList<LinkedList<Boolean>> field;
    /*a játéktér dimenziói*/
    private int HorizontalSize;
    private int VerticalSize;
    /*a játéktér koordináta transzformációját megkönnyítő változók, melyek a koordinátarendszerben felvehető legkisebb (negatív)értékeket tárolják*/
    private int minX;
    private int minY;

    public GameOfLife(int HorizontalSize, int VerticalSize) {
        field = new LinkedList<LinkedList<Boolean>>();
        /**/
        for (int i = 0; i < VerticalSize; i++) {
            field.add(new LinkedList<Boolean>());
            for (int j = 0; j < HorizontalSize; j++) {
                field.get(i).add(false);
            }
        }
        /*a játéktér közepét 0;0-nak jelöljük ki
        ez alapján meghatározzuk, hogy a koordináta rendszer x és y tengelyén meddig mehetünk el negatív irányban*/
        minX = -Math.floorDiv(HorizontalSize, 2);
        minY = -Math.floorDiv(VerticalSize, 2);

        this.HorizontalSize = HorizontalSize;
        this.VerticalSize = VerticalSize;
    }

    private void nextGeneration() {
        int neighbors;
        /*eltároljuk, hogy szükségünk van-e a játéktér bővítésére, és ah igen milyen irányban*/
        boolean addSpaceToDown = false;
        boolean addSpaceToUp = false;
        boolean addSpaceToLeft = false;
        boolean addSpaceToRight = false;
        /*listákban tároljuk a született és elhalálozott sejteket*/
        LinkedList<Point> born = new LinkedList<Point>();
        LinkedList<Point> death = new LinkedList<Point>();

        for (int i = 0; i < VerticalSize; i++) {
            for (int j = 0; j < HorizontalSize; j++) {
                neighbors = 0;
                for (int k = -1; k < 2; k++) {
                    for (int l = -1; l < 2; l++) {
                        /*a túl és alul indexelés try catch blokkal le van kezelve*/
                        try {
                            if (field.get(i + k).get(j + l)) {
                                neighbors++;
                            }
                        } catch (IndexOutOfBoundsException e) {
                            /*ha akkor keletkezett kivétel, amikor egy élő sejt szomszéd celláit figyeltük meg,
                            akkor bővítjük a teret a megfelelő irányba*/
                            if (field.get(i).get(j)) {
                                if ((i + k) >= VerticalSize) {
                                    addSpaceToDown = true;
                                }
                                if ((i + k) < 1) {
                                    addSpaceToUp = true;
                                }
                                if ((j + l) >= HorizontalSize) {
                                    addSpaceToRight = true;
                                }
                                if ((j + l) < 1) {
                                    addSpaceToLeft = true;
                                }
                            }
                        };
                    }
                }
                /*a szabály szerint az aktuálisan vizsgált cellát felvesszük vagy a született, vagy a halott listába
                a szabályhoz képest a számok értéke 1-gyel meg van növelve, mivel a szomszédokhoz hozzásoroltuk önmagát is*/
                if (!field.get(i).get(j) && neighbors == 3) {
                    born.add(new Point(i, j));
                } else if (field.get(i).get(j) && (neighbors < 3 || neighbors > 4)) {
                    death.add(new Point(i, j));
                }
            }
        }
        /*update-eljük a játékteret azokon a pontokon, ahol változás történt (születés vagy halál)*/
        for (Point p : born) {
            field.get(p.x).set(p.y, true);
        }
        for (Point p : death) {
            field.get(p.x).set(p.y, false);
        }
        /*a játékteret bővítjük a szükséges irány(ok)ba*/
        if (addSpaceToDown) {
            addSpaceToDown();
        }
        if (addSpaceToUp) {
            addSpaceToUp();
        }
        if (addSpaceToLeft) {
            addSpaceToLeft();
        }
        if (addSpaceToRight) {
            addSpaceToRight();
        }
    }

    public void startSimulation(int numOfGen) {
        for (int i = 0; i < numOfGen; i++) {
            nextGeneration();
        }
    }

    private void add(LinkedList<Boolean> l) {
        l.add(false);
    }

    private void addFirst(LinkedList<Boolean> l) {
        l.addFirst(false);
    }
    
    public void addSpaceToLeft() {
        /*a játékos szemszőgéből nézve, a tér bővítése a bal oldali irányba*/
        field.forEach(f -> addFirst(f));
        HorizontalSize++;
        minX--;
    }

    public void addSpaceToRight() {
        /*a játékos szemszögéből nézve, a tér bővítése a jobb oldali irányba*/
        field.forEach(f -> add(f));
        HorizontalSize++;
    }

    public void addSpaceToUp() {
        /*a játékos szemszögéből nézve, a tér bővítése felfelé*/
        field.addFirst(new LinkedList<Boolean>());
        for (int i = 0; i < HorizontalSize; i++) {
            field.getFirst().add(false);
        }
        VerticalSize++;
        minY--;
    }

    public void addSpaceToDown() {
        /*a játékos szemszögéből nézve, a tér bővítése lefelé*/
        field.add(new LinkedList<Boolean>());
        for (int i = 0; i < HorizontalSize; i++) {
            field.getLast().add(false);
        }
        VerticalSize++;
    }

    public void addCell(int x, int y) {
        /*kiszámítjuk az x,y koordinátáju cella indexeit*/
        int tempX = x - minX;
        int tempY = y - minY;
        /*ha a cella alul vagy túlindexelést okozna, akkor a teret a megfelelő irányba bővítjük olyan mértékben, hogy ezt elkerüljük*/
        for (int i = 0; i < -tempX; i++) {
            addSpaceToLeft();
        }
        for (int i = HorizontalSize; i <= tempX; i++) {
            addSpaceToRight();
        }
        for (int i = 0; i < -tempY; i++) {
            addSpaceToUp();
        }
        for (int i = VerticalSize; i <= tempY; i++) {
            addSpaceToDown();
        }
        /*a cella értékét negáljuk
        egy üres cellába elhelyezünk egy sejtet, vagy eltávolítjuk onnan, ez oldható meg a negálás művelettel*/
        field.get(y - minY).set(x - minX, !field.get(y - minY).get(x - minX));
    }

    public LinkedList<Point> getAliveCellsFromTheVisibleParts(int x, int y, int resolution) {
        /*az sejteket tartalmazó cellák koordinátáit összegyűjtük*/
        LinkedList<Point> aliveCells = new LinkedList<Point>();
        Point center = new Point(-minX + 1, -minY + 1);
        /*a resolution értékének felhasználásával kiszámítjuk, hogy a koordináta rendszerben melyik ponttól kezdődjön a cilus*/
        int resDivTwo = Math.floorDiv(resolution, 2);
        Point startingPoint = new Point(center.x - resDivTwo + x, center.y - resDivTwo + y);
        for (int i = startingPoint.y; i < startingPoint.y + resolution; i++) {
            for (int j = startingPoint.x; j < startingPoint.x + resolution; j++) {
                /*try catch blokkal akadályozzuk meg az esetleges túl vagy alul indexelésbőlfakadó hibákat*/
                try {
                    if (field.get(i).get(j)) {
                        aliveCells.add(new Point(j + minX, i + minY));
                    }
                } catch (IndexOutOfBoundsException e) {
                };
            }
        }
        return aliveCells;
    }

    public int getHorizontalSize() {
        return HorizontalSize;
    }

    public int getVerticalSize() {
        return VerticalSize;
    }

}
