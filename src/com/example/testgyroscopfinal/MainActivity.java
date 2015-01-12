package com.example.testgyroscopfinal;

import java.text.DecimalFormat;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.testgyroscopfinal.BallPanel;

 
public class MainActivity extends Activity implements SensorEventListener{
 
    private TextView tv;
    private SensorManager mSensorManager;
    private Sensor mGyroSensor;
    public static final float EPSILON = 0.000000001f;
    

    private float[] deltaRotationMatrix;
    private float[] currentRotationMatrix;
    private float[] gyroscopeOrientation;
    
    static final String LOG_TAG = "GYROCAPTURE";
    static final String SAMPLING_SERVICE_ACTIVATED_KEY = "samplingServiceActivated";
	static final String GYROACCEL_KEY = "sampleCounter";

 // Create a constant to convert nanoseconds to seconds.
    private static final float NS2S = 1.0f / 1000000000.0f;
    private float[] deltaRotationVector = new float[4];
    private float timestamp;
    
    private Button bAbout;
    private Button bStart;
    private Button bExit;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {

    	
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        bAbout= (Button) findViewById(R.id.button2);
        bAbout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				launchAbout();
				}
		});
        bStart= (Button)findViewById(R.id.button1);
        bStart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				launchStart();
				}
		});
        bExit= (Button) findViewById(R.id.button3);
        bExit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				launchExit();
				}
		});
        
        
    
        
        Log.d( LOG_TAG, "onCreate");
        if( savedInstanceState != null ) {
			sampleCounterText = savedInstanceState.getString( GYROACCEL_KEY );
			samplingServiceActivated = savedInstanceState.getBoolean( SAMPLING_SERVICE_ACTIVATED_KEY, false );
        } else
            samplingServiceActivated = false;
        Log.d( LOG_TAG, "onCreate; samplingServiceActivated: "+samplingServiceActivated );
        bindSamplingService();
 
        tv= (TextView)findViewById(R.id.txt2);
        initMaths();
        // Get an instance of the sensor service
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mGyroSensor=mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
         
        PackageManager PM= this.getPackageManager();
        boolean gyro = PM.hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE);
        
         
        if(gyro){
        	Toast.makeText(getApplicationContext(),"Gyroscope sensor is present in this device", Toast.LENGTH_LONG).show();
        }
        else{
        	Toast.makeText(getApplicationContext(),"Sorry, can't do nothing...Your device doesn't have gyroscope sensor", Toast.LENGTH_LONG).show();
        }
        
        /*Button objbt1= (Button) findViewById (R.id.button1);
        objbt1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// TODO Auto-generated method stub
				//before start look up if the device is in the rigth position to start
				//verify if coordenates x=0.00, y=0.00 and z=0.00
				boolean isChecked = checkCoordinates();
				if(isChecked){
					samplingServiceActivated = true;
					startSamplingService();
				}
			}
		});*/
    }
    public void launchAbout(){
    	Intent i= new Intent(this, About.class);
    	startActivity(i);
    }
    public void launchStart(){
    	//Intent i= new Intent(this, Start.class);
    	//startActivity(i);
    	setContentView(R.layout.start);
    }
    public void launchExit(){
    	
    	
    }
 
    private void bindSamplingService() {
		// TODO Auto-generated method stub
    	samplingServiceConnection = new SamplingServiceConnection();
		Intent i = new Intent();
		i.setClassName( "aexp.gyroaccel", "aexp.gyroaccel.SamplingService" );
		bindService( i, samplingServiceConnection, Context.BIND_AUTO_CREATE);
		
	}

	/*@Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something if sensor accuracy changes.
    }*/
    public final boolean checkCoordinates(){
    	
    	if(deltaRotationVector[2]==0.00 || deltaRotationVector[2]==-0.00 && deltaRotationVector[1]==0.00 || deltaRotationVector[1]==-0.00 && deltaRotationVector[0]==0.00 || deltaRotationVector[0]==-0.00 ){
    		return true;
    	}
    	return false;
    }
    //alterar
    
	private void startSamplingService() {
		if( samplingServiceRunning )	// shouldn't happen
			stopSamplingService();
        //sampleCounterText = "0";
        Intent i = new Intent();
       
        i.setClassName( "com.example.testgyroscopfinal","com.example.testgyroscopfinal.SamplingService" );
        startService( i );
		samplingServiceRunning = true;				
	}

	private void stopSamplingService() {
		Log.d( LOG_TAG, "stopSamplingService" );
		if( samplingServiceRunning ) {
			stopSampling();
			samplingServiceRunning = false;
		}
	}
	private void stopSampling() {
			Log.d( LOG_TAG, "stopSampling" );
			if( samplingService == null )
				Log.e( LOG_TAG, "stopSampling: Service not available" );
			else {
				try {
					samplingService.stopSampling();
				} catch( DeadObjectException ex ) {
					Log.e( LOG_TAG, "DeadObjectException",ex );
				} catch( RemoteException ex ) {
					Log.e( LOG_TAG, "RemoteException",ex );
				}
			}
		}
 
    @Override
    public final void onSensorChanged(SensorEvent event) {
 
    	 if (timestamp != 0) {
             final float dT = (event.timestamp - timestamp) * NS2S;
             // Axis of the rotation sample, not normalized yet.
             double axisX = event.values[0];
             double axisY = event.values[1];
             double axisZ = event.values[2];
             double funct =(axisX*axisX + axisY*axisY + axisZ*axisZ);
             // Calculate the angular speed of the sample
             double omegaMagnitude = Math.sqrt(funct);
             
             // Normalize the rotation vector if it's big enough to get the axis
             // (that is, EPSILON should represent your maximum allowable margin of error)
             if (omegaMagnitude > EPSILON) {
               axisX /= omegaMagnitude;
               axisY /= omegaMagnitude;
               axisZ /= omegaMagnitude;
             }
             
             // Integrate around this axis with the angular speed by the timestep
             // in order to get a delta rotation from this sample over the timestep
             // We will convert this axis-angle representation of the delta rotation
             // into a quaternion before turning it into the rotation matrix.
             double thetaOverTwo =  (omegaMagnitude * dT / 2.0f);
             //
             double sinThetaOverTwo = Math.sin(thetaOverTwo);
             
             double cosThetaOverTwo = Math.cos(thetaOverTwo);
             //deltarotationVector in rad2/s
             deltaRotationVector[0] = (float) (sinThetaOverTwo * axisX);
             deltaRotationVector[1] = (float) (sinThetaOverTwo * axisY);
             deltaRotationVector[2] = (float) (sinThetaOverTwo * axisZ);
             deltaRotationVector[3] = (float) cosThetaOverTwo;
           }
           timestamp = event.timestamp;
           
           SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
           // User code should concatenate the delta rotation we computed with the current rotation
           // in order to get the updated rotation.
           currentRotationMatrix = matrixMultiplication(
                   currentRotationMatrix,
                   deltaRotationMatrix);

           SensorManager.getOrientation(currentRotationMatrix,
                   gyroscopeOrientation);

           tv.setText("\n\nOrientation X (Roll) :" + (new DecimalFormat("#.##").format((double)gyroscopeOrientation[0]))
                   + "\n\n" + "Orientation Y (Pitch) :"
                   + (new DecimalFormat("#.##").format((double)gyroscopeOrientation[1]))+ "\n\n"
                   + "Orientation Z (Yaw) :" + (new DecimalFormat("#.##").format((double)gyroscopeOrientation[2])));
          //show new normalized axis values
    }
    
    /**
	 * Initialize the data structures required for the maths.
	 */
	private void initMaths()
	{
		

		deltaRotationVector = new float[4];
		deltaRotationMatrix = new float[9];
		currentRotationMatrix= new float[9];
		gyroscopeOrientation= new float[3];

		// Initialize the current rotation matrix as an identity matrix...
		currentRotationMatrix[0] = 1.0f;
		currentRotationMatrix[4] = 1.0f;
		currentRotationMatrix[8] = 1.0f;
	}

 
    @Override
    protected void onResume() {
        // Register a listener for the sensor.
        super.onResume();
        mSensorManager.registerListener(this, mGyroSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
 
    @Override
    protected void onPause() {
        // important to unregister the sensor when the activity pauses.
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
    /**
	 * Multiply matrix a by b. Android gives us matrices results in
	 * one-dimensional arrays instead of two, so instead of using some (O)2 to
	 * transfer to a two-dimensional array and then an (O)3 algorithm to
	 * multiply, we just use a static linear time method.
	 * 
	 * @param a
	 * @param b
	 * @return a*b
	 */
	private float[] matrixMultiplication(float[] a, float[] b)
	{
		float[] result = new float[9];

		result[0] = a[0] * b[0] + a[1] * b[3] + a[2] * b[6];
		result[1] = a[0] * b[1] + a[1] * b[4] + a[2] * b[7];
		result[2] = a[0] * b[2] + a[1] * b[5] + a[2] * b[8];

		result[3] = a[3] * b[0] + a[4] * b[3] + a[5] * b[6];
		result[4] = a[3] * b[1] + a[4] * b[4] + a[5] * b[7];
		result[5] = a[3] * b[2] + a[4] * b[5] + a[5] * b[8];

		result[6] = a[6] * b[0] + a[7] * b[3] + a[8] * b[6];
		result[7] = a[6] * b[1] + a[7] * b[4] + a[8] * b[7];
		result[8] = a[6] * b[2] + a[7] * b[5] + a[8] * b[8];

		return result;
	}
	private String getStateName( int state ) {
		String stateName = null;
		switch( state ) {
		case SamplingService.ENGINESTATES_IDLE:
			stateName = "Idle";
			break;

		case SamplingService.ENGINESTATES_CALIBRATING:
			stateName = "Calibrating";
			break;
			
		case SamplingService.ENGINESTATES_MEASURING:
			stateName = "Measuring";
			break;
			
		default:
			stateName = "N/A";
			break;
		}
		return stateName;
	}

	private Handler uiHandler;
    private int state = SamplingService.ENGINESTATES_IDLE;
    private ISamplingService samplingService = null;
    private SamplingServiceConnection samplingServiceConnection = null;
	private boolean samplingServiceRunning = false;
    private boolean samplingServiceActivated = false;
	private TextView sampleCounterTV;
	private TextView statusMessageTV;
	private String sampleCounterText = null;
	public static BallPanel ballPanel = null;
	
    class SamplingServiceConnection implements ServiceConnection {
        public void onServiceConnected(ComponentName className, 
			IBinder boundService ) {
			Log.d( LOG_TAG, "onServiceConnected" );        	
        	samplingService = ISamplingService.Stub.asInterface((IBinder)boundService);
	    	//setCallbackOnService();
			//updateSamplingServiceRunning();
			//updateState();
              
		    //CheckBox cb = (CheckBox)findViewById( R.id.cb_sampling );
            //cb.setChecked( samplingServiceRunning );
			if( statusMessageTV  != null ) {
				statusMessageTV.setText( getStateName( state ) );
			}
			if( state == SamplingService.ENGINESTATES_MEASURING )
				ballPanel.setVisibility( View.VISIBLE);
		 	Log.d( LOG_TAG,"onServiceConnected" );
        }

        public void onServiceDisconnected(ComponentName className) {
        	samplingService = null;
			Log.d( LOG_TAG,"onServiceDisconnected" );
        }
    }

	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	};
 
}
