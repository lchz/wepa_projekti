
package projekti;

import projekti.domain.Comment;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long>{
    List<Comment> findByMessageIdentity(Long messageId, Pageable page);
    void deleteByMessageIdentity(Long messageId);
}
