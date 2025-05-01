package guru.qa.niffler.db.entity.auth;

import guru.qa.niffler.api.model.AuthUserJson;
import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.util.*;

import static jakarta.persistence.FetchType.EAGER;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "\"user\"")
public class AuthUserEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, columnDefinition = "UUID default gen_random_uuid()")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Boolean enabled;

    @Column(name = "account_non_expired", nullable = false)
    private Boolean accountNonExpired;

    @Column(name = "account_non_locked", nullable = false)
    private Boolean accountNonLocked;

    @Column(name = "credentials_non_expired", nullable = false)
    private Boolean credentialsNonExpired;

    @OneToMany(fetch = EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
    private List<AuthAuthorityEntity> authorities = new ArrayList<>();

    public void removeAuthority(AuthAuthorityEntity authority) {
        this.authorities.remove(authority);
        authority.setUser(null);
    }

    public void addAuthorities(AuthAuthorityEntity... authorities) {
        addAuthorities(Arrays.asList(authorities));
    }

    public void addAuthorities(List<AuthAuthorityEntity> authorities) {
        authorities.forEach(au -> au.setUser(this));
        this.authorities.addAll(authorities);
    }

    public void addAuthorities(AuthAuthorityEntity authority) {
        this.authorities.add(authority);
        authority.setUser(this);
    }


    public static AuthUserEntity fromJson(AuthUserJson json) {
        return new AuthUserEntity()
                .setId(json.getId())
                .setUsername(json.getUsername())
                .setPassword(json.getPassword())
                .setEnabled(json.getEnabled())
                .setAccountNonExpired(json.getAccountNonExpired())
                .setAccountNonLocked(json.getAccountNonLocked())
                .setCredentialsNonExpired(json.getCredentialsNonExpired())
                .setAuthorities(AuthAuthorityEntity.fromJson(json.getAuthorities()));
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        AuthUserEntity that = (AuthUserEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

}
