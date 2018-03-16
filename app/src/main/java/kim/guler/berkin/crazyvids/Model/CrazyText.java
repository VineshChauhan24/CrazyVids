package kim.guler.berkin.crazyvids.Model;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Random;

/**
 * Created by Berkin on 15.03.2018.
 */

/**
 * The model object for the texts entered and moved around by the user. There are only two fields
 * to be mentioned here. The first one is a simple TextView field representing the text and the
 * other crucial field is the SparseArray holding the (time, Point) pairs for that given text.
 * So when a sampling is made, the resuling pair (time, Point) is put into the "path" field and then
 * read back when the video is played / previewed.
 */
public class CrazyText {

    private TextView crazyTextView;
    private SparseArray<Point> path;

    CrazyText(String text, Context context) {
        this.crazyTextView = new TextView(context);
        this.crazyTextView.setText(text);
        this.crazyTextView.setTextColor(getRandomColor());
        this.crazyTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40);
        this.crazyTextView.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        this.crazyTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        this.path = new SparseArray<>();
    }

    private static int getRandomColor() {
        Random random = new Random();
        return Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    public void addSample(int time, Point point) {
        this.path.put(time, point);
    }

    public TextView getCrazyTextView() {
        return this.crazyTextView;
    }

    public Point getSample(int time) {
        return this.path.get(time);
    }

    @Override
    public String toString() {
        return this.crazyTextView.getText().toString();
    }
}
