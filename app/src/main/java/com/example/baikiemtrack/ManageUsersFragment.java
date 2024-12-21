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

import com.example.baikiemtrack.model.Thongtin;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;
import java.util.UUID;

public class ManageUsersFragment extends Fragment {

    private RecyclerView recyclerView;
    private com.example.baikiemtrack.adapter.ThongtinAdapter userAdapter;
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

    private void deleteUser(Thongtin thongtin) {

    }




}
