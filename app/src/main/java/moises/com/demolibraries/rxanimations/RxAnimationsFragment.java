package moises.com.demolibraries.rxanimations;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import moises.com.demolibraries.R;
import oxim.digital.rx2anim.RxAnimationBuilder;

import static oxim.digital.rx2anim.RxAnimations.animateTogether;
import static oxim.digital.rx2anim.RxAnimations.enter;
import static oxim.digital.rx2anim.RxAnimations.fadeIn;
import static oxim.digital.rx2anim.RxAnimations.leave;
import static oxim.digital.rx2anim.RxAnimations.slideHorizontal;
import static oxim.digital.rx2anim.RxAnimations.slideVertical;

public class RxAnimationsFragment extends Fragment {

    @BindView(R.id.layoutA)
    protected View layoutA;
    @BindView(R.id.layoutB)
    protected View layoutB;
    @BindView(R.id.text_view)
    TextView textView;
    @BindView(R.id.image_view)
    ImageView imageView;

    private boolean expand;

    private Disposable animationDisposable = Disposables.disposed();

    public static RxAnimationsFragment newInstance() {
        return new RxAnimationsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rxanimations, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.image_view)
    public void onImageViewClick() {
        //animateNow();
        //animateNow2();
        showLayoutB();
    }

    private void animateNow() {
        animationDisposable = fadeIn(imageView, 500)
                .andThen(slideHorizontal(imageView, 500, -100))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> textView.setText("I have finished"));
    }

    private void animateNow2() {
        expand = !expand;

        animateTogether(textViewAnimation(), imageViewAnimation())
                .doOnComplete(() -> imageView.setImageResource(getImage()))
                .subscribe();
    }

    private int getImage() {
        return expand ? R.mipmap.dragon_123 : R.mipmap.dragon_ball_z;
        /*if (expand)
            imageView.setImageResource(R.mipmap.dragon_123);
        else
            imageView.setImageResource(R.mipmap.dragon_ball_z);*/
    }

    private Completable textViewAnimation() {
        int value = textView.getHeight();
        return slideVertical(textView, 300, expand ? -value : value);
    }

    private Completable imageViewAnimation() {
        float dXY = 0.5f;//getView().getPivotX() - imageView.getRight();
        return RxAnimationBuilder.animate(imageView)
                .scale(imageView.getWidth() / 2, imageView.getWidth() / 2)
                .scaleX(expand ? dXY : -dXY)
                .scaleY(expand ? dXY : -dXY)
                .duration(300)
                .interpolator(new AccelerateDecelerateInterpolator())
                .schedule();
    }

    private void showLayoutB() {
        Completable a = leave(layoutA, -layoutA.getWidth(), 0)
                .doOnComplete(() -> layoutB.setVisibility(View.VISIBLE));
        Completable b = slideHorizontal(layoutB, layoutB.getLeft(), 0);
        a.andThen(b).subscribe();
    }
}





















