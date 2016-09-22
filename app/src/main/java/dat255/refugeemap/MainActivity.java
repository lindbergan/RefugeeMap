package dat255.refugeemap;

import android.app.FragmentManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends FragmentActivity
        implements EventListFragment.OnListFragmentInteractionListener
{

  private ImageButton mButton;
  private String ACTIVE_FRAGMENT;

  @Override protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    firstStart();
  }

  public void showEventList(){
    if (ACTIVE_FRAGMENT.equals(GMapFragment.class.getSimpleName())) {
      FragmentManager fm = getFragmentManager();
      fm.beginTransaction().replace(R.id.fragment_container, new EventListFragment()).commit();
      ACTIVE_FRAGMENT = EventListFragment.class.getSimpleName();
    }

    else if (ACTIVE_FRAGMENT.equals(EventListFragment.class.getSimpleName())) {
      FragmentManager fm = getFragmentManager();
      fm.beginTransaction().replace(R.id.fragment_container, new GMapFragment()).commit();
      ACTIVE_FRAGMENT = GMapFragment.class.getSimpleName();
    }
  }

  public void firstStart() {
    FragmentManager fm = getFragmentManager();
    initializeViews(findViewById(R.id.main_layout));
    fm.beginTransaction().replace(R.id.fragment_container, new GMapFragment()).commit();
    ACTIVE_FRAGMENT = GMapFragment.class.getSimpleName();
  }

  @Override
  public void onListFragmentInteraction(StaticContent.StaticItem item){
    Toast.makeText(this, "You selected: " + item.title, Toast.LENGTH_SHORT).show();
  }

  public void initializeViews(View view) {
    mButton = (ImageButton) view.findViewById(R.id.change_views_button);

    mButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        showEventList();
        Drawable map = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_map_black_48dp, null);
        Drawable list = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_list_black_48dp, null);
        if (mButton != null && map != null && list != null) {
          if (mButton.getDrawable().getConstantState().equals(map.getConstantState())) {
            mButton.setImageDrawable(list);
          }
          else {
            mButton.setImageDrawable(map);
          }
        }
      }
    });
  }
}