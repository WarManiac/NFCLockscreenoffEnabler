package pk.qwerty12.nfclockscreenoffenabler.adapters;

import android.app.Fragment;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pk.qwerty12.nfclockscreenoffenabler.NFCLockScreenOffEnablerActivity;
import pk.qwerty12.nfclockscreenoffenabler.fragments.NfcTagsFragment;
import pk.qwerty12.nfclockscreenoffenabler.R;
import pk.qwerty12.nfclockscreenoffenabler.fragments.CreditsFragment;
import pk.qwerty12.nfclockscreenoffenabler.fragments.SettingsFragment;

public class MainMenuAdapter extends RecyclerView.Adapter<MainMenuAdapter.ViewHolder> {

    private final int[] items;

    public MainMenuAdapter(int[] items){
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.vw_main_menu_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setDataOnView(position);
    }

    @Override
    public int getItemCount() {
        return items.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final View v;
        private final AppCompatTextView tv;

        public ViewHolder(View itemView) {
            super(itemView);
            v = itemView;
            tv = (AppCompatTextView) itemView.findViewById(R.id.vw_main_menu_item_tv);
        }

        public void setDataOnView(int cur) {

            final int resId = items[cur];
            tv.setText(resId);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context c = v.getContext();
                    if (c instanceof NFCLockScreenOffEnablerActivity){

                        Fragment frag;

                        switch (resId){
                            case R.string.str_settings:
                                frag = SettingsFragment.newInstance();
                                break;
                            case R.string.str_nfc_tags:
                                frag = NfcTagsFragment.newInstance();
                                break;
                            case R.string.str_credits:
                                frag = CreditsFragment.newInstance();
                                break;
                            default:
                                Snackbar.make(v, "Unknown menu item", Snackbar.LENGTH_SHORT).show();
                                return;
                        }

                        ((NFCLockScreenOffEnablerActivity)c).setFragment(frag);
                    }
                }
            });

        }

    }
}
