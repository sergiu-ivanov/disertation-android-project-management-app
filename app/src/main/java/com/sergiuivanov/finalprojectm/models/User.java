package com.sergiuivanov.finalprojectm.models;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String email, name, pass, id, type;


    public User(String email, String name, String pass, String id, String type) {
        this.email = email;
        this.name = name;
        this.pass = pass;
        this.id = id;
        this.type = type;
    }

    public User(){}

    protected User(Parcel in) {
        email = in.readString();
        name = in.readString();
        pass = in.readString();
        id = in.readString();
        type = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(name);
        dest.writeString(pass);
        dest.writeString(id);
        dest.writeString(type);
    }
}
