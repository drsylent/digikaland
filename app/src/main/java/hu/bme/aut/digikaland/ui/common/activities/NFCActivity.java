package hu.bme.aut.digikaland.ui.common.activities;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.TagLostException;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.PatternMatcher;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import hu.bme.aut.digikaland.R;

public class NFCActivity extends AppCompatActivity {

    TextView nfctext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);
        nfctext = findViewById(R.id.nfctext);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        mNfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

        private NfcAdapter mNfcAdapter;
        private PendingIntent mNfcPendingIntent;
        private IntentFilter[] mWriteTagFilters;

        private void enableTagWriteMode() {
            IntentFilter tagDetected = new IntentFilter(
                    NfcAdapter.ACTION_NDEF_DISCOVERED);
            tagDetected.addDataScheme("vnd.android.nfc");
//            tagDetected.addDataPath("/digikaland", PatternMatcher.PATTERN_PREFIX);
//            tagDetected.addDataAuthority("ext", null);
            mWriteTagFilters = new IntentFilter[] { tagDetected };
            mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent,
                    mWriteTagFilters, null);
        }

        @Override
        protected void onNewIntent(Intent intent) {
            // Tag writing mode
//            if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
//                Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
//
//                if (!fieldStateToSave.equals("")) {
//                    NdefRecord record = createCustomRecord(fieldStateToSave);
//
//                    NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
//
//                    if (writeTag(msg, detectedTag)) {
//                        Toast.makeText(this, "Success: game state saved!",
//                                Toast.LENGTH_LONG).show();
//                        finish();
//                    } else {
//                        Toast.makeText(this, "Write failed", Toast.LENGTH_LONG)
//                                .show();
//                    }
//                }
//            }
            readNFC(intent);
        }

    @Override
    public void onResume() {
        super.onResume();
        enableTagWriteMode();
//        readNFC(getIntent());
    }

    @Override
    protected void onPause() {
        super.onPause();
        mNfcAdapter.disableForegroundDispatch(this);
    }

    private void readNFC(Intent intent){
            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
//                NdefMessage[] msgs = null;
//
//                Parcelable[] rawMsgs = getIntent().getParcelableArrayExtra(
//                        NfcAdapter.EXTRA_NDEF_MESSAGES);
                try {
                    Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                    Ndef ndef = Ndef.get(tagFromIntent);
                    ndef.connect();
                    byte[] bytes = ndef.getCachedNdefMessage().getRecords()[0].getPayload();
                    String test = new String(bytes);
                    nfctext.setText(test);
                }catch (IOException e){
                    Log.e("io error", "I/O Failure");
//                }catch (FormatException e){
//                    Log.e("format error", "Malformed tag");
                }catch (NullPointerException e) {
                    Log.e("nullpointer", "Unable to read");
                }
//                if (rawMsgs != null) {
//                    msgs = new NdefMessage[rawMsgs.length];
//                    for (int i = 0; i < rawMsgs.length; i++) {
//                        msgs[i] = (NdefMessage) rawMsgs[i];
//                    }
//                }
//
//                if (msgs != null) {
//                    for (NdefMessage tmpMsg : msgs) {
//                        nfctext.setText(new String(tmpMsg.getRecords()[0]
//                                .getPayload()));
//                    }
//                }
            }
        }
}
