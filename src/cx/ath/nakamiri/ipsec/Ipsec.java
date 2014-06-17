package cx.ath.nakamiri.ipsec;

import client.IsakmpClient;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Ipsec extends Activity implements OnClickListener {

	private EditText ip = null;
	private EditText port = null;
	private Button submit = null;

	private TextView text = null;


	private ProgressDialog progress;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		ip = (EditText) findViewById(R.id.ip);
		port = (EditText) findViewById(R.id.port);
		submit = (Button) findViewById(R.id.submit);

		text = (TextView) findViewById(R.id.log);

		text.setText("hoge");

		SharedPreferences pref = getSharedPreferences("ipsec_test", MODE_PRIVATE);
		ip.setText(pref.getString("ip", ""));
		port.setText(pref.getString("port", ""));


		submit.setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		SharedPreferences pref = getSharedPreferences("ipsec_test", MODE_PRIVATE);
		SharedPreferences.Editor edit = pref.edit();
		edit.putString("ip", ip.getText().toString());
		edit.putString("port", port.getText().toString());
		edit.commit();

		progress = new ProgressDialog(this);
		progress.setMessage("í êMíÜ...");
		progress.setCancelable(true);
		progress.show();

		(new Thread(runnable)).start();


	}

	private Runnable runnable = new Runnable() {
		public void run() {
			
			for (int i = 0; i < 100; i++) {
				IsakmpClient client = new IsakmpClient(ip.getText().toString(), Integer.parseInt(port.getText().toString()));

				long start = System.currentTimeMillis();

				client.run();

				long stop = System.currentTimeMillis();

				progress.dismiss();

				long time = stop - start;

				Message message = new Message();
				Bundle bundle = new Bundle();

				if (client.getExit_code() == 0) {
					bundle.putString("time", Long.toString(time));
				} else {
					bundle.putString("time", "failed.");
				}

				message.setData(bundle);
				handler.sendMessage(message);
			}
		}
	};
	
	private final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String text = msg.getData().get("time").toString();
			TextView log = (TextView)findViewById(R.id.log);
			log.setText(text + "\n" + log.getText());
		}
	};

	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, 0, 0, "exit");

		return true;
	}

	@Override
	public boolean onOptionsItemSelected (MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			finish();
		}

		return true;
	}
}