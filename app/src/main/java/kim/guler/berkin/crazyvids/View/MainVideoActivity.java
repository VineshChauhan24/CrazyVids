package kim.guler.berkin.crazyvids.View;

import android.graphics.Point;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;

import java.util.ArrayList;
import java.util.List;

import kim.guler.berkin.crazyvids.Controller.MainVideoButtonController;
import kim.guler.berkin.crazyvids.Controller.VideoSeekBarProgressController;
import kim.guler.berkin.crazyvids.Controller.VideoViewController;
import kim.guler.berkin.crazyvids.Model.CrazyText;
import kim.guler.berkin.crazyvids.Model.CrazyTextHolder;
import kim.guler.berkin.crazyvids.R;

public class MainVideoActivity extends AppCompatActivity {

    public static final int SAMPLING_RATE = 30;

    private PlayerView videoView;
    private Button playPauseButton, addTextButton, saveButton, deleteCrazyTextButton;
    private SeekBar videoSeekBar;
    private RelativeLayout deleteTextsLayout;
    private Spinner crazyTextsSpinner;
    private FrameLayout videoCanvas;

    private CrazyTextHolder crazyTextHolder = CrazyTextHolder.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_video);

        this.assignViews();
        this.enableUserVideoTracking();

        MainVideoButtonController mainVideoButtonController = new MainVideoButtonController(this);

        this.playPauseButton.setOnClickListener(mainVideoButtonController);
        this.addTextButton.setOnClickListener(mainVideoButtonController);
        this.saveButton.setOnClickListener(mainVideoButtonController);
        this.deleteCrazyTextButton.setOnClickListener(mainVideoButtonController);
        this.videoSeekBar.setOnSeekBarChangeListener(new VideoSeekBarProgressController(this));

        this.loadVideo();

    }

    private void disableUserVideoTracking() {
        this.videoSeekBar.setOnTouchListener((view, motionEvent) -> true);
    }

    private void enableUserVideoTracking() {
        this.videoSeekBar.setOnTouchListener((view, motionEvent) -> false);
    }

    private void assignViews() {
        this.playPauseButton = findViewById(R.id.play_pause_button);
        this.addTextButton = findViewById(R.id.add_text_button);
        this.saveButton = findViewById(R.id.save_button);
        this.videoView = findViewById(R.id.video_player);
        this.videoSeekBar = findViewById(R.id.video_seek_bar);
        this.deleteTextsLayout = findViewById(R.id.delete_texts_holder_layout);
        this.deleteCrazyTextButton = findViewById(R.id.delete_button);
        this.crazyTextsSpinner = findViewById(R.id.crazy_text_spinner);
        this.videoCanvas = findViewById(R.id.video_canvas);
    }

    /**
     * In this method, the provided mp4 video file is included as a raw resource and therefore
     * loaded into the ExoPlayer accordingly.
     */
    private void loadVideo() {

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        this.videoView.setPlayer(player);

        DataSpec dataSpec = new DataSpec(RawResourceDataSource.buildRawResourceUri(R.raw.video));
        RawResourceDataSource rawResourceDataSource = new RawResourceDataSource(this);
        try {
            rawResourceDataSource.open(dataSpec);
        } catch (RawResourceDataSource.RawResourceDataSourceException ex) {
            ex.printStackTrace();
        }
        DataSource.Factory factory = () -> rawResourceDataSource;
        MediaSource videoSource = new ExtractorMediaSource.Factory(factory).createMediaSource(rawResourceDataSource.getUri());
        player.prepare(videoSource);

        player.addListener(new MediaPlayerListener());

        player.setRepeatMode(Player.REPEAT_MODE_ALL);
    }

    /**
     * This method enables the video to play on the screen either when we are sampling the finger
     * path or when the play button is pressed to preview the video.
     * <p>
     * CountDownTimer implemented in this method takes care of moving the previously saved texts
     * around the video according to their own paths.
     *
     * @param samplingMode true if user is saving a touch path on the video
     */
    public void playVideo(boolean samplingMode) {
        if (!samplingMode) {
            this.playPauseButton.setText(getString(R.string.pause_button_text));
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) this.playPauseButton.getLayoutParams();
            params.removeRule(RelativeLayout.ALIGN_PARENT_START);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            this.playPauseButton.setLayoutParams(params);
            this.addTextButton.setVisibility(View.GONE);
        } else
            this.videoView.getPlayer().setRepeatMode(Player.REPEAT_MODE_OFF);

        this.videoView.getPlayer().setPlayWhenReady(true);
        this.videoSeekBar.postDelayed(new TrackVideoUpdateSeekBarRunnable(), 100);

        if (this.crazyTextHolder.getCrazyTextList().size() > 0) {

            for (CrazyText crazyText : this.crazyTextHolder.getCrazyTextList()) {
                TextView crazyTextView = crazyText.getCrazyTextView();

                new CountDownTimer(this.videoView.getPlayer().getDuration(), SAMPLING_RATE) {

                    @Override
                    public void onTick(long millisUntilFinished) {
                        if (!videoView.getPlayer().getPlayWhenReady())
                            cancel();

                        Point point = crazyText.getSample((int) videoView.getPlayer().getCurrentPosition() / SAMPLING_RATE);
                        if (point != null) {
                            crazyTextView.animate().x(point.x).y(point.y).setDuration(0).start();
                        }
                    }

                    @Override
                    public void onFinish() {
                        if (videoView.getPlayer().getPlayWhenReady())
                            this.start();
                    }
                }.start();
            }
        }
    }

    /**
     * This method pauses the video on the screen either when the user is done with touching the
     * screen or when the play button is pressed to preview the video.
     * @param samplingMode true if user was saving a touch path on the video.
     */
    public void pauseVideo(boolean samplingMode) {
        if (!samplingMode) {
            this.playPauseButton.setText(getString(R.string.play_button_text));
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) this.playPauseButton.getLayoutParams();
            params.removeRule(RelativeLayout.CENTER_HORIZONTAL);
            params.addRule(RelativeLayout.ALIGN_PARENT_START);
            this.playPauseButton.setLayoutParams(params);
            this.addTextButton.setVisibility(View.VISIBLE);
        } else
            this.videoView.getPlayer().setRepeatMode(Player.REPEAT_MODE_ALL);
        this.videoView.getPlayer().setPlayWhenReady(false);
    }

    public void showAddTextDialog() {
        AlertDialog.Builder inputTextDialogBuilder = new AlertDialog.Builder(this);
        inputTextDialogBuilder.setTitle("Add Text");
        final EditText inputText = new EditText(this);
        inputText.setInputType(InputType.TYPE_CLASS_TEXT);
        inputTextDialogBuilder.setView(inputText);
        inputTextDialogBuilder.setPositiveButton("OK", (dialog, which) -> addText(inputText.getText().toString()));
        inputTextDialogBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        inputTextDialogBuilder.show();
    }

    /**
     * Called from the text input dialog when the OK button is clicked. This method switches the
     * screen from state 1 to 2.
     *
     * @param text text entered into the text input dialog
     */
    private void addText(String text) {
        if (text.isEmpty())
            return;

        this.playPauseButton.setVisibility(View.GONE);
        this.addTextButton.setVisibility(View.GONE);
        this.saveButton.setVisibility(View.VISIBLE);

        this.videoView.getPlayer().seekTo(0);
        this.videoSeekBar.setProgress(0);

        VideoViewController videoViewController = new VideoViewController(this, text);
        this.videoView.setOnTouchListener(videoViewController);

        this.showDeleteTextsLayout(false);
        this.disableUserVideoTracking();
    }

    /**
     * Called when the save button is pressed on the screen 2 which switches the screen back to 1
     * while also saving the previously entered text and taking care of resetting video to its
     * initial position.
     */
    public void saveCrazyText() {
        this.saveButton.setVisibility(View.GONE);
        this.playPauseButton.setVisibility(View.VISIBLE);
        this.addTextButton.setVisibility(View.VISIBLE);
        this.showDeleteTextsLayout(true);

        this.videoView.getPlayer().seekTo(0);
        this.videoSeekBar.setProgress(0);

        for (CrazyText crazyText : this.crazyTextHolder.getCrazyTextList()) {
            TextView crazyTextView = crazyText.getCrazyTextView();
            Point initialPoint = crazyText.getSample(0);
            if (initialPoint != null) {
                crazyTextView.setX(initialPoint.x);
                crazyTextView.setY(initialPoint.y);
            }
        }

        this.refreshCrazyTextSpinner();

        this.showDeleteTextsLayout(true);
        this.enableUserVideoTracking();
    }

    public String getSelectedCrazyText() {
        return this.crazyTextsSpinner.getSelectedItem().toString();
    }

    public void removeCrazyText(TextView crazyTextView) {
        this.videoCanvas.removeView(crazyTextView);
        this.refreshCrazyTextSpinner();
    }

    /**
     * This method populates the spinner if there is any text that can be deleted. Takes care of
     * creating a valid adapter for the spinner basically.
     */
    private void refreshCrazyTextSpinner() {
        if (this.crazyTextHolder.getCrazyTextList().size() == 0) {
            this.showDeleteTextsLayout(false);
            return;
        }

        List<String> spinnerArray = new ArrayList<>();

        for (CrazyText crazyText : this.crazyTextHolder.getCrazyTextList())
            spinnerArray.add(crazyText.toString());

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerArray);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.crazyTextsSpinner.setAdapter(spinnerAdapter);
    }

    public void seekVideoTo(int time) {
        this.videoView.getPlayer().seekTo(time);
        for (CrazyText crazyText : this.crazyTextHolder.getCrazyTextList()) {
            TextView crazyTextView = crazyText.getCrazyTextView();
            Point sample = crazyText.getSample(time / SAMPLING_RATE);
            if (sample != null) {
                crazyTextView.setX(sample.x);
                crazyTextView.setY(sample.y);
            }
        }
    }

    private void showDeleteTextsLayout(boolean show) {
        if (show)
            this.deleteTextsLayout.setVisibility(View.VISIBLE);
        else
            this.deleteTextsLayout.setVisibility(View.GONE);
    }

    /**
     * This class synchronizes the seekbar with the video progress.
     */
    private class TrackVideoUpdateSeekBarRunnable implements Runnable {
        @Override
        public void run() {
            if (videoView.getPlayer().getPlayWhenReady()) {
                videoSeekBar.setProgress((int) videoView.getPlayer().getCurrentPosition());
                videoSeekBar.postDelayed(this, 100);
            }
        }
    }

    /**
     * This class is a listener for the events being fired in the ExoPlayer which enables us to
     * detect the video length so that we can set the seekbar max value.
     */
    private class MediaPlayerListener implements Player.EventListener {

        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
            videoSeekBar.setMax((int) videoView.getPlayer().getDuration());
        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

        }

        @Override
        public void onLoadingChanged(boolean isLoading) {

        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

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
}
