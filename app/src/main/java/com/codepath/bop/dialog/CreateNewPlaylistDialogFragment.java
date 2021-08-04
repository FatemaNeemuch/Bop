package com.codepath.bop.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.codepath.bop.R;
import com.codepath.bop.activities.MainActivity;
import com.codepath.bop.managers.SpotifyDataManager;

public class CreateNewPlaylistDialogFragment extends DialogFragment {

    //class constants
    public static final String TAG = "CreateNewPlaylistDialog";

    //instance variable
    private EditText etPlaylistName;
    private ImageButton btnCancelNewPlaylist;
    private Button btnCreatePlaylist;

    public CreateNewPlaylistDialogFragment(){
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static CreateNewPlaylistDialogFragment newInstance(String title) {
        CreateNewPlaylistDialogFragment frag = new CreateNewPlaylistDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_new_playlist, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        etPlaylistName = view.findViewById(R.id.etPlaylistName);
        btnCancelNewPlaylist = view.findViewById(R.id.btnCancelNewPlaylist);
        btnCreatePlaylist = view.findViewById(R.id.btnCreatePlaylist);
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter Playlist Name");
        getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field
        etPlaylistName.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        btnCancelNewPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnCreatePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String playlistName = etPlaylistName.getText().toString();
                if (!playlistName.isEmpty()){
                    //create url for posting a playlist
                    String url = String.format("https://api.spotify.com/v1/users/%s/playlists", SpotifyDataManager.getUserID());
                    //make API call to make new playlist
                    SpotifyDataManager.createNewPlaylist(url, MainActivity.getmAccessToken(), playlistName, getContext(), CreateNewPlaylistDialogFragment.this);
                }else{
                    Toast.makeText(getContext(), "Playlist name cannot empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
