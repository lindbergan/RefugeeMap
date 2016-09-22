package dat255.refugeemap;

import java.util.ArrayList;
import java.util.List;

//TODO: Remake specifically for events!
/**
 * Helper class for providing sample content for EventListFragment
 */
public class StaticContent {
  /**
   * An list of sample (dummy) items.
   */
  private static final List<StaticItem> EVENTS = new ArrayList<StaticItem>();

  private static final int COUNT = 25;

  static {
    // Add some sample items.
    for (int i = 1; i <= COUNT; i++) {
      addItem(createStaticItem(i));
    }
  }

  private static void addItem(StaticItem item) {
        EVENTS.add(item);
    }

  private static StaticItem createStaticItem(int position) {
    return new StaticItem("Item " + position, "Address", "Contact", "Description", "Category",
            new String[] {"Tag1", "Tag2"});
  }

  /**
   * A static dummy item representing a piece of content.
   */
  public static class StaticItem {
    public final String title;
    public final String address;
    public final String contact;
    public final String description;
    public final String category;
    public final String[] tags;

    public StaticItem(String title, String address, String contact, String description,
                      String category, String[] tags) {
      this.title = title;
      this.address = address;
      this.contact = contact;
      this.description = description;
      this.category = category;
      this.tags = tags;
    }

    @Override
    public String toString() {
            return title;
        }
  }

  public static List<StaticItem> getEvents(){

    return EVENTS;
  }
}
