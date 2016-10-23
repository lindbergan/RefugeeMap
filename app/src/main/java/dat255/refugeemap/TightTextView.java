package dat255.refugeemap;


import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;
import android.widget.TextView;

/*
 * Courtesy: http://stackoverflow.com/questions/10913384/how-to-make-textview-wrap-its-multiline-content-exactly
 * Tightly wraps the text when setting the maxWidth.
 * @author sky
 */

/**
 * An improved {@link TextView} with better line-wrapping
 */
public class TightTextView extends TextView {
    private boolean hasMaxWidth;

  /**
   * Constructor with default {@link AttributeSet} and default style
   * @param context the current {@link Context} of the app
   */
  public TightTextView(Context context) {
        this(context, null, 0);
    }

  /**
   * Constructor with default default style
   * @param context the current {@link Context} of the app
   * @param attrs the desired {@link AttributeSet}
   */
    public TightTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

  /**
   * Constructor.
   * @param context the current {@link Context} of the app
   * @param attrs the desired {@link AttributeSet}
   * @param defStyle the desired default style
   */
    public TightTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (hasMaxWidth) {
            int specModeW = MeasureSpec.getMode(widthMeasureSpec);
            if (specModeW != MeasureSpec.EXACTLY) {
                Layout layout = getLayout();
                int linesCount = layout.getLineCount();
                if (linesCount > 1) {
                    float textRealMaxWidth = 0;
                    for (int n = 0; n < linesCount; ++n) {
                        textRealMaxWidth = Math.max(textRealMaxWidth, layout.getLineWidth(n));
                    }
                    int w = Math.round(textRealMaxWidth);
                    if (w < getMeasuredWidth()) {
                        super.onMeasure(MeasureSpec.makeMeasureSpec(w, MeasureSpec.AT_MOST),
                            heightMeasureSpec);
                    }
                }
            }
        }
    }


    @Override
    public void setMaxWidth(int maxpixels) {
        super.setMaxWidth(maxpixels);
        hasMaxWidth = true;
    }

    @Override
    public void setMaxEms(int maxems) {
        super.setMaxEms(maxems);
        hasMaxWidth = true;
    }
}