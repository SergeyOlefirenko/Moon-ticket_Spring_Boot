package moon.ticket.wrapper;

import lombok.*;

@Getter
@Data


public class LanguageWrapper {
    private String lang;

    public LanguageWrapper() {
        // Необходим конструктор без параметров для корректной работы сессии
        //Но здесь у нас Lombok, поэтому, возможно, в этом и нет необходимости.
    }

    public LanguageWrapper(String lang) {
        this.lang = lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}


