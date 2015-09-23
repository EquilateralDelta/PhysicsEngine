package MyPhysicsEngine;

import org.lwjgl.opengl.GL11;

public class DrawingUtil {
    public static void DrawCross(Vector2 position)
    {
        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glColor3f(1, 0, 0);

        GL11.glVertex2f(-3 + position.x, position.y);
        GL11.glVertex2f(3 + position.x, position.y);

        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINE_STRIP);

        GL11.glVertex2f(position.x, -3 + position.y);
        GL11.glVertex2f(position.x, 3 + position.y);

        GL11.glColor3f(1, 1, 1);
        GL11.glEnd();
    }

    public static void DrawArrow(Vector2 start, Vector2 end)
    {
        GL11.glBegin(GL11.GL_LINE_STRIP);

        GL11.glVertex2f(start.x, start.y);
        GL11.glVertex2f(end.x, end.y);

        GL11.glEnd();

        float angleCos1 = (float)Math.cos(Math.PI / 6),
                angleSin1 = (float)Math.sin(Math.PI / 6),
                angleCos2 = (float)Math.cos(-Math.PI / 6),
                angleSin2 = (float)Math.sin(-Math.PI / 6);


        Vector2 turningMatrixX1 = new Vector2(angleCos1, -angleSin1),
                turningMatrixY1 = new Vector2(angleSin1, angleCos1),
                turningMatrixX2 = new Vector2(angleCos2, -angleSin2),
                turningMatrixY2 = new Vector2(angleSin2, angleCos2);

        Vector2 base = end.ReturnSub(start);
        base = base.Normalized().ReturnMultiplied(-5);

        GL11.glBegin(GL11.GL_LINE_STRIP);

        GL11.glVertex2f(end.x, end.y);
        GL11.glVertex2f(end.x + base.x * turningMatrixX1.x + base.y * turningMatrixX1.y,
                end.y + base.x * turningMatrixY1.x + base.y * turningMatrixY1.y);

        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINE_STRIP);

        GL11.glVertex2f(end.x, end.y);
        GL11.glVertex2f(end.x + base.x * turningMatrixX2.x + base.y * turningMatrixX2.y,
                end.y + base.x * turningMatrixY2.x + base.y * turningMatrixY2.y);

        GL11.glEnd();
    }
}
