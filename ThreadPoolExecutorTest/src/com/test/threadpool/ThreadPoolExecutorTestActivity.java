package com.test.threadpool;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

public class ThreadPoolExecutorTestActivity extends Activity {
	/** Called when the activity is first created. */
	private TextView tv1;
	private TextView tv2;
	private TextView tv3;
	private TextView tv4;
	private TextView tv5;
	private TextView tv6;
	private TextView tv7;
	private TextView tv8;
	private Handler hasHandler = new Handler();
	private static int count = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		tv1 = (TextView) findViewById(R.id.textView1);
		tv2 = (TextView) findViewById(R.id.textView2);
		tv3 = (TextView) findViewById(R.id.textView3);
		tv4 = (TextView) findViewById(R.id.textView4);
		tv5 = (TextView) findViewById(R.id.textView5);
		tv6 = (TextView) findViewById(R.id.textView6);
		tv7 = (TextView) findViewById(R.id.textView7);
		tv8 = (TextView) findViewById(R.id.textView8);
		count = 0;
		MyThreadPoolExecutor mtpe = new MyThreadPoolExecutor();
		// start first one
		mtpe.runTask(new Runnable() {
			public void run() {
				for (int i = 0; i < 10; i++) {
					try {
						// tv1.setText("First Task");
						Log.d(this.toString(), "First Task");
						// radioGroup.setBackgroundColor(Color.RED);
						Thread.sleep(1000);
					} catch (InterruptedException ie) {
					}
					hasHandler.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							tv1.setText("First Task:"
									+ Integer.toString(count++));
							// Log.d(this.toString(), "First Task");
						}
					});
				}
			}

		});
		// start second one
		/*
		 * try{ Thread.sleep(500); }catch(InterruptedException ie){}
		 */
		mtpe.runTask(new Runnable() {
			public void run() {
				for (int i = 0; i < 10; i++) {
					try {
						// tv1.setText("Second Task");
						Log.d(this.toString(), "Second Task");

						Thread.sleep(1000);
					} catch (InterruptedException ie) {
					}
					hasHandler.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							tv2.setText("Second Task:"
									+ Integer.toString(count++));
							// Log.d(this.toString(), "First Task");
						}
					});
				}
			}
		});
		// start third one
		/*
		 * try{ Thread.sleep(500); }catch(InterruptedException ie){}
		 */
		mtpe.runTask(new Runnable() {
			public void run() {
				for (int i = 0; i < 10; i++) {
					try {
						// tv1.setText("Third Task");
						Log.d(this.toString(), "Third Task");
						Thread.sleep(1000);
					} catch (InterruptedException ie) {
					}
					hasHandler.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							tv3.setText("Third Task:"
									+ Integer.toString(count++));
							// Log.d(this.toString(), "First Task");
						}
					});
				}
			}

		});
		// start fourth one
		/*
		 * try{ Thread.sleep(500); }catch(InterruptedException ie){}
		 */
		mtpe.runTask(new Runnable() {
			public void run() {
				for (int i = 0; i < 10; i++) {
					try {
						// tv1.setText("Fourth Task");
						Log.d(this.toString(), "Fourth Task");
						Thread.sleep(1000);
					} catch (InterruptedException ie) {
					}
					hasHandler.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							tv4.setText("Fourth Task:"
									+ Integer.toString(count++));
							// Log.d(this.toString(), "First Task");
						}
					});
				}

			}
		});
		// start fifth one
		/*
		 * try{ Thread.sleep(500); }catch(InterruptedException ie){}
		 */
		mtpe.runTask(new Runnable() {
			public void run() {
				for (int i = 0; i < 10; i++) {
					try {
						// tv1.setText("Fifth Task");
						Log.d(this.toString(), "Fifth Task");
						Thread.sleep(1000);
					} catch (InterruptedException ie) {
					}
					hasHandler.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							tv5.setText("Fifth Task:"
									+ Integer.toString(count++));
							// Log.d(this.toString(), "First Task");
						}
					});
				}
			}
		});
		// start Sixth one
		/*
		 * try{ Thread.sleep(500); }catch(InterruptedException ie){}
		 */
		mtpe.runTask(new Runnable() {
			public void run() {
				for (int i = 0; i < 10; i++) {
					try {
						// tv1.setText("Sixth Task");
						Log.d(this.toString(), "Sixth Task");
						Thread.sleep(1000);
					} catch (InterruptedException ie) {
					}
					hasHandler.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							tv6.setText("Sixth Task:"
									+ Integer.toString(count++));
							// Log.d(this.toString(), "First Task");
						}
					});
				}
			}
		});
		// mtpe.shutDown();
		mtpe.runTask(new Runnable() {
			public void run() {
				for (int i = 0; i < 10; i++) {
					try {
						// tv1.setText("Sixth Task");
						System.out.println("7 Task");
						Thread.sleep(1000);
					} catch (InterruptedException ie) {
					}
					hasHandler.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							tv7.setText("7 Task:" + Integer.toString(count++));
							// Log.d(this.toString(), "First Task");
						}
					});
				}
			}
		});

	}
}