package application;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RadioStation {
    private String name;
    private String logoPath;
    private String url;
}
