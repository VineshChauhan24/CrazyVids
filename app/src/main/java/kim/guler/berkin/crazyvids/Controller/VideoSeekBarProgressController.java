package kim.guler.berkin.crazyvids.Controller;

import android.widget.SeekBar;

import kim.guler.berkin.crazyvids.View.MainVideoActivity;

/**
 * Created by Berkin on 14.03.2018.
 */

public class VideoSeekBarProgressController implements SeekBar.OnSeekBarChangeListener {

    private MainVideoActivity mainVideoActivity;

    public VideoSeekBarProgressController(MainVideoActivity mainVideoActivity) {
        this.mainVideoActivity = mainVideoActivity;
    }

    /**
     * Just a basic controller letting user to seek video to wherever desired
     *
     * @param seekBar
     * @param progress
     * @param fromUser
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!fromUser)
            return;

        this.mainVideoActivity.seekVideoTo(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
//        this.mainVideoActivity.seekVideoTo(seekBar.getProgress());
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
//        this.mainVideoActivity.seekVideoTo(seekBar.getProgress());
    }
}
