package cc.mrbird.demo.domain;

import lombok.*;
import org.springframework.stereotype.Component;

/**
 * @author MrBird
 */
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Data
// @Component
public class User {
    private String name;
    private Integer age;
}
