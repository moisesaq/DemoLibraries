package moises.com.demolibraries.mapbox;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import moises.com.demolibraries.R;

import static com.mapbox.mapboxsdk.style.layers.Filter.all;
import static com.mapbox.mapboxsdk.style.layers.Filter.gte;
import static com.mapbox.mapboxsdk.style.layers.Filter.lt;
import static com.mapbox.mapboxsdk.style.layers.Filter.neq;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleBlur;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleRadius;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textField;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textSize;

public class HeatMapboxFragment extends Fragment implements OnMapReadyCallback{

    private Unbinder unbinder;
    private MapboxMap mapboxMap;
    @BindView(R.id.mapView) MapView mapView;

    public static HeatMapboxFragment newInstance() {
        return new HeatMapboxFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_heat_mapbox, container, false);
        unbinder = ButterKnife.bind(this, view);
        setUp(savedInstanceState);
        return view;
    }

    private void setUp(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        addClusteredGeoJsonSource();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private void addClusteredGeoJsonSource() {

        // Add a new source from our GeoJSON data and set the 'cluster' option to true.
        mapboxMap.addSource(new GeoJsonSource("earthquakes", loadJSONFromAsset(), createGeoJsonOptions()));

        //showUnclustereds();
        showClusters();
        showCountLabels();
    }

    private void showUnclustereds(){
        CircleLayer unclustered = new CircleLayer("unclustered-points", "earthquakes");
        unclustered.setProperties(
                circleColor(Color.parseColor("#FF4081")),
                circleRadius(20f),
                circleBlur(1f));
        unclustered.setFilter(neq("cluster", true)
        );
        mapboxMap.addLayerBelow(unclustered, "building");
    }

    private void showClusters(){
        // Each point range gets a different fill color.
        final int[][] layers = new int[][]{
                new int[]{150, Color.parseColor("#66ff66")},
                new int[]{20, Color.parseColor("#ffcc00")},
                new int[]{0, Color.parseColor("#ff0000")}
        };

        for (int i = 0; i < layers.length; i++) {
            CircleLayer circles = new CircleLayer("cluster-" + i, "earthquakes");
            circles.setProperties(
                    circleColor(layers[i][1]),
                    circleRadius(50f),
                    circleBlur(1f)
            );
            int testI0 = layers[i][0];
            int testI_1 = i == 0 ? layers[i][0] : layers[i - 1][0];
            log("Test i|0 : " + testI0 + " test i-1|0 : " + testI_1);
            circles.setFilter(i == 0 ? gte("point_count", testI0) :
                            all(gte("point_count", testI0), lt("point_count", testI_1))
            );
            mapboxMap.addLayerBelow(circles, "building");
        }
    }

    private void showCountLabels(){
        //Add the count labels
        SymbolLayer count = new SymbolLayer("count", "earthquakes");
        count.setProperties(
                textField("{point_count}"),
                textSize(12f),
                textColor(Color.WHITE)
        );
        mapboxMap.addLayer(count);
    }

    private void log(String value){
        Log.e(HeatMapboxFragment.class.getSimpleName(), value);
    }

    private GeoJsonOptions createGeoJsonOptions(){
        return new GeoJsonOptions()
                .withCluster(true)
                .withClusterMaxZoom(15) // Max zoom to cluster points on
                .withClusterRadius(20); // Use small cluster radius for the hotspots look
    }

    public String loadJSONFromAsset() {
        String json;
        try {
            InputStream is = getActivity().getAssets().open("depths.geojson");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
