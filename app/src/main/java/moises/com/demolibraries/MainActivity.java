package moises.com.demolibraries;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import moises.com.demolibraries.lottie.LottieFragment;

public class MainActivity extends AppCompatActivity {

    private Unbinder unbinder;
    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        showFragment();
    }

    private void showFragment(){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_main, LottieFragment.newInstance())
                .commit();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
