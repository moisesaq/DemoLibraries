package moises.com.demolibraries.rxjava;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import moises.com.demolibraries.App;
import moises.com.demolibraries.R;
import moises.com.demolibraries.SecondActivity;
import moises.com.demolibraries.customAlertDialog.CustomAlertDialog;

public class RxJavaFragment extends Fragment {

    private static final String TAG = RxJavaFragment.class.getSimpleName();
    private static final int MAX_ATTEMPTS = 3;
    private int errorHttp = 401;
    private boolean isNotUpdating = false;
    private int attempts = 0;
    private Unbinder unbinder;
    private CompositeDisposable compositeDisposable;

    public static RxJavaFragment newInstance() {
        return new RxJavaFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rxjava, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.btn_start, R.id.btn_change_flag})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_start:
                executeDelayAndShowAlert();
                break;
            case R.id.btn_change_flag:
                //isNotUpdating = true;
                //showAlert(MockHome.getInstance().getHomes().get(0));
                SecondActivity.start(getContext());
                break;
        }
    }

    private void executeDelayAndShowAlert(){
        log("Executing delay");
        Observable.timer(3, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(compositeDisposable::add)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> showAlert(MockHome.getInstance().getHomes().get(1)),
                        this::logError);
    }

    private void showAlert(Home home){
        log("Show alert");
        //AlertsDispatcher.getInstance().showAlert(home);
        CustomAlertDialog customAlertDialog = new CustomAlertDialog(App.context, home);
        customAlertDialog.show();
    }

    private void executeOperatorRepeatWhenWithDelay(){
        Observable.just(MockHome.getInstance().getHomes())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(compositeDisposable::add)
                .map(homes -> {
                    MockHome.getInstance().increaseCounter();
                    return homes;
                })
                .repeatWhen(notification -> notification.flatMap(o ->
                                Observable.just(o).delay(2, TimeUnit.SECONDS)))
                .takeUntil(homes -> getHomesRunning(homes) == homes.size())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showResult, this::logError);
    }

    private void showResult(List<Home> homes){
        for (Home home: homes){
            System.out.print(home);
            if (home.getStatus().equals("finished"))
                showAlert(home);
        }
    }

    private int getHomesRunning(List<Home> homes){
        int counter = 0;
        for (Home home: homes){
            if (!home.getStatus().equals("running"))
                counter++;
        }
        log(" >>>> " + counter);
        return counter;
    }



    public void simpleTestOfOperatorRepeatWhen() {
        AtomicInteger a = new AtomicInteger();

        Observable.just(1, 2, 3, 4, 5, 6, 7, 8)
                .repeatWhen(notification -> {
                    return notification.flatMap(o -> {

                        System.out.println("'repeatWhen'");
                        if(a.getAndAdd(1) < 10){
                            return Observable.just(o).delay(1,TimeUnit.SECONDS);
                        }else{
                            return Observable.empty();
                        }
                    });

                })//
                .subscribe(System.out::println);
    }

    /**
     * Implementation of concat two observables but sequence
     */
    private void merge(){
        executeDelay().subscribe(text -> {
            log(text);
            //attempts = 0;
            isNotUpdating = false;
        });
    }

    private Observable<String> createObservable(){
        return Observable.fromCallable(() -> "Hello Android :) :) ;)")
                .doOnSubscribe(compositeDisposable::add)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<String> executeDelay(){
        return Observable.timer(2, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(compositeDisposable::add)
                .doOnNext(o -> {
                    attempts++;
                    log("NUMBER ATTEMPTS: " + attempts);
                })
                .repeat(MAX_ATTEMPTS)
                .takeUntil(observer -> isNotUpdating) //This will filter repeat operator
                .filter(o -> attempts >= MAX_ATTEMPTS || isNotUpdating) //And this will filter if createObservable will be execute or not
                .concatMap(o -> createObservable())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private <T> Observable<T> executeRepeatWhenWithDelayAndResumeObservable(Observable<T> toBeResumed){
        if (errorHttp == 401 && isNotUpdating){
            log("execute delay");
            return Observable.timer(2, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe(compositeDisposable::add)
                    .observeOn(AndroidSchedulers.mainThread())
                    /*.repeatUntil(() -> isNotUpdating)
                    .repeatWhen(objectObservable -> objectObservable.delay(3, TimeUnit.SECONDS).concatMap(o -> objectObservable))*/
                    .concatMap(o -> toBeResumed)
                    .doOnComplete(() -> log("Delay has finished"));
                    //.subscribe(o -> log("Delay has finished"));*/
        }
        return toBeResumed;
    }

    private void log(String value){
        Log.e(TAG, value);
    }

    private void logError(Throwable throwable){
        Log.e(TAG, "Error: " + throwable.toString());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        log("destroy");
        compositeDisposable.dispose();
        unbinder.unbind();
    }
}
