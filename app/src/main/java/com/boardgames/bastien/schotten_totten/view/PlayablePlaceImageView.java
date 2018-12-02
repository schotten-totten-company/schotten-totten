package com.boardgames.bastien.schotten_totten.view;

import android.widget.ImageView;

public class PlayablePlaceImageView {

    private boolean isPlaceToPlay = false;
    private final ImageView imageView;

    public PlayablePlaceImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public ImageView getImageView() {
        return this.imageView;
    }
    public boolean getIsPlaceToPlay() {
        return this.isPlaceToPlay;
    }

    public void setIsPlaceToPlay(final boolean newValue) {
        this.isPlaceToPlay = newValue;
    }
}
