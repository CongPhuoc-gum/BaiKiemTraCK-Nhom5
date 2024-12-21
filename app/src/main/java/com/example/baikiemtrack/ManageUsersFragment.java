package com.example.baikiemtrack;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;
import java.util.UUID;

public class ManageUsersFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private ArrayList<User> userList;
    private DatabaseReference databaseReference;



    public ManageUsersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_users, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList, this::editUser, this::deleteUser);
        recyclerView.setAdapter(userAdapter);

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        loadUsers();

        view.findViewById(R.id.btn_add_user).setOnClickListener(v -> showAddUserDialog());

        return view;
    }

    private void loadUsers() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    User user = data.getValue(User.class);
                    userList.add(user);
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi tải dữ liệu!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddUserDialog() {
        AddEditUserDialog dialog = new AddEditUserDialog(getContext(), null, this::saveUser);
        dialog.show();
    }

    private void editUser(User user) {
        AddEditUserDialog dialog = new AddEditUserDialog(getContext(), user, this::saveUser);
        dialog.show();
    }

    private void saveUser(User user) {
        if (user.getId() == null || user.getId().isEmpty()) {
            user.setId(UUID.randomUUID().toString()); // Tạo ID nếu chưa có
        }

        if (selectedImageUri != null) {
            uploadImage(user, uri -> {
                user.setAvatarUrl(uri);
                databaseReference.child(user.getId()).setValue(user).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Lưu thành công!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Lỗi lưu dữ liệu!", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        } else {
            databaseReference.child(user.getId()).setValue(user).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Lưu thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Lỗi lưu dữ liệu!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void deleteUser(User user) {
        if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
            StorageReference fileRef = FirebaseStorage.getInstance().getReferenceFromUrl(user.getAvatarUrl());
            fileRef.delete().addOnSuccessListener(aVoid -> {
                databaseReference.child(user.getId()).removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Xóa thành công!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Lỗi xóa dữ liệu!", Toast.LENGTH_SHORT).show();
                    }
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Lỗi xóa ảnh!", Toast.LENGTH_SHORT).show();
            });
        } else {
            databaseReference.child(user.getId()).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Xóa thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Lỗi xóa dữ liệu!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.getData();
        }
    }

    private void uploadImage(User user, OnSuccessListener<String> onSuccessListener) {
        if (selectedImageUri != null) {
            StorageReference fileRef = storageReference.child("avatars/" + user.getId() + ".jpg");
            fileRef.putFile(selectedImageUri).addOnSuccessListener(taskSnapshot ->
                    fileRef.getDownloadUrl().addOnSuccessListener(onSuccessListener)
            ).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Lỗi upload ảnh!", Toast.LENGTH_SHORT).show();
            });
        } else {
            onSuccessListener.onSuccess(null);
        }
    }
}
