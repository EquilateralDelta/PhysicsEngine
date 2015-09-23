package MyPhysicsEngine;

import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class CollisionDetection {
    public static Collision Collide(Body body1, Body body2)
    {
        float minPenetrationDistance = Float.MAX_VALUE;
        Collision associatedCollision = null;

        boolean isLastCollisionOnThisBody = false;

        for (int i = 0; i < body1.absoluteVertices.length; i++)
        {
            Vector2 edgePoint1 = body1.absoluteVertices[i],
                    edgePoint2 = body1.absoluteVertices[(i + 1) % body1.absoluteVertices.length];

            Vector2 normal = edgePoint2.ReturnSub(edgePoint1).Normal();

            List<Integer> supportPoints = GetSupportVectors(normal.Negative(), body2.absoluteVertices);

            for (int j = 0; j < supportPoints.size(); j++)
            {
                Vector2 newP1 = body2.absoluteVertices[supportPoints.get(j)].ReturnSub(edgePoint1),
                        newP2 = body2.absoluteVertices[supportPoints.get(j)].ReturnSub(edgePoint2);

                if (new Vector2().ReturnSub(newP1).VectorMultiply(newP2.ReturnSub(newP1)) < 0)
                    return null;

                float dist = DistanceFromPointToLine(new Vector2(), newP1, newP2);
                if ((isLastCollisionOnThisBody) &&
                        (dist > minPenetrationDistance - Settings.differenceForEdgeCollision) &&
                        (dist < minPenetrationDistance + Settings.differenceForEdgeCollision))
                    associatedCollision.AddPenetratingPointToCollision(supportPoints.get(j));
                if (dist < minPenetrationDistance) {
                    minPenetrationDistance = dist;
                    associatedCollision = new Collision(normal, dist, body2, supportPoints.get(j), body1, i);
                    isLastCollisionOnThisBody = true;
                }
            }
        }

        isLastCollisionOnThisBody = false;

        for (int i = 0; i < body2.absoluteVertices.length; i++) {
            Vector2 edgePoint1 = body2.absoluteVertices[i],
                    edgePoint2 = body2.absoluteVertices[(i + 1) % body2.absoluteVertices.length];

            Vector2 normal = edgePoint2.ReturnSub(edgePoint1).Normal();

            List<Integer> supportPoints = GetSupportVectors(normal.Negative(), body1.absoluteVertices);

            for (int j = 0; j < supportPoints.size(); j++) {
                Vector2 newP1 = edgePoint1.ReturnSub(body1.absoluteVertices[supportPoints.get(j)]),
                        newP2 = edgePoint2.ReturnSub(body1.absoluteVertices[supportPoints.get(j)]);

                if (new Vector2().ReturnSub(newP1).VectorMultiply(newP2.ReturnSub(newP1)) < 0)
                    return null;

                float dist = DistanceFromPointToLine(new Vector2(), newP1, newP2);
                if ((isLastCollisionOnThisBody) &&
                        (dist > minPenetrationDistance - Settings.differenceForEdgeCollision) &&
                        (dist < minPenetrationDistance + Settings.differenceForEdgeCollision))
                    associatedCollision.AddPenetratingPointToCollision(supportPoints.get(j));
                if (dist < minPenetrationDistance) {
                    minPenetrationDistance = dist;
                    associatedCollision = new Collision(normal, dist, body1, supportPoints.get(j), body2, i);
                    isLastCollisionOnThisBody = true;
                }
            }
        }

        return associatedCollision;
    }

    public static void DrawMinkovsky(Body body1, Body body2, float multiply)
    {
        List<Vector2> points = new Vector<Vector2>();

        for (int i = 0; i < body1.absoluteVertices.length; i++)
        {
            Vector2 edgePoint1 = body1.absoluteVertices[i],
                    edgePoint2 = body1.absoluteVertices[(i + 1) % body1.absoluteVertices.length];

            Vector2 normal = edgePoint2.ReturnSub(edgePoint1).Normal();

            List<Integer> supportPoints = GetSupportVectors(normal.Negative(), body2.absoluteVertices);

            for (int j = 0; j < supportPoints.size(); j++)
            {
                Vector2 newP1 = body2.absoluteVertices[supportPoints.get(j)].ReturnSub(edgePoint1),
                        newP2 = body2.absoluteVertices[supportPoints.get(j)].ReturnSub(edgePoint2);
                points.add(newP1); points.add(newP2);
            }
        }

        for (int i = 0; i < body2.absoluteVertices.length; i++) {
            Vector2 edgePoint1 = body2.absoluteVertices[i],
                    edgePoint2 = body2.absoluteVertices[(i + 1) % body2.absoluteVertices.length];

            Vector2 normal = edgePoint2.ReturnSub(edgePoint1).Normal();

            List<Integer> supportPoints = GetSupportVectors(normal.Negative(), body1.absoluteVertices);

            for (int j = 0; j < supportPoints.size(); j++) {
                Vector2 newP1 = edgePoint1.ReturnSub(body1.absoluteVertices[supportPoints.get(j)]),
                        newP2 = edgePoint2.ReturnSub(body1.absoluteVertices[supportPoints.get(j)]);
                points.add(newP1); points.add(newP2);
            }
        }

        GL11.glTranslatef(400, 300, 0);
        for (int i = 0; i < points.size() - 1; i+= 2)
            DrawingUtil.DrawArrow(points.get(i).ReturnMultiplied(multiply), points.get(i + 1).ReturnMultiplied(multiply));
        GL11.glTranslatef(-400, -300, 0);
    }

    static List<Integer> GetSupportVectors(Vector2 direction, Vector2[] verticies)
    {
        float maxDistance = -Float.MAX_VALUE;
        List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < verticies.length; i++)
        {
            float distance = verticies[i].Dot(direction);
            if (distance > maxDistance)
            {
                maxDistance = distance;
                result = new Vector<Integer>();
            }
            if (distance == maxDistance)
                result.add(i);
        }

        return result;
    }

    static float DistanceFromPointToLine(Vector2 point, Vector2 line1, Vector2 line2)
    {
        float area = point.ReturnSub(line1).VectorMultiply(line2.ReturnSub(line1));
        return Math.abs(area / line2.ReturnSub(line1).Distance());
    }
}
