package com.example.fitnessapp.itemsInMore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.fitnessapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.Map;

public class profile extends AppCompatActivity {

    MaterialCardView backBtn;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    CircularProgressIndicator pb;

    MaterialTextView email, username, age, H, W, stepGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if(user == null) return;

        backBtn = findViewById(R.id.backButtonProfilePage);
        email = findViewById(R.id.emailInProfile);
        username = findViewById(R.id.usernameInProfile);
        age = findViewById(R.id.ageInProfile);
        H = findViewById(R.id.heightInProfile);
        W = findViewById(R.id.weightInProfile);
        stepGoal = findViewById(R.id.stepGOalInProfile);
        pb = findViewById(R.id.profilePB);

        pb.setVisibility(View.VISIBLE);

        DocumentReference documentReference = firestore.collection("users").document(user.getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {

                    DocumentSnapshot documentSnapshot = task.getResult();

                    if (!documentSnapshot.exists()) {
                        Toast.makeText(profile.this, "No data found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Map<String, Object> map = documentSnapshot.getData();
                    if(map == null) return;

                    for(Map.Entry<String, Object> entry : map.entrySet()) {

                        char letter = entry.getKey().charAt(0);

                        if(letter == 'A')
                            age.setText("Age : " + entry.getValue().toString());
                        else if(letter == 'E')
                            email.setText("Email : " + entry.getValue().toString());
                        else if(letter == 'H')
                            H.setText("Height : " + entry.getValue().toString() + " CM");
                        else if(letter == 'S')
                            stepGoal.setText("Daily step goal : " + entry.getValue().toString());
                        else if(letter == 'W')
                            W.setText("Weight : " + entry.getValue().toString() + " KG");
                        else
                            username.setText("Username : " + entry.getValue().toString());
                    }

                    pb.setVisibility(View.INVISIBLE);

                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}