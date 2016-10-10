package dat255.refugeemap;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
	private static final String ARG_TITLE = "title";
	private static final String ARG_ORGANIZATION = "organization";
	private static final String ARG_DESCRIPTION = "description";
	private static final String ARG_PHONENBR = "phoneNumber";
	private static final String ARG_DATE = "date";
    private static final String ARG_ID = "id";

	private String title;
	private String organization;
	private String description;
	private String phoneNumber;
	private String date;
    private String id;

	private ImageButton saveButton;
	private OnFragmentInteractionListener mListener;


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
		args.putString(ARG_TITLE, data[0]);
		args.putString(ARG_ORGANIZATION, data[1]);
		args.putString(ARG_DESCRIPTION, data[2]);
		args.putString(ARG_PHONENBR, data[3]);
		args.putString(ARG_DATE, data[4]);
        args.putString(ARG_ID, data[5]);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			title = getArguments().getString(ARG_TITLE);
			organization = getArguments().getString(ARG_ORGANIZATION);
			description = getArguments().getString(ARG_DESCRIPTION);
			phoneNumber = getArguments().getString(ARG_PHONENBR);
			date = getArguments().getString(ARG_DATE);
            id = getArguments().getString(ARG_ID);

		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		if (savedInstanceState == null)
			System.out.println("nullSaved");
		View rootView = inflater.inflate(R.layout.fragment_detail,
			container, false);
		((TextView) rootView.findViewById(R.id.titleTextView)).setText(title);
		((TextView) rootView.findViewById(
			R.id.organizationTextView)).setText(organization);
		((TextView) rootView.findViewById(R.id.descTextView)).setText(description);
		((TextView) rootView.findViewById(R.id.phoneTextView)).setText(phoneNumber);
		((TextView) rootView.findViewById(R.id.dateTextView)).setText(date);
        saveButton = (ImageButton) rootView.findViewById(R.id.saveButton);

		setUpSaveButton();

		return rootView;
	}


	public void setUpSaveButton(){
		if(mListener.isEventSaved(this.id)){
			saveButton.setBackgroundResource(R.drawable.ic_remove_circle_black_48dp);
		}else{
			saveButton.setBackgroundResource(R.drawable.ic_add_circle_black_48dp);
		}
		saveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onButtonPressed(getString(R.string.save_event_button_clicked));
			}
		});
	}

	public void onButtonPressed(String action) {
        if(mListener != null && action == getString(R.string.save_event_button_clicked)) {

            boolean actionSuccessful = mListener.onSaveEventButtonPressed(this.id);

				if(actionSuccessful){

					mListener.updateSavedEventsFrag();

					if(mListener.isEventSaved(this.id)){

                        saveButton.setBackgroundResource(R.drawable.ic_remove_circle_black_48dp);
					    Toast.makeText(getActivity().getApplicationContext(), "Event saved", Toast.LENGTH_SHORT).show();
                    }else{
						saveButton.setBackgroundResource(R.drawable.ic_add_circle_black_48dp);
						Toast.makeText(getActivity().getApplicationContext(), "Event removed", Toast.LENGTH_SHORT).show();
					}
				}else{
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
		void updateSavedEventsFrag();
	}

}


/*
  @Override
  public void onBackPressed(){
    Fragment fragment = FragmentManager().findFragmentByTag("DETAIL_FRAGMENT");
    if(getFragmentManager().getBackStackEntryCount() == 0)
      super.onBackPressed();
    else if(fragment != null && fragment.isVisible()){
      getFragmentManager().popBackStack();
    }else
      super.onBackPressed();
  }
*/

/*
 {
   fm.replace(android.R.id.content, myFragment, "DETAIL_FRAGMENT"); //String res.
 }
 */
