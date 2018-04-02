package moises.com.demolibraries.mapbox;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.style.functions.Function;
import com.mapbox.mapboxsdk.style.functions.SourceFunction;
import com.mapbox.mapboxsdk.style.functions.stops.IntervalStops;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.layers.PropertyValue;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.Source;
import com.mapbox.services.commons.geojson.Feature;
import com.mapbox.services.commons.geojson.FeatureCollection;
import com.xw.repo.BubbleSeekBar;

import java.io.IOException;
import java.io.InputStream;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import moises.com.demolibraries.R;

import static com.mapbox.mapboxsdk.style.functions.stops.Stop.stop;
import static com.mapbox.mapboxsdk.style.layers.Filter.all;
import static com.mapbox.mapboxsdk.style.layers.Filter.gte;
import static com.mapbox.mapboxsdk.style.layers.Filter.has;
import static com.mapbox.mapboxsdk.style.layers.Filter.lt;
import static com.mapbox.mapboxsdk.style.layers.Filter.neq;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleBlur;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleRadius;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textField;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textSize;

public class HeatMapboxFragment extends Fragment implements OnMapReadyCallback{

    private static final String GEOJSON_SOURCE_ID = "geojson_buenos_aires";
    private Unbinder unbinder;
    private MapboxMap mapboxMap;
    private FeatureCollection featureCollection;
    private CircleLayer circleLayer;

    @BindView(R.id.mapView) MapView mapView;
    @BindView(R.id.bubble_seek_bar) BubbleSeekBar bubbleSeekBar;

    @BindColor(R.color.green) int green;
    @BindColor(R.color.yellow) int yellow;
    @BindColor(R.color.red) int red;

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
        bubbleSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                //log("Progress float: " + progressFloat);
                /*if (progressFloat != increase){
                    increase = progressFloat;
                    updateFeatureCollection(increase);
                }*/
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                log("getProgressOnActionUp: " + progressFloat);
                updateFeatureCollection(progressFloat);
            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                log("getProgressOnFinally: " + progressFloat);
            }
        });
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        //addClusteredGeoJsonSource();
        showHeatMap();
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

    private static final String SOURCE_DEEPS_ID = "source_deeps_id";
    private static final String LAYER_DEEPS_ID = "layer_deeps_id";
    private static final String IMAGE_DEEP_ID = "image_deep_id";
    private static final String CIRCLE_DEEP_ID = "circle_deep_id";

    private void showHeatMap(){
        featureCollection = FeatureCollection.fromJson(loadJSONFromAsset());
        Source source = new GeoJsonSource(SOURCE_DEEPS_ID, featureCollection);
        mapboxMap.addSource(source);

        circleLayer = new CircleLayer(CIRCLE_DEEP_ID, SOURCE_DEEPS_ID);
        circleLayer.withProperties(circleColor(getCircleColor("depth")),
                circleRadius(15f), circleBlur(0f));
        mapboxMap.addLayer(circleLayer);
        showFeatures(featureCollection);
    }

    private SourceFunction<Number, String> getCircleColor(String property){
        return Function.property(property, IntervalStops.interval(
                        stop(0, circleColor(red)),
                        stop(1.3, circleColor(red)),
                        stop(1.4, circleColor(yellow)),
                        stop(1.5, circleColor(yellow)),
                        stop(1.6, circleColor(green)),
                        stop(5.5, circleColor(green))
                ));
    }

    /*las profundidades que sean mayores a 1,5 el punto va en verde.
    las profundidades que esten entre 1,3 y 1,5 van en amarillo.
    las profundidades menores a 1,3 van en rojo.*/
    private void updateFeatureCollection(float numberDeep){
        for (Feature feature: featureCollection.getFeatures()){
            feature.setProperties(updateProperties(feature, numberDeep));
        }
        GeoJsonSource geoJsonSource = (GeoJsonSource) mapboxMap.getSource(SOURCE_DEEPS_ID);
        if (geoJsonSource != null)
            geoJsonSource.setGeoJson(featureCollection);
        circleLayer.setProperties(circleColor(getCircleColor("modifiedDepth")),
                circleRadius(15f), circleBlur(0f));
    }

    private JsonObject updateProperties(Feature feature, float numberDepth){
        float depth = feature.getProperty("depth").getAsFloat();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("depth", depth);
        jsonObject.addProperty("modifiedDepth", depth + numberDepth);
        log("New depth: " + jsonObject.toString());
        return jsonObject;
    }

    private Bitmap generateBitmap(){
        return BitmapFactory.decodeResource(getResources(), R.drawable.ic_marker);
    }

    private void showFeatures(FeatureCollection collection){
        for (Feature feature: collection.getFeatures()){
            log("Property: " + feature.getProperties().toString());
        }
    }

    /**
     * Show heat map with clusters
     */
    private void addClusteredGeoJsonSource() {
        // Add a new source from our GeoJSON data and set the 'cluster' option to true.
        mapboxMap.addSource(new GeoJsonSource(GEOJSON_SOURCE_ID, loadJSONFromAsset(),
                createGeoJsonOptions()));

        //showUnclustereds();
        showSimplePoint();
        showClusters();
        showCountLabels();
    }

    private void getSourcesAndShow(){
        for (Source source: mapboxMap.getSources()){
            log("Source: " + source.toString());
        }
    }

    private void showSimplePoint(){
        //Creating a marker layer for single data points
        SymbolLayer unclustered = new SymbolLayer("unclustered-points", "earthquakes");
        unclustered.setProperties(iconImage("marker-15"));
        mapboxMap.addLayer(unclustered);
    }

    private void showUnclustereds(){
        CircleLayer unclustered = new CircleLayer("unclustered-points", "earthquakes");
        unclustered.setProperties(
                circleColor(Color.parseColor("#FF4081")),
                circleRadius(10f),
                circleBlur(0f));
        unclustered.setFilter(neq("cluster", true));
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
            CircleLayer circles = new CircleLayer("cluster-" + i, GEOJSON_SOURCE_ID);
            circles.setProperties(
                    circleColor(layers[i][1]),
                    circleRadius(10f),
                    circleBlur(0f)
            );
            int pointsI0 = layers[i][0];
            int pointsI_1 = i == 0 ? layers[i][0] : layers[i - 1][0];
            log("Test i|0 : " + pointsI0 + " test i-1|0 : " + pointsI_1);

            circles.setFilter(i == 0 ? gte("point_count", pointsI0) :
                            all(gte("point_count", pointsI0), lt("point_count", pointsI_1))
            );
            mapboxMap.addLayerBelow(circles, "building");
        }
    }

    private void showCountLabels(){
        //Add the count labels
        SymbolLayer count = new SymbolLayer("count", GEOJSON_SOURCE_ID);
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
