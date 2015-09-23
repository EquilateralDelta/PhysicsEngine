package MyPhysicsEngine;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class World{
    private Physics parent;
    private List<Body> bodies;
    private List<Collision> collisions;

    public World(Physics p)
    {
        parent = p;
        bodies = new ArrayList<Body>();
        collisions = new ArrayList<Collision>();
    }

    public void Init()
    {
        Vector2[] newBodyShape;
        Body b;

        newBodyShape = new Vector2[]{
                new Vector2(20, 30),
                new Vector2(30, 20),
                new Vector2(30, -20),
                new Vector2(20, -30),
                new Vector2(-20, -30),
                new Vector2(-30, -20),
                new Vector2(-30, 20),
                new Vector2(-20, 30)
        };
        for (int i = 0; i < 10; i++)
        {
            b = new Body(newBodyShape, .002f);
            bodies.add(b);
            b.position = new Vector2(400 + i * 15 , 100 + i * 70);
            b.color = new Color(0, 15, 255);
            b.velocity = new Vector2(0, 0);
        }

        newBodyShape = new Vector2[]{
                new Vector2(-Main.DisplayWidth, 100),
                new Vector2(Main.DisplayWidth, 100),
                new Vector2(Main.DisplayWidth, -100),
                new Vector2(-Main.DisplayWidth, -100),
        };
        b = new Body(newBodyShape, 0);
        bodies.add(b);
        b.position = new Vector2(Main.DisplayWidth / 2f, -90);
        b.color = new Color(255, 233, 19);

        newBodyShape = new Vector2[]{
                new Vector2(-100, Main.DisplayHeight),
                new Vector2(100, Main.DisplayHeight),
                new Vector2(100, -Main.DisplayHeight),
                new Vector2(-100, -Main.DisplayHeight),
        };
        b = new Body(newBodyShape, 0);
        bodies.add(b);
        b.position = new Vector2(-90, Main.DisplayHeight / 2f);
        b.color = new Color(255, 233, 19);


        newBodyShape = new Vector2[]{
                new Vector2(-100, Main.DisplayHeight),
                new Vector2(100, Main.DisplayHeight),
                new Vector2(100, -Main.DisplayHeight),
                new Vector2(-100, -Main.DisplayHeight),
        };
        b = new Body(newBodyShape, 0);
        bodies.add(b);
        b.position = new Vector2(Main.DisplayWidth + 90, Main.DisplayHeight / 2f);
        b.color = new Color(255, 233, 19);
    }

    public void CreateBodyByDots(Vector2[] dots)
    {
        if (dots.length < 3)
            return;

        float massSum = 0;
        Vector2 massCenter = new Vector2();

        for (int i = 2; i < dots.length; i++)
        {
            Vector2 firstEdge = dots[i - 1].ReturnSub(dots[i]);
            Vector2 secondEdge = dots[0].ReturnSub(dots[i]);
            float mass = Math.abs(firstEdge.VectorMultiply(secondEdge)) / 2;
            massSum += mass;

            Vector2 localMassCenter = (dots[i].ReturnAdded(dots[i - 1]).ReturnAdded(dots[0])).ReturnMultiplied(1f/3);

            massCenter.Add(localMassCenter.ReturnMultiplied(mass));
        }

        massCenter.Mult(1 / massSum);

        for (int i = 0; i < dots.length; i++)
            dots[i].Sub(massCenter);

        Body b;
        b = new Body(dots, 1 / massSum);
        b.position = massCenter;
        b.color = new Color(76, 255, 57);
        bodies.add(b);
    }

    public void Update(float dt, int iteration_count) {
        float timestep = dt / iteration_count;
        for (int h = 0; h < iteration_count; h++) {
            for (Body body : bodies)
                body.Update(timestep);

            try {
                parent.drawingDataSemaphore.acquire();
                parent.points = new Vector2[bodies.size()][];
                parent.colors = new Color[bodies.size()];
                parent.centers = new Vector2[bodies.size()];
                parent.boundingBoxes = new Vector2[bodies.size()][2];
                parent.velocities = new Vector2[bodies.size()];
                parent.angleSpeeds = new float[bodies.size()];

                for (int i = 0; i < bodies.size(); i++)
                {
                    Body body = bodies.get(i);
                    parent.points[i] = new Vector2[body.absoluteVertices.length];

                    for (int j = 0; j < body.absoluteVertices.length; j++)
                        parent.points[i][j] = body.absoluteVertices[j];

                    parent.colors[i] = bodies.get(i).color;
                    parent.centers[i] = bodies.get(i).position;
                    parent.boundingBoxes[i][0] = bodies.get(i).boundingBox.min;
                    parent.boundingBoxes[i][1] = bodies.get(i).boundingBox.max;
                    parent.velocities[i] = body.velocity;
                    parent.angleSpeeds[i] = body.angleSpeed;
                }

                parent.drawingDataSemaphore.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            collisions.clear();
            for (int i = 0; i < bodies.size() - 1; i++)
                for (int j = i + 1; j < bodies.size(); j++)
                    if ((bodies.get(i) != bodies.get(j)) && (bodies.get(i).boundingBox.isColliding(bodies.get(j).boundingBox))) {
                        Collision result = CollisionDetection.Collide(bodies.get(i), bodies.get(j));
                        if (result != null)
                            collisions.add(result);
                    }

            for (int i = 0; i < 20; i++)
                for (Collision collision : collisions) {
                    float mass_sum = collision.penetratedBody.IMass + collision.penetratingBody.IMass;
                    if (mass_sum != 0)
                        collision.PenetrationPush(mass_sum);
                }
            for (Body body : bodies)
                body.UpdatePseudoSpeed();

            for (Collision collision : collisions)
                collision.Solve();
        }
    }
}
