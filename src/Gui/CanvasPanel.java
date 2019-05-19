/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *
 * @author csdvi
 */
public class CanvasPanel extends JPanel implements ActionListener {

    private Timer t;
    /*a timer alapértelmezett daley értékét tárolja*/
    private int fps = 60;
    /*a legenerált hátteret tartalmazó változók*/
    private BufferedImage canvas;
    private Graphics canvasGraphics;

    /*a megjelenített cellák középpontja
    mivel NxN-es mezőt jelenítünk meg,így mindkét dimenzióban azonos érték szerepelne
    ezt egyszerűsítendő egyetlen változó lett alkalmazva*/
    private int center;
    /*a megjeleníteni kívánt cellák 0,0 koordinátától való elmozdulása*/
    private int canvasVirtualPosX;
    private int canvasVirtualPosY;
    /*az NxN-es megjelenített mező N értékét tárolja*/
    private int resolution;
    /*megjelenítendő sejtek listája*/
    private LinkedList<Point> aliveCells;

    public CanvasPanel(int Width, int Height, int resolution, int canvasVirtualPosX, int canvasVirtualPosY) {
        this.setSize(Width, Height);
        this.resolution = resolution;

        setVirtualPos(canvasVirtualPosX, canvasVirtualPosY);
        /*kezdetben üres lista*/
        aliveCells = new LinkedList<Point>();

        canvas = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_BGR);
        canvasGraphics = canvas.getGraphics();
        /*háttér legenerálása*/
        generateField();
        /*középpont kiszámítása*/
        setCenter();
        /*szál létrehozása és indítása*/
        t = new Timer(1000 / fps, this);
        t.start();

    }

    @Override
    public void paint(Graphics g) {
        /*ideiglenes háttér
        elkerülhetővé teszi a háttér újboli legenerálásának szükségességét*/
        BufferedImage temp = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_BGR);
        Graphics tempGraphics = temp.getGraphics();

        tempGraphics.drawImage(canvas, 0, 0, null);
        tempGraphics.setColor(Color.yellow);
        /*egységnyi cella méretének kiszámítása*/
        float size = this.getWidth() / (float) resolution;
        /*az élő sejteket tartalmazó listán végig iterálunk és megjelenítjük őket*/
        for (Point p : aliveCells) {
            /*a sejtek koordinátáit átalakítjuk, hogy azok megfeleljenek a megjelenítéshez használt koordinátáknak*/
            tempGraphics.fillOval(Math.round(size * (p.x - canvasVirtualPosX + center - 2)), Math.round(size * (p.y - canvasVirtualPosY + center - 2)), Math.round(size), Math.round(size));
        }
        g.drawImage(temp, 0, 0, null);
    }

    public void decResolution() {
        if (resolution > 5) {
            resolution--;
            generateField();
        }
    }

    public void incResolution() {
        if (resolution < 50) {
            resolution++;
            generateField();
        }
    }

    public boolean zoomOut(int num) {
        return setResolution(resolution + num);
    }

    public boolean zoomIn(int num) {
        return setResolution(resolution - num);
    }

    private void generateField() {
        /*egységnyi cella méretének kiszámítása*/
        float size = this.getWidth() / (float) resolution;
        /*háttér festése adott színre*/
        canvasGraphics.setColor(Color.darkGray);
        canvasGraphics.fillRect(0, 0, this.getWidth(), this.getHeight());
        canvasGraphics.setColor(Color.lightGray);
        /*vonalak felrajzolása*/
        for (int i = 1; i < resolution; i++) {
            int coordinate = Math.round(size * i);
            canvasGraphics.drawLine(coordinate, 0, coordinate, this.getHeight());
            canvasGraphics.drawLine(0, coordinate, this.getWidth(), coordinate);
        }
    }

    public void setAliveCells(LinkedList<Point> aliveCells) {
        this.aliveCells = aliveCells;
    }

    public boolean setResolution(int res) {
        if (res < 71 && res > 5) {
            resolution = res;
            setCenter();
            generateField();
        }
        return !((res == 69) || (res == 7));
    }

    void setVirtualPos(int canvasVirtualPosX, int canvasVirtualPosY) {
        this.canvasVirtualPosX = canvasVirtualPosX;
        this.canvasVirtualPosY = canvasVirtualPosY;
    }

    void setVirtualPosX(int canvasVirtualPosX) {
        this.canvasVirtualPosX = canvasVirtualPosX;
    }

    void setVirtualPosY(int canvasVirtualPosY) {
        this.canvasVirtualPosY = canvasVirtualPosY;
    }

    private void setCenter() {
        center = ((resolution % 2 == 0) ? resolution / 2 : (resolution + 1) / 2);
    }

    public int getResolution() {
        return resolution;
    }

    public int getCenter() {
        return center;
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        repaint();
    }

}
