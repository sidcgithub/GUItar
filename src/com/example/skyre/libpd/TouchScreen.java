package com.example.skyre.libpd;

import android.content.Context;
import android.opengl.GLSurfaceView;
import com.example.skyre.libpd.GtRenderer;

/**
 * Created by Avtopalm on 07.06.2016.
 */
public class TouchScreen extends GLSurfaceView {
	public GtRenderer tSRend;

	public TouchScreen(Context context) {
		super(context);
		setEGLContextClientVersion(2);
		tSRend = new GtRenderer();
		setRenderer(tSRend);
	}
}

