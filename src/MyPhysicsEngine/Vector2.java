package MyPhysicsEngine;

public class Vector2 {
    public float x;
    public float y;

    public Vector2(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    public Vector2()
    {
        this.x = 0;
        this.y = 0;
    }

    public Vector2(Vector2 base)
    {
        x = base.x;
        y = base.y;
    }

    public Vector2 ReturnAdded(Vector2 var)
    {
        return new Vector2(this.x + var.x, this.y + var.y);
    }

    public void Add(Vector2 var)
    {
        this.x += var.x;
        this.y += var.y;
    }

    public Vector2 ReturnSub(Vector2 var)
    {
        return new Vector2(this.x - var.x, this.y - var.y);
    }

    public void Sub(Vector2 var)
    {
        this.x -= var.x;
        this.y -= var.y;
    }

    public Vector2 ReturnMultiplied(float var) { return new Vector2(this.x * var, this.y * var); }

    public void Mult(float var)
    {
        this.x *= var;
        this.y *= var;
    }

    public float Distance() { return (float)Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2)); }

    public float VectorMultiply(Vector2 other) { return this.x * other.y - this.y * other.x; }

    public Vector2 VectorMultiplyScalarRight(float scalar) { return new Vector2(scalar * this.y, -scalar * this.x); }

    public Vector2 VectorMultiplyScalarLeft(float scalar) { return new Vector2(-scalar * this.y, scalar * this.x); }

    public float Dot(Vector2 other) { return this.x * other.x + this.y * other.y; }

    public Vector2 Normalized()
    {
        if ((this.x == 0) && (this.y == 0))
            return this;

        float dist = Distance();
        return new Vector2(this.x / dist, this.y / dist);
    }

    public Vector2 Normal()
    {
        Vector2 temp = this.Normalized();
        return new Vector2(-temp.y, temp.x);
    }

    public Vector2 Negative() { return new Vector2(-this.x, -this.y); }
}
