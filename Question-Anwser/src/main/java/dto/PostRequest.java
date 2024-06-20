package dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class PostRequest {
    private String title;
    private String content;
    private Set<String> tags;
}
