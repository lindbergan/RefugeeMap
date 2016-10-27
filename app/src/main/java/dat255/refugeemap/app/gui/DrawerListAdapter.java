package dat255.refugeemap.app.gui;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import dat255.refugeemap.R;


/**
 * Adapter for the list items in the drawer
 * @author Jonathan S
 */

public class DrawerListAdapter extends BaseAdapter {

	private final Context mContext;
	private final List<String> mNavItems;

	/**
	 * Constructor.
	 * @param context The current context
	 * @param navItems List of strings
	 */
	public DrawerListAdapter(Context context, List<String> navItems){
		mContext = context;
		mNavItems = navItems;
	}

	@Override
	public int getCount() {
		return mNavItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mNavItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;

		if(convertView == null){
			LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.drawer_list_item, null);
		}else{
			view = convertView;
		}
		TextView titleView = (TextView) view.findViewById(R.id.
			drawer_list_item_title);
		ImageView iconView = (ImageView) view.findViewById(R.id.
			drawer_list_item_icon);

		titleView.setText(mNavItems.get(position));

		TypedArray iconsArray = mContext.getResources().obtainTypedArray(R.
			array.drawer_list_items_icons);
		iconView.setImageResource(iconsArray.getResourceId(position,
			R.drawable.ic_logo));
		iconsArray.recycle();

		return view;
	}
}