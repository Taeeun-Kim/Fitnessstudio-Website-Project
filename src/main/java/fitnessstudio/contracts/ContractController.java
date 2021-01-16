package fitnessstudio.contracts;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin/contracts")
public class ContractController {

    private ContractManager contractManager;

    public ContractController(ContractManager contractManager) {
        this.contractManager = contractManager;
    }

    @GetMapping("")
    public String contracts(Model model) {
        model.addAttribute("contracts", contractManager.findAll());
        return "pages/admin/contracts/index";
    }

    @GetMapping("/add")
    public String addGet(Model model, ContractForm contractForm) {
        model.addAttribute("contractForm", contractForm);
        return "pages/admin/contracts/add";
    }

    @PostMapping("/add")
    public String addPost(@Valid ContractForm contractForm, Errors result) {
        if (contractManager.findByTitle(contractForm.getTitle()).isPresent()) {
            result.rejectValue("title", "title.duplicated", "Titel bereits vergeben");
        }

        if (result.hasErrors()) {
            return "pages/admin/contracts/add";
        }

        contractManager.addContract(contractForm);

        return "redirect:/admin/contracts";
    }

    @GetMapping("/{id}")
    public String editContractGet(@PathVariable Long id, Model model) {
        Contract contract = contractManager.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Contract ID"));

        model.addAttribute("contractForm", new ContractForm(contract.getTitle(), contract.getDescription(),
                contract.getFee().getNumber().floatValue()));

        return "pages/admin/contracts/edit";
    }

    @PostMapping("/{id}")
    public String editContractPost(@PathVariable Long id, ContractForm contractForm, Errors result) {
        Contract contract = contractManager.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Contract ID"));

        if (contractManager.findByTitle(contractForm.getTitle()).isPresent()) {
            result.rejectValue("title", "title.duplicated", "Titel bereits vergeben");
        }

        if (result.hasErrors()) {
            return "pages/admin/contracts/edit";
        }

        contractManager.editContract(contractForm, contract);

        return "redirect:/admin/contracts";
    }

    @PostMapping("/{id}/delete")
    public String deleteContractPost(@PathVariable Long id) {
        Contract contract = contractManager.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Contract ID"));

        contractManager.deleteContract(contract);

        return "redirect:/admin/contracts";
    }
}