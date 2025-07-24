package akilsuh.proj.entity;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.sql.SQLType;

@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table (name = "raw_data")
@Convert (attributeName = "jsonb", converter = JsonBinaryType.class)
public class RawData {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column (name = "event", columnDefinition = "jsonb")
    private Update event;
}
