package com.gerskom;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Grid {
    public int width;
    public int height;
    public int nMax;
    public int[][] table;
    private final int[][] tmpTable;
    public final Color bgColor = new Color(50, 50, 50);
    public final Color fireColor = new Color(250, 110, 0);
    public final Color treeColor = new Color(110, 125, 40);

    private int i = 0;

    private final float fireBrushSpeed = .4f;
    private final float treesBrushSpeed = .4f;

    private final double fireP = 35;
    private final double randomFireP = 0.000005;
    private final double resurrectionP = 0.0005;
    private final double burnP = 1.5;

    public Grid(int width, int height, int nMax) {
        this.width = width;
        this.height = height;
        this.nMax = nMax;
        this.table = new int[width][height];

        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                this.table[x][y] = 0;
        this.tmpTable = new int[width][height];
        dataCopier();
    }

    public void startSimulation() {
        for(int x = 1; x < width - 1; x++) {
            for(int y = 1; y < height - 1; y++) {
                Random random = new Random();
                double r = random.nextDouble() * 100;

                if(tmpTable[x][y] == 0 && resurrectionP >= r)
                    table[x][y] = 1;

                else if(tmpTable[x][y] == 1 && neighbourFireScan(x, y) >= r)
                    table[x][y] = 2;

                else if(tmpTable[x][y] == 2 && burnP >= r)
                    table[x][y] = 0;
            }
        }
        dataCopier();
    }

    double neighbourFireScan(int w, int h) {
        int counter = 0;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (!(i == 0 && j == 0)) {
                    if (tmpTable[w + i][h + j] == 2) {
                        counter++;
                    }
                }
            }
        }
        if (counter == 0) return randomFireP;
        else return counter * fireP;
    }

    public void dataCopier() {
        i++;
        for(int x = 0; x < width; x++)
            for(int y = 0; y < height; y++)
                this.tmpTable[x][y] = table[x][y];
    }

    public void addTree(int xCor, int yCor) {
        this.table[xCor][yCor] = 1;
    }

    public void addFire(int xCor, int yCor) {
        this.table[xCor][yCor] = 2;
    }

    public void addBrushOfTrees(int xCor, int yCor, float size) {
        for (double r = 0; r <= size / 2; r += 0.1) {
            for (double a = 0; a < 2 * Math.PI; a += 0.1) {
                Random random = new Random();
                float rand = 100 * random.nextFloat();

                int x = (int)(r * Math.cos(a));
                int y = (int)(r * Math.sin(a));

                if (treesBrushSpeed >= rand)
                    if (x + xCor > 0 && x + xCor < width && y + yCor > 0 && y + yCor < height &&
                        y + yCor > 0 && y + yCor < width && x + xCor > 0 && x + xCor < height) {
                        addTree(x + xCor, y + yCor);
                        //System.out.println(x + xCor +", "+ y + yCor);
                    }
            }
        }
    }

    public void addBrushOfFire(int xCor, int yCor, float size) {
        for (double r = 0; r <= size / 2; r += 0.1) {
            for (double a = 0; a < 2 * Math.PI; a += 0.1) {
                Random random = new Random();
                float rand = 100 * random.nextFloat();

                int x = (int)(r * Math.cos(a));
                int y = (int)(r * Math.sin(a));

                if (fireBrushSpeed >= rand)
                    if (x + xCor > 0 && x + xCor < width && y + yCor > 0 && y + yCor < height &&
                        y + yCor > 0 && y + yCor < width && x + xCor > 0 && x + xCor < height)
                        addFire(x + xCor, y + yCor);
            }
        }
    }

    public void addInitialRandomTrees() {
        for(int c = 0; c < nMax; c++) {
            Random ran = new Random();
            int x = ran.nextInt(width - 2) + 1;
            int y = ran.nextInt(height - 2) + 1;
            addTree(x, y);
        }
    }

    public void exportImage(String fileName) throws IOException {
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2D = bufferedImage.createGraphics();

        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                if (table[x][y] == 0)
                    g2D.setColor(bgColor);
                else if (table[x][y] == 1)
                    g2D.setColor(treeColor);
                else if (table[x][y] == 2)
                    g2D.setColor(fireColor);

                g2D.fillRect(x, y, 1, 1);
            }
        }
        g2D.dispose();

        String formatName = "png";
        File file = new File("output/" + fileName + "_" + i + "." + formatName);
        ImageIO.write(bufferedImage, formatName, file);
    }
}