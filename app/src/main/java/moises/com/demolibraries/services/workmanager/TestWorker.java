package moises.com.demolibraries.services.workmanager;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import androidx.work.Worker;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class TestWorker extends Worker{

    public static final String TAG = "Test123123TAG";
    public static final String GREETING = "greeting";

    private boolean flag = false;
    private CompositeDisposable compositeDisposable;

    public TestWorker() {
        super();
        compositeDisposable = new CompositeDisposable();
    }

    @NonNull
    @Override
    public Result doWork() {
        /*try {

            log( "completeJob: work started");
            //This task takes 2 seconds to complete.
            //Thread.sleep(2000);
            log(getInputData().getString(GREETING, "Not message, periodic work request"));
            log("completeJob: work finished");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.FAILURE;
        }*/
        flag = false;
        executeExampleRepeatWhen();
        return Result.SUCCESS;
    }

    @Override
    public void onStopped(boolean cancelled) {
        super.onStopped(cancelled);
        stopWorker();
        log("TestWorker was stopped " + cancelled);

    }

    private void stopWorker() {
        flag = true;
        if (compositeDisposable != null) {
            compositeDisposable.clear();
            //compositeDisposable.dispose();
        }
    }
    private void executeExampleRepeatWhen() {
        compositeDisposable.add(Flowable.timer(3, TimeUnit.SECONDS)
                /*.repeatWhen(objectFlowable -> objectFlowable.flatMap(o -> {
                    log(getInputData().getString(GREETING, "Not message, periodic work request"));
                    return Flowable.just(o);
                }))
                .takeUntil(aLong -> isFlag() == 1)*/
                .repeat(3)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> log("Finish execution"), this::logError));
    }

    private int isFlag() {
        return flag ? 1 : 0;
    }

    private void log(String message){
        Log.i(TAG, message);
    }

    private void logError(Throwable throwable) {
        Log.e(TAG, "ERROR: " + throwable.toString());
    }
}
