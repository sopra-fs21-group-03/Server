package ch.uzh.ifi.hase.soprafs21.rest.mapper;

import ch.uzh.ifi.hase.soprafs21.entity.GameEntity;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g., UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for creating information (POST).
 */
@Mapper
public interface DTOMapper {

    DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

    @Mapping(source = "password", target = "password")
    @Mapping(source = "username", target = "username")
    User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "status", target = "status")
    UserGetDTO convertEntityToUserGetDTO(User user);

    @Mapping(source = "token", target = "token")
    User convertUserPutDTOtoEntity(UserPutDTO userPutDTO);

    @Mapping(source = "gameName", target = "gameName")
    @Mapping(source = "round", target = "round")
    @Mapping(source = "river", target = "river")
    @Mapping(source = "pot", target = "pot")
    @Mapping(source = "onTurn", target = "onTurn")
    @Mapping(source = "playersInTurnOrder", target = "players")
    GameGetDTO convertEntityToGameGetDTO(GameEntity gameEntity);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "money", target = "money")
    @Mapping(source = "blind", target = "blind")
    OpponentInGameGetDTO convertEntityToOpponentInGameGetDTO(User opponent);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "cards", target = "cards")
    @Mapping(source = "money", target = "money")
    @Mapping(source = "blind", target = "blind")
    PlayerInGameGetDTO convertEntityToPlayerInGameGetDTO(User player);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "gameName", target = "name")
    @Mapping(source = "playerCount", target = "playerCount")
    @Mapping(source = "inGame", target = "inGame")
    LobbyGetDTO convertEntityToLobbyGetDTO(GameEntity gameEntity);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "gamestatus", target = "readyStatus")
    PlayerInLobbyGetDTO convertEntityToPlayerInLobbyGetDTO(User player);


    @Mapping(source = "gameName", target = "name")
    @Mapping(source = "lobbyplayers", target = "players")
    @Mapping(source = "gameCanStart", target = "gameCanStart")
    SpecificLobbyGetDTO convertEntityToSpecificLobbyGetDTO(GameEntity gameEntity);
}
