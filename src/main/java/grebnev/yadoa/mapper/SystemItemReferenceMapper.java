package grebnev.yadoa.mapper;

import grebnev.yadoa.service.SystemItemType;
import org.springframework.stereotype.Component;

@Component
public class SystemItemReferenceMapper {
    SystemItemType stringToType(String s) {
        return SystemItemType.valueOf(s);
    }
}
