package pk.qwerty12.nfclockscreenoffenabler.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pk.qwerty12.nfclockscreenoffenabler.R;
import pk.qwerty12.nfclockscreenoffenabler.adapters.CreditsAdapter;

public class CreditsFragment extends Fragment {

    public static CreditsFragment newInstance() {
        return new CreditsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_credits, container, false);

        String[] contributors = getResources().getStringArray(R.array.contributors);
        String[] contributions = getResources().getStringArray(R.array.contributors_how);

        RecyclerView rv = (RecyclerView) v.findViewById(R.id.frag_credits_rv);
        rv.setLayoutManager(new LinearLayoutManager(v.getContext()));
        rv.setAdapter(new CreditsAdapter(contributors, contributions));

        return v;
    }

    @Override
    public void onResume(){
        super.onResume();
        getActivity().setTitle(R.string.str_credits);
    }

}
