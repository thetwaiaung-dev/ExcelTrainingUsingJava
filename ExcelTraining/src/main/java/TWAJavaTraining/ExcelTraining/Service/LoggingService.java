package TWAJavaTraining.ExcelTraining.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LoggingService {

    private static final Logger logger = LoggerFactory.getLogger(LoggingService.class);

    public void InfoLog(String message) {
        logger.info(message);
    }

    public void ErrorLog(String message) {
        logger.error(message);
    }

}
