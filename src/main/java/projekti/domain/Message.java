package projekti.domain;

import java.time.*;
import java.util.*;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message extends AbstractPersistable<Long> {
    
    @ManyToOne
    private Account user;
    
    private String content;
    private LocalDateTime time;
    
    @OneToOne(mappedBy="message")
    private Picture picture;
    
    @OneToMany
    private List<Comment> comments = new ArrayList<>();
    
    @OneToMany(mappedBy="message")
    private List<ThumbUp> likes = new ArrayList<>();
    
}
