package hu.bme.aut.digikaland.ui.common.activities;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.PatternMatcher;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;

import hu.bme.aut.digikaland.R;

public class NFCActivity extends AppCompatActivity {

    TextView nfctext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);
        nfctext = findViewById(R.id.nfctext);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    private NfcAdapter nfcAdapter;
    private PendingIntent nfcPendingIntent;

    private void enableForegroundDispatch() {
        IntentFilter tagDetected = new IntentFilter(
                NfcAdapter.ACTION_NDEF_DISCOVERED);
        tagDetected.addDataScheme("vnd.android.nfc");
        tagDetected.addDataPath("/digikaland", PatternMatcher.PATTERN_PREFIX);
        tagDetected.addDataAuthority("ext", null);
        IntentFilter[] tagFilters = new IntentFilter[] { tagDetected };
        nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent,
                tagFilters, null);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        readNFC(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        enableForegroundDispatch();
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
    }

    private void readNFC(Intent intent){
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            try {
                Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                Ndef ndef = Ndef.get(tagFromIntent);
                ndef.connect();
                byte[] bytes = ndef.getCachedNdefMessage().getRecords()[0].getPayload();
                String test = new String(bytes);
                nfctext.setText(test);
            }catch (IOException e){
                Log.e("io error", "I/O Failure");
            }catch (NullPointerException e) {
                Log.e("nullpointer", "Unable to read");
            }
        }
    }
}
