package moises.com.demolibraries;

import android.app.Application;
import android.content.Context;

import com.mapbox.mapboxsdk.Mapbox;

public class App extends Application{

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        setUpMapbox();
        context = getApplicationContext();
    }

    private void setUpMapbox(){
        Mapbox.getInstance(this,
                "pk.eyJ1IjoibW9pc2VzYXEiLCJhIjoiY2pmYnpqbnlrMG1tZTJ3cGFjeDkyZ216cCJ9.p-Qhe0Zz0HwI3kAYYFSshQ");

    }

    public void setContext(Context context){
        this.context = context;
    }
}
