package com.example.baikiemtrack.model;

public class Thongtin {
    private String name;
    private String Msv;
    private String Lop;

    public Thongtin() {
    }

    public Thongtin(String name, String Msv, String Lop) {
        this.name = name;
        this.Msv = Msv;
        this.Lop = Lop;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMsv() {
        return Msv;
    }

    public void setMsv(String msv) {
        Msv = msv;
    }

    public String getLop() {
        return Lop;
    }

    public void setLop(String lop) {
        Lop = lop;
    }
}
