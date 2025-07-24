package akilsuh.proj.dao;

import akilsuh.proj.entity.ChildToParent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChildToParentDAO extends JpaRepository<ChildToParent, Long> {
    List<ChildToParent> findChildToParentByParentTelegramUserId(Long id);
    List<ChildToParent> findChildToParentByChildTelegramUserId(Long id);
}
