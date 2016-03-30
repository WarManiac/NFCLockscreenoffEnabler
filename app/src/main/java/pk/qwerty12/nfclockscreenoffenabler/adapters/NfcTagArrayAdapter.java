package pk.qwerty12.nfclockscreenoffenabler.adapters;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pk.qwerty12.nfclockscreenoffenabler.NfcTag;
import pk.qwerty12.nfclockscreenoffenabler.R;

public class NfcTagArrayAdapter extends RecyclerView.Adapter<NfcTagArrayAdapter.ViewHolder> {

	private ArrayList<NfcTag> mNfcTags = null;

	public NfcTagArrayAdapter(ArrayList<NfcTag> nfcTags) {
		this.mNfcTags = nfcTags;
	}

    /**
     * Returns index of added item
     * */
    public int add(NfcTag tag){
        int newPos = getItemCount();
        this.mNfcTags.add(tag);
        return newPos;
    }

    public void remove(int position){
        if (position >= 0 && position < getItemCount()) {
            this.mNfcTags.remove(position);
        }
    }

	public Set<String> getTagNames() {
		HashSet<String> names = new HashSet<>();
		for (int i = 0; i < getItemCount(); i++) {
			names.add(mNfcTags.get(i).getTagName());
		}

		return names;
	}

	public Set<String> getTagIds() {
		HashSet<String> ids = new HashSet<>();
		for (int i = 0; i < getItemCount(); i++) {
			ids.add(mNfcTags.get(i).getTagId());
		}

		return ids;
	}

	public boolean containsTagId(String nfcTagId) {
		for (int i = 0; i < getItemCount(); i++) {
			if (nfcTagId.equals(mNfcTags.get(i).getTagId()))
				return true;
		}

		return false;
	}

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.nfc_tag_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setDataOnView(position);
    }

    @Override
    public int getItemCount() {
        return mNfcTags.size();
    }

    public NfcTag getItem(int position) {
        return mNfcTags.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final AppCompatTextView tagIdTextview, tagNameTextview;

        public ViewHolder(View v) {
            super(v);

            tagIdTextview = (AppCompatTextView) v.findViewById(R.id.nfc_tag_id_textview);
            tagNameTextview = (AppCompatTextView) v.findViewById(R.id.nfc_tag_friendly_name_textview);
        }

        public void setDataOnView(int position) {
            NfcTag nfcTag = mNfcTags.get(position);
            tagIdTextview.setText(nfcTag.getTagId());
            tagNameTextview.setText(nfcTag.getTagName());
        }
    }
}
