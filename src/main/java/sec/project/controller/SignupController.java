package sec.project.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sec.project.domain.Signup;
import sec.project.repository.SignupRepository;

@Controller
public class SignupController {

    @Autowired
    private SignupRepository signupRepository;

    @RequestMapping("/")
    public String home(Model model) {
        model.addAttribute("text", "Hello World!");
        return "index";
    }

    @RequestMapping("/list")
    public String listPeople(Model model, @RequestParam(defaultValue = "false") String admin) {
      List<String> names = new ArrayList<>();
      if (admin.equals("true")) {
        for (Signup signup : signupRepository.findAll()) {
          names.add(signup.getName() + ", " + signup.getAddress());
        }
      } else {
        names.add("Error: Permission denied (admin=false)!");
      }
      model.addAttribute("list", names);
      return "list";
    }

    @RequestMapping("*")
    public String defaultMapping() {
        return "redirect:/";
    }

    @RequestMapping(value = "/form", method = RequestMethod.GET)
    public String loadForm() {
        return "form";
    }

    @RequestMapping(value = "/form", method = RequestMethod.POST)
    public String submitForm(@RequestParam String name, @RequestParam String address) {
        signupRepository.save(new Signup(name, address));
        return "done";
    }

}
