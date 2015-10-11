package casak.ru.parallaxed;

import android.graphics.Bitmap;

public class Frame {
    private Bitmap image;
    private String title;
    private String name;
    private int viewCount;

    public Frame(Bitmap image, String title, String name, int viewCount){
        this.image = image;
        this.title = title;
        this.name = name;
        this.viewCount = viewCount;
    }

    @Override
    public String toString(){
        return "Image: " + (image == null ? "doesen`t exist." : "exist.") + " Title: " +
                title + " Artist: " + name + " Views: " + viewCount;
    }

    public Bitmap getImage(){
        return image;
    }

    public String getTitle(){
        return title;
    }

    public String getName(){
        return name;
    }

    public int getViews(){
        return viewCount;
    }

}