package com.example.fitnessapp.workoutPage;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fitnessapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class walkingSession extends Fragment {

    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    CircularProgressIndicator pb;
    RecyclerView recyclerView;
    TextView NothingFoundMessage, heading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_walking_session, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        SharedPreferences themeSP = getActivity().getSharedPreferences("appThemeMode", Context.MODE_PRIVATE);
        int themeKey = themeSP.getInt("nightModeOn", 0);

        pb = view.findViewById(R.id.sessionPB);
        heading = view.findViewById(R.id.previousSessionHeading);
        recyclerView = (RecyclerView) view.findViewById(R.id.sessionsRecyclerview);
        NothingFoundMessage = view.findViewById(R.id.nothingFoundSessions);

        if(themeKey == 2) heading.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.white)));

        setRecyclerView();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        setRecyclerView();
    }

    private void setRecyclerView() {

        firestore.collection("walkSessions").document(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()) {

                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(!documentSnapshot.exists()) {

                        pb.setVisibility(View.INVISIBLE);
                        NothingFoundMessage.setVisibility(View.VISIBLE);
                        return;
                    }

                    ArrayList<String> ar = new ArrayList<>();
                    Map<String, Object> map = documentSnapshot.getData();

                    if(map == null) {

                        pb.setVisibility(View.INVISIBLE);
                        NothingFoundMessage.setVisibility(View.VISIBLE);
                        return;
                    }

                    for(Object val : map.values())
                        ar.add(String.valueOf(val));

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
                    Collections.sort(ar, new Comparator<String>(){
                        @Override
                        public int compare(String a, String b) {

                            String date1 = a.substring(0, 7), date2 = b.substring(0, 7);
                            Date realDate1 = new Date(), realDate2 = new Date();

                            try {
                                realDate1 = sdf.parse(date1);
                                realDate2 = sdf.parse(date2);
                            }
                            catch(Exception e) {

                            }

                            if(realDate1.after(realDate2))
                                return -1;
                            else
                                return 1;
                        }
                    });

                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    sessionAdapter adapter = new sessionAdapter(getActivity(), ar);
                    recyclerView.setAdapter(adapter);

                    NothingFoundMessage.setVisibility(View.INVISIBLE);
                    pb.setVisibility(View.INVISIBLE);
                }
                else
                    Toast.makeText(getActivity(), String.valueOf(task.getException()), Toast.LENGTH_SHORT).show();
            }
        });
    }
}