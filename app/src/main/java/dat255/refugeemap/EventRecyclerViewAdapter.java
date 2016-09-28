package dat255.refugeemap;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import dat255.refugeemap.EventListFragment.OnListFragmentInteractionListener;
import dat255.refugeemap.StaticContent.StaticItem;
import dat255.refugeemap.model.db.Event;
import dat255.refugeemap.model.db.EventCollection;
import dat255.refugeemap.model.db.impl.EventList;

/**
 * {@link RecyclerView.Adapter} that can display a {@link dat255.refugeemap.model.db.Event} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class EventRecyclerViewAdapter
	extends RecyclerView.Adapter<EventRecyclerViewAdapter.ViewHolder> {

	private final EventCollection mEvents;
	private final OnListFragmentInteractionListener mListener;

	public EventRecyclerViewAdapter(EventCollection events,
									OnListFragmentInteractionListener listener) {
		mEvents = events;
		mListener = listener;
	}


	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext())
			.inflate(R.layout.fragment_event_item, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {
		holder.mItem = mEvents.get(position);
		holder.mIdView.setText(mEvents.get(position).getTitle());
		//holder.mContentView.setText(mEvents.get(position).getDescription());
		holder.mView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (null != mListener) {
					// Notify the active callbacks interface (the activity, if the
					// fragment is attached to one) that an item has been selected.
					mListener.onListFragmentInteraction(holder.mItem);
				}
			}
		});
	}

	@Override
	public int getItemCount() {
		return mEvents.getSize();
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		public final View mView;
		public final TextView mIdView;
		public final TextView mContentView;
		public Event mItem;

		public ViewHolder(View view) {
			super(view);
			mView = view;
			mIdView = (TextView) view.findViewById(R.id.id);
			mContentView = (TextView) view.findViewById(R.id.content);
		}
		@Override
		public String toString() {
			return super.toString() + " '" + mContentView.getText() + "'";
		}
	}
}
