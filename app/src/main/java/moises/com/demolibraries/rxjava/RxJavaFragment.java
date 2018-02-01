package moises.com.demolibraries.rxjava;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import moises.com.demolibraries.R;

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
                merge();
                break;
            case R.id.btn_change_flag:
                isNotUpdating = true;
                break;
        }
    }

    private void merge(){
        executeDelay().subscribe(text -> {
            log(text);
            attempts = 0;
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

    private <T> Observable<T> testRepeatWhen(Observable<T> toBeResumed){
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
        unbinder.unbind();
    }
}
