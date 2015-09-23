package MyPhysicsEngine;

public class AddBodyChange implements Change {
    private Vector2[] dots;

    public AddBodyChange(Vector2[] d)
    {
        dots = d;
    }

    public void Apply(Physics p) {
        p.CreateBodyByDots(dots);
    }
}
