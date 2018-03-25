package com.fatecerss.tcc.apprendendo.DAO;

import android.support.annotation.NonNull;

import com.fatecerss.tcc.apprendendo.model.Learner;
import com.fatecerss.tcc.apprendendo.model.Teacher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Sandro on 24/03/2018.
 */

public class TeacherDAO extends UserDAO {

    //Variaveis
    private static FirebaseAuth firebaseAuth;
    private static DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private static DatabaseReference teacherReference = databaseReference.child("teachers");
    private static DatabaseReference resultReference = databaseReference.child("teachers");
    private static DatabaseReference updateReference = databaseReference.child("teachers");

    private static int USERALREADYEXISTS = 0;
    private static int USERDOESNOTEXISTS = 1;
    private static int SUCCESS = 1;
    private static int result = 0;
    Teacher resultTeacher;
    Teacher updateTeacher;

    //Metodos

    public TeacherDAO(){

    }

    public int signUp(final Teacher teacher){

        String email = teacher.getEmail().trim();
        String password = teacher.getPassword().trim();

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            if (validateUserInDatabase(teacher) == 1) {
                                saveUserInDatabase(teacher);
                                result = SUCCESS;
                            }
                        }
                    }
                });
        if (result == SUCCESS){
            return 1;
        }
        return 0;
    }

    protected void saveUserInDatabase(Object teacherObject){
        Teacher teacher = (Teacher) teacherObject;
        teacherReference.child(teacher.getUsername()).setValue(teacher);
    }

    public Object readUserInDatabase(Object teacherObject){
        Learner learner = (Learner) teacherObject;
        resultReference = teacherReference.child(learner.getUsername());
        resultReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                resultTeacher = (Teacher) dataSnapshot.getValue();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return resultTeacher;
    }

    public void enableUserInDatabase(Object teacherObject){
        Teacher teacher = (Teacher) teacherObject;
        updateReference = teacherReference.child(teacher.getUsername());
        updateReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                updateTeacher = (Teacher) dataSnapshot.getValue();
                updateTeacher.setStatus("ENABLED");
                saveUserInDatabase(updateTeacher);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void disableUserInDatabase(Object teacherObject){
        Teacher teacher = (Teacher) teacherObject;
        updateReference = teacherReference.child(teacher.getUsername());
        updateReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                updateTeacher = (Teacher) dataSnapshot.getValue();
                updateTeacher.setStatus("DISABLED");
                saveUserInDatabase(updateTeacher);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public int validateUserInDatabase(Object teacherObject) {
        Teacher teacher = (Teacher) teacherObject;
        Query queryRef = null;
        queryRef = teacherReference.orderByChild("username").equalTo(teacher.getUsername());
        if (queryRef == null) {
            return USERDOESNOTEXISTS;
        } else {
            return USERALREADYEXISTS;
        }
    }

}