package Gui;

import Code.GameOfLife;
import Enum.GameStatusEnum;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author csdvi
 */
public class MainFrame extends javax.swing.JFrame implements ActionListener {

    /**
     * Creates new form MainFrame
     */
    /*az ablak dimenziói*/
    private static final int WIDTH = 640;
    private static final int HEIGHT = 480;
    /*ez az osztály dolgozza fel és jeleníti meg a játéktér számunkra releváns részletét*/
    private CanvasPanel canvasPanel;

    private JButton startButton;
    private JButton stopButton;
    private JButton restartButton;
    private JButton zoomInButton;
    private JButton zoomOutButton;
    private JButton goLeftButton;
    private JButton goRightButton;
    private JButton goUpButton;
    private JButton goDownButton;

    /*az iránygombokat helyezzük rá*/
    private JPanel arrowButtonContainer;

    private JSlider speedSlider;
    /*a megjeleníteni kívánt cellák 0,0 koordinátától való elmozdulása*/
    private int canvasVirtualPosX;
    private int canvasVirtualPosY;
    /*az NxN-es megjelenített mező N értékét tárolja*/
    private int resolution = 15;
    /*a játék aktuális állapotát tárolja, ami lehet WAITING és RUNNING*/
    private GameStatusEnum gameStatus;

    private GameOfLife gol;

    private Timer t;
    /*a timer alapértelmezett daley értékét tárolja*/
    private int fps = 2;

    public MainFrame() {
        initComponents();
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT + 39));
        this.setSize(WIDTH, HEIGHT + 39);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setTitle("Game Of Life");

        gameStatus = GameStatusEnum.WAITING;

        canvasVirtualPosX = 0;
        canvasVirtualPosY = 0;
        canvasPanel = new CanvasPanel(480, 480, resolution, canvasVirtualPosX, canvasVirtualPosY);
        canvasPanel.addMouseListener(new MouseListener() {

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mouseClicked(MouseEvent e) {
                /*WAITING állapotban sejtek felélesztését és elpusztítását végzi ez a fv
                ezeket a változásokat a GameOfLife osztály megkapja, majd a canvasPanel élő sejteket tartalmazó listáját és frissítjük*/
                if (gameStatus == GameStatusEnum.WAITING) {
                    int resolution = canvasPanel.getResolution();
                    gol.addCell(((e.getX() * resolution) / canvasPanel.getWidth()) - canvasPanel.getCenter() + 2 + canvasVirtualPosX,
                            ((e.getY() * resolution) / canvasPanel.getHeight()) - canvasPanel.getCenter() + 2 + canvasVirtualPosY);
                    canvasPanel.setAliveCells(gol.getAliveCellsFromTheVisibleParts(canvasVirtualPosX, canvasVirtualPosY, canvasPanel.getResolution()));
                }
            }
        }
        );

        startButton = new JButton("Start");
        startButton.setBounds(500, 25, 105, 30);
        startButton.setToolTipText("Start the simulation");
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startButtonPressed();
            }
        });

        stopButton = new JButton("Stop");
        stopButton.setBounds(500, 60, 105, 30);
        stopButton.setToolTipText("Stop the simulation");
        stopButton.setEnabled(false);
        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stopButtonPressed();
            }
        });

        restartButton = new JButton("Restart");
        restartButton.setBounds(500, 95, 105, 30);
        restartButton.setToolTipText("Restart the simulation");
        restartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                restartButtonPressed();
            }
        });
        /*az iránygombok a megjelenítendő karaktereket html kódként kapják, hogy a kívánt Unicode karaktert lehessen megjeleníteni*/
        goLeftButton = new JButton("<html>&#11207;</html>");
        goLeftButton.setBounds(0, 0, 40, 40);
        goLeftButton.setToolTipText("Go left");
        goLeftButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                goLeftButtonPressed();
            }
        });

        goRightButton = new JButton("<html>&#11208;</html>");
        goRightButton.setBounds(0, 0, 40, 40);
        goRightButton.setToolTipText("Go right");
        goRightButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                goRightButtonPressed();
            }
        });

        goUpButton = new JButton("<html>&#11205;</html>");
        goUpButton.setBounds(0, 0, 40, 40);
        goUpButton.setToolTipText("Go up");
        goUpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                goUpButtonPressed();
            }
        });

        goDownButton = new JButton("<html>&#11206;</html>");
        goDownButton.setBounds(0, 0, 40, 40);
        goDownButton.setToolTipText("Go down");
        goDownButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                goDownButtonPressed();
            }
        });

        arrowButtonContainer = new JPanel();
        arrowButtonContainer.setBounds(500, 200, 105, 100);

        arrowButtonContainer.add(goLeftButton);
        arrowButtonContainer.add(goRightButton);
        arrowButtonContainer.add(goUpButton);
        arrowButtonContainer.add(goDownButton);

        zoomInButton = new JButton("+");
        zoomInButton.setBounds(565, 380, 40, 40);
        zoomInButton.setToolTipText("Zoom in");
        zoomInButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                zoomInButtonPressed();
            }
        });

        zoomOutButton = new JButton("-");
        zoomOutButton.setBounds(565, 425, 40, 40);
        zoomOutButton.setToolTipText("Zoom out");
        zoomOutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                zoomOutButtonPressed();
            }
        });

        speedSlider = new JSlider();
        speedSlider.setOrientation(JSlider.VERTICAL);
        speedSlider.setBounds(510, 380, 30, 85);
        speedSlider.setToolTipText("Speed of simulation");
        speedSlider.setMinimum(2);
        speedSlider.setMaximum(100);
        speedSlider.setValue(2);
        speedSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent arg0) {
                speedSliderValueChanged();
            }
        });

        this.add(canvasPanel);
        this.add(startButton);
        this.add(stopButton);
        this.add(restartButton);
        this.add(arrowButtonContainer);
        this.add(zoomInButton);
        this.add(zoomOutButton);
        this.add(speedSlider);

        gol = new GameOfLife(resolution, resolution);
        canvasPanel.setAliveCells(gol.getAliveCellsFromTheVisibleParts(canvasVirtualPosX, canvasVirtualPosY, resolution));

        t = new Timer(1000 / fps, this);
        t.start();

        this.setVisible(true);
    }

    private void goLeftButtonPressed() {
        canvasVirtualPosX--;
        canvasPanel.setVirtualPosX(canvasVirtualPosX);
        canvasPanel.setAliveCells(gol.getAliveCellsFromTheVisibleParts(canvasVirtualPosX, canvasVirtualPosY, canvasPanel.getResolution()));
    }

    private void goRightButtonPressed() {
        canvasVirtualPosX++;
        canvasPanel.setVirtualPosX(canvasVirtualPosX);
        canvasPanel.setAliveCells(gol.getAliveCellsFromTheVisibleParts(canvasVirtualPosX, canvasVirtualPosY, canvasPanel.getResolution()));
    }

    private void goUpButtonPressed() {
        canvasVirtualPosY--;
        canvasPanel.setVirtualPosY(canvasVirtualPosY);
        canvasPanel.setAliveCells(gol.getAliveCellsFromTheVisibleParts(canvasVirtualPosX, canvasVirtualPosY, canvasPanel.getResolution()));
    }

    private void goDownButtonPressed() {
        canvasVirtualPosY++;
        canvasPanel.setVirtualPosY(canvasVirtualPosY);
        canvasPanel.setAliveCells(gol.getAliveCellsFromTheVisibleParts(canvasVirtualPosX, canvasVirtualPosY, canvasPanel.getResolution()));
    }

    private void speedSliderValueChanged() {
        fps = speedSlider.getValue();
        t.setDelay(1000 / fps);
    }

    private void zoomOutButtonPressed() {
        /*amint elértük a kizoomolás határát, a zoomOut gomb-ot nem lehet megnyomni*/
        if (!canvasPanel.zoomOut(2)) {
            zoomOutButton.setEnabled(false);
        } else if (!zoomInButton.isEnabled()) {
            zoomInButton.setEnabled(true);
        }
    }

    private void zoomInButtonPressed() {
        /*amint elértük a zoomolás határát, a zoomIn gomb-ot nem lehet megnyomni*/
        if (!canvasPanel.zoomIn(2)) {
            zoomInButton.setEnabled(false);
        } else if (!zoomOutButton.isEnabled()) {
            zoomOutButton.setEnabled(true);
        }
    }

    private void startButtonPressed() {
        /*a gomb, a lenyomást követően elérhetettlenné válik egy stop vagy restart gomb megnyomása erejéig*/
        gameStatus = GameStatusEnum.RUNNING;
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
    }

    private void stopButtonPressed() {
        /*a gomb, a lenyomást követően elérhetettlenné válik egy start gomb megnyomása erejéig*/
        gameStatus = GameStatusEnum.WAITING;
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
    }

    private void restartButtonPressed() {
        /*a gomb lenyomása után minden változó és objektum visszaáll az alapértelmezett állapotába*/
        stopButtonPressed();
        canvasVirtualPosX = 0;
        canvasVirtualPosY = 0;
        zoomInButton.setEnabled(true);
        zoomOutButton.setEnabled(true);
        speedSlider.setValue(2);
        canvasPanel.setResolution(resolution);
        canvasPanel.setVirtualPos(canvasVirtualPosX, canvasVirtualPosY);
        gol = new GameOfLife(canvasPanel.getResolution(), canvasPanel.getResolution());
        canvasPanel.setAliveCells(gol.getAliveCellsFromTheVisibleParts(canvasVirtualPosX, canvasVirtualPosY, canvasPanel.getResolution()));
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        /*ezt a függvényt a Timer osztály a delay leteltével újra és újra meghívja
        a start gomb lenyomása után indulhat csak el a következő generáció kiszámítása*/
        if (gameStatus == GameStatusEnum.RUNNING) {
            gol.startSimulation(1);
            canvasPanel.setAliveCells(gol.getAliveCellsFromTheVisibleParts(canvasVirtualPosX, canvasVirtualPosY, canvasPanel.getResolution()));
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrame();//.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
