package akilsuh.proj.entity;

import akilsuh.proj.enums.UserRole;
import akilsuh.proj.enums.UserState;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "app_user")
public class AppUser {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    private Long telegramUserId;

    @CreationTimestamp
    private LocalDateTime firstLoginDate;

    private String tgFirstName;

    private String tgLastName;

    private String username;

    @Enumerated (EnumType.STRING)
    private UserRole role;

    private String firstName;

    private String lastName;

    private Integer classNum;

    private Integer groupNum;

    private String phoneNumber;

    private Boolean isActive;

    @Enumerated (EnumType.STRING)
    private UserState state;
    private String callBackId;

    @Override
    public String toString() {
        return "Роль: " + this.getRole().toString() +
                "\nИмя: " + this.getFirstName() +
                "\nФамилия: " + this.getLastName() +
                "\nКласс: " + this.getClassNum() +
                "\nГруппа: " + this.getGroupNum() +
                "\nНомер телефона: " + this.getPhoneNumber();
    }
}
