package moises.com.demolibraries;

import android.content.Context;
import android.util.Log;

import moises.com.demolibraries.customAlertDialog.CustomAlertDialog;
import moises.com.demolibraries.rxjava.Home;

public class AlertsDispatcher {

    private Context context;
    private static AlertsDispatcher alertsDispatcher;
    private AlertsDispatcher(){
        this.context = App.context;
    }

    public static AlertsDispatcher getInstance(){
        if (alertsDispatcher == null)
            alertsDispatcher = new AlertsDispatcher();
        return alertsDispatcher;
    }

    public void showAlert(Home home){
        Log.e("---", "Show alert");
        CustomAlertDialog customAlertDialog = new CustomAlertDialog(context, home);
        customAlertDialog.show();
    }
}
