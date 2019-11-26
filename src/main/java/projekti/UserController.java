package projekti;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PictureRepository pictureRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private FollowingMessageRepository msgFRepository;
    

/*    // Get profile picture
    @GetMapping(path = "/{userId}")
    public ResponseEntity<byte[]> viewProfilePicture(@PathVariable Long userId) {
        Picture picture = this.userRepository.getOne(userId).getProfilePic();
        
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(picture.getMediaType()));
        headers.setContentLength(picture.getSize());
        
        return new ResponseEntity<>(picture.getContent(), headers, HttpStatus.CREATED);
    } */

// Get profile picture
    @GetMapping(path = "/{userId}", produces = "image/*")
    @ResponseBody
    public byte[] get(@PathVariable Long userId) {
        return this.userRepository.getOne(userId).getProfilePic().getContent();	
    }
    
    // Get user's name, messages, followings, followers, following messages
    @GetMapping("/{userId}")
    public String userHome(Model model, @PathVariable Long userId) {
        User user = this.userRepository.getOne(userId);
        List<Message> messages = user.getMessages();
        
        List<Followingship> followings = user.getFollowings();
        List<Followership> followers = user.getFollowers();
        
        model.addAttribute("user", user);
        model.addAttribute("messages", messages); 
        model.addAttribute("followings", followings);
        model.addAttribute("followers", followers);
        
        Pageable pageable = PageRequest.of(0, 25, Sort.by("time").descending());
        List<FollowingMessage> msgF = this.msgFRepository.findByUser(user, pageable);
        
        model.addAttribute("msgFList", msgF);
        
        return "user";
    }
 
    // post a new message
    @PostMapping("/{userId}")
    public String saveNewMessage(@RequestParam String content, @PathVariable Long userId) {
        User user = this.userRepository.getOne(userId);
        
        Message m = new Message();
        m.setComments(new ArrayList<>());
        m.setContent(content);
        m.setTime(LocalDateTime.now());
        m.setLikes(0);
        m.setUser(user);

        List<FollowingMessage> userMsgF = new ArrayList<>();
        for(Followership f : user.getFollowers()) {
            FollowingMessage msgF = new FollowingMessage();
            msgF.setContent(content);
            msgF.setTime(m.getTime());
            msgF.setWriterIdentity(userId);
            msgF.setWriterFamilyname(user.getFamilyname());
            msgF.setWriterFirstname(user.getFirstname());
            
            User person = this.userRepository.getOne(f.getFollower());
            msgF.setUser(person);
            this.msgFRepository.save(msgF);
        }
        
        
        this.messageRepository.save(m);
        
        return "redirect:/{userId}";
    }
    
    // show a comment to a message
    @GetMapping("/{userId}/messages/{messageId}/comment")
    public String addComment(Model model, @PathVariable Long userId, @PathVariable Long messageId) {
        model.addAttribute("user", this.userRepository.getOne(userId));
        model.addAttribute("message", this.messageRepository.getOne(messageId));
        return "comment";
    }
    
    // Now the messages are shown only on the userId's wall.
    // Which means when userId comment on followers' messages, it is ok to get userId as the write
    // If the comment is writing on userId's profile page, then it would be a problem.
    // Better way is to get writerId from the registered info. 
    // Correct this later!!!!!!!!!!!!!!!!!!!!
    // post a comment to the message
    @PostMapping("/{userId}/messages/{messageId}/comment") 
    public String postComment(Model model, @RequestParam String comment, @PathVariable Long userId, @PathVariable Long messageId) {
        Comment c = new Comment();
        Message m = this.messageRepository.getOne(messageId);
        User w = this.userRepository.getOne(userId);
        User mp = this.userRepository.getOne(userId);
        
        c.setContent(comment);
        c.setDate(LocalDate.now());
        c.setTime(LocalTime.now());
        c.setMessage(m);
        c.setWriter(w);
        c.setMessagePoster(mp);
      
        this.commentRepository.save(c);
        
        return "redirect:/{userId}";
    }
    
    
    // likes of a message add up 

}
