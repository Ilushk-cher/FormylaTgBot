package akilsuh.proj.service.commands.usersWithStates;

import akilsuh.proj.entity.AppUser;
import akilsuh.proj.entity.ChildToParent;
import akilsuh.proj.service.enums.states.AddChildStates;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
public class AddChildUser {
    private AppUser user;
    private AddChildStates state;
    private ChildToParent childToParent;
    private List<AppUser> students;

    private String lastName;
    private int classNum;
    private String name;

    public void filterByClassNum(int classNum) {
        students = students.stream().filter(appUser -> appUser.getClassNum() == classNum).toList();
    }

    public List<AppUser> filterByLastName() {
        return students.stream().filter(appUser -> appUser.getLastName().equals(lastName)).toList();
    }

    public void filterByName(String name) {
        students = students.stream().filter(appUser -> appUser.getFirstName().equals(name)).toList();
    }

    public List<Integer> getUniqueClassNum() {
        return filterByLastName().stream().map(AppUser::getClassNum).distinct().toList();
    }
}
