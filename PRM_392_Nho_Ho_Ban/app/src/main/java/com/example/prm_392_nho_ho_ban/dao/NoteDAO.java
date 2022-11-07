package com.example.prm_392_nho_ho_ban.dao;

import static com.example.prm_392_nho_ho_ban.MyApplication.dbRoom;

import com.example.prm_392_nho_ho_ban.bean.Note;
import com.example.prm_392_nho_ho_ban.bean.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class NoteDAO {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void syncRoomToFirebase(FirebaseCallBack firebaseCallBack) {
        ArrayList<Note> allNote = (ArrayList<Note>) dbRoom.createNoteDAO().getAllNote(User.USER.getUid());
        for (Note note : allNote) {
            if (note.getId().length() > 0) {
                db.collection("note").document(note.getId())
                        .set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                firebaseCallBack.onCallBack();
                            }
                        });
            }
        }
    }

    public void syncFirebaseToRoom(FirebaseCallBack firebaseCallBack, String uId) {
        RoomNoteDAO roomNoteDAO = dbRoom.createNoteDAO();
        db.collection("note").whereEqualTo("uid", uId)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            roomNoteDAO.insert(document.toObject(Note.class));
                        }
                        firebaseCallBack.onCallBack();
                    }
                });

    }

    public void createNote(FirebaseCallBack firebaseCallBack, Note note) {
        db.collection("note").add(note).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                firebaseCallBack.onCallBack();
            }
        });
    }

    public void updateNote(FirebaseCallBack firebaseCallBack, Note note, String id) {
        db.collection("note").document(id).set(note)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        firebaseCallBack.onCallBack();
                    }
                });
    }

    public void deleteNote(FirebaseCallBack firebaseCallBack, String id) {
        db.collection("note").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                firebaseCallBack.onCallBack();
            }
        });
    }


    public interface FirebaseCallBack {
        void onCallBack(ArrayList<Note> noteList);

        void onCallBack();

        void onCallBack(ArrayList<Note> noteList, ArrayList<Note> noteUnpinList);
    }

}
