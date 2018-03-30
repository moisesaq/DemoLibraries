package moises.com.demolibraries.mapbox;

import com.google.gson.Gson;

public class DeepValues {
    private float deep;
    private int deepColor;

    public float getDeep() {
        return deep;
    }

    public void setDeep(float deep) {
        this.deep = deep;
    }

    public int getDeepColor() {
        return deepColor;
    }

    public void setDeepColor(int deepColor) {
        this.deepColor = deepColor;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
