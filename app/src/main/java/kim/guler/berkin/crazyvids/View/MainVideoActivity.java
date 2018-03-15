package kim.guler.berkin.crazyvids.View;

import android.graphics.Point;
import android.media.MediaPlayer;
import android.net.Uri;
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
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.List;

import kim.guler.berkin.crazyvids.Controller.MainVideoButtonController;
import kim.guler.berkin.crazyvids.Controller.VideoViewController;
import kim.guler.berkin.crazyvids.Model.CrazyText;
import kim.guler.berkin.crazyvids.Model.CrazyTextHolder;
import kim.guler.berkin.crazyvids.R;

public class MainVideoActivity extends AppCompatActivity {

    private VideoView videoView;
    private MediaPlayer videoViewMediaPlayer;
    private Button playPauseButton, addTextButton, saveButton, deleteCrazyTextButton;
    private SeekBar videoSeekBar;
    private RelativeLayout deleteTextsLayout;
    private Spinner crazyTextsSpinner;
    private FrameLayout videoCanvas;

    private MainVideoButtonController mainVideoButtonController;
    private VideoViewController videoViewController;

    private CrazyTextHolder crazyTextHolder = CrazyTextHolder.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_video);

        this.assignViews();
        this.disableUserTracking();

        this.mainVideoButtonController = new MainVideoButtonController(this);

        this.playPauseButton.setOnClickListener(this.mainVideoButtonController);
        this.addTextButton.setOnClickListener(this.mainVideoButtonController);
        this.saveButton.setOnClickListener(this.mainVideoButtonController);
        this.deleteCrazyTextButton.setOnClickListener(this.mainVideoButtonController);

        this.loadVideo();

    }

    private void disableUserTracking() {
        this.videoSeekBar.setOnTouchListener((view, motionEvent) -> true);
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

    private void loadVideo() {
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video);
        this.videoView.setVideoURI(videoUri);
        MediaPlayer mp = MediaPlayer.create(this, videoUri);
        this.videoSeekBar.setMax(mp.getDuration());
        mp.release();
        this.videoView.setOnPreparedListener(mediaPlayer -> {
            this.videoViewMediaPlayer = mediaPlayer;
            this.videoViewMediaPlayer.setLooping(true);
            this.videoViewMediaPlayer.seekTo(100);
        });
    }

    public void playVideo(boolean samplingMode) {
        if (!samplingMode) {
            this.playPauseButton.setText(getString(R.string.pause_button_text));
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) this.playPauseButton.getLayoutParams();
            params.removeRule(RelativeLayout.ALIGN_PARENT_START);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            this.playPauseButton.setLayoutParams(params);
            this.addTextButton.setVisibility(View.GONE);
        } else
            this.videoViewMediaPlayer.setLooping(false);

        this.videoView.start();
        this.videoSeekBar.postDelayed(new TrackVideoUpdateSeekBarRunnable(), 100);

        if (this.crazyTextHolder.getCrazyTextList().size() > 0) {

            for (CrazyText crazyText : this.crazyTextHolder.getCrazyTextList()) {
                TextView crazyTextView = crazyText.getCrazyTextView();

                new CountDownTimer(this.videoView.getDuration(), 30) {

                    @Override
                    public void onTick(long millisUntilFinished) {
                        Point point = crazyText.getSample(videoView.getCurrentPosition() / 25);
                        if (point != null) {
                            crazyTextView.animate().x(point.x).y(point.y).setDuration(0).start();
                        }
                    }

                    @Override
                    public void onFinish() {
                        if (videoView.isPlaying())
                            this.start();
                    }
                }.start();
            }
        }
    }

    public void pauseVideo(boolean samplingMode) {
        if (!samplingMode) {
            this.playPauseButton.setText(getString(R.string.play_button_text));
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) this.playPauseButton.getLayoutParams();
            params.removeRule(RelativeLayout.CENTER_HORIZONTAL);
            params.addRule(RelativeLayout.ALIGN_PARENT_START);
            this.playPauseButton.setLayoutParams(params);
            this.addTextButton.setVisibility(View.VISIBLE);
        } else
            this.videoViewMediaPlayer.setLooping(true);
        this.videoView.pause();
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

    private void addText(String text) {
        if (text.isEmpty())
            return;

        this.playPauseButton.setVisibility(View.GONE);
        this.addTextButton.setVisibility(View.GONE);
        this.saveButton.setVisibility(View.VISIBLE);

        this.videoView.seekTo(0);
        this.videoSeekBar.setProgress(0);

        this.videoViewController = new VideoViewController(this, text);
        this.videoView.setOnTouchListener(this.videoViewController);

        this.showDeleteTextsLayout(false);
    }

    public void saveCrazyText() {
        this.saveButton.setVisibility(View.GONE);
        this.playPauseButton.setVisibility(View.VISIBLE);
        this.addTextButton.setVisibility(View.VISIBLE);
        this.showDeleteTextsLayout(true);

        this.videoView.seekTo(0);
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
    }

    public String getSelectedCrazyText() {
        return this.crazyTextsSpinner.getSelectedItem().toString();
    }

    public void removeCrazyText(TextView crazyTextView) {
        this.videoCanvas.removeView(crazyTextView);
        this.refreshCrazyTextSpinner();
    }

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
        this.videoView.seekTo(time);
    }

    private void showDeleteTextsLayout(boolean show) {
        if (show)
            this.deleteTextsLayout.setVisibility(View.VISIBLE);
        else
            this.deleteTextsLayout.setVisibility(View.GONE);
    }

    private class TrackVideoUpdateSeekBarRunnable implements Runnable {
        @Override
        public void run() {
            if (videoView.isPlaying()) {
                videoSeekBar.setProgress(videoView.getCurrentPosition());
                videoSeekBar.postDelayed(this, 100);
            }
        }
    }
}
