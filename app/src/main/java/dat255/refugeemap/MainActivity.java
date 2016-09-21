package dat255.refugeemap;

import android.app.FragmentManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends FragmentActivity
        implements EventListFragment.OnListFragmentInteractionListener
{
  @Override protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    FragmentManager fm = getFragmentManager();

    //replace the default container:
    fm.beginTransaction().replace(R.id.fragment_container, new GMapFragment()).commit();
  }

  //TODO: Denna metod skall kallas när man klickar på Adrians byta-vy knapp för att visa listan
  public void showEventList(Bundle savedInstanceState){
//    setContentView(R.layout.activity_dummy);
//
//    if (savedInstanceState == null) {
//      getSupportFragmentManager().beginTransaction().add(R.id.root_layout,
//              EventListFragment.newInstance()).commit();
//    }
  }

  @Override
  public void onListFragmentInteraction(StaticContent.StaticItem item){
    Toast.makeText(this, "You selected: " + item.title, Toast.LENGTH_SHORT).show();
  }
}