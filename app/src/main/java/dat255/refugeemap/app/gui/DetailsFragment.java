package dat255.refugeemap.app.gui;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import dat255.refugeemap.R;
import dat255.refugeemap.app.base.AppDatabase;
import dat255.refugeemap.model.db.Event;
import lombok.val;

/**
 * A {@link Fragment} subclass used for displaying data from an {@link Event}.
 * Also allows interaction with the data through buttons.
 * Activities that contain this fragment must implement the
 * {@link DetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailsFragment extends Fragment {
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_ID = "id";
	private static String EVENT_ID;
	private int id;
	private String title;
	private String address;
	private String time;
	private String contact;
	private String description;
	private double longitude;
	private double latitude;
	private ImageButton saveButton;
	private OnFragmentInteractionListener mListener;
	private ImageView categoryIcon;
	private Event mActiveEvent;
	private View mRootView;

	public DetailsFragment() {
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment, using the provided {@link Event}.
	 *
	 * @param event An {@link Event}.
	 * @return A new instance of DetailsFragment.
	 */
	public static DetailsFragment newInstance(Event event) {
		DetailsFragment fragment = new DetailsFragment();
		Bundle args = new Bundle();
		args.putString(ARG_ID, String.valueOf(event.getID()));
		EVENT_ID = String.valueOf(event.getID());
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		App app = App.getInstance();

		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mActiveEvent = AppDatabase.getDatabaseInstance().
				getEvent(Integer.parseInt(EVENT_ID));
			id = mActiveEvent.getID();
			address = mActiveEvent.getAddress();
			time = mActiveEvent.getDateInformation();
			contact = mActiveEvent.getContactInformation();
			if (app.needTranslation(mActiveEvent)) {
				Event.Translation result = app.translateEvent(mActiveEvent);
				title = result.getTitle();
				description = result.getDescription();
			}
			else {
				title = mActiveEvent.getTitle(app.getLocaleCode());
				description = mActiveEvent.getDescription(app.getLocaleCode());
			}
			longitude = mActiveEvent.getLongitude();
			latitude = mActiveEvent.getLatitude();
		}
	}

	private static int[] DRAWABLE_IDS = {
		R.drawable.marker0, R.drawable.marker1,
		R.drawable.marker2, R.drawable.marker3
	};

	private void setCategoryIcon() {
		int id = mActiveEvent.getCategories()[0];
		if (id < 0 || id > 3) id = 0;
		categoryIcon.setImageDrawable(getResources().
			getDrawable(DRAWABLE_IDS[id]));
	}

	@Override public View onCreateView(LayoutInflater inflater,
		ViewGroup container, Bundle savedInstanceState)
	{
		mRootView = inflater.inflate(R.layout.fragment_detail,
			container, false);
		setUpSaveButton();
		setUpDirectionButton();
		repaint();
		setCategoryIcon();
		return mRootView;
	}

	/**
	 * Updates the visual aspects, in case one of
	 * the displayed values has changed
	 */
	private void repaint() {
		categoryIcon = (ImageView) mRootView.findViewById(R.id.detail_baloon);
		((TextView)mRootView.findViewById(R.id.detail_title)).
			setText(title);
		((TextView)mRootView.findViewById(R.id.detail_adress_event)).
			setText(address);
		((TextView)mRootView.findViewById(R.id.detal_time_event)).
			setText(time);
		((TextView)mRootView.findViewById(R.id.detail_contact_event)).
			setText(contact);
		((TextView)mRootView.findViewById(R.id.detail_description_event)).
			setText(description);
	}

	private void setUpDirectionButton()
	{
		val mDirectionButton = (ImageButton)mRootView.
			findViewById(R.id.directionButton);
		mDirectionButton.setOnClickListener(new View.OnClickListener()
			{
				@Override public void onClick(View view)
				{
					LatLng destination = new LatLng(latitude, longitude);
					String transportationMode = "walking";
					mListener.onDirectionButtonPressed(destination,
						transportationMode);
				}
			}
		);
	}

	/** Author: Jonathan S */
	private void setUpSaveButton() {

		saveButton = (ImageButton) mRootView.findViewById(R.id.saveButton);

		if (mListener.isEventSaved((String.valueOf(id)))) {
			saveButton.setBackgroundResource(R.drawable.ic_star_yellow_button);
		} else {
			saveButton.setBackgroundResource(R.drawable.ic_star_black_button);
		}
		saveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onButtonPressed(getString(R.string.save_event_button_clicked_key));
			}
		});
	}
	/** Author: Jonathan S*/
	private void onButtonPressed(String action)
	{
		if (mListener != null &&
			action.equals(getString(R.string.save_event_button_clicked_key)))
		{

			boolean actionSuccessful =
				mListener.onSaveEventButtonPressed(String.valueOf(id));

			if (actionSuccessful)
			{
				mListener.updateSavedEventsFrag();

				if (mListener.isEventSaved(String.valueOf(id)))
				{
					saveButton.setBackgroundResource(R.drawable.
						ic_star_yellow_button);
					Toast.makeText(getActivity().getApplicationContext(),
						R.string.toast_detail_event_saved,
						Toast.LENGTH_SHORT).show();
				}

				else
				{
					saveButton.setBackgroundResource(R.drawable.
						ic_star_black_button);
					Toast.makeText(getActivity().getApplicationContext(),
						R.string.toast_detail_event_removed,
						Toast.LENGTH_SHORT).show();
				}
			}

			else Toast.makeText(getActivity().getApplicationContext(),
				R.string.toast_detail_action_failed,
				Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
				+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 * See the Android training lesson "Communicating
	 * with Other Fragments" for more information.
	 *
	 * @author Jonathan S */
	public interface OnFragmentInteractionListener {
		boolean onSaveEventButtonPressed(String id);
		boolean isEventSaved(String id);
		void onDirectionButtonPressed(LatLng destination,
			String transportationMode);
		void updateSavedEventsFrag();
	}
}
