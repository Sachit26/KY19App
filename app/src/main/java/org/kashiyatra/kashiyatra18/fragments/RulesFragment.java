package org.kashiyatra.kashiyatra18.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.kashiyatra.kashiyatra18.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RulesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RulesFragment extends Fragment {

    private static final String ARG_EVENT_POSITION = "event_position";
    private int mEventPosition;

    public RulesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param eventPosition Event Position.
     * @return A new instance of fragment RulesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RulesFragment newInstance(int eventPosition) {
        RulesFragment fragment = new RulesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_EVENT_POSITION, eventPosition);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEventPosition = getArguments().getInt(ARG_EVENT_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_rules, container, false);
        TextView rulesTextView = rootView.findViewById(R.id.rules_text_view);
        rulesTextView.setText(getResources().getStringArray(R.array.event_rules)[mEventPosition]);
        return rootView;
    }

}
