package pk.qwerty12.nfclockscreenoffenabler.adapters;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pk.qwerty12.nfclockscreenoffenabler.R;

public class CreditsAdapter extends RecyclerView.Adapter<CreditsAdapter.ViewHolder> {

    private final String[] contributors, contributions;

    public CreditsAdapter(String[] contributors, String[] contributions){
        this.contributors = contributors;
        this.contributions = contributions;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_credits_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setDataOnView(position);
    }

    @Override
    public int getItemCount() {
        return contributors.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final AppCompatTextView title, summary;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (AppCompatTextView) itemView.findViewById(R.id.vw_credits_item_title);
            summary = (AppCompatTextView) itemView.findViewById(R.id.vw_credits_item_summary);
        }

        public void setDataOnView(int cur) {

            if (cur < contributors.length) {
                String contributor = contributors[cur];
                title.setText(contributor);
            }
            if (cur < contributions.length) {
                String contribution = contributions[cur];
                summary.setText(contribution);
            }

        }

    }
}