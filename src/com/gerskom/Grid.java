package com.gerskom;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class Grid {
    public int width;
    public int height;
    public int nMax;
    public int[][] table;
    private final int[][] tmpTable;
    public final int[][] imageTable;
    public int[][] inputImageTable;

    public int i = 0;

    public static int tree = -1;
    public static int fire = -2;
    public static int burnt = -3;

    private final float fireBrushSpeed = 6.5f;     //0.9f
    private final float treesBrushSpeed = 14f;     //0.75f

    private final double fireP = 37.5;
    private final double randomFireP = 0.0000075;
    private final double resurrectionP = 0.1;
    private final double burnP = 5.5f;

    public Grid(int width, int height, int nMax) {
        this.width = width;
        this.height = height;
        this.nMax = nMax;
        this.table = new int[width][height];
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                this.table[x][y] = burnt;
        this.tmpTable = new int[width][height];
        this.imageTable = new int[width][height];
        this.inputImageTable = new int[width][height];
        dataCopier();
    }

    public Grid(ImageData id, int nMax) throws IOException {
        this.width = id.width;
        this.height = id.height;
        this.nMax = nMax;
        this.table = new int[width][height];
        this.tmpTable = new int[width][height];
        this.imageTable = new int[width][height];
        this.inputImageTable = new int[width][height];
        backgroundImage();
        importImageData(id);
        dataCopier();
    }

    public void startSimulation() {
        for(int x = 1; x < width - 1; x++) {
            for(int y = 1; y < height - 1; y++) {
                if(imageTable[x][y] != 0) {
                    Random random = new Random();
                    double r = random.nextDouble() * 100;

                    if (tmpTable[x][y] == burnt && resurrectionP >= r)
                        addTree(x, y);
                    else if (tmpTable[x][y] == tree && neighbourFireScan(x, y) >= r)
                        addFire(x, y);
                    else if (tmpTable[x][y] == fire && burnP >= r)
                        table[x][y] = burnt;
                }
            }
        }
        i++;
        dataCopier();
    }

    double neighbourFireScan(int w, int h) {
        int counter = 0;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (!(i == 0 && j == 0)) {
                    if (tmpTable[w + i][h + j] == fire) {
                        counter++;
                    }
                }
            }
        }
        if (counter == 0) return randomFireP;
        else return counter * fireP;
    }

    public void dataCopier() {
        for(int x = 0; x < width; x++)
            if (height >= 0) System.arraycopy(table[x], 0, this.tmpTable[x], 0, height);
    }

    public void addTree(int xCor, int yCor) {
        if(imageTable[xCor][yCor] != 0)
            this.table[xCor][yCor] = tree;
    }

    public void addFire(int xCor, int yCor) {
        if(imageTable[xCor][yCor] != 0)
            this.table[xCor][yCor] = fire;
    }

    public void addBrushOfTrees(int xCor, int yCor, float size) {
        for (double r = 4; r <= size / 2; r += 1.5) {
            for (double a = 0; a < 2 * Math.PI; a += 0.05) {
                Random random = new Random();
                float rand = 100 * random.nextFloat();

                int x = (int)(r * Math.cos(a));
                int y = (int)(r * Math.sin(a));

                if (treesBrushSpeed >= rand)
                    if ((x + xCor) > 0 && (x + xCor) < width && (y + yCor) > 0 && (y + yCor) < height)
                        addTree(x + xCor, y + yCor);
            }
        }
    }

    public void addBrushOfFire(int xCor, int yCor, float size) {
        for (double r = 3; r <= size / 2; r += 1.5) {
            for (double a = 0; a < 2 * Math.PI; a += 0.05) {
                Random random = new Random();
                float rand = 100 * random.nextFloat();

                int x = (int)(r * Math.cos(a));
                int y = (int)(r * Math.sin(a));

                if (fireBrushSpeed >= rand)
                    if ((x + xCor) > 0 && (x + xCor) < width && (y + yCor) > 0 && (y + yCor) < height)
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

    private void importImageData(ImageData id) {
        for (int x = 0; x < width; x++)
            if (height >= 0) System.arraycopy(id.dataTable[x], 0, this.imageTable[x], 0, height);
    }

    private void backgroundImage() throws IOException {
        ImageData bg = new ImageData("input/input_map.bmp");
        this.inputImageTable = bg.dataTable;
    }


}