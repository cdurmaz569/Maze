package edu.wm.cs.cs301.KeisterDurmaz.falstad;

public interface MazeCompleteObserver {
	/**
	 * notifies the playactivity that the game is over
	 */
	void startFinishActivity(int outcome);
}
