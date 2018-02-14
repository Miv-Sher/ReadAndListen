package com.miv_sher.readlistening;

/**
 * Created by Miv-Sher on 08.07.2015.
 */
import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;



import java.util.ArrayList;
import java.util.List;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private List<Book> dataSource;
    private Activity mainActivity;

    public RecyclerAdapter(List<Book> library, Activity mainActivity){
        dataSource = library;
        this.mainActivity = mainActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false);


        ViewHolder viewHolder = new ViewHolder(view,mainActivity);
        return viewHolder;


    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Book book = dataSource.get(position);

        holder.bookName.setText(book.name);
        holder.bookAuthor.setText(book.author);
        holder.bookCover.setImageResource(book.cover);
        holder.article = book.name;
        holder.path =  book.path;
        holder.audio = book.audio;
        holder.json = book.json;

    }

    @Override
    public int getItemCount() {
        return dataSource.size();

    }

    public static  class ViewHolder extends RecyclerView.ViewHolder{
        protected TextView bookName;
        protected TextView bookAuthor;
        protected ImageView bookCover;
        protected String path;
        protected int audio;
        protected  String article;
        protected String json;

        public ViewHolder(final View itemView, final Activity activity) {
            super(itemView);
            bookName =  (TextView) itemView.findViewById(R.id.book_name);
            bookAuthor =  (TextView) itemView.findViewById(R.id.book_author);
            bookCover =  (ImageView) itemView.findViewById(R.id.book_cover);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Intent intent = new Intent(activity, ReaderActivity.class);
                    intent.putExtra("path", path);
                    intent.putExtra("audio", audio);
                    intent.putExtra("name", article);
                    intent.putExtra("json", json);
                    itemView.getContext().startActivity(intent);
                }
            });


        }


    }
}