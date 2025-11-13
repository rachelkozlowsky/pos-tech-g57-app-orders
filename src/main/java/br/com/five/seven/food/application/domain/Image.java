package br.com.five.seven.food.application.domain;

import br.com.five.seven.food.adapter.in.payload.products.ImageResponse;
public class Image {

    private String url;

    public Image(String url) {
        this.url = url;
    }

    public Image() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ImageResponse toResponse() {
        ImageResponse response = new ImageResponse();
        response.setUrl(this.url);
        return response;
    }
}
