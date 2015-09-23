package MyPhysicsEngine;

public class Settings {
    public static boolean drawContacts = false;
    public static boolean drawBoundingBox = false;
    public static boolean drawBodyCenter = true;
    public static boolean drawBodyStroke = true;

    public static float penetrationPushingByTimestep = .4f;
    public static float penetrationLowerBound = .1f;

    public static Vector2 gravity = new Vector2(0, -9.8f);

    public static float differenceForEdgeCollision = 1e-4f;
}
