package io.lemonjuice.flan_mai_plugin.model;

import java.util.ArrayList;
import java.util.List;

public class Song {
    public int id;
    public String title;
    public String type;
    public List<Float> ds = new ArrayList<>();
    public List<String> level = new ArrayList<>();
    public List<Integer> cids = new ArrayList<>();
    public List<Chart> charts = new ArrayList<>();
    public Info info;

    public List<String> alias = new ArrayList<>();

    public static class Chart {
        public List<Integer> notes = new ArrayList<>();
        public String author;
        public float fitDIff = -1;
    }

    public static class Info {
        public String title;
        public String artist;
        public String category;
        public int bpm;
        public String releaseDate;
        public String from;
        public boolean isNew;
    }
}
