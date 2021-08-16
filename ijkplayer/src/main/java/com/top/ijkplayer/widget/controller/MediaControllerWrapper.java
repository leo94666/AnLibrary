package com.top.ijkplayer.widget.controller;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.MediaController;

public class MediaControllerWrapper extends MediaController {

    //private final BilibiliControlView bilibiliControlView;

    public MediaControllerWrapper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MediaControllerWrapper(Context context, boolean useFastForward) {
        super(context, useFastForward);
    }

    public MediaControllerWrapper(Context context) {
        super(context);
    }
}
