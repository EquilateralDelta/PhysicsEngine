package MyPhysicsEngine;

public class BoundingBox {
    Vector2 min,
            max;

    Body body;

    public BoundingBox(Body body)
    {
        min = new Vector2();
        max = new Vector2();

        this.body = body;
    }

    public void Update(Vector2[] verticies)
    {
        min = new Vector2(verticies[0]);
        max = new Vector2(verticies[0]);
        for (int i = 1; i < verticies.length; i++)
        {
            Vector2 currentDot = verticies[i];
            if (currentDot.x > max.x)
                max.x = currentDot.x;
            if (currentDot.x < min.x)
                min.x = currentDot.x;
            if (currentDot.y > max.y)
                max.y = currentDot.y;
            if (currentDot.y < min.y)
                min.y = currentDot.y;
        }
    }

    public boolean isColliding(BoundingBox other)
    {
        if (this.max.x < other.min.x)
            return false;

        if (this.max.y < other.min.y)
            return false;

        if (this.min.x > other.max.x)
            return false;

        if (this.min.y > other.max.y)
            return false;

        return true;
    }
}
