package moon.ticket.controllers;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.io.InputStream;

@Controller
public class FontController {


    @GetMapping("/fonts/{fontName}")
    public void getFont(@PathVariable String fontName, HttpServletResponse response) throws IOException {
        // Чтение файла шрифта и запись его в HttpServletResponse
        try (InputStream fontStream = getClass().getResourceAsStream("/static/fonts/" + fontName)) {
            if (fontStream != null) {
                IOUtils.copy(fontStream, response.getOutputStream());
                response.setContentType("application/font-truetype");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        }
    }
}

