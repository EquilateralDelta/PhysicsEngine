package MyPhysicsEngine;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ClicksBodyCreation {
    Main parent;
    List<Vector2> dots;

    public ClicksBodyCreation(Main p)
    {
        parent = p;
        dots = new ArrayList<Vector2>();
    }

    public void MouseClick()
    {
        dots.add(new Vector2(Mouse.getX(), Mouse.getY()));

        if (dots.size() > 3) {
            int minDotIndex = 0;

            for (int i = 1; i < dots.size(); i++)
                if (dots.get(i).x < dots.get(minDotIndex).x)
                    minDotIndex = i;

            final Vector2 basePoint = dots.remove(minDotIndex);

            /*boolean isFinished = false;
            while (!isFinished) {
                isFinished = true;

                for (int i = 1; i < dots.size() - 1; i++) {
                    Vector2 first = dots.get(i).ReturnSub(dots.get(0)),
                            second = dots.get(i + 1).ReturnSub(dots.get(0));

                    if (first.VectorMultiply(second) < 0) {
                        dots.add(i, dots.remove(i + 1));
                        isFinished = false;
                    }
                }
            }*/

            dots.sort(new Comparator<Vector2>() {
                public int compare(Vector2 o1, Vector2 o2) {
                    Vector2 first = o1.ReturnSub(basePoint),
                            second = o2.ReturnSub(basePoint);

                    float fRes = first.VectorMultiply(second);
                    int iRes = ((fRes > 0) ? 1 : ((fRes < 0) ? -1 : 0));

                    return iRes;
                }
            });

            dots.add(0, basePoint);

            for (int i = 0; i < dots.size(); i++)
            {
                Vector2 first = dots.get(i).ReturnSub(dots.get((i != 0) ? i - 1: dots.size() - 1)),
                        second = dots.get((i != dots.size() - 1) ? i + 1: 0).ReturnSub(dots.get(i));

                if (first.VectorMultiply(second) > 0) {
                    dots.remove(i);
                    i = -1;
                }
            }
        }
    }

    public AddBodyChange EndDrawing()
    {
        Vector2[] dotsArray = new Vector2[dots.size()];
        dots.toArray(dotsArray);
        dots.clear();

        return new AddBodyChange(dotsArray);
    }

    public void Draw()
    {
        GL11.glColor3f(1, 1, 1);

        GL11.glBegin(GL11.GL_LINE_LOOP);
        for (int i = 0; i < dots.size(); i++) {
            GL11.glVertex2f(dots.get(i).x, dots.get(i).y);
        }
        GL11.glEnd();
    }
}
