package akilsuh.proj.dao;

import akilsuh.proj.entity.AppUser;
import akilsuh.proj.enums.UserRole;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppUserDAO extends JpaRepository<AppUser, Long> {
    AppUser findAppUserByTelegramUserId(Long id);
    List<AppUser> findAppUserByRole(UserRole role);
}
