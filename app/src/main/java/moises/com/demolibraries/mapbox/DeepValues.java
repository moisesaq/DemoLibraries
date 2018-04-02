package moises.com.demolibraries.mapbox;

import com.google.gson.Gson;

public class DeepValues {
    private float depth;
    private float modifiedDepth;

    public float getDepth() {
        return depth;
    }

    public void setDepth(float depth) {
        this.depth = depth;
    }

    public float getModifiedDepth() {
        return modifiedDepth;
    }

    public void setModifiedDepth(float modifiedDepth) {
        this.modifiedDepth = modifiedDepth;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
