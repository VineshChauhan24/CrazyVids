package kim.guler.berkin.crazyvids.Controller;

import android.view.View;
import android.widget.TextView;

import kim.guler.berkin.crazyvids.Model.CrazyTextHolder;
import kim.guler.berkin.crazyvids.R;
import kim.guler.berkin.crazyvids.View.MainVideoActivity;

/**
 * Created by Berkin on 14.03.2018.
 */

public class MainVideoButtonController implements View.OnClickListener {

    private MainVideoActivity mainVideoActivity;
    private boolean isPlaying = false;
    private CrazyTextHolder crazyTextHolder = CrazyTextHolder.getInstance();

    public MainVideoButtonController(MainVideoActivity mainVideoActivity) {
        this.mainVideoActivity = mainVideoActivity;
    }

    /**
     * Decides what to do depending on the button pressed by the user
     *
     * @param view
     */

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.play_pause_button:
                if (isPlaying) {
                    this.mainVideoActivity.pauseVideo(false);
                    this.isPlaying = false;
                } else {
                    this.mainVideoActivity.playVideo(false);
                    this.isPlaying = true;
                }
                break;
            case R.id.add_text_button:
                this.mainVideoActivity.showAddTextDialog();
                break;
            case R.id.save_button:
                this.mainVideoActivity.saveCrazyText();
                break;
            case R.id.delete_button:
                String crazyText = this.mainVideoActivity.getSelectedCrazyText();
                TextView crazyTextView = this.crazyTextHolder.removeCrazyText(crazyText);
                if (crazyTextView != null)
                    this.mainVideoActivity.removeCrazyText(crazyTextView);
                break;
        }

    }
}
