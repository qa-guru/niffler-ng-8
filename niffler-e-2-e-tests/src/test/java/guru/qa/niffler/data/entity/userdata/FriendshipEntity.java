package guru.qa.niffler.data.entity.userdata;

import guru.qa.niffler.data.FriendShipId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "friendship")
@IdClass(FriendShipId.class)
public class FriendshipEntity {

    @Id
    @ManyToOne
    @JoinColumn(name = "requester_id", referencedColumnName = "id")
    private UserEntity requester;

    @Id
    @ManyToOne
    @JoinColumn(name = "addressee_id", referencedColumnName = "id")
    private UserEntity addressee;

    @Column(name = "created_date", columnDefinition = "DATE", nullable = false)
    private Date createdDate;

    @Enumerated(EnumType.STRING)
    private FriendshipStatus status;
}
