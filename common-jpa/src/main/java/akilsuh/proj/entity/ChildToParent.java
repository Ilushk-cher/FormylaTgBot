package akilsuh.proj.entity;

import akilsuh.proj.enums.ParentRole;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@EqualsAndHashCode (exclude = "id")
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table (name = "child_to_parent")
public class ChildToParent {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    private Long childTelegramUserId;

    private Long parentTelegramUserId;

    private String childFirstName;

    private String childLastName;

    private Integer classNum;

    private String parentFirstName;

    private String parentLastName;

    @Enumerated (EnumType.STRING)
    private ParentRole parentRole;
}
