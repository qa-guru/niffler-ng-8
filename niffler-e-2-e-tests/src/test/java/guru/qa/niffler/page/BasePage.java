package guru.qa.niffler.page;

public abstract class BasePage<T extends BasePage<T>>  {

    public T checkLoadedPage(){
        return (T) this;
    }

}
