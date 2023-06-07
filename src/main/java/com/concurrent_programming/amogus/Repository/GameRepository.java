package com.concurrent_programming.amogus.Repository;

import com.concurrent_programming.amogus.Model.GameState;
import com.concurrent_programming.amogus.Model.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public class GameRepository{
    Firestore firestore = FirestoreClient.getFirestore();

    public GameState createGame(GameState game) {
        if (game.isInLobby() == true) {
            return game;
        }
        // ApiFuture<WriteResult> apiFuture = firestore.collection("game").document(game.isInLobby()).set(game);
        return game;
    }

    public GameState update(GameState game) throws ExecutionException, InterruptedException {
        DocumentReference documentReference = firestore.collection("game").document(game.currentPlayerSurvived().getId());
        ApiFuture<DocumentSnapshot> apiFuture = documentReference.get();
        DocumentSnapshot doc = apiFuture.get();
        if(doc.exists()) {
            ApiFuture<WriteResult> writeResultApiFuture = firestore.collection("game").document(game.getId()).set(game);
            return game;
        }
        return null;
    }

    public GameState save(GameState game) throws ExecutionException, InterruptedException{
        DocumentReference documentReference = firestore.collection("game").document(game.currentPlayerSurvived().getId());
        ApiFuture<DocumentSnapshot> apiFuture = documentReference.get();
        DocumentSnapshot doc = apiFuture.get();
        if(doc.exists()) {
            ApiFuture<WriteResult> writeResultApiFuture = firestore.collection("game").document(game.getId()).set(game);
            return game;
        }
        return null;
    }
}

