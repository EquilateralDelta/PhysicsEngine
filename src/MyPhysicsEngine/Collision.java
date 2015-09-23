package MyPhysicsEngine;

import org.lwjgl.opengl.GL11;

public class Collision {
    Vector2 penetrationNormal;
    float penetrationDepth;
    Vector2 penetrationVector;

    Body penetratingBody;
    int vertexIndex;
    int supportVertexIndex;

    Body penetratedBody;
    int edgeIndex;

    public Collision(Vector2 pN, float pD, Body ingB, int vInd, Body edB, int eInd)
    {
        penetrationNormal = pN;
        penetrationDepth = pD;
        penetrationVector = penetrationNormal.ReturnMultiplied(penetrationDepth);

        penetratingBody = ingB;
        vertexIndex = vInd;
        supportVertexIndex = -1;

        penetratedBody = edB;
        edgeIndex = eInd;
    }

    public void AddPenetratingPointToCollision(int index)
    {
        supportVertexIndex = index;
    }

    public void Draw()
    {
        Vector2[] penetrated = penetratedBody.absoluteVertices;
        Vector2[] penetrating = penetratingBody.absoluteVertices;

        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glColor3f(1, 0, 0);

        Vector2 temp = penetrating[vertexIndex == 0 ? penetrating.length - 1 : vertexIndex - 1];
        GL11.glVertex2f(temp.x, temp.y);
        GL11.glVertex2f(penetrating[vertexIndex].x,
                penetrating[vertexIndex].y);
        GL11.glVertex2f(penetrating[(vertexIndex + 1) % penetrating.length].x,
                penetrating[(vertexIndex + 1) % penetrating.length].y);


        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINE_STRIP);

        GL11.glVertex2f(penetrated[edgeIndex].x, penetrated[edgeIndex].y);
        GL11.glVertex2f(penetrated[(edgeIndex + 1) % penetrated.length].x,
                penetrated[(edgeIndex + 1) % penetrated.length].y);

        GL11.glEnd();

        DrawingUtil.DrawArrow(penetrating[vertexIndex], penetrating[vertexIndex].ReturnAdded(
                penetrationVector));
        GL11.glColor3f(1, 1, 1);
    }

    public void Solve()
    {
        Vector2 contactPoint;
        if (supportVertexIndex == -1)
            contactPoint = penetratingBody.absoluteVertices[vertexIndex].ReturnAdded(
                    penetrationVector);
        else {
            int k = 0;
            contactPoint = penetratingBody.absoluteVertices[vertexIndex].ReturnAdded(
                    penetratingBody.absoluteVertices[supportVertexIndex]).ReturnMultiplied(
                    1f / 2).ReturnAdded(penetrationVector);
        }
        float mass_sum = penetratedBody.IMass + penetratingBody.IMass;
        if (mass_sum == 0)
            return;

        Vector2 r0 = contactPoint.ReturnSub(penetratingBody.position);
        Vector2 r1 = contactPoint.ReturnSub(penetratedBody.position);
        Vector2 v0 = penetratingBody.velocity.ReturnAdded(r0.VectorMultiplyScalarLeft(penetratingBody.angleSpeed));
        Vector2 v1 = penetratedBody.velocity.ReturnAdded(r1.VectorMultiplyScalarLeft(penetratedBody.angleSpeed));
        Vector2 dv = v0.ReturnSub(v1);

        float relMov = - dv.Dot(penetrationNormal);
        if ( relMov < -0.01f )
            return;

        float e = (penetratingBody.elasticitate + penetratedBody.elasticitate) / 2;
        float normDiv = mass_sum + penetrationNormal.Dot(
                r0.VectorMultiplyScalarLeft(r0.VectorMultiply(penetrationNormal) * penetratingBody.IMoment).ReturnAdded(
                        r1.VectorMultiplyScalarLeft(r1.VectorMultiply(penetrationNormal) * penetratedBody.IMoment)));

        float jn = -(1 + e)* dv.Dot(penetrationNormal) / normDiv;
        if (jn < 0)
            return;

        penetratingBody.velocity.Add(penetrationNormal.ReturnMultiplied(jn * penetratingBody.IMass));
        penetratingBody.angleSpeed += r0.VectorMultiply(penetrationNormal.ReturnMultiplied(jn)) * penetratingBody.IMoment;

        penetratedBody.velocity.Sub(penetrationNormal.ReturnMultiplied(jn * penetratedBody.IMass));
        penetratedBody.angleSpeed -= r1.VectorMultiply(penetrationNormal.ReturnMultiplied(jn)) * penetratedBody.IMoment;

        Vector2 tangent = dv.ReturnSub(penetrationNormal.ReturnMultiplied(dv.Dot(penetrationNormal)));
        tangent = tangent.Normalized();
        float tangentDiv = mass_sum + tangent.Dot(
                r0.VectorMultiplyScalarLeft(r0.VectorMultiply(tangent) * penetratingBody.IMoment).ReturnAdded(
                r1.VectorMultiplyScalarLeft(r1.VectorMultiply(tangent) * penetratedBody.IMoment)));

        float f = (penetratingBody.friction + penetratedBody.friction) / 2;

        float jt = -f * dv.Dot(tangent) / tangentDiv;

        penetratingBody.velocity.Add(tangent.ReturnMultiplied(jt * penetratingBody.IMass));
        penetratingBody.angleSpeed += r0.VectorMultiply(tangent.ReturnMultiplied(jt)) * penetratingBody.IMoment;

        penetratedBody.velocity.Sub(tangent.ReturnMultiplied(jt * penetratedBody.IMass));
        penetratedBody.angleSpeed -= r1.VectorMultiply(tangent.ReturnMultiplied(jt)) * penetratedBody.IMoment;
    }

    public void PenetrationPush(float mass_sum)
    {
        float newPenDepth = penetrationDepth;
        if (penetratingBody.pseudoSpeed != null)
            newPenDepth += penetratingBody.pseudoSpeed.Dot(penetrationNormal);
        if (penetratedBody.pseudoSpeed != null)
            newPenDepth -= penetratedBody.pseudoSpeed.Dot(penetrationNormal);

        if (penetrationDepth > Settings.penetrationLowerBound) {
            penetratedBody.ManagePseudoSpeed(penetrationNormal.ReturnMultiplied(
                    -penetratedBody.IMass / mass_sum * Settings.penetrationPushingByTimestep * newPenDepth));
            penetratingBody.ManagePseudoSpeed(penetrationNormal.ReturnMultiplied(
                    penetratingBody.IMass / mass_sum * Settings.penetrationPushingByTimestep * newPenDepth));
        }
    }

    float GetFullEnergy(Body body)
    {
        if (body.IMass == 0)
            return Float.POSITIVE_INFINITY;
        float moveEnergy = (float)Math.pow(body.velocity.Distance(), 2) / body.IMass / 2;
        float turningEnergy = (float)Math.pow(body.angleSpeed, 2) / body.IMoment / 2;

        return  moveEnergy + turningEnergy;
    }
}
