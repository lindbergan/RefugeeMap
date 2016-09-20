package dat255.refugeemap;

import android.app.FragmentManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

public class MainActivity extends FragmentActivity
{
  @Override protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    FragmentManager fm = getFragmentManager();

    //replace the default container:
    fm.beginTransaction().replace(R.id.fragment_container, new GMapFragment()).commit();
  }
}