package kim.guler.berkin.crazyvids.Controller;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.VideoView;

import kim.guler.berkin.crazyvids.Model.CrazyText;
import kim.guler.berkin.crazyvids.Model.CrazyTextHolder;
import kim.guler.berkin.crazyvids.R;
import kim.guler.berkin.crazyvids.View.MainVideoActivity;

/**
 * Created by Berkin on 15.03.2018.
 */

public class VideoViewController implements View.OnTouchListener, MediaPlayer.OnCompletionListener {

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
        Log.d("ahmet", MotionEvent.actionToString(event.getAction()));
        VideoView videoView = (VideoView) view;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                FrameLayout frameLayout = mainVideoActivity.findViewById(R.id.video_canvas);
                frameLayout.addView(this.crazyText.getCrazyTextView());

                int x = (int) event.getRawX() - this.crazyText.getCrazyTextView().getWidth() / 2;
                int y = (int) event.getRawY() - this.crazyText.getCrazyTextView().getHeight() / 2;

                this.crazyText.getCrazyTextView().setX(x);
                this.crazyText.getCrazyTextView().setY(y);

                this.crazyText.addSample(0, new Point(x, y));

                videoView.setOnCompletionListener(this);

                this.mainVideoActivity.playVideo(true);

                return true;
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_MOVE:
                if (!isVideoComplete) {
                    int curx = (int) event.getRawX() - this.crazyText.getCrazyTextView().getWidth() / 2;
                    int cury = (int) event.getRawY() - this.crazyText.getCrazyTextView().getHeight() / 2;

                    this.crazyText.getCrazyTextView().animate().x(curx).y(cury).setDuration(0).start();
                    this.crazyText.addSample(videoView.getCurrentPosition() / 25, new Point(curx, cury));

                    return true;
                } else {
                    this.mainVideoActivity.pauseVideo(true);
                    videoView.setOnTouchListener((v, motionEvent) -> false);
                    return false;
                }
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                this.mainVideoActivity.pauseVideo(true);
                videoView.setOnTouchListener((v, motionEvent) -> false);
                return false;
            default:
                return true;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        this.isVideoComplete = true;
    }
}
