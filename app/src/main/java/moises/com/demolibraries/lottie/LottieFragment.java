package moises.com.demolibraries.lottie;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import moises.com.demolibraries.R;

public class LottieFragment extends Fragment {

    public static LottieFragment newInstance() {
        return new LottieFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lottie, container, false);
    }
}
