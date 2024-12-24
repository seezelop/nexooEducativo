package com.nexo.nexoeducativo.repository;


import com.nexo.nexoeducativo.models.dto.request.InfoUsuarioSegunRolDTO;
import com.nexo.nexoeducativo.models.dto.request.JefeColegioModificacionDTO;
import com.nexo.nexoeducativo.models.dto.request.NombreCompletoDTO;
import com.nexo.nexoeducativo.models.dto.request.UsuarioView;
import com.nexo.nexoeducativo.models.entities.Curso;
import com.nexo.nexoeducativo.models.entities.Rol;
import com.nexo.nexoeducativo.models.entities.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PathVariable;

/**
 *
 * @author Martina
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer>{
       final Logger LOGGER = LoggerFactory.getLogger(UsuarioRepository.class);
    
    boolean existsByDni(int dni);

    boolean existsByMail(String mail);

    boolean existsByRolidrolAndIdUsuario(Rol rolidrol, Integer idUsuario);
    
    //@Query("SELECT")
    boolean existsByIdUsuarioAndRolidrol(int idUsuario, Rol rolidrol );

    //@Query("SELECT u.rol FROM Usuario u WHERE u.idUsuario = :idUsuario")
   @Query("SELECT u.rolidrol FROM Usuario u WHERE u.idUsuario = :idUsuario")
Rol findRolidrolByIdUsuario(@Param("idUsuario") Integer idUsuario);

    
    
    
    //boolean existsByRolidrolAndIdUsuario(Rol rolidrol, Integer idUsuario);
    //EL PROBLEMA ES QUE EL MAIL NO ESTA LLEGANDO BIEN AL METODO, DESDE EL CONTROLADOR FUNCIONA BIEN PERO NO LLEGA ACA

    //@Query(value = "SELECT * FROM Usuario u WHERE u.mail=:mail", nativeQuery = true)
    //Usuario findByMail(String mail);

    @Query(value = "SELECT u.nombre, u.apellido FROM Usuario u WHERE id_usuario = :idUsuario", nativeQuery = true)
    List<NombreCompletoDTO> getFullName(@Param("idUsuario") int idUsuario);

    @Query(value = "SELECT u.id_usuario, u.nombre, u.apellido FROM usuario u LEFT JOIN escuela_usuario eu ON eu.usuario_id_usuario=u.id_usuario WHERE u.Rol_id_rol=2 and eu.escuela_id_escuela IS NULL", nativeQuery = true)
    List<NombreCompletoDTO> getJefeColegioWithoutSchool();
    
    //PARA EL LOGIN
     Optional<Usuario> findByMail(String mail);
     
     
     
     //para chequear usuario logueado
      Usuario getUsuarioByMail(String mail); 
      
      //para obtener todos los usuario segun el rol
      @Query(value="SELECT new com.nexo.nexoeducativo.models.dto.request.InfoUsuarioSegunRolDTO(u.idUsuario, u.nombre, u.apellido, u.dni) FROM Usuario u INNER JOIN Rol r ON r.idRol=u.rolidrol WHERE r.nombre = :nombre")
      List<InfoUsuarioSegunRolDTO>getUsuarioByRol(@PathVariable(value = "nombre") String nombre);
       
      //para obtener nombre y apellido del usuario logueado
          @Query(value = "SELECT u.nombre, u.apellido FROM Usuario u WHERE mail = :mail", nativeQuery = true)
          NombreCompletoDTO getFullName(String mail);
          
      Usuario findNombreAndApellidoByIdUsuario(Integer idUsuario);
      
      @Query("SELECT new com.nexo.nexoeducativo.models.dto.request.UsuarioView(u.nombre, u.apellido) FROM Usuario u "
              + "JOIN CursoUsuario cu ON u.idUsuario=cu.usuarioIdUsuario"
              + " WHERE cu.cursoIdCurso= :curso and u.rolidrol=7")
      
      List<UsuarioView> infoAlumnos(@Param("curso") Curso curso);
      
       @Query("SELECT new com.nexo.nexoeducativo.models.dto.request.UsuarioView(u.nombre, u.apellido) FROM Usuario u "
              + "JOIN CursoUsuario cu ON u.idUsuario=cu.usuarioIdUsuario"
              + " WHERE cu.cursoIdCurso= :curso and u.rolidrol=4")
      
      UsuarioView infoPreceptor(@Param("curso") Curso curso);
      
      //para mostrar numero y division de los cursos de un colegio
      @Query("SELECT eu.escuela_id_escuela FROM Usuario u INNER JOIN escuela_usuario eu ON u.id_usuario=eu.usuario_id_usuario"
              + "WHERE u.Rol_id_rol=2 and u.mail= :mail")
      int buscarEscuelaDelJefeColegio(@Param("mail") String mail);
          
          
          
          
       
       
}
