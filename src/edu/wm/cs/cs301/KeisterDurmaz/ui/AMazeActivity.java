package edu.wm.cs.cs301.KeisterDurmaz.ui;

import java.io.File;
import java.io.FileReader;

import javax.xml.transform.stream.StreamResult;

import edu.wm.cs.cs301.KeisterDurmaz.R;
import edu.wm.cs.cs301.KeisterDurmaz.R.array;
import edu.wm.cs.cs301.KeisterDurmaz.R.id;
import edu.wm.cs.cs301.KeisterDurmaz.R.layout;
import edu.wm.cs.cs301.KeisterDurmaz.R.menu;
import edu.wm.cs.cs301.KeisterDurmaz.falstad.MazeFileReader;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * State Title
 * This is the main activity
 */
public class AMazeActivity extends Activity {
	
	private String builder = "Default algorithm";
	private int level = 1;
	private String driver = "Manual Driver";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_amaze);
		
		// level bar and level text
		SeekBar levelBar = (SeekBar) findViewById(R.id.level_bar);
		final TextView levelText = (TextView)findViewById(R.id.level_text);
		createLevelBarListener(levelBar, levelText);
		
		// builder spinner
		final Spinner builderSpinner = (Spinner) findViewById(R.id.builder_spinner);
		createBuilderSpinnerListener(builderSpinner, levelBar);
		
		// driver spinner
		Spinner driverSpinner = (Spinner) findViewById(R.id.driver_spinner);
	    createDriverSpinnerListener(driverSpinner);

	}
	
	/**
	 * Creates the level bar listener
	 * @param levelBar
	 * @param levelText
	 */
	private void createLevelBarListener(SeekBar levelBar, final TextView levelText) {
		levelBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar levelBar, int progress, boolean fromUser) {
				Log.v("AMazeActivity: createLevelBarListener: onProgressChanged", "User changed level to " + progress);
				level = progress;
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// nothing here
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				String s = "Level " + level;
				levelText.setText(String.valueOf(s));
				Toast.makeText(AMazeActivity.this, s, Toast.LENGTH_SHORT).show();
			} 
			
		});
	}
	
	/**
	 * Creates the builder spinner listener (including the adapter for the strings)
	 * @param builderSpinner
	 * @param levelBar
	 */
	private void createBuilderSpinnerListener(final Spinner builderSpinner, final SeekBar levelBar) {
		ArrayAdapter<CharSequence> builderAdapter = ArrayAdapter.createFromResource(this, R.array.maze_builders, android.R.layout.simple_spinner_item);
		builderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		builderSpinner.setAdapter(builderAdapter);
        builderSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            	// change the max level to 4 if loading maze from file, 15 if generating from algorithm
            	Toast.makeText(AMazeActivity.this, parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
            	Log.v("AMazeActivity: createBuilderSpinnerListener: onItemSelected", "User selected '" + builderSpinner.getSelectedItem().toString() + "'");
            	builder = builderSpinner.getSelectedItem().toString();
                if (builderSpinner.getSelectedItem().toString() == "From file") {
                	Log.v("AMazeActivity: createBuilderSpinnerListener: onItemSelected", "Registered that user selected 'From file'");
                	levelBar.setMax(4);
                }else {
    				Log.v("AMazeActivity: createBuilderSpinnerListener: onItemSelected", "Registered that user selected a non-file maze builder");
                	levelBar.setMax(15);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            	//nothing here
            }
        });
        
	}
	
	/**
	 * Creates the driver spinner listener (including the adapter for the strings)
	 * @param driverSpinner
	 */
	private void createDriverSpinnerListener(final Spinner driverSpinner) {
		ArrayAdapter<CharSequence> driverAdapter = ArrayAdapter.createFromResource(this, R.array.maze_drivers, android.R.layout.simple_spinner_item);
		driverAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		driverSpinner.setAdapter(driverAdapter);
		driverSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            	Toast.makeText(AMazeActivity.this, parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
            	Log.v("AMazeActivity: createDriverSpinnerListener: onItemSelected", "User selected '" + driverSpinner.getSelectedItem().toString() + "'");
            	driver = driverSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            	//nothing here
            }
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.amaze, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * creates the intent with variables builder, level, and driver, and starts the generating activity
	 * @param view
	 */
	public void startGeneratingActivity(View view) {
		//StreamResult result = new StreamResult(new File("maze.xml"));
		//result.close();
		
		if (builder.toString() == "From file") {
			File xmlFile = new File(((Context)this).getFilesDir() + "/maze.xml");
			boolean y = xmlFile.exists();
			
			boolean r = canReadFile("maze.xml");
			if (!r) {
				Toast.makeText(this, "Couldn't open saved maze.", Toast.LENGTH_SHORT).show();
				return;
			}
		}
		Intent intent = new Intent(this, GeneratingActivity.class);
		intent.putExtra("builder", builder);
		intent.putExtra("level", level);
		intent.putExtra("driver", driver);
		startActivity(intent);
		finish();
	}

	/**
	 * As the title says, check to see if we can read the file
	 * @param filename
	 * @return true/false
	 */
	private boolean canReadFile(String filename){
		File f = new File(filename);
		
		if (!f.exists()) 
			return false;
		if (!f.canRead())
			return false;
		try {
			FileReader fileReader = new FileReader(f.getAbsolutePath());
			fileReader.read();
			fileReader.close();
		} catch (Exception e) {
			Log.v("AMazeActivity", "Exception when checked file can read with message:" + e.getMessage());
			return false;
		}
		return true;
	}
}
