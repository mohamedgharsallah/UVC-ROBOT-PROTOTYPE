package com.example.mobileapp.ui.dashboard;

import com.example.mobileapp.connectionmanager;
import com.example.mobileapp.ui.home.*;

import android.util.Log;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobileapp.R;
import com.example.mobileapp.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private connectionmanager connectionManager;
    private FragmentDashboardBinding binding;
    private VideoView videoView;
    private ProgressBar progress_bar;
    private TextView text_view_progress;

    private String receivedData = "0";
    private int receivedValue=0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        connectionManager = connectionmanager.getInstance();

        videoView = root.findViewById(R.id.stream);
        progress_bar = root.findViewById(R.id.progress_bar);
        text_view_progress = root.findViewById(R.id.text_view_progress);

        Button button1 = binding.onbutton;
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                senddata("ON");
            }
        });

        Button button2 = binding.offbutton;
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                senddata("OFF");
            }
        });

        return root;
    }

    private void updateProgressBar(int progr) {
        progress_bar.setProgress(progr);
        text_view_progress.setText(String.valueOf(progr));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void senddata(String command) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    connectionManager = connectionmanager.getInstance();

                    // Check if connectionManager is not null
                    if (connectionManager.isConnected()) {
                        // Send the command
                        connectionManager.sendCommand(command);

                        // Continuously receive data
                        while (true) {
                            receivedData = connectionManager.receiveResponse();

                            try {
                                 receivedValue = Integer.parseInt(receivedData);
                                Log.d("DashboardFragment", receivedData);
                                if (receivedValue>100){receivedValue=100;}

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        updateProgressBar(receivedValue);
                                    }
                                });
                            } catch (NumberFormatException e) {
                                Log.d("DashboardFragment", "Error: Failed to parse received data into an integer.");
                            }
                        }
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Handle connection not established
                            }
                        });
                    }

                    // Print the status
                    Log.d("DashboardFragment", "Connection status: " + connectionManager.isConnected());

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("DashboardFragment", "Catch: " + connectionManager.isConnected());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Handle failed to receive data
                            Log.d("DashboardFragment", "Run: " + connectionManager.isConnected());
                        }
                    });
                }
            }
        }).start();
    }
}
