package TWAJavaTraining.ExcelTraining.Controllers;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import TWAJavaTraining.ExcelTraining.Models.User;
import TWAJavaTraining.ExcelTraining.Repo.UserRepo;
import TWAJavaTraining.ExcelTraining.Reports.UserExcelReporter;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;

/**
 * UserController
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepo userRepo;

    @GetMapping
    public String GetList(Model model) {

        List<User> userList = userRepo.findAll();
        model.addAttribute("userList", userList);

        return "user_list";
    }

    @GetMapping("/export/excel")
    public void ExportExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerkey = "Content-Disposition";
        String headerValue = "attachment; filename=userList_" + currentDateTime + ".xlsx";
        response.setHeader(headerkey, headerValue);

        List<User> userList = userRepo.findAll();
        UserExcelReporter excelReporter = new UserExcelReporter(userList);
        excelReporter.export(response);
    }

}