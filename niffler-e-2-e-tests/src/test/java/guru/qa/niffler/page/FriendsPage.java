package guru.qa.niffler.page;

import guru.qa.niffler.page.component.FriendsTable;
import guru.qa.niffler.page.component.basicComponents.Button;
import org.openqa.selenium.By;

public class FriendsPage {

    public FriendsTable table = new FriendsTable();

    public class Delete {
        public Button deleteButton = new Button(By.xpath("//h2[.='Delete friend']//following::button[.='Delete']"));
    }

    public Delete delete() {
        return new FriendsPage.Delete();
    }

    public class Decline {
        public Button declineBtn = new Button(By.xpath("//h2[.='Decline friendship']//following::button[.='Decline']"));
    }

    public Decline decline() {
        return new FriendsPage.Decline();
    }

}
