package dat255.refugeemap;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

public interface GoogleServicesAdapter extends
    GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback,
    GoogleMap.InfoWindowAdapter, GoogleMap.OnMarkerClickListener,
    GoogleMap.OnMapClickListener{}
