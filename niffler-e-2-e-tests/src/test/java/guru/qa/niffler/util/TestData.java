package guru.qa.niffler.util;

import guru.qa.niffler.api.model.CategoryJson;
import guru.qa.niffler.api.model.SpendJson;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TestData {

    private final List<CategoryJson> categories = new ArrayList<>();
    private final List<SpendJson> spends = new ArrayList<>();
    private final List<String> friendsNames = new ArrayList<>();
    private final List<String> InInviteNames = new ArrayList<>();
    private final List<String> OutInviteNames = new ArrayList<>();

}
