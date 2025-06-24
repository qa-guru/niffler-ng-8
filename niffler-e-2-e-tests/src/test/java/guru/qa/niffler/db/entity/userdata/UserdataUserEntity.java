package guru.qa.niffler.db.entity.userdata;

import guru.qa.niffler.api.model.CurrencyValues;
import guru.qa.niffler.api.model.UserdataUserJson;
import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.proxy.HibernateProxy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Stream;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "\"user\"")
@ParametersAreNonnullByDefault
public class UserdataUserEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, columnDefinition = "UUID default gen_random_uuid()")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CurrencyValues currency;

    @Column()
    private String firstname;

    @Column()
    private String surname;

    @Column(name = "full_name")
    private String fullname;

    @Column(name = "photo", columnDefinition = "bytea")
    private byte[] photo;

    @Column(name = "photo_small", columnDefinition = "bytea")
    private byte[] photoSmall;

    @OneToMany(mappedBy = "requester", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserdataFriendshipEntity> friendshipRequests = new ArrayList<>();

    @OneToMany(mappedBy = "addressee", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserdataFriendshipEntity> friendshipAddressees = new ArrayList<>();

    public @Nonnull UserdataUserEntity setId(String id) {
        this.id = UUID.fromString(id);
        return this;
    }

    public @Nonnull UserdataUserEntity setId(UUID id) {
        this.id = id;
        return this;
    }

    public void addFriends(FriendshipStatus status, UserdataUserEntity... friends) {
        List<UserdataFriendshipEntity> friendsEntities = Stream.of(friends)
                .map(f -> {
                    UserdataFriendshipEntity fe = new UserdataFriendshipEntity();
                    fe.setRequester(this);
                    fe.setAddressee(f);
                    fe.setStatus(status);
                    fe.setCreatedDate(new Date());
                    return fe;
                }).toList();
        this.friendshipRequests.addAll(friendsEntities);
    }

    public void addInvitations(UserdataUserEntity... invitations) {
        List<UserdataFriendshipEntity> invitationsEntities = Stream.of(invitations)
                .map(i -> {
                    UserdataFriendshipEntity fe = new UserdataFriendshipEntity();
                    fe.setRequester(i);
                    fe.setAddressee(this);
                    fe.setStatus(FriendshipStatus.PENDING);
                    fe.setCreatedDate(new Date());
                    return fe;
                }).toList();
        this.friendshipAddressees.addAll(invitationsEntities);
    }

    public void removeFriends(UserdataUserEntity... friends) {
        List<UUID> idsToBeRemoved = Arrays.stream(friends).map(UserdataUserEntity::getId).toList();
        for (Iterator<UserdataFriendshipEntity> i = getFriendshipRequests().iterator(); i.hasNext(); ) {
            UserdataFriendshipEntity friendsEntity = i.next();
            if (idsToBeRemoved.contains(friendsEntity.getAddressee().getId())) {
                friendsEntity.setAddressee(null);
                i.remove();
            }
        }
    }

    public void removeInvites(UserdataUserEntity... invitations) {
        List<UUID> idsToBeRemoved = Arrays.stream(invitations).map(UserdataUserEntity::getId).toList();
        for (Iterator<UserdataFriendshipEntity> i = getFriendshipAddressees().iterator(); i.hasNext(); ) {
            UserdataFriendshipEntity friendsEntity = i.next();
            if (idsToBeRemoved.contains(friendsEntity.getRequester().getId())) {
                friendsEntity.setRequester(null);
                i.remove();
            }
        }
    }

    public static UserdataUserEntity fromJson(UserdataUserJson json) {
        return new UserdataUserEntity()
                .setId(json.getId())
                .setUsername(json.getUsername())
                .setCurrency(json.getCurrency())
                .setFirstname(json.getFirstname())
                .setSurname(json.getSurname())
                .setFullname(json.getFullname())
                .setPhoto(json.getPhoto())
                .setPhotoSmall(json.getPhotoSmall());
    }

    @Override
    public final boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        UserdataUserEntity that = (UserdataUserEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

}