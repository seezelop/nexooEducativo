/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nexo.nexoeducativo.service;

import com.nexo.nexoeducativo.configuration.FailureHandler;
import com.nexo.nexoeducativo.exception.EscuelaNotFoundException;
import com.nexo.nexoeducativo.exception.UsuarioExistingException;
import com.nexo.nexoeducativo.models.dto.request.EscuelaDTO;
import com.nexo.nexoeducativo.models.dto.request.EscuelaModificacionDTO;
import com.nexo.nexoeducativo.models.dto.request.JefeColegioModificacionDTO;
import com.nexo.nexoeducativo.models.dto.request.NombreDireccionEscuelaDTO;
import com.nexo.nexoeducativo.models.entities.Escuela;
import com.nexo.nexoeducativo.models.entities.EscuelaUsuario;
import com.nexo.nexoeducativo.models.entities.Plan;
import com.nexo.nexoeducativo.models.entities.Usuario;
import com.nexo.nexoeducativo.repository.EscuelaRepository;
import com.nexo.nexoeducativo.repository.EscuelaUsuarioRepository;
import com.nexo.nexoeducativo.repository.PlanRepository;
import static com.nexo.nexoeducativo.service.UsuarioService.convertirSHA256;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Martina
 */
@Service
public class EscuelaService {

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private EscuelaRepository escuelaRepository;

    @Autowired
    private EscuelaUsuarioRepository escuelaUsuarioRepository;
    
     @Autowired
    private Validator validator;
     
     private static final Logger LOGGER = LoggerFactory.getLogger(EscuelaService.class);

    public void crearEscuela(EscuelaDTO e) {
        if (escuelaRepository.existsByDireccion(e.getDireccion())) {
            throw new EscuelaNotFoundException("La escuela ya existe en la plataforma");
        }

        if (!planRepository.existsById(e.getIdPlan())) {
            throw new IllegalArgumentException("El plan seleccionado no existe: " + e.getIdPlan());
        }

        Escuela escuela = new Escuela();
        escuela.setNombre(e.getNombre());
        escuela.setDireccion(e.getDireccion());
        escuela.setActivo(e.getActivo());
        escuela.setPlanIdPlan(planRepository.findById(e.getIdPlan()).orElseThrow());

        Usuario jefeColegio = new Usuario();
        jefeColegio.setIdUsuario(e.getJefeColegio());

        EscuelaUsuario escuelaUsuario = new EscuelaUsuario();
        escuelaUsuario.setEscuelaIdEscuela(escuela);
        escuelaUsuario.setUsuarioIdUsuario(jefeColegio);

        escuelaRepository.save(escuela);
        escuelaUsuarioRepository.save(escuelaUsuario);
    }

    public void borrarEscuela(int idEscuela) {
        escuelaRepository.deleteById(idEscuela);
    }

    public List<NombreDireccionEscuelaDTO> obtenerEscuelas() {
        return escuelaRepository.getInfoEscuelas();
    }
    
       public void validarElDto(EscuelaModificacionDTO j) {
        Set<ConstraintViolation<EscuelaModificacionDTO>> violaciones = validator.validate(j);
        if (!violaciones.isEmpty()) {
            throw new ConstraintViolationException(violaciones);
        }
        
    }
       
       public void actualizarCampos( EscuelaModificacionDTO dto, Escuela e, Plan p){
         if (dto.getNombre() != null) {
             e.setNombre(dto.getNombre());
         }
         
         if (dto.getDireccion() != null && !escuelaRepository.existsByDireccion(dto.getDireccion())) {
             e.setDireccion(dto.getDireccion());
             LOGGER.info("LA DIRECCION TRAIDA DEL DTO ES: "+dto.getDireccion());
             //LOGGER.info("LA DIRECCION TRAIDA DEL DTO ES: "+dto.getDireccion());
         }else if(dto.getDireccion()==null){
             
         }
         else{
             LOGGER.info("LA DIRECCION TRAIDA DEL DTO (else) ES: "+dto.getDireccion());
             throw new EscuelaNotFoundException("Ya existe una escuela registrada en esa direccion");
              
         }
        
         if (dto.getIdPlan()!=null) {
             p.setIdPlan(dto.getIdPlan());
             e.setPlanIdPlan(p);
         }
         
         if(dto.getJefeColegio()!=null){
             Usuario u=new Usuario(); //obtener el nuevo jefe colegio
             u.setIdUsuario(dto.getJefeColegio());
        
             EscuelaUsuario eu=new EscuelaUsuario();
              eu.setEscuelaIdEscuela(e);
              eu.setUsuarioIdUsuario(u);
             
         }
        
         if (dto.getActivo() != 0) {
             e.setActivo(dto.getActivo());
         }
         
     }
       
        @Transactional
    public EscuelaModificacionDTO actualizarEscuela(int id, EscuelaModificacionDTO e) {
    Escuela escuelaIngresada = escuelaRepository.findById(id)
            .orElseThrow(() -> new EscuelaNotFoundException("La escuela que se desea modificar no existe"));

        validarElDto(e);
        Plan p=new Plan();
        p.setIdPlan(e.getIdPlan());
        
        actualizarCampos(e,escuelaIngresada,p);
    Escuela actualizado= escuelaRepository.save(escuelaIngresada);
     //LOGGER.info("EL OBJETO ACTUALIZADO QUE SE VA A GUARDAR: "+actualizado.toString());
     return new EscuelaModificacionDTO (actualizado);
    }

}
