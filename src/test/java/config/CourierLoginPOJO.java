package config;
import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@Builder
public class CourierLoginPOJO {
    private String login;
    private String password;
}
