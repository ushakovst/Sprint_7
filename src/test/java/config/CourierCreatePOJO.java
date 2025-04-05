package config;
import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class CourierCreatePOJO {
    private String login;
    private String password;
    private String firstName;
}
//спасибо вам за ваше терпение и понимание!