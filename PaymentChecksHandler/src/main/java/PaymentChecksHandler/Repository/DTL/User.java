package main.java.PaymentChecksHandler.Repository.DTL;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long id;
    private String email;
    private String phoneNumber;
    private String firstName;
    private String lastName;
}