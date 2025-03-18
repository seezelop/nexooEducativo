package com.nexo.nexoeducativo.repository;

import com.nexo.nexoeducativo.models.dto.request.MensajeDTO;
import com.nexo.nexoeducativo.models.dto.request.MensajeView;
import com.nexo.nexoeducativo.models.entities.Mensaje;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Martina
 */
@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Integer> {
    @Modifying
    @Query("UPDATE Mensaje m SET m.contenido =:contenido WHERE m.idMensaje= :idMensaje")
    void updateContenido(@Param(value="idMensaje") Integer idMensaje,
            @Param(value="contenido") String contenido);
    
    Optional<Mensaje> findByIdMensaje(Integer idMensaje);
    
    @Query("SELECT new com.nexo.nexoeducativo.models.dto.request.MensajeView (m.idMensaje, m.contenido, m.fecha, "
            + "um.remitente.nombre,um.remitente.apellido) FROM Mensaje m"
            + " JOIN UsuarioMensaje um ON m.idMensaje=um.mensajeIdMensaje "
            + " WHERE um.remitente.idUsuario= :idUsuario AND LOWER(m.contenido) NOT LIKE '%cbu%' "
            + "AND LOWER(m.contenido) NOT LIKE '%cvu%'")
    List<MensajeView> mensajes (@Param("idUsuario") Integer idUsuario);
    
    @Query("SELECT new com.nexo.nexoeducativo.models.dto.request.MensajeDTO(m.idMensaje, m.contenido, um.remitente.mail) " +
       "FROM Mensaje m " +
       "JOIN UsuarioMensaje um ON um.mensajeIdMensaje = m.idMensaje " +
       "WHERE um.destinatario.mail = :destinatarioMail AND um.remitente.mail = :remitenteMail")
    List<MensajeDTO> obtenerMensajesPorDestinatario(@Param("destinatarioMail") String destinatarioMail, @Param("remitenteMail") String remitenteMail);
}
