package guru.qa.niffler.db.entity.userdata;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
public class FriendShipId implements Serializable {

    private UUID requester;
    private UUID addressee;

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendShipId friendsId = (FriendShipId) o;
        return Objects.equals(requester, friendsId.requester) && Objects.equals(addressee, friendsId.addressee);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requester, addressee);
    }
}
