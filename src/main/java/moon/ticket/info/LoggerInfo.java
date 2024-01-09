package moon.ticket.info;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerInfo {
    private static final Logger logger = LogManager.getLogger(LoggerInfo.class);
    public void loggerMethod() {
        logger.warn("Предупреждение: Важное сообщение");
        logger.error("Ошибка: Что-то пошло не так");
        String d = "logging.level.org.hibernate.SQL=DEBUG";
        logger.debug("Сообщение отладки: " + d);
        logger.info("Информационное сообщение: Запуск приложения");
        logger.trace("Сообщение трассировки: Подробные сведения о процессе");
    }
}
