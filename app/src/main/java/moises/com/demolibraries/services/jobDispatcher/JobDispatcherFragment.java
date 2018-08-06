package moises.com.demolibraries.services.jobDispatcher;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import moises.com.demolibraries.MainActivity;
import moises.com.demolibraries.R;


public class JobDispatcherFragment extends Fragment{
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String ALARM_TAG = "ALARM_TAG";
    private FirebaseJobDispatcher dispatcher;
    private Unbinder unbinder;

    @BindView(R.id.text_view) TextView textView;

    public static JobDispatcherFragment newInstance() {
        return new JobDispatcherFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_job_dispatcher, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    private void setUp(){
        dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(getContext()));
        textView.setText("Firebase job dispatcher");
    }

    @OnClick({R.id.btn_start, R.id.btn_update, R.id.btn_stop})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_start:
                scheduleJob();
                Log.i(TAG, "CLICK START");
                break;
            case R.id.btn_update:
                updateJob(dispatcher);
                Log.i(TAG, "CLICK UPDATED");
                break;
            case R.id.btn_stop:
                cancelJob();
                Log.i(TAG, "CLICK STOP");
                break;
        }
    }

    private void scheduleJob(){
        Job job = createJob(dispatcher);
        dispatcher.mustSchedule(job);
    }

    public static Job createJob(FirebaseJobDispatcher dispatcher){
        Job job = dispatcher.newJobBuilder()
                //persist the task across boots
                //.setLifetime(Lifetime.FOREVER)
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                //call this service when the criteria are met.
                .setService(ScheduledJobService.class)
                //unique id of the task
                .setTag(ALARM_TAG)
                //don't overwrite an existing job with the same tag
                .setReplaceCurrent(true)
                // We are mentioning that the job is periodic.
                .setRecurring(true)
                // Run between 30 - 60 seconds from now.
                .setTrigger(Trigger.executionWindow(28, 30))
                // retry with exponential backoff
                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                //.setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                //Run this job only when the network is available.
                //.setConstraints(Constraint.ON_ANY_NETWORK, Constraint.DEVICE_CHARGING)
                .build();
        return job;
    }

    public static Job updateJob(FirebaseJobDispatcher dispatcher) {
        Job newJob = dispatcher.newJobBuilder()
                //update if any task with the given tag exists.
                .setReplaceCurrent(true)
                //Integrate the job you want to start.
                .setService(ScheduledJobService.class)
                .setTag(ALARM_TAG)
                // Run between 30 - 60 seconds from now.
                .setTrigger(Trigger.executionWindow(15, 30))
                .build();
        return newJob;
    }

    public void cancelJob(){
        //Cancel all the jobs for this package
        dispatcher.cancelAll();
        // Cancel the job for this tag
        dispatcher.cancel(ALARM_TAG);

    }
}
