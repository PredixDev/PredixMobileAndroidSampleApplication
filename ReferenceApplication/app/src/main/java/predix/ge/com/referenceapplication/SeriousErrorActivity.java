package predix.ge.com.referenceapplication;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by 212572548 on 9/22/16.
 */
public class SeriousErrorActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.serious_error);
        String message1 = getIntent().getExtras().getString("message1");
        ((TextView) findViewById(R.id.mainMessage)).setText(message1);

        String message2 = getIntent().getExtras().getString("message2");
        ((TextView) findViewById(R.id.topMessage)).setText(message2);

        String message3 = getIntent().getExtras().getString("message3");
        ((TextView) findViewById(R.id.subMessage)).setText(message3);
    }
}
