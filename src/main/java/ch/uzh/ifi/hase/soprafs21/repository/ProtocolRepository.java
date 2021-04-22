package ch.uzh.ifi.hase.soprafs21.repository;

import ch.uzh.ifi.hase.soprafs21.game.protocol.ProtocolElement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("protocolRepository")
public interface ProtocolRepository extends JpaRepository<ProtocolElement, Long> {

}