package com.example.myapplication;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


    @Entity
    public class User {
        @PrimaryKey (autoGenerate = true)
        public int uid;

        @ColumnInfo(name = "Amount")
        public String paidAmount;

        @ColumnInfo(name = "Day")
        public String paiDay;

        @ColumnInfo(name = "Month")
        public String paiMonth;

        @ColumnInfo(name = "Year")
        public String paiYear;

        @ColumnInfo(name = "SysTime")
        public int systemTime;


        public User(int uid, String paidAmount, String paiDay,  String paiMonth,  String paiYear ,int systemTime) {
            this.uid = uid;
            this.paidAmount = paidAmount;
            this.paiDay = paiDay;
            this.paiMonth = paiMonth;
            this.paiYear = paiYear;
            this.systemTime= systemTime;


        }

        public int getUid() {
            return uid;
        }

        public String getPaidAmount() {
            return paidAmount;
        }

        public String getPaiDay() {
            return paiDay;
        }

        public void setPaiDay(String paiDay) {
            this.paiDay = paiDay;
        }

        public String getPaiMonth() {
            return paiMonth;
        }

        public void setPaiMonth(String paiMonth) {
            this.paiMonth = paiMonth;
        }

        public String getPaiYear() {
            return paiYear;
        }

        public void setPaiYear(String paiYear) {
            this.paiYear = paiYear;
        }


        public int getSystemTime() {
            return systemTime;
        }

        public void setSystemTime(int systemTime) {
            this.systemTime = systemTime;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public void setPaidAmount(String paidAmount) {
            this.paidAmount = paidAmount;
        }


    }
