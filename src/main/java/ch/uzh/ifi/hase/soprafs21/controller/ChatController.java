package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.game.protocol.ProtocolElement;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs21.service.ChatService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class ChatController {
    private final ChatService chatService;

    ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/games/{gameId}/{userId}/chats")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<ProtocolElement> getChat(@PathVariable Long gameId, @PathVariable Long userId, @RequestHeader(value="Authorization") String token) {
        var user = chatService.getUserInGameById(gameId, userId);
        if(!(token.equals(user.getToken()))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authorized to see this chat");
        }
        return chatService.getProtocol(gameId);
    }

    @PutMapping("/games/{gameId}/{userId}/chats")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void userChats(@PathVariable Long gameId, @PathVariable Long userId, @RequestBody UserPutDTO userPutDTO){
        String chatMessage = userPutDTO.getMessage();
        var checkerUserInput = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);
        var user = chatService.getUserInGameById(gameId, userId);
        if(!(checkerUserInput.getToken().equals(user.getToken()))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authorized to send a chat message");
        } else{
            var game = chatService.findGameEntity(gameId);
            chatService.addChatMessage(game, user, chatMessage);
        }


    }


}
