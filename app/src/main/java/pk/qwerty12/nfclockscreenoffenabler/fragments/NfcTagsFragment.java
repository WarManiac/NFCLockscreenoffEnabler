package pk.qwerty12.nfclockscreenoffenabler.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import pk.qwerty12.nfclockscreenoffenabler.Common;
import pk.qwerty12.nfclockscreenoffenabler.NFCLockScreenOffEnabler;
import pk.qwerty12.nfclockscreenoffenabler.NFCLockScreenOffEnablerActivity;
import pk.qwerty12.nfclockscreenoffenabler.NfcTag;
import pk.qwerty12.nfclockscreenoffenabler.R;
import pk.qwerty12.nfclockscreenoffenabler.UndoBarController;
import pk.qwerty12.nfclockscreenoffenabler.UndoBarController.UndoListener;
import pk.qwerty12.nfclockscreenoffenabler.adapters.NfcTagArrayAdapter;

public class NfcTagsFragment extends Fragment implements UndoListener {

	private SharedPreferences mPrefs = null;
	private NfcAdapter mNfcAdapter = null;
    private boolean mAlreadySetup = false;
	private RecyclerView mListView = null;
	private NfcTagArrayAdapter mArrayAdapter;
	private boolean mDialogShowing = false;
	private UndoBarController mUndoBarController;

    public static NfcTagsFragment newInstance() {
        return new NfcTagsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_nfc_tags, container, false);

        mPrefs = getActivity().getSharedPreferences(NFCLockScreenOffEnabler.MY_PACKAGE_NAME, Context.MODE_PRIVATE);

        mListView = (RecyclerView) v.findViewById(R.id.listView);
        mUndoBarController = new UndoBarController(v.findViewById(R.id.undobar), this);
        setupListViewFromSet();

        return v;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(context);
    }

	private void setupListViewFromSet() {
		HashSet<String> defaultValue = new HashSet<>();
        Iterator<String> nfcTagIds = mPrefs.getStringSet(Common.PREF_NFC_KEYS, defaultValue).iterator();
        Iterator<String> nfcTagNames = mPrefs.getStringSet(Common.PREF_NFC_KEYS_NAMES, defaultValue).iterator();

		ArrayList<NfcTag> nfcTagsArray = new ArrayList<>();

        while (nfcTagIds.hasNext() && nfcTagNames.hasNext()){
			String tagId = nfcTagIds.next();
			String tagName;
			try {
				tagName = nfcTagNames.next();
			} catch (Exception e) {
				tagName = getString(R.string.unnamed_tag);
			}

			nfcTagsArray.add(new NfcTag(tagId, tagName));
		}

		mArrayAdapter = new NfcTagArrayAdapter(nfcTagsArray);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAdapterPosition();
                NfcTag tag = mArrayAdapter.getItem(position);
                mArrayAdapter.remove(position);
                mArrayAdapter.notifyItemRemoved(swipeDir);
                mUndoBarController.showUndoBar(false, R.string.tag_unauthorized, tag);
            }
        });

        mListView.setLayoutManager(new LinearLayoutManager(getActivity()));
		mListView.setAdapter(mArrayAdapter);
        itemTouchHelper.attachToRecyclerView(mListView);

	}

	@Override
	public void onPause() {

        Editor editor = mPrefs.edit();

        editor.putStringSet(Common.PREF_NFC_KEYS, mArrayAdapter.getTagIds());
        editor.putStringSet(Common.PREF_NFC_KEYS_NAMES, mArrayAdapter.getTagNames());
        editor.apply();

		disableForegroundDispatchSystem();

		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
        setUpForegroundDispatchSystem();
        getActivity().setTitle(R.string.str_nfc_tags);
    }

    private void disableForegroundDispatchSystem(){
        Log.i("NFC", "Disable nfc");
        if (mNfcAdapter != null) {
            mNfcAdapter.disableForegroundDispatch(getActivity());
        }
    }

	private void setUpForegroundDispatchSystem() {
        if (mAlreadySetup || mNfcAdapter == null) {
            Log.i("NFCTags", "Already enabled");
            return;
        }
        Log.i("NFCTags", "Enabling foreground");

        Activity a = getActivity();
        if (a instanceof NFCLockScreenOffEnablerActivity) {
            NFCLockScreenOffEnablerActivity act = (NFCLockScreenOffEnablerActivity) a;
            PendingIntent mPendingIntent = PendingIntent.getActivity(act, 0, new Intent(act,
                    act.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

            IntentFilter tech = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);

            IntentFilter[] mIntentFiltersArray = new IntentFilter[]{tech};
            String[][] mTechListsArray = new String[][]{
                    new String[]{NfcA.class.getName()},
                    new String[]{NfcB.class.getName()},
                    new String[]{NfcF.class.getName()},
                    new String[]{NfcV.class.getName()},
                    new String[]{IsoDep.class.getName()},
                    new String[]{MifareClassic.class.getName()},
                    new String[]{MifareUltralight.class.getName()},
                    new String[]{NdefFormatable.class.getName()},
                    new String[]{Ndef.class.getName()}
            };

            mAlreadySetup = true;
            mNfcAdapter.enableForegroundDispatch(act, mPendingIntent, mIntentFiltersArray, mTechListsArray);
        }
	}

    public void receiveNfcEvent(Intent intent){

        Log.i("NFCTags", "Receiving nfc");

        if (mDialogShowing){
            return;
        }

        Log.i("NFCTags", "Not showing");

        Tag t = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        String uuid = Common.byteArrayToHexString(t.getId());

        if (mArrayAdapter.containsTagId(uuid)) {
            View v = getView();
            if (v != null) {
                Snackbar.make(v, R.string.tag_already_added, Snackbar.LENGTH_SHORT).show();
            }
            return;
        }

        createAskForNameDialog(uuid);
    }

	private void createAskForNameDialog(final String uuid) {

        Context c = getActivity();

        AlertDialog dlg = new AlertDialog.Builder(c)
            .setTitle(R.string.new_tag_detected)
            .setMessage(R.string.type_in_name_for_tag)
            .setView(R.layout.dlg_ask_for_name)
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {
                    View v = getView();
                    if (v != null) {
                        AppCompatEditText acet = (AppCompatEditText) v.findViewById(R.id.dlg_ask_for_name_edittext);
                        String name = acet.getText().toString();
                        if (TextUtils.isEmpty(name)) {
                            name = getString(R.string.unnamed_tag);
                        }
                        int pos = mArrayAdapter.add(new NfcTag(uuid, name));
                        mArrayAdapter.notifyItemInserted(pos);
                        mDialogShowing = false;
                        dialog.dismiss();
                    }
                }
            })
            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mDialogShowing = false;
                    dialog.dismiss();
                }
            })

            .setOnCancelListener(new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    mDialogShowing = false;
                    dialog.dismiss();
                }
            })
            .show();

        dlg.getWindow().setSoftInputMode (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        mDialogShowing = true;

		/*input.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
					dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();
				}
				return false;
			}
		});*/
	}

	@Override
	public void onUndo(Parcelable token) {
		NfcTag tag = (NfcTag) token;
		mArrayAdapter.add(tag);
		mArrayAdapter.notifyDataSetChanged();
	}
}
