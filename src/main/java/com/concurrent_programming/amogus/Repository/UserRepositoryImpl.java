package com.concurrent_programming.amogus.Repository;

import com.concurrent_programming.amogus.Model.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public class UserRepositoryImpl implements UserRepository {

    Firestore firestore = FirestoreClient.getFirestore();

    @Override
    public List<User> getAllUsers() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> apiFuture = firestore.collection("user").get();
        List<QueryDocumentSnapshot> docs = apiFuture.get().getDocuments();
        List<User> users = new ArrayList<>();
        docs.forEach(doc -> {
            users.add(doc.toObject(User.class));
        });

        return users;
    }
    @Override
    public User getUser(String id) throws ExecutionException, InterruptedException {
        DocumentReference documentReference = firestore.collection("user").document(id);
        ApiFuture<DocumentSnapshot> apiFuture = documentReference.get();
        DocumentSnapshot doc = apiFuture.get();
        User user;
        if(doc.exists()) {
            user = doc.toObject(User.class);
            return user;
        }

        return null;
    }

    @Override
    public User createUser(User user) {
        ApiFuture<WriteResult> apiFuture = firestore.collection("user").document(user.getId()).set(user);
        return user;
    }
    @Override
    public User updateUser(User user) throws ExecutionException, InterruptedException {
        DocumentReference documentReference = firestore.collection("user").document(user.getId());
        ApiFuture<DocumentSnapshot> apiFuture = documentReference.get();
        DocumentSnapshot doc = apiFuture.get();
        if(doc.exists()) {
            ApiFuture<WriteResult> writeResultApiFuture = firestore.collection("user").document(user.getId()).set(user);
            return user;
        }
        return null;
    }

    @Override
    public String deleteUser(String id) throws ExecutionException, InterruptedException {
        DocumentReference documentReference = firestore.collection("user").document(id);
        ApiFuture<DocumentSnapshot> apiFuture = documentReference.get();
        DocumentSnapshot doc = apiFuture.get();
        if(doc.exists()) {
            ApiFuture<WriteResult> writeResultApiFuture = firestore.collection("user").document(id).delete();
            return "Deleted";
        }
        return "";
    }

}
