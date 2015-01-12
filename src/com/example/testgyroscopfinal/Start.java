package com.example.testgyroscopfinal;
import com.example.testgyroscopfinal.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.testgyroscopfinal.BallPanel;

public class Start extends Activity {
	private TextView tv;
	static final float SCALE = 10f;	// Maximum acceleration in m/s2
	public static BallPanel ballPanel = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.start);
    	ballPanel = (BallPanel)findViewById( R.id.ball);
    	
    	
        //setContentView(new ExampleView(this));
       // tv= (TextView)findViewById(R.id.txt3);
        //tv.setText("\n\nNew WINDOW" );
        

    }
    
    /*
    public class ExampleView extends View{
    	public ExampleView(Context context){
    		super(context);
    	}
    
    	
    	protected void onDraw(Canvas canvas){
    		Paint pincel = new Paint();
        	pincel.setColor(Color.BLUE);
        	pincel.setStrokeWidth(8);
        	pincel.setStyle(Style.STROKE);
        	canvas.drawCircle(150, 150, 100, pincel);
    		//drawCircle(float cx, float cy, float radio, Paint pincel);
    	}
    	/*
    	public void drawBall( Canvas c, 
				boolean ball, 
				float x, 
				float y, 
				float z ) {
		c.drawRGB( 0,0,0);
		Paint p = new Paint();
		p.setColor( Color.WHITE);
		float halfWidth = panelWidth / 2.0f;
		float halfHeight = panelHeight / 2.0f;
		c.drawLine( halfWidth,0,halfWidth,panelHeight,p);
		c.drawLine( 0,halfHeight,panelWidth,halfHeight,p);
		if( ball ) {
			float cx = halfWidth + ( halfWidth * ( x / SCALE ) );
			float cy = halfHeight + ( halfHeight * ( y / SCALE ) );
			float radius = 100 + ( 100* ( z / SCALE ) );
			if( radius < 0.0f )
				radius = 1.0f;
			p.setStyle( Paint.Style.FILL);
			c.drawCircle(cx, cy, radius, p );
		}
	}

	float panelHeight;
	float panelWidth;
    }
*/
    //}
   
 
}