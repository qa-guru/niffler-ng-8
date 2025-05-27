package guru.qa.niffler.model;

import guru.qa.niffler.condition.Color;
import org.openqa.selenium.WebElement;

public record Bubble(
        Color color,
        String text
) {

    public String expected(){
        return formatResult(color.rgb,text);
    }

    public static class Actual{
        private final String color;
        private final String text;

        public Actual(WebElement element){
            this.color = element.getCssValue("background-color");
            this.text = element.getText();
        }

        public String color(){
            return this.color;
        }
        public String text(){
            return this.text;
        }

        public String actual(){
            return formatResult(
                    color,
                    text
            );
        }
    }

    public boolean equals(Actual actual){
        return color.rgb.equals(actual.color) && text.equals(actual.text);
    }



    private static String formatResult(String color, String description){
        return String.format(
                "color: %s ; description: '%S'",
                color,
                description
        );
    }


    public static Bubble bubble(Color color, SpendJson spendJson){
        return new Bubble(
                color,
                spendJson.description()
        );
    }
}
