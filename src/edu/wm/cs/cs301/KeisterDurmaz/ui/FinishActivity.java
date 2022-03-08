package edu.wm.cs.cs301.KeisterDurmaz.ui;
 
import edu.wm.cs.cs301.KeisterDurmaz.R;
import edu.wm.cs.cs301.KeisterDurmaz.R.id;
import edu.wm.cs.cs301.KeisterDurmaz.R.layout;
import edu.wm.cs.cs301.KeisterDurmaz.R.menu;
import edu.wm.cs.cs301.KeisterDurmaz.falstad.Constants;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
 
public class FinishActivity extends Activity {
 
        int pathLength;
        float batteryLevel;
        String selectedItem;
        int outcome;
 
        TextView endMessage;
       
        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_finish);
                Log.v("FinishActivity: onCreate", "Activity created");
          
                batteryLevel = getIntent().getFloatExtra("batteryLevel", (float) 0);
                if (batteryLevel < 0)
                	batteryLevel = 0;
   
            	pathLength = getIntent().getIntExtra("pathLength", 0);
                outcome = getIntent().getIntExtra("outcome", 1);
                
                // set battery text
                TextView battery = (TextView) findViewById(R.id.final_batteryLevel);
                battery.setText("Battery Level: " + batteryLevel);
               
                // set pathLength text
                TextView path = (TextView) findViewById(R.id.final_pathLength);
                path.setText("Path Length: " + pathLength);
               
                // set outcome text
                TextView result = (TextView) findViewById(R.id.Result);
                if (outcome == Constants.OUTCOME_WIN) {
                        result.setText("You won!!");
                        Log.v("FinishActivity: onCreate", "Win outcome set");
                }else if (outcome == Constants.OUTCOME_BATTERY) {
                        batteryLevel = 0; //Changed to mimic battery dying
                        result.setText("Sorry, you ran out of battery.");
                        Log.v("FinishActivity onCreate", "Fail-Battery outcome set");
                }else if (outcome == Constants.OUTCOME_ERROR){
                        result.setText("Oops, something broke...");
                        Log.v("FinishActivity onCreate", "Fail-Error outcome set");
                }
        }
 
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
                // Inflate the menu; this adds items to the action bar if it is present.
                getMenuInflater().inflate(R.menu.finish, menu);
                return true;
        }
 
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
                // Handle action bar item clicks here. The action bar will
                // automatically handle clicks on the Home/Up button, so long
                // as you specify a parent activity in AndroidManifest.xml.
                Toast.makeText(this, "You selected " + item.getTitle(), Toast.LENGTH_LONG).show();
                Log.v("FinishActivity: onOptionsItemSelected", "Menu Item selected: " + item.getTitle());
                int id = item.getItemId();
                if (id == R.id.action_settings) {
                	return true;
                }
                return super.onOptionsItemSelected(item);
        }
       
        /**
         * Creates the intent and switches back to State Title
         * @param view
         */
        public void startAMazeActivity(View view){
                 // Responds to a button click
                Log.v("FinishActivity: startAMazeActivity", "Going to the AMaze State");
                Intent intent = new Intent(this, AMazeActivity.class);
                startActivity(intent);
        }
}