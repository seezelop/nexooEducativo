package com.nexo.nexoeducativo.service;

import com.nexo.nexoeducativo.exception.CursoNotFound;
import com.nexo.nexoeducativo.exception.FormatoIncorrectoException;
import com.nexo.nexoeducativo.exception.RolNotFound;
import com.nexo.nexoeducativo.exception.UsuarioNotFoundException;
import com.nexo.nexoeducativo.models.dto.request.FormContactoDTO;
import com.nexo.nexoeducativo.models.dto.request.MensajeDTO;
import com.nexo.nexoeducativo.models.dto.request.MensajeIndividualDTO;
import com.nexo.nexoeducativo.models.dto.request.MensajeView;
import com.nexo.nexoeducativo.models.dto.request.NombreCompletoDTO;
import com.nexo.nexoeducativo.models.dto.request.NovedadesDTO;
import com.nexo.nexoeducativo.models.entities.Curso;
import com.nexo.nexoeducativo.models.entities.CursoMensaje;
import com.nexo.nexoeducativo.models.entities.Escuela;
import com.nexo.nexoeducativo.models.entities.Mensaje;
import com.nexo.nexoeducativo.models.entities.Rol;
import com.nexo.nexoeducativo.models.entities.Usuario;
import com.nexo.nexoeducativo.models.entities.UsuarioMensaje;
import com.nexo.nexoeducativo.repository.CursoMensajeRepository;
import com.nexo.nexoeducativo.repository.CursoRepository;
import com.nexo.nexoeducativo.repository.MensajeRepository;
import com.nexo.nexoeducativo.repository.RolRepository;
import com.nexo.nexoeducativo.repository.UsuarioMensajeRepository;
import com.nexo.nexoeducativo.repository.UsuarioRepository;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Martina
 */
@Service
public class MensajeService {
    @Autowired
    private MensajeRepository mensajeRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private RolRepository rolRepository;
    
    @Autowired
    private CursoRepository cursoRepository;
    
    @Autowired
    private CursoMensajeRepository cmRepository;
    
    @Autowired
    private UsuarioMensajeRepository umRepository;
    LocalDateTime hoy = LocalDateTime.now();
     DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    
    
    public void altaInfoPagoMensaje(String info, Escuela escuela, Usuario remitente){
        Mensaje infoPago = new Mensaje();
                
        int cantLetras=info.length();
        if(cantLetras>255 || cantLetras<10){
            throw new FormatoIncorrectoException("El mensaje debe tener entre 10 y 255 caracteres");
        }   
        infoPago.setContenido(info);
        mensajeRepository.save(infoPago);
        
        //obtengo el id del rol del padre
        Rol rol=rolRepository.findByNombre("padre").orElseThrow(
                ()-> new RolNotFound("No existe ese rol en la abse de datos"));
        
        List<NombreCompletoDTO> padres=usuarioRepository.obtenerInfoUsuario(rol, escuela);
        asignarMensajeAPadres(infoPago, padres, remitente);
    }
    
    public void asignarMensajeAPadres (Mensaje m, List<NombreCompletoDTO> padres, Usuario remitente){
        for(NombreCompletoDTO n:padres){
            //obtengo cada padre
            Usuario u=usuarioRepository.findById(n.getId_usuario()).orElseThrow(
                    ()-> new UsuarioNotFoundException("No existe el id "+n.getId_usuario()));
            
            UsuarioMensaje um=new UsuarioMensaje();
            um.setMensajeIdMensaje(m);
            um.setRemitente(remitente);
            um.setDestinatario(u);
            umRepository.save(um);
            
            
        }
        
    }
    
    @Transactional
     public void editarMensaje(String nuevoMensaje, Escuela escuela, Rol r){
                  
        int cantLetras=nuevoMensaje.length();
        if(cantLetras>255 || cantLetras<10){
            throw new FormatoIncorrectoException("El mensaje debe tener entre 10 y 255 caracteres");
        }
        
         Mensaje infoPago =umRepository.obtenerMensajesEscuela(escuela, r.getIdRol(), "cbu");
         if(infoPago==null){
             throw new UsuarioNotFoundException("NO HAY NINGUN MENSAJE RELACIONADO A LA INFO DE PAGO");
         }
        infoPago.setContenido(nuevoMensaje);
       mensajeRepository.updateContenido(infoPago.getIdMensaje(), nuevoMensaje);
      
        }
     
     /*public Mensaje altaMensaje(MensajeGrupalDTO mensaje){
          Mensaje m = new Mensaje();
          m.setContenido(mensaje.getContenido());
        if (mensaje.getArchivo().isEmpty() || mensaje.getArchivo().isBlank()) {
            m.setArchivo(mensaje.getArchivo());
        }
        String fechaNueva = hoy.format(formato);
        LocalDateTime actual = LocalDateTime.parse(fechaNueva, formato);
        Date fechaDate = Date.from(actual.atZone(ZoneId.systemDefault()).toInstant());
        m.setFecha(fechaDate);
        
         m = mensajeRepository.save(m);
         
          Usuario comunicador=usuarioRepository.findByMail(mensaje.getComunicador()).orElseThrow(
                    ()-> new UsuarioNotFoundException("No existe el destinatario"));
          
          UsuarioMensaje usuarioMensaje = new UsuarioMensaje();
        usuarioMensaje.setMensajeIdMensaje(m);
        usuarioMensaje.setUsuarioIdUsuario(comunicador);
        umRepository.save(usuarioMensaje);
          
         for (DesplegableChatView usuario : mensaje.getGrupoUsuarios()) {
             //buscar cada usuario dentro del grupo de destinatarios
             Usuario destinatario = usuarioRepository.findByMail(usuario.getMail()).orElseThrow(
                     () -> new UsuarioNotFoundException("No existe el destinatario"));

              // tabla intermedia
             UsuarioMensaje usuarioMensajeDestinatario = new UsuarioMensaje();
             usuarioMensajeDestinatario.setMensajeIdMensaje(m);
             usuarioMensajeDestinatario.setUsuarioIdUsuario(destinatario);
             umRepository.save(usuarioMensajeDestinatario);

         }
        return m;
         
     }*/
     
     public Mensaje buscarMensaje(Integer id){
         Optional<Mensaje> mensaje= mensajeRepository.findByIdMensaje(id);
         Mensaje m=mensaje.get();
         return m;
         
     }
     
     public List<MensajeView> obtenerMensajes(String mail){
         Usuario u=usuarioRepository.findByMail(mail).orElseThrow(
                 ()-> new UsuarioNotFoundException("No existe el usuario"));
          List<MensajeView> obtenerMensajes=mensajeRepository.mensajes(u.getIdUsuario());
          return obtenerMensajes;
     }
     
     public List<MensajeDTO> obtenerMensajesDesdeDestinatario(Usuario remitente, Usuario destinatario){
         List<MensajeDTO> mensajes= mensajeRepository.obtenerMensajesEntreUsuarios(destinatario.getMail(), remitente.getMail());
         return mensajes;
     }
     
    public Mensaje altaMensajeIndividual(MensajeIndividualDTO mensaje, String comunicadorI) {
        Mensaje m = new Mensaje();
        if(mensaje.getContenido()==null){
            throw new UsuarioNotFoundException("El mensja ellego vacio "+mensaje.toString());
        }
        m.setContenido(mensaje.getContenido());
       
        String fechaNueva = hoy.format(formato);
        LocalDateTime actual = LocalDateTime.parse(fechaNueva, formato);
        Date fechaDate = Date.from(actual.atZone(ZoneId.systemDefault()).toInstant());
        m.setFecha(fechaDate);

        m = mensajeRepository.save(m);

        Usuario comunicador = usuarioRepository.findByMail(comunicadorI).orElseThrow(
                () -> new UsuarioNotFoundException("No existe el comunicador"));

        Usuario destinatario = usuarioRepository.findByMail(mensaje.getDestinatario()).orElseThrow(
                () -> new UsuarioNotFoundException("No existe el destinatario"));

        UsuarioMensaje usuarioMensaje = new UsuarioMensaje();
        usuarioMensaje.setMensajeIdMensaje(m);
        usuarioMensaje.setRemitente(comunicador);
        usuarioMensaje.setDestinatario(destinatario);
        umRepository.save(usuarioMensaje);
        return m;

    }
    
    public Mensaje contactarDesdeForm(FormContactoDTO contenido){
        Mensaje m=new Mensaje();
        m.setFecha(contenido.getFecha());
        m.setContenido(contenido.getContenido());
        return mensajeRepository.save(m);
    }
    
    @Transactional
    public boolean editarMensaje(Integer idMensaje, String mensaje, String mailRemitente){
        boolean sePudo=false;
        if(mensaje.length()>255 || mensaje.length()<2){
            throw new FormatoIncorrectoException("El mensaje debe tener entre 2 y 255 caracteres");
        }else{
            Mensaje m=mensajeRepository.findById(idMensaje).orElseThrow(
                    ()-> new UsuarioNotFoundException("No existe el mensaje"));
            Optional<UsuarioMensaje> um=umRepository.findByMensajeIdMensaje(m);
     
            if(um.isPresent()){
                UsuarioMensaje buscarRemitente=um.get();
                //ver si el mail del remitente coincide con el usuario autenticado
            //si coincide, se puede editar el mensaje sino, no
                if(buscarRemitente.getRemitente().getMail().equals(mailRemitente)){
                    mensajeRepository.updateContenido(idMensaje, mensaje);
                    sePudo=true;
                }
            }
        }
        return sePudo;
    }
    
    @Transactional
    public void borrarMensaje(Integer idMensaje){
        mensajeRepository.deleteById(idMensaje);
    }
    
    public String infoPago(Integer padre){
        return mensajeRepository.infoPago(padre);
    }
    
    public Mensaje altaNovedades(NovedadesDTO mensaje, Integer idCurso) {
        Mensaje m = new Mensaje();
        if(mensaje.getContenido()==null){
            throw new UsuarioNotFoundException("El mensja ellego vacio "+mensaje.toString());
        }
        m.setContenido(mensaje.getContenido());
       
        String fechaNueva = hoy.format(formato);
        LocalDateTime actual = LocalDateTime.parse(fechaNueva, formato);
        Date fechaDate = Date.from(actual.atZone(ZoneId.systemDefault()).toInstant());
        m.setFecha(fechaDate);

        m = mensajeRepository.save(m);

        Curso c=cursoRepository.findById(idCurso).orElseThrow(
                ()-> new CursoNotFound("No existe el curso"));

        CursoMensaje um=new CursoMensaje();
        um.setCursoIdCurso(c);
        um.setMensajeIdMensaje(m);
        cmRepository.save(um);
        return m;

    }
    
    public List<NovedadesDTO> verNovedades (Integer idCurso){
        return mensajeRepository.verNovedades(idCurso);
    }
    
        
    }


