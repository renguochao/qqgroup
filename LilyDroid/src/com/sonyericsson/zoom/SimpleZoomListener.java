
package com.sonyericsson.zoom;

import android.view.MotionEvent;
import android.view.View;

/**
 * Simple on touch listener for zoom view
 */
public class SimpleZoomListener implements View.OnTouchListener {

    /**
     * Which type of control is used
     */
    public enum ControlType {
        PAN, ZOOM
    }

    /** State being controlled by touch events */
    private ZoomState mState;

    /** Current control type being used */
    private ControlType mControlType = ControlType.ZOOM;

    /** X-coordinate of previously handled touch event */
    private float mX;

    /** Y-coordinate of previously handled touch event */
    private float mY;

    /**
     * Sets the zoom state that should be controlled
     * 
     * @param state Zoom state
     */
    public void setZoomState(ZoomState state) {
        mState = state;
    }

    /**
     * Sets the control type to use
     * 
     * @param controlType Control type
     */
    public void setControlType(ControlType controlType) {
        mControlType = controlType;
    }

    // implements View.OnTouchListener
    public boolean onTouch(View v, MotionEvent event) {
        final int action = event.getAction();
        final float x = event.getX();
        final float y = event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mX = x;
                mY = y;
                break;

            case MotionEvent.ACTION_MOVE: {
                final float dx = (x - mX) / (v.getWidth()*mState.getZoom());
                final float dy = (y - mY) / (mState.getmPicY()*(v.getWidth()/mState.getmPicX())*mState.getZoom());

                if (mControlType == ControlType.ZOOM) {
                    mState.setZoom(mState.getZoom() * (float)Math.pow(20, -dy));
                    mState.notifyObservers();
                } else {
                    mState.setPanX(mState.getPanX() - dx);
                    mState.setPanY(mState.getPanY() - dy);
                    mState.notifyObservers();
                }

                mX = x;
                mY = y;
                break;
            }

        }

        return true;
    }

}
