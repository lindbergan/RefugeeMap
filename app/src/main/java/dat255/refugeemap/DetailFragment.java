package dat255.refugeemap;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import dat255.refugeemap.helpers.UrlConnectionHelper;
import dat255.refugeemap.model.db.Event;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment {
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
    private ImageButton mDirectionButton;
	private String toLocale = App
			.getInstance()
			.getBaseContext()
			.getResources()
			.getConfiguration()
			.locale
			.toString();
	private Event mActiveEvent;
	private View mRootView;

	public DetailFragment() {
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param event An Event.
	 * @return A new instance of fragment DetailFragment.
	 */
	public static DetailFragment newInstance(Event event) {
		DetailFragment fragment = new DetailFragment();
		Bundle args = new Bundle();
		args.putString(ARG_ID, String.valueOf(event.getID()));
		EVENT_ID = String.valueOf(event.getID());
        fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mActiveEvent = AppDatabase.getDatabaseInstance()
				.getEvent(Integer.parseInt(EVENT_ID));
			id = mActiveEvent.getID();
			title = mActiveEvent.getTitle();
			address = mActiveEvent.getAddress();
			time = mActiveEvent.getDateInformation();
			contact = mActiveEvent.getContactInformation();
			description = mActiveEvent.getDescription("sv"); //NOTE: need to be set to currentLanguage
			longitude = mActiveEvent.getLongitude();
			latitude = mActiveEvent.getLatitude();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		if (savedInstanceState == null)
			System.out.println("nullSaved");

		mRootView = inflater.inflate(R.layout.fragment_detail,
			container, false);

		setUpSaveButton();
		setUpDirectionButton();
		repaint();
		return mRootView;
	}

	public void translateEvent() {
		if (getArguments() != null) {
			if (needTranslation()) {
				HashMap<String, String> mValues = null;
				try {
					mValues = getEventTranslated(toLocale, mActiveEvent.getDescription("sv"));
				} catch (ExecutionException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				//title = mValues.get(ARG_TITLE);
				Log.v(DetailFragment.class.getSimpleName(), title);
				//description = mValues.get(ARG_DESCRIPTION);
				Log.v(DetailFragment.class.getSimpleName(), description);
			}
			else {
				title = mActiveEvent.getTitle();
				description = mActiveEvent.getDescription("sv");
				contact = mActiveEvent.getContactInformation();
				id = mActiveEvent.getID();
			}
			repaint();
		}
	}

	public boolean needTranslation() {
		return !mActiveEvent.getAvailableDescriptionLanguages().contains(toLocale);
	}

	public HashMap<String, String> getEventTranslated(String userLocale, String currentTextLocale) throws ExecutionException, InterruptedException {
		UrlConnectionHelper urlConnectionHelper = new UrlConnectionHelper();
		urlConnectionHelper.setFromLocale(currentTextLocale);
		urlConnectionHelper.setToLocale(userLocale);
		HashMap<String, String> mValues = new HashMap<>();
		mValues.put(mActiveEvent.getTitle(), title);
		mValues.put(mActiveEvent.getDescription(toLocale), description);
		mValues.put(ARG_ID, String.valueOf(id));
		return urlConnectionHelper.getTranslatedEventDescription(mValues);
	}

	public void repaint() {
		((TextView) mRootView.findViewById(R.id.detail_title)).setText(title);
		((TextView) mRootView.findViewById(R.id.detail_adress_event)).setText(address);
		((TextView) mRootView.findViewById(R.id.detal_time_event)).setText(time);
		((TextView) mRootView.findViewById(R.id.detail_contact_event)).setText(contact);
		((TextView) mRootView.findViewById(R.id.detail_description_event)).setText(description);
	}

	public void setUpDirectionButton(){

		mDirectionButton = (ImageButton) mRootView.findViewById(R.id.directionButton);

		mDirectionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				LatLng destination = new LatLng(latitude,longitude);
				String transportationMode ="walking";

                mListener.DirectionButtonPressed(destination,transportationMode);

			}
		});
	}

	public void setUpSaveButton() {

		saveButton = (ImageButton) mRootView.findViewById(R.id.saveButton);

		if (mListener.isEventSaved((String.valueOf(id)))) {
			saveButton.setBackgroundResource(R.drawable.ic_remove_circle_black_48dp);
		} else {
			saveButton.setBackgroundResource(R.drawable.ic_add_circle_black_48dp);
		}
		saveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onButtonPressed(getString(R.string.save_event_button_clicked_key));
			}
		});
	}

	public void onButtonPressed(String action) {
		if (mListener != null && action == getString(R.string.save_event_button_clicked_key)) {

			boolean actionSuccessful = mListener.onSaveEventButtonPressed(String.valueOf(id));

			if (actionSuccessful) {

				mListener.updateSavedEventsFrag();

				if (mListener.isEventSaved(String.valueOf(id))) {

					//saveButton.setBackgroundResource(R.drawable.ic_remove_circle_black_48dp);
					Toast.makeText(getActivity().getApplicationContext(), "Event saved", Toast.LENGTH_SHORT).show();
				} else {
					//saveButton.setBackgroundResource(R.drawable.ic_add_circle_black_48dp);
					Toast.makeText(getActivity().getApplicationContext(), "Event removed", Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(getActivity().getApplicationContext(), "Action failed", Toast.LENGTH_SHORT).show();
			}
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
	 * <p/>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener {
		boolean onSaveEventButtonPressed(String id);
		boolean isEventSaved(String id);
        void DirectionButtonPressed(LatLng destination, String transportationMode);
		void updateSavedEventsFrag();
	}
}
