package kim.guler.berkin.crazyvids.Controller;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;

import kim.guler.berkin.crazyvids.Model.CrazyText;
import kim.guler.berkin.crazyvids.Model.CrazyTextHolder;
import kim.guler.berkin.crazyvids.R;
import kim.guler.berkin.crazyvids.View.MainVideoActivity;

/**
 * Created by Berkin on 15.03.2018.
 */

public class VideoViewController implements View.OnTouchListener, Player.EventListener {

    private boolean isVideoComplete = false;
    private MainVideoActivity mainVideoActivity;
    private CrazyText crazyText;
    private CrazyTextHolder crazyTextHolder = CrazyTextHolder.getInstance();

    public VideoViewController(MainVideoActivity mainVideoActivity, String crazyText) {
        this.mainVideoActivity = mainVideoActivity;
        this.crazyText = this.crazyTextHolder.createCrazyText(crazyText, mainVideoActivity);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        PlayerView videoView = (PlayerView) view;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                FrameLayout frameLayout = mainVideoActivity.findViewById(R.id.video_canvas);
                frameLayout.addView(this.crazyText.getCrazyTextView());

                int x = (int) event.getRawX() - this.crazyText.getCrazyTextView().getWidth() / 2;
                int y = (int) event.getRawY() - this.crazyText.getCrazyTextView().getHeight() / 2;

                this.crazyText.getCrazyTextView().setX(x);
                this.crazyText.getCrazyTextView().setY(y);

                this.crazyText.addSample(0, new Point(x, y));

                videoView.getPlayer().addListener(this);

                this.mainVideoActivity.playVideo(true);

                return true;
            case MotionEvent.ACTION_MOVE:
                if (!isVideoComplete) {
                    int curx = (int) event.getRawX() - this.crazyText.getCrazyTextView().getWidth() / 2;
                    int cury = (int) event.getRawY() - this.crazyText.getCrazyTextView().getHeight() / 2;

                    this.crazyText.getCrazyTextView().animate().x(curx).y(cury).setDuration(0).start();
                    this.crazyText.addSample((int) videoView.getPlayer().getCurrentPosition() / 25, new Point(curx, cury));

                    return true;
                } else {
                    this.mainVideoActivity.pauseVideo(true);
                    videoView.setOnTouchListener((v, motionEvent) -> false);
                    return false;
                }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                this.mainVideoActivity.pauseVideo(true);
                videoView.setOnTouchListener((v, motionEvent) -> false);
                return false;
            default:
                return true;
        }
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == Player.STATE_ENDED)
            this.isVideoComplete = true;
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }
}
