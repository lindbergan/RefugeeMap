package dat255.refugeemap;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

/**
 * Interface that extends mutliple Google Services in order to reduce clutter.
 */
public interface GoogleServicesAdapter extends
    GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback,
    GoogleMap.InfoWindowAdapter, GoogleMap.OnMarkerClickListener,
    GoogleMap.OnMapClickListener{}
