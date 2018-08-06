package moises.com.demolibraries.services.workmanager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import moises.com.demolibraries.MainActivity;
import moises.com.demolibraries.R;


public class TestWorkManagerFragment extends Fragment {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Unbinder unbinder;

    @BindView(R.id.text_view)
    TextView textView;

    public static TestWorkManagerFragment newInstance() {
        return new TestWorkManagerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_job_dispatcher, container, false);
        unbinder = ButterKnife.bind(this, view);
        setUp();
        return view;
    }

    private void setUp() {
        textView.setText("Testing work manager");
    }

    @OnClick({R.id.btn_start, R.id.btn_update, R.id.btn_stop})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                executeRequest(createOneTimeWorkRequest());
                //executeRequest(createPeriodicWorkRequest());
                //exampleRepeatWhen();
                break;
            case R.id.btn_update:

                break;
            case R.id.btn_stop:
                stopWorkRequest(TestWorker.TAG);
                //compositeDisposable.clear();
                break;
        }
    }

    private void stopWorkRequest(String tag) {
        WorkManager.getInstance().cancelAllWork();
    }

    private void executeRequest(WorkRequest workRequest) {
        WorkManager.getInstance().enqueue(workRequest);
    }

    private WorkRequest createOneTimeWorkRequest() {
        return new OneTimeWorkRequest.Builder(TestWorker.class)
                .setConstraints(createConstraints())
                .setInputData(createInputData())
                .addTag(TestWorker.TAG)
                .build();
    }

    private WorkRequest createPeriodicWorkRequest() {
        return new PeriodicWorkRequest.Builder(TestWorker.class, 15, TimeUnit.MINUTES)
                .addTag(TestWorker.TAG)
                .setConstraints(createConstraints())
                .build();
    }

    private Constraints createConstraints() {
        return new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
    }

    private Data createInputData() {
        return new Data.Builder().putString(TestWorker.GREETING, "Hello everybody Android").build();
    }

    private void log(String message) {
        Log.i(TAG, message);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
