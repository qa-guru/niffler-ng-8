package guru.qa.niffler.db.entity.auth;

import guru.qa.niffler.api.model.Authority;
import guru.qa.niffler.api.model.AuthorityJson;
import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "authority")
public class AuthAuthorityEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, columnDefinition = "UUID default gen_random_uuid()")
    private UUID id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Authority authority;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private AuthUserEntity user;

    public static AuthAuthorityEntity fromJson(AuthorityJson json) {
        return new AuthAuthorityEntity()
                .setId(json.getId())
                .setAuthority(json.getAuthority());
    }

    public static List<AuthAuthorityEntity> fromJson(List<AuthorityJson> jsons) {
        return jsons.stream()
                .map(AuthAuthorityEntity::fromJson)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "AuthAuthorityEntity{" +
                "authority=" + authority +
                ", id=" + id +
                ", userId=" + user.getId() +
                '}';
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        AuthAuthorityEntity that = (AuthAuthorityEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

}
