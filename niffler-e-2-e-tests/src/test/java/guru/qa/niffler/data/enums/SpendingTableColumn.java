package guru.qa.niffler.data.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum SpendingTableColumn {

    category("Category"),
    amount("Amount"),
    description("Description"),
    date("Date");

    public final String name;
}