package moises.com.demolibraries;

import android.app.Application;
import android.content.Context;

public class App extends Application{

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public void setContext(Context context){
        this.context = context;
    }
}
