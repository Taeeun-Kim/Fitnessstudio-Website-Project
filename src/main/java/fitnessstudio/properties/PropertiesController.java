package fitnessstudio.properties;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin")
public class PropertiesController {

    private final FitnessstudioProperties properties;

    public PropertiesController(FitnessstudioProperties properties) {
        this.properties = properties;
    }

    @GetMapping("")
    public String overview(Model model) {
        model.addAttribute("form", properties.toForm());
        return "pages/admin/index";
    }

    @PostMapping("")
    public String properties(@Valid PropertiesForm form, Errors err) {

        if (err.hasErrors()) {
            return "pages/admin/index";
        }

        properties.setName(form.getName());
        properties.setBusinessHoursFromLocalTimes(form.getStarts(), form.getEnds());
        properties.setBounty(form.getBounty());
        properties.save();

        return "redirect:/admin";
    }
}
