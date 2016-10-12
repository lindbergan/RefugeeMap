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
    private static final String ARG_TITLE = "title";
    private static final String ARG_ADRESS = "adress";
    private static final String ARG_CONTACT = "phoneNumber";
    private static final String ARG_DESCRIPTION = "description";
    private static final String ARG_LONGITUDE = "longitude";
    private static final String ARG_LATITUDE = "latitude";

    private String id;
	private String title;
	private String adress;
	private String contact;
	private String description;
	private String longitude;
    private String latitude;
	private String loremIpsum = "Lorem ipsum dolor sit amet, consectetur " +
		"adipiscing elit. Duis suscipit justo ac metus malesuada aliquet. " +
		"Donec ac arcu nulla. Sed nec pellentesque dui. Maecenas placerat mi " +
		"eget ante sagittis, sit amet commodo lacus mollis. Cras " +
		"consecteturegestas ligula, et malesuada libero auctor at. Cras " +
		"congue " + "lacus a venenatis hendrerit. Quisque vel faucibus ligula.";

	private ImageButton saveButton;
	private OnFragmentInteractionListener mListener;
    private Button mDirectionButton;

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
	 * @param data An array of strings.
	 * @return A new instance of fragment DetailFragment.
	 */
	public static DetailFragment newInstance(String[] data) {
		DetailFragment fragment = new DetailFragment();
		Bundle args = new Bundle();
		args.putString(ARG_ID, data[0]);
		args.putString(ARG_TITLE, data[1]);
		args.putString(ARG_ADRESS, data[2]);
		args.putString(ARG_CONTACT, data[3]);
		args.putString(ARG_DESCRIPTION, data[4]);
        args.putString(ARG_LONGITUDE, data[5]);
        args.putString(ARG_LATITUDE, data[6]);
        fragment.setArguments(args);
		return fragment;
	}

	public void translateEvent() {
		if (getArguments() != null) {
			mActiveEvent = AppDatabase.getDatabaseInstance().getEvent(Integer.valueOf(id));
			if (needTranslation()) {
				HashMap<String, String> mValues = null;
				try {
					mValues = getEventTranslated(toLocale, mActiveEvent.getDescription("sv"));
				} catch (ExecutionException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				title = mValues.get(ARG_TITLE);
				Log.v(DetailFragment.class.getSimpleName(), title);
				description = mValues.get(ARG_DESCRIPTION);
				Log.v(DetailFragment.class.getSimpleName(), description);
			}
			else {
				title = mActiveEvent.getTitle();
				//organization = String.valueOf(mActiveEvent.getOwnerID());
				description = mActiveEvent.getDescription("sv");
				contact = mActiveEvent.getContactInformation();
				id = String.valueOf(mActiveEvent.getID());
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
		mValues.put(ARG_TITLE, title);
		mValues.put(ARG_DESCRIPTION, description);
		mValues.put(ARG_ID, id);
		return urlConnectionHelper.getTranslatedEventDescription(mValues);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
            id = getArguments().getString(ARG_ID);
            title = getArguments().getString(ARG_TITLE);
            adress = getArguments().getString(ARG_ADRESS);
            contact = getArguments().getString(ARG_CONTACT);
            description = getArguments().getString(ARG_DESCRIPTION);
            longitude = getArguments().getString(ARG_LONGITUDE);
            latitude = getArguments().getString(ARG_LATITUDE);
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

		((TextView) mRootView.findViewById(R.id.detail_title)).setText(title);
		((TextView) mRootView.findViewById(R.id.detail_adress_event)).setText(adress);
		((TextView) mRootView.findViewById(R.id.detail_contact_event)).setText(contact);
		((TextView) mRootView.findViewById(R.id.detail_description_event)).setText(description);

		//NOTE: Remove later on, when detailed view is confirmed working:
		((TextView) mRootView.findViewById(R.id.detail_description_event)).setText(loremIpsum);

		saveButton = (ImageButton) mRootView.findViewById(R.id.saveButton);
		setUpSaveButton();
        mDirectionButton = (Button) mRootView.findViewById(R.id.directionButton);
		setUpDirectionButton();

		repaint();
		return mRootView;
	}

	public void repaint() {
		((TextView) mRootView.findViewById(R.id.detail_title)).setText(title);
		//((TextView) mRootView.findViewById(R.id.organizationTextView)).setText(organization);
		((TextView) mRootView.findViewById(R.id.detail_description_event)).setText(description);
		((TextView) mRootView.findViewById(R.id.detail_contact_event)).setText(contact);
		//((TextView) mRootView.findViewById(R.id.dateTextView)).setText(date);
		saveButton = (ImageButton) mRootView.findViewById(R.id.saveButton);
		setUpSaveButton();
	}

	public void setUpDirectionButton(){

        mDirectionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				LatLng origin = new LatLng(57.70, 11.97);//mGoogleAPIHelper.getCurrentLocation(); Should be our current pos
				LatLng destination = new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));
				String transportationMode ="walking";

                mListener.DirectionButtonPressed(origin,destination,transportationMode);

			}
		});
	}

	public void setUpSaveButton() {
		if (mListener.isEventSaved(this.id)) {
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

			boolean actionSuccessful = mListener.onSaveEventButtonPressed(this.id);

			if (actionSuccessful) {

				mListener.updateSavedEventsFrag();

				if (mListener.isEventSaved(this.id)) {

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
        void DirectionButtonPressed(LatLng origin, LatLng destination, String transportationMode);
		void updateSavedEventsFrag();
	}
}
