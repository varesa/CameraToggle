package fi.esav.cameratoggle;

import android.content.Intent;
import android.media.audiofx.BassBoost;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuItemImpl;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    TextView status;

    Requests req;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        status = (TextView) findViewById(R.id.text_status);

        findViewById(R.id.button_enable).setOnClickListener(this);
        findViewById(R.id.button_disable).setOnClickListener(this);
        findViewById(R.id.button_refresh).setOnClickListener(this);

        this.req = new Requests(getApplicationContext(), this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        req.getStatus();
    }

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.button_enable:
                req.setMD(true);
                break;
            case R.id.button_disable:
                req.setMD(false);
                break;
            case R.id.button_refresh:
                req.getStatus();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                this.startActivity(intent);
                break;
        }
        return true;
    }
}
