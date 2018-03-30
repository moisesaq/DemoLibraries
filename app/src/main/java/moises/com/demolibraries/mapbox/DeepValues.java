package moises.com.demolibraries.mapbox;

import com.google.gson.Gson;

public class DeepValues {
    private float deep;

    public float getDeep() {
        return deep;
    }

    public void setDeep(float deep) {
        this.deep = deep;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
