package com.example.myapplication;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;


@Dao
    public interface UserDao {
        @Query("SELECT * FROM user")
        List<User> getAll();

        @Query("SELECT COUNT(*) FROM user")
        int countAll();


        @Query("SELECT * FROM user WHERE uid IN (:userIds)")
        List<User> loadAllByIds(int[] userIds);

        //Obsolete function --> Will be cleaned up
//        @Query("SELECT * FROM user WHERE Amount LIKE :first AND " +
//                "Date LIKE :last LIMIT 1")
//        User findByName(String first, String last);

        @Query("SELECT * FROM user WHERE uid=:userid")
        List<User> getinfo(int userid);

        @Query("DELETE FROM user")

        void drop_table();

        //Special methods to get Dates and costs respectively
        //Dates

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void insertrecord(User users);

        @Delete
        void delete(User user);




}

