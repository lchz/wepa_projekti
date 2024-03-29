package projekti;

import projekti.domain.Account;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Profile({"production", "default", "test"})
public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByFirstnameAndFamilyname(String firstname, String familyname);

    List<Account> findByFirstname(String firstname);

    List<Account> findByFamilyname(String familyname);

    Account findByUsername(String username);

    Account findBySignal(String signal);
}
