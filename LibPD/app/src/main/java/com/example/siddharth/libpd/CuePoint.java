package com.example.siddharth.libpd;

import android.graphics.Point;
import android.util.Log;

/**
 * Created by Avtopalm on 11.07.2016.
 */
public class CuePoint extends Point {

	private int maxAreaDistance;
	private Point scrSize;

	float freq ,tmb;

	public CuePoint(float x, float y, Point scrSize) {
		this.scrSize = scrSize;

		Log.v("Raw - X axis", Float.toString(x));
		Log.v("Raw - Y axis", Float.toString(y));

		// Set the timbre earlier just cause I'm logging it
		this.tmb = y / 20;
		this.y = (int)y;

		this.setNoteCoord(x);

		// Setting maximum distance from point where touch will be applied
		this.maxAreaDistance = scrSize.x / 20;
	}

	private void setNoteCoord(float x) {
		int rawFreq = (int)(1000 * (x / this.scrSize.x) + 200);
		//Log.v("Frequency", freq.toString());
		boolean choosed = false;
		float curr = 0, prev = 0;

		for (int i = 0; i < 1000; i++) {

			// Getting note symbol into the string
			String note = Frequency.NOTE_SYMBOL[i % 12];
			note = note.concat(Integer.toString(i / 12));

			prev = curr;
			curr = Frequency.valueOf(note);

			if (rawFreq < curr && rawFreq > prev) {
				Log.v("Raw Note", note);
				Log.v("Raw Note 2", Frequency.makeNoteSymbol(prev));
				this.freq = curr - rawFreq < rawFreq - prev ? curr : prev;
				Log.v("Result Note", Frequency.makeNoteSymbol(this.freq));
				break;
			}
			/*
			Log.v("Frequency", note);
			Log.v("Frequency value", Float.toString(Frequency.valueOf(note)));
			//if (Frequency.parseNoteSymbol(note) > freq)
			*/


		}

		this.x = (int)((this.freq * this.scrSize.x) / 1000 - 200);

		Log.v("Frequency", Float.toString(this.freq));
		Log.v("Timbre", Float.toString(this.tmb));
		Log.v("Precise - X axis", Integer.toString(this.x));
		Log.v("Precise - Y axis", Integer.toString(this.y));

	}

	public boolean areaIncludes(float x, float y) {
		if ((this.x + this.maxAreaDistance > x) && (this.x - this.maxAreaDistance < x)
				&& (this.y + this.maxAreaDistance > y) && (this.x - this.maxAreaDistance < y)) {
			return true;
		}
		return false;
	}
}
