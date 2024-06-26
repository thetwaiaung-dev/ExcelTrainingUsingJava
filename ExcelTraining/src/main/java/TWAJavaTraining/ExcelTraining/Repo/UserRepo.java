package TWAJavaTraining.ExcelTraining.Repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import TWAJavaTraining.ExcelTraining.Models.User;

public interface UserRepo extends JpaRepository<User,Short>{

    List<User> findAllByOrderByAddress();
}
