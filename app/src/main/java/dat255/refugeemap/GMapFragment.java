package dat255.refugeemap;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class GMapFragment extends Fragment implements OnMapReadyCallback {

    @Override
    public void onMapReady(GoogleMap googleMap){

        LatLng gbgMarker = new LatLng(57.70887000, 11.97456000);//def of GBG

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(gbgMarker,15)); //zoom in on marker

        googleMap.addMarker(new MarkerOptions() //place the marker
                .position(gbgMarker)
                .title("Hello world"));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MapFragment fragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gmap, container, false);
    }

}
