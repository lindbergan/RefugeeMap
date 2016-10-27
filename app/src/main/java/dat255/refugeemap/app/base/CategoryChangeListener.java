package dat255.refugeemap.app.base;

/**
 * Interface for listening to the changing of selected categories.
 */
public interface CategoryChangeListener {
	/**
	 * Should be called when filtered categories have changed.
	 */
	void onCategoryChange();
}