package moon.ticket.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AjaxController {
    @GetMapping("/ajax")
    public String index() {
        return "ajax";
    }
}
