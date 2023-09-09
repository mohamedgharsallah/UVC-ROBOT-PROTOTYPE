package com.example.mobileapp.ui.home;
import com.example.mobileapp.connectionmanager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobileapp.databinding.FragmentHomeBinding;

import java.io.IOException;

public class HomeFragment extends Fragment {

    private connectionmanager connectionManager;
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button button = binding.connectbutton;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connection();
            }
        });

        TextView typingt = binding.typingt;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                typingt.setText("M");
            }
        }, 300);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                typingt.append("A");
            }
        }, 1000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                typingt.append("K");
            }
        }, 1900);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                typingt.append("E");
            }
        }, 2800);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                typingt.append(" IT");
            }
        }, 3700);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                typingt.append(" E");
            }
        }, 4600);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                typingt.append("A");
            }
        }, 5500);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                typingt.append("S");
            }
        }, 6400);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                typingt.append("Y");
            }
        }, 7300);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void connection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    connectionManager = connectionmanager.getInstance();
                    connectionManager.connect();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Connected to device", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Failed to connect", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }


}

