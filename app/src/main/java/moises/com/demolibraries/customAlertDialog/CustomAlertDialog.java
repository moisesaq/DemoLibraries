package moises.com.demolibraries.customAlertDialog;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import moises.com.demolibraries.R;
import moises.com.demolibraries.rxjava.Home;

public class CustomAlertDialog extends AlertDialog {
    public static final String TAG = CustomAlertDialog.class.getSimpleName();
    private View view;
    private Unbinder unbinder;
    private Home home;

    @BindView(R.id.tv_title) TextView tvTitle;
    @BindView(R.id.tv_message) TextView tvMessage;
    @BindView(R.id.btn_ok) Button btnOk;

    public CustomAlertDialog(@NonNull Context context, Home home) {
        super(context, R.style.CustomAlertDialog);
        this.home = home;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_custom_alert, null);
        unbinder = ButterKnife.bind(this, view);
        setUp();
        setUpType();
        setView(view);
        super.onCreate(savedInstanceState);
    }

    private void setUp(){
        tvTitle.setText(home.getLabel());
        tvMessage.setText(home.getStatus());
    }

    private void setUpType(){
        if (getWindow() != null)
            getWindow().setType(prepareType());
    }

    private int prepareType(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ? WindowManager.LayoutParams
                .TYPE_APPLICATION_PANEL : WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        unbinder.unbind();
    }

    @OnClick(R.id.btn_ok)
    public void okClick(){
        dismiss();
    }
}
