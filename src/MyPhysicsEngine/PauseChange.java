package MyPhysicsEngine;

public class PauseChange implements Change {
    public void Apply(Physics p) {
        p.PauseChange();
    }
}
