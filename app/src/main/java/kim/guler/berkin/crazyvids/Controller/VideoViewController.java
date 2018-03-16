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

    /**
     * This method implements an onTouch method logic so that we can detect when the user presses
     * on the video view to enter a path for his text. Here we have 3 states:
     * <p>
     * 1- ACTION_DOWN
     * This state means that the user touched the video view for the first time. Therefore, this
     * coordinate (x,y) will be the initial point for the text to appear on the video.
     * <p>
     * 2- ACTION_MOVE
     * If we are receiving actions with this event state, it means that the user is still pressing
     * on the screen so that we need to move the text according to this movement while also sampling
     * these movements to be used later on.
     * However, we also need to check whether the video is ended or not. We can allow user to move
     * on the screen as long as the video continues and whenever the video is finished we have to
     * return false to indicate that we can no longer accept any movement on the screen.
     * <p>
     * 3- ACTION_UP
     * If we are in this state, either the user stopped pressing on the screen indicating that
     * the movement path for this text should only be this much long, or the video is ended and we
     * forced user to stop entering any further path by previously returning false in the previous
     * state.
     *
     * @param view
     * @param event
     * @return
     */
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
                    this.crazyText.addSample((int) videoView.getPlayer().getCurrentPosition() / MainVideoActivity.SAMPLING_RATE, new Point(curx, cury));

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

    /**
     * This method is fired when there is a state chance occurred in the ExoPlayer so by checking
     * if that was a STATE_ENDED switch, then we can say that the video is ended. This is needed by
     * the onTouch() method explained above.
     *
     * @param playWhenReady
     * @param playbackState
     */
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
