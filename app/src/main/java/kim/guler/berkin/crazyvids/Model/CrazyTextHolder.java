package kim.guler.berkin.crazyvids.Model;

import android.content.Context;

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


}
