package kim.guler.berkin.crazyvids.Model;

import android.content.Context;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Berkin on 14.03.2018.
 */

public class CrazyTextHolder {
    private static final CrazyTextHolder ourInstance = new CrazyTextHolder();

    private List<CrazyText> crazyTextList = new ArrayList<>();

    private CrazyTextHolder() {
    }

    public static CrazyTextHolder getInstance() {
        return ourInstance;
    }

    public List<CrazyText> getCrazyTextList() {
        return this.crazyTextList;
    }

    public CrazyText createCrazyText(String text, Context context) {
        CrazyText crazyText = new CrazyText(text, context);
        this.crazyTextList.add(crazyText);
        return crazyText;
    }

    public TextView removeCrazyText(String crazyText) {
        CrazyText remove = null;
        for (CrazyText ct : this.crazyTextList) {
            if (ct.toString().equals(crazyText))
                remove = ct;
        }
        if (remove != null) {
            this.crazyTextList.remove(remove);
            return remove.getCrazyTextView();
        } else
            return null;
    }


}
