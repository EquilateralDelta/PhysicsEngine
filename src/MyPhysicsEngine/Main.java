package MyPhysicsEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.*;
import org.lwjgl.opengl.DisplayMode;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import static org.lwjgl.opengl.EXTFramebufferObject.*;

public class Main{
    public Semaphore changeListSemaphore;
    public List<Change> changeList;
    static public int DisplayWidth = 800,
         DisplayHeight = 600;

    private ClicksBodyCreation clicksBodyCreation;

    private Physics physics;

    public Main()
    {
        InitGL();
        clicksBodyCreation = new ClicksBodyCreation(this);
        changeListSemaphore = new Semaphore(1);
        changeList = new ArrayList<Change>();

        physics = new Physics(this);
        Thread physicsThread = new Thread(physics);
        physicsThread.start();

        while (!Display.isCloseRequested()) {
            HandleInput();
            Render();

            Display.update();
            Display.sync(60);
        }
        physics.abort = true;
    }

    void HandleInput()
    {
        List<Change> tempChangeList = new ArrayList<Change>();

        while (Keyboard.next()) {
            if ((Keyboard.getEventKey() == Keyboard.KEY_SPACE) && (!Keyboard.getEventKeyState()))
                tempChangeList.add(new PauseChange());
        }

        while (Mouse.next()){
            if ((Mouse.getEventButton() == 0) && (!Mouse.getEventButtonState()))
                clicksBodyCreation.MouseClick();

            if ((Mouse.getEventButton() == 1) && (!Mouse.getEventButtonState()))
                tempChangeList.add(clicksBodyCreation.EndDrawing());
        }

        try {
            changeListSemaphore.acquire();
            changeList.addAll(tempChangeList);
            changeListSemaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void Render()
    {
        GL11.glClearColor (0, 0, 0, 1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        //getting data from physics thread
        Vector2[][] points = null;
        Color[] colors = null;
        Vector2[] centers = null;
        Vector2[][] boundingBoxes = null;
        Vector2[] velocities = null;
        float[] angleSpeeds = null;

        try {
            physics.drawingDataSemaphore.acquire();

            points = physics.points;
            colors = physics.colors;
            centers = physics.centers;
            boundingBoxes = physics.boundingBoxes;
            velocities = physics.velocities;
            angleSpeeds = physics.angleSpeeds;

            physics.drawingDataSemaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        //drawing bodies
        for (int i = 0; i < points.length; i++) {
            float[] col = colors[i].getRGBColorComponents(null);
            GL11.glColor3f(col[0], col[1], col[2]);

            //main drawing
            GL11.glBegin(GL11.GL_POLYGON);
            for (int j = 0; j < points[i].length; j++) {
                GL11.glVertex2f(points[i][j].x, points[i][j].y);
            }
            GL11.glEnd();

            //drawing white stroke above body
            if (Settings.drawBodyStroke) {
                GL11.glColor3f(1, 1, 1);
                GL11.glBegin(GL11.GL_LINE_LOOP);
                for (int j = 0; j < points[i].length; j++) {
                    GL11.glVertex2f(points[i][j].x, points[i][j].y);
                }
                GL11.glEnd();
            }

            //drawing body center
            if (Settings.drawBodyCenter){
                DrawingUtil.DrawCross(centers[i]);
            }

            //drawing body bounding box
            if (Settings.drawBoundingBox){
                GL11.glBegin(GL11.GL_LINE_LOOP);
                GL11.glColor3f(.4f, .4f, .4f);
                GL11.glVertex2f(boundingBoxes[i][0].x, boundingBoxes[i][0].y);
                GL11.glVertex2f(boundingBoxes[i][1].x, boundingBoxes[i][0].y);
                GL11.glVertex2f(boundingBoxes[i][1].x, boundingBoxes[i][1].y);
                GL11.glVertex2f(boundingBoxes[i][0].x, boundingBoxes[i][1].y);
                GL11.glEnd();
            }

            //drawing body velocity
            if (velocities[i].Distance() > 1)
                DrawingUtil.DrawArrow(centers[i], centers[i].ReturnAdded(velocities[i].ReturnMultiplied(2)));

            //drawing body angle velocity
            GL11.glBegin(GL11.GL_LINE_STRIP);
            for (int j = 0; j < (int)(Math.abs(angleSpeeds[i]) * 200); j++)
            {
                float degInRad;

                if (angleSpeeds[i] >= 0)
                    degInRad = (360 - j) * (3.14159f/180);
                else
                    degInRad = j * (3.14159f/180);

                GL11.glVertex2f(centers[i].x + (float)Math.cos(degInRad) * 12, centers[i].y + (float)Math.sin(degInRad) * 12);
            }
            GL11.glEnd();

            if (angleSpeeds[i] > .05f)
                DrawingUtil.DrawArrow(centers[i].ReturnAdded(new Vector2(12, 0)), centers[i].ReturnAdded(new Vector2(12, 1)));
            if (angleSpeeds[i] < -.05f)
                DrawingUtil.DrawArrow(centers[i].ReturnAdded(new Vector2(12, 0)), centers[i].ReturnAdded(new Vector2(12, -1)));
        }

        clicksBodyCreation.Draw();
    }

    void InitGL()
    {
        //initializing window
        try {
            Display.setDisplayMode(new DisplayMode(DisplayWidth, DisplayHeight));
            Display.create(new PixelFormat(0, 8, 0, 4));
            Display.setVSyncEnabled(true);
        } catch (LWJGLException e) {
            e.printStackTrace();
            System.exit(0);
        }

        //initializing matrises
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, 800, 0, 600, 1, -1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        //initializing antialising
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
    }

    public static void main(String[] args){
            new Main();
        }
}
