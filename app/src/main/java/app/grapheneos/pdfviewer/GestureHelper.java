package app.grapheneos.pdfviewer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

/*
    The GestureHelper present a simple gesture api for the PdfViewer
*/

class GestureHelper {
    public interface GestureListener {
        boolean onTapUp();
        void onSwipe(boolean value);
        // Can be replaced with ratio when supported
        void onZoomIn(float value);
        void onZoomOut(float value);
        void onZoomEnd();
    }

    @SuppressLint("ClickableViewAccessibility")
    static void attach(Context context, View gestureView, GestureListener listener) {

        final GestureDetector detector = new GestureDetector(context,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapUp(MotionEvent motionEvent) {
                        return listener.onTapUp();
                    }

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                        float distanceX = e2.getX() - e1.getX();
                        float distanceY = e2.getY() - e1.getY();

                        if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > 100) {
                            listener.onSwipe(distanceX > 0);
                            return true;
                        }
                        return false;
                    }
                });

        final ScaleGestureDetector scaleDetector = new ScaleGestureDetector(context,
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    final float SPAN_RATIO = 600;
                    float initialSpan;
                    float prevNbStep;

                    @Override
                    public boolean onScaleBegin(ScaleGestureDetector detector) {
                        initialSpan = detector.getCurrentSpan();
                        prevNbStep = 0;
                        return true;
                    }

                    @Override
                    public boolean onScale(ScaleGestureDetector detector) {
                        float spanDiff = initialSpan - detector.getCurrentSpan();
                        float curNbStep = spanDiff / SPAN_RATIO;

                        float stepDiff = curNbStep - prevNbStep;
                        if (stepDiff > 0) {
                            listener.onZoomOut(stepDiff);
                        } else {
                            listener.onZoomIn(Math.abs(stepDiff));
                        }
                        prevNbStep = curNbStep;

                        return true;
                    }

                    @Override
                    public void onScaleEnd(ScaleGestureDetector detector) {
                        listener.onZoomEnd();
                    }
                });

        gestureView.setOnTouchListener((view, motionEvent) -> {
            detector.onTouchEvent(motionEvent);
            scaleDetector.onTouchEvent(motionEvent);
            return false;
        });
    }

}
