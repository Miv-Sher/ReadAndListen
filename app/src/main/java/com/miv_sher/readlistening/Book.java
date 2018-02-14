package com.miv_sher.readlistening;

import android.media.Image;

/**
 * Created by NessaMain on 08.07.2015.
 *
 * Keep all needed information about loaded book.
 *
 * Should I get all info from .epub right in this class? Yep, TODO
 */
public class Book {
    /**
     * a path to the book on a local storage.
     * Should we do String[] to keep pathes to all parts of this book in one obj?
     */
    public String path;
    public String json;
    public int audio;
    public String name;
    public String author;

    //just for test for now
    public int cover;


    public Book(String path, String json, int audio, String name, String author, int cover)
    {
        this.path = path;
        this.json = json;
        this.audio = audio;
        this.name = name;
        if(author != null) {
            this.author = author;
        }
        else
        {
            this.author = "Unknown";
        }
        this.cover = cover;
    }

}
