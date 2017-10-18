package moises.com.demolibraries;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class ScheduledJobService extends JobService{

    private static final String TAG = ScheduledJobService.class.getSimpleName();

    private static final int NOTIFICATION_REQUEST_CODE = 0;
    private static final String NOTIFICATION_CHANNEL = "notificationChannel";
    private static final int NOTIFICATION_ID = 112;
    private static final long TIME_VIBRATE_PUSH = 1000;
    private static final int TIME_LIGHT_PUSH = 4000;

    @Override
    public boolean onStartJob(JobParameters job) {
        //executeDelay(job);
        //test(job);
        showNotification();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }

    private void test(JobParameters jobParameters){
        new Thread(new Runnable() {
            @Override
            public void run() {
                codeYouWantToRun(jobParameters);
            }
        }).start();
    }

    public void codeYouWantToRun(final JobParameters parameters) {
        try {

            Log.d(TAG, "completeJob: " + "jobStarted");
            //This task takes 2 seconds to complete.
            Thread.sleep(5000);

            Log.d(TAG, "completeJob: " + "jobFinished");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //Tell the framework that the job has completed and doesnot needs to be reschedule
            jobFinished(parameters, true);
        }
    }

    private void executeDelay(final JobParameters jobParameters){
        Observable.timer(3, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> this.showNotification(), this::error);
    }

    private void showNotification(){
        Log.e(TAG, "TEST SCHEDULERS FINISH");
       /* NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null)
            notificationManager.notify(NOTIFICATION_ID, getNotificationBuilder().build());*/
    }

    private void error(Throwable throwable){
        Log.e(TAG, "ERROR: " + throwable.toString());
    }

    private NotificationCompat.Builder getNotificationBuilder(){
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        return new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(getPendingIntent())
                .setAutoCancel(true)
                .setVibrate(getVibrate(TIME_VIBRATE_PUSH))
                .setLights(Color.MAGENTA, TIME_LIGHT_PUSH, TIME_LIGHT_PUSH)
                .setSound(alarmSound)
                .setContentTitle("Title notification")
                .setContentText("Text notification");
    }

    private long[] getVibrate(long time){
        return new long[]{time, time, time, time, time};
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(intent);
        return stackBuilder.getPendingIntent(NOTIFICATION_REQUEST_CODE, PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
