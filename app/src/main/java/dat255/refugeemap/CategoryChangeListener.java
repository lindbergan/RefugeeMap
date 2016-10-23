package dat255.refugeemap;

import java.util.List;

/**
 * Interface for listening the changing categories
 */
public interface CategoryChangeListener {
	/**
	 * Should be called when the filtered categories have changed
	 * @param activeCategory the currently active category
   */
	void onCategoryChange(int activeCategory);
}
