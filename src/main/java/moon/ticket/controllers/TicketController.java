package moon.ticket.controllers;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import moon.ticket.configuration.DataConfig;
import moon.ticket.datamodel.Person;
import moon.ticket.info.LoggerInfo;
import moon.ticket.repositories.PersonRepository;
import moon.ticket.wrapper.LanguageWrapper;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.*;

@Data
@Controller
@Slf4j
public class TicketController {
    private final Map<String, Map<String, String>> languageData;
    private final DataConfig dataConfig;
    private Map<String, Person> userDataMap = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private PersonRepository personRepository;

    @GetMapping("/ticket")
    public String checkEmailForm(@RequestParam(name = "lang", required = false) String lang, HttpSession session, Model model) {
        if (lang == null) {
            lang = "en"; // Устанавливаем значения языка по умолчанию
        }
        session.setAttribute("lang", lang);
        dataConfig.addAttributesToModel(model, new LanguageWrapper(lang));

        return "check";
    }

    @PostMapping("/ticket")
    public String countPersonsByLocation(@RequestParam(name = "email", required = false) String email,
                                         Model model, HttpSession session) {
        LoggerInfo loggerInfo = new LoggerInfo();
        loggerInfo.loggerMethod();

        String lang = (String) session.getAttribute("lang"); // Получаем язык из сессии

        if (lang == null) {
            lang = "en";
            session.setAttribute("lang", lang);
        }

        if (email == null || email.isEmpty()) {
            dataConfig.addAttributesToModel(model, new LanguageWrapper(lang));
            return "ticket";
        }

        List<Person> people = personRepository.findByEmail(email);

        if (people != null && !people.isEmpty()) {
            model.addAttribute("person", people.get(0));
            session.setAttribute("userData", people.get(0)); // Сохраняем данные пользователя в сессии
            dataConfig.addAttributesToModel(model, new LanguageWrapper(lang));
            return "ticket";
        } else {
            return "redirect:/";
        }
    }

    @PostMapping("/ticketlanguage")
    public String mapLanguage(@RequestParam(name = "lang") String lang, HttpSession session) {
        session.setAttribute("lang", lang);
        return "redirect:/check";
    }

    @PostMapping("/downloadPdf")
    public void downloadPdf(HttpSession session, HttpServletResponse response) {
        // Выбранный язык из сеанса
        String lang = (String) session.getAttribute("lang");
        // Если язык не выбран, устанавливаем значение по умолчанию
        if (lang == null) {
            lang = "en"; // Язык по умолчанию
        }
        Person person = (Person) session.getAttribute("userData");
        if (person != null) {
            byte[] pdfBytes = generatePdf(person, lang);

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=ticket.pdf");

            try {
                response.getOutputStream().write(pdfBytes);
                response.getOutputStream().flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private byte[] generatePdf(Person person, String lang) {
        try (PDDocument document = new PDDocument()) {
            List<PDPage> pages = new ArrayList<>();
            PDPage page = new PDPage();
            pages.add(page);
            document.addPage(page);

            // Путь шрифту
            String fontRegularPath = "src/main/resources/static/fonts/static/NotoSans-Regular.ttf";

            // Загрузка шрифта
            PDType0Font fontRegular = PDType0Font.load(document, new File(fontRegularPath));

            for (PDPage currentPage : pages) {
                try (PDPageContentStream contentStream = new PDPageContentStream(document, currentPage, PDPageContentStream.AppendMode.APPEND, true)) {

                    // Добавление background image
                    InputStream backgroundImageStream = getClass().getResourceAsStream("/static/images/ticket.png");
                    assert backgroundImageStream != null;
                    PDImageXObject backgroundImage = PDImageXObject.createFromByteArray(document, IOUtils.toByteArray(backgroundImageStream), "background");

                    float imageWidth = backgroundImage.getWidth();
                    float imageHeight = backgroundImage.getHeight();

                    float pageWidth = currentPage.getMediaBox().getWidth();
                    float pageHeight = currentPage.getMediaBox().getHeight();

                    float scale = Math.min(pageWidth / imageWidth, pageHeight / imageHeight);

                    float x = (pageWidth - imageWidth * scale) / 2;
                    float y = (pageHeight - imageHeight * scale) / 2;

                    // Добавление background image
                    contentStream.drawImage(backgroundImage, x, y, imageWidth * scale, imageHeight * scale);

                    // Отрисовка текста
                    contentStream.setFont(fontRegular, 12);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(100, 700);
                    float moveDown = 74 * 2.83465f;
                    contentStream.newLineAtOffset(100, -moveDown);
                    Map<String, String> langData = getLanguageData(lang);

                    contentStream.showText(langData.get("greetings") + " " + person.getFirstName() + " " + person.getLastName());
                    contentStream.newLineAtOffset(0, -20); // Перенос на новую строку
//                    contentStream.showText(langData.get("information"));
//                    contentStream.newLineAtOffset(0, -24); // Перенос на новую строку
                    contentStream.showText("KY " + person.getId());
                    contentStream.newLineAtOffset(0, -20); // Перенос на новую строку
                    contentStream.showText(langData.get("moonTicket"));
                    contentStream.newLineAtOffset(0, -20); // Перенос на новую строку
                    contentStream.showText(person.getFirstName());
                    contentStream.newLineAtOffset(0, -20); // Перенос на новую строку
                    contentStream.showText(person.getLastName());
                    contentStream.newLineAtOffset(0, -20); // Перенос на новую строку
                    contentStream.showText(langData.get("location") + ": " + langData.get("locationParameter"));
                    contentStream.newLineAtOffset(0, -20); // Перенос на новую строку
                    contentStream.showText(langData.get("rocket") + ": " + langData.get("rocketName"));
                    contentStream.newLineAtOffset(0, -20); // Перенос на новую строку
                    contentStream.showText(langData.get("departure") + ": " + langData.get("departure"));
                    contentStream.newLineAtOffset(0, -20); // Перенос на новую строку
                    contentStream.showText(langData.get("arrival") + ": " + langData.get("arrival"));
                    contentStream.newLineAtOffset(0, -20); // Перенос на новую строку
                    contentStream.showText(langData.get("earthName") + ": " + langData.get("earthName"));
                    contentStream.newLineAtOffset(0, -20); // Перенос на новую строку
                    contentStream.showText(langData.get("moonName") + ": " + langData.get("moonName"));
                    contentStream.endText();

                    // Применение стилей (красный цвет)
                    contentStream.setFont(fontRegular, 14);
                    contentStream.setNonStrokingColor(Color.RED);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(100, 600);

//                contentStream.showText("Styled Text");
                    contentStream.endText();
                }
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            document.save(byteArrayOutputStream);
            document.close();

            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }


    private Map<String, String> getLanguageData(String lang) {
        // Получение данных для выбранного языка
        return languageData.getOrDefault(lang, Collections.emptyMap());
    }

    @Bean
    public Map<String, Map<String, String>> getLanguageData() {
        try {
            // Проверяем наличие файла data.json
            ClassPathResource resource = new ClassPathResource("/data/data.json");
            if (!resource.exists()) {
                throw new RuntimeException("File data.json not found");
            }
            // Считываем данные из файла
            InputStream is = resource.getInputStream();
            JavaType type = objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Map.class);
            Map<String, Map<String, String>> data = objectMapper.readValue(is, type);

            // Проверяем наличие данных в файле
            if (data == null || data.isEmpty()) {
                throw new RuntimeException("No data found in data.json");
            }
            return data;
        } catch (IOException e) {
            throw new RuntimeException("Error loading language data", e);
        }
    }
}








