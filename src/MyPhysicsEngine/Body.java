package MyPhysicsEngine;

import java.awt.*;

public class Body {
    Vector2 position;
    Vector2 velocity;
    Vector2 pseudoSpeed;

    float angle;
    float getAngleInDegrees() { return angle * 180 / (float)Math.PI; }
    float angleSpeed;

    float IMass;
    float IMoment;

    float elasticitate;
    float friction;
    float airResistance;
    float airAngleResistance;

    Vector2[] vertices;
    Vector2[] absoluteVertices;

    public BoundingBox boundingBox;

    Color color;

    public Body(Vector2[] vertices, float IMass) {
        ManageVertices(vertices);

        absoluteVertices = new Vector2[vertices.length];
        this.IMass = IMass;
        IMoment = GetIMoment();
        position = new Vector2();
        velocity = new Vector2();
        angle = 0;
        angleSpeed = 0;

        elasticitate = .2f;
        friction = .05f;
        airResistance = .01f;
        airAngleResistance = .01f;

        boundingBox = new BoundingBox(this);

        color = new Color(1, 1, 1);
    }

    public void Update(float dt)
    {
        if (IMass != 0)
            velocity.Add(Settings.gravity.ReturnMultiplied(dt));

        position.Add(velocity.ReturnMultiplied(dt));
        angle += angleSpeed * dt;

        velocity = velocity.ReturnMultiplied(1 - airResistance * dt);
        angleSpeed -= airAngleResistance * angleSpeed * dt;

        float angleCos = (float)Math.cos(angle),
                angleSin = (float)Math.sin(angle);

        Vector2 turningMatrixX = new Vector2(angleCos, -angleSin),
                turningMatrixY = new Vector2(angleSin, angleCos);

        for (int i = 0; i < vertices.length; i++)
            absoluteVertices[i] = new Vector2(vertices[i].x * turningMatrixX.x + vertices[i].y * turningMatrixX.y + position.x,
                    vertices[i].x * turningMatrixY.x + vertices[i].y * turningMatrixY.y + position.y);

        boundingBox.Update(absoluteVertices);
    }

    void ManageVertices(Vector2[] vertices_p)
    {
        vertices = vertices_p;

        Vector2 temp = vertices[1].ReturnSub(vertices[0]), temp2 = vertices[2].ReturnSub(vertices[1]);
        if (temp.VectorMultiply(temp2) > 0)
        {
            //reversing vertexs;
            Vector2[] tempArray = new Vector2[vertices.length];
            for (int i = 0; i < vertices.length; i++)
                tempArray[i] = vertices_p[vertices.length - 1 - i];
            this.vertices = tempArray;
        }
        //checking for bulge
        try{
            for (int i = 0; i < vertices.length; i++)
            {
                temp = vertices[(i + 1) % vertices.length].ReturnSub(vertices[i]);
                temp2 = vertices[(i + 2) % vertices.length].ReturnSub(vertices[(i + 1) % vertices.length]);
                if (temp.VectorMultiply(temp2) > 0)
                {
                    throw new Exception("Figure is not bulge!");
                }
            }
        } catch(Exception e) { System.out.println(e.getMessage()); }
    }

    float GetIMoment()
    {
        if(IMass == 0)
            return 0;

        float upSum = 0, downSum = 0;
        float result = 1  / (IMass * 6);

        for (int i = 0; i < vertices.length; i++)
        {
            Vector2 vertex1 = vertices[i],
                    vertex2 = vertices[(i + 1) % vertices.length];

            float vM = Math.abs(vertex2.VectorMultiply(vertex1));
            float m = vertex1.Dot(vertex1) + vertex1.Dot(vertex2) + vertex2.Dot(vertex2);

            upSum += m * vM;
            downSum += vM;
        }

        result *= upSum / downSum;
        return 1 / result;
    }

    public void ManagePseudoSpeed(Vector2 penVector)
    {
        if (pseudoSpeed != null)
        {
            if (pseudoSpeed.x * penVector.x >= 0)
                pseudoSpeed.x = Math.abs(pseudoSpeed.x) > Math.abs(penVector.x) ? pseudoSpeed.x : penVector.x;
            else
                pseudoSpeed.x += penVector.x;

            if (pseudoSpeed.y * penVector.y >= 0)
                pseudoSpeed.y = Math.abs(pseudoSpeed.y) > Math.abs(penVector.y) ? pseudoSpeed.y : penVector.y;
            else
                pseudoSpeed.y += penVector.y;
        }
        else
            pseudoSpeed = penVector;
    }

    public void UpdatePseudoSpeed()
    {
        if (pseudoSpeed != null)
            position.Add(pseudoSpeed);
        pseudoSpeed = null;
    }
}
