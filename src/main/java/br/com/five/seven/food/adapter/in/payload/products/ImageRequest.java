package br.com.five.seven.food.adapter.in.payload.products;

import br.com.five.seven.food.application.domain.Image;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ImageRequest {

    private String url;

    public ImageRequest(String url) {
        this.url = url;
    }

    @JsonCreator
    public static ImageRequest from(@JsonProperty("url") String url) {
        return new ImageRequest(url);
    }

    public Image toDomain() {
        Image image = new Image();
        image.setUrl(this.url);
        return image;
    }

}
