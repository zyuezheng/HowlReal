package com.wolf32.cz3002.howlreal.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.wolf32.cz3002.howlreal.FetchNewsData;
import com.wolf32.cz3002.howlreal.R;
import com.wolf32.cz3002.howlreal.ReadNewsActivity;
import com.wolf32.cz3002.howlreal.adapters.NewsAdapter;
import com.wolf32.cz3002.howlreal.model.News;
import com.wolf32.cz3002.howlreal.model.User;

import java.util.ArrayList;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsFragment extends Fragment implements FetchNewsData.RetrieveListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "NewsFragment";
    private static String[] title = new String[100];
    private ListView newsListView;
    private NewsAdapter mAdapter;
    private NewsFragment.OnFragmentInteractionListener mListener;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private User mUser;
    private String name;
    private String email;
    private Uri profilePicUrl;

    public NewsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewsFragment newInstance(String param1, String param2) {
        Log.e(TAG, "NewsFragment constructor");

        NewsFragment fragment = new NewsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.e(TAG, "onCreateView");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            name = user.getDisplayName();
            email = user.getEmail();
            profilePicUrl = user.getPhotoUrl();
            String uid = user.getUid();
            Uri mUri = user.getPhotoUrl();
            String uri = "";
            if (mUri != null) {
                uri = mUri.toString();
            }
            boolean emailVerified = user.isEmailVerified();
            mUser = new com.wolf32.cz3002.howlreal.model.User(name, uid, email, uri, emailVerified);
        }


        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_news, container, false);

        newsListView = (ListView) mView.findViewById(R.id.fragment_news_listView);

        if (getArguments() != null) {
            Log.e(TAG, "args: " + getArguments().getString("type"));
        }

        String category = getArguments().getString("type");
        FetchNewsData fnd = new FetchNewsData(this);

        int userType = -1;
        if (mUser.isAdmin())
            userType = 1;
        else
            userType = 0;

        fnd.getData(category, userType);


        return mView;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        Log.e(TAG, "pressed.");
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NewsFragment.OnFragmentInteractionListener) {
            mListener = (NewsFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSuccess(final ArrayList<News> newsList) {
        Log.e(TAG, "newsList, size onSuccess: " + newsList.size());

        mAdapter = new NewsAdapter(Objects.requireNonNull(getContext()), newsList);
        newsListView.setAdapter(mAdapter);

        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                News currentNews = newsList.get(position);

                Intent newsArticleIntent = new Intent(newsListView.getContext(), ReadNewsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("news", currentNews);
                newsArticleIntent.putExtras(bundle);

                newsArticleIntent.putExtra("position", position);
                startActivity(newsArticleIntent);


            }
        });
    }

    @Override
    public void onFailure() {
        Log.e(TAG, "onFailure");

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
