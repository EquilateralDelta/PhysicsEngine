package MyPhysicsEngine;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Physics implements Runnable {
    public Semaphore drawingDataSemaphore;
    public Vector2[][] points;
    public Color[] colors;
    public Vector2[] centers;
    public Vector2[][] boundingBoxes;
    public Vector2[] velocities;
    public float[] angleSpeeds;
    public boolean abort;

    private Main parent;
    private World w;
    private boolean isPaused = true;

    public Physics(Main p)
    {
        parent = p;
        drawingDataSemaphore = new Semaphore(1);
        points = new Vector2[0][0];
        colors = new Color[0];
        abort = false;

        w = new World(this);
        w.Init();
    }

    public void CreateBodyByDots(Vector2[] dots)
    {
        w.CreateBodyByDots(dots);
    }

    public void PauseChange()
    {
        isPaused = !isPaused;
    }

    public void run()
    {
        double timeBuffer = 0;

        w.Update(0, 1);

        while (!abort) {
            long startTime = System.nanoTime();

            if (timeBuffer > 5) {
                timeBuffer -= 5;
                timeBuffer = timeBuffer % 5;
                if (!isPaused)
                    w.Update(.02f, 40);
            }

            List<Change> changeList = null;
            try {
                parent.changeListSemaphore.acquire();
                changeList = parent.changeList;
                parent.changeList = new ArrayList<Change>();
                parent.changeListSemaphore.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (Change change : changeList)
                change.Apply(this);

            w.Update(0, 1);

            timeBuffer += (double)(System.nanoTime() - startTime) / 1000000;
        }
    }
}
