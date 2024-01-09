package moon.ticket.controllers;

import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moon.ticket.api.ExternalAPIService;
import moon.ticket.command.CreatePersonCommand;
import moon.ticket.configuration.DataConfig;
import moon.ticket.counter.LocationCountDTO;
import moon.ticket.datamodel.Person;
import moon.ticket.dto.JsonCountries;
import moon.ticket.info.LoggerInfo;
import moon.ticket.repositories.PersonRepository;
import moon.ticket.wrapper.LanguageWrapper;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Slf4j
@Controller
@RequiredArgsConstructor
public class AdminController {
    private final PersonRepository personRepository;
    private final ApplicationContext applicationContext;
    private final ExternalAPIService externalAPIService;
    private final Map<String, Map<String, String>> languageData;
    private final DataConfig dataConfig;
    private final JsonCountries jsonCountries;
    @PostMapping("/changeLanguage")
    public String changeLanguage(@RequestParam(name = "lang") String lang, HttpSession session, Model model) {
        session.setAttribute("lang", lang);
        return "redirect:/";
    }

    @GetMapping()
    public String countAllPersons(Model model, HttpSession session) {
        LoggerInfo loggerInfo = new LoggerInfo();
        loggerInfo.loggerMethod();

        String lang = (String) session.getAttribute("lang");
        if (lang == null) {
            lang = "en";
            session.setAttribute("lang", lang);
        }

        dataConfig.addAttributesToModel(model, new LanguageWrapper(lang));

        List<Person> getPeople = personRepository.findAll();
        long count = personRepository.count();
        model.addAttribute("countries", jsonCountries.getData().stream().map(c -> c.getCountry()).toList());
        model.addAttribute("persons", getPeople);
        model.addAttribute("profileName", applicationContext.getBean("profileName", String.class));
        model.addAttribute("totalCount", languageData.get(lang).get("totalCount") + "\n " + count);

        return "index";
    }

    @GetMapping("/index/cities")
    public ResponseEntity<List<String>> getCitiesByCountry(@RequestParam(name = "country") String country) {
        var countryWithCity = jsonCountries.getData().stream().filter(c-> c.getCountry().equals(country)).findFirst();
        return ResponseEntity.ok(countryWithCity.get().getCities());
    }

    @PostMapping("/maplanguage")
    public String mapLanguage(@RequestParam(name = "lang") String lang, HttpSession session, Model model) {
        session.setAttribute("lang", lang);
        return "redirect:/map";
    }

    @GetMapping("/map")
    public String countStudentsByLocation(@RequestParam(name = "selectedCountry", required = false) String selectedCountry,
                                          HttpSession session, Model model) {
        String lang = (String) session.getAttribute("lang");
        if (lang == null) {
            lang = "en";
            session.setAttribute("lang", lang);
        }

        dataConfig.addAttributesToModel(model, new LanguageWrapper(lang));

        if (selectedCountry == null) {
            List<String> countries = personRepository.findAllCountries();
            model.addAttribute("countries", countries);
            return "map";
        }

        List<Object[]> locationCountList = personRepository.countPersonsByCityAndCountry(selectedCountry);
        List<LocationCountDTO> locationCountDTOList = locationCountList.stream()
                .map(row -> new LocationCountDTO((String) row[0], (Long) row[1]))
                .collect(Collectors.toList());

        Long totalCountryCount = locationCountDTOList.stream()
                .mapToLong(LocationCountDTO::getCount)
                .sum();

        model.addAttribute("locationCountDTOList", locationCountDTOList);
        model.addAttribute("totalCountryCount", totalCountryCount);
        model.addAttribute("selectedCountry", selectedCountry);

        return "map";
    }

    @PostMapping("/faqlanguage")
    public String faqLanguage(@RequestParam(name = "lang") String lang, HttpSession session, Model model) {
        session.setAttribute("lang", lang);
        return "redirect:/faq";
    }
    @GetMapping("/faq")
    public String faqPage(HttpSession session, Model model) {
        String lang = (String) session.getAttribute("lang");
        if (lang == null) {
            lang = "en";
            session.setAttribute("lang", lang);
        }

        LanguageWrapper languageWrapper = new LanguageWrapper(lang);
        dataConfig.addAttributesToModel(model, languageWrapper);
        return "faq";
    }
    @PostMapping("/checklanguage")
    public String checkLanguage(@RequestParam(name = "lang") String lang, HttpSession session, Model model) {
        session.setAttribute("lang", lang);
        return "redirect:/check";
    }
    @GetMapping("/check")
    public String checkPage(HttpSession session, Model model) {
        String lang = (String) session.getAttribute("lang");
        if (lang == null) {
            lang = "en";
            session.setAttribute("lang", lang);
        }

        LanguageWrapper languageWrapper = new LanguageWrapper(lang);
        dataConfig.addAttributesToModel(model, languageWrapper);
        return "check";
    }

    @ModelAttribute("person")
    public Person createPerson() {
        return new Person();
    }

    @ModelAttribute("personCommand")
    public CreatePersonCommand createPersonCommand() {
        return new CreatePersonCommand();
    }

    @Transactional
    @PostMapping("/registration")
    public String create(@Validated CreatePersonCommand createPersonCommand, BindingResult bindingResult,
                         @RequestParam(name = "email", required = false) String email,
                         Model model, HttpSession session) {
        String lang = (String) session.getAttribute("lang");
        if (lang == null) {
            lang = "en";
            session.setAttribute("lang", lang);
        }

        if (bindingResult.hasErrors()) {
            log.error(bindingResult.toString());
            model.addAttribute("createPersonCommand", createPersonCommand);
            model.addAttribute("bindingResult", bindingResult);
            model.addAttribute("status", "ERROR");

            LanguageWrapper languageWrapper = new LanguageWrapper(lang);
            dataConfig.addAttributesToModel(model, languageWrapper);

            return "error";
        }

        if (personRepository.existsByEmail(email)) {
            model.addAttribute("status", "NOT_CONFIRMED");
        } else {
            Person person = createPersonCommand.toEntity();
            personRepository.save(person);
            model.addAttribute("status", "CONFIRMED");
        }

        LanguageWrapper languageWrapper = new LanguageWrapper(lang);
        dataConfig.addAttributesToModel(model, languageWrapper);

        return "index";
    }
}
