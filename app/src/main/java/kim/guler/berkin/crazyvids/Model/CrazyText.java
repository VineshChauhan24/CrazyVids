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
