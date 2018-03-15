package kim.guler.berkin.crazyvids.Controller;

import android.view.View;

import kim.guler.berkin.crazyvids.R;
import kim.guler.berkin.crazyvids.View.MainVideoActivity;

/**
 * Created by Berkin on 14.03.2018.
 */

public class MainVideoButtonController implements View.OnClickListener {

    private MainVideoActivity mainVideoActivity;
    private boolean isPlaying = false;

    public MainVideoButtonController(MainVideoActivity mainVideoActivity) {
        this.mainVideoActivity = mainVideoActivity;
    }

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
        }

    }
}
