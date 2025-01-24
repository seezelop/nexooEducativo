package com.nexo.nexoeducativo.service;

import com.nexo.nexoeducativo.exception.CursoNotFound;
import com.nexo.nexoeducativo.exception.EscuelaNotFoundException;
import com.nexo.nexoeducativo.exception.HoraInvalidatedexception;
import com.nexo.nexoeducativo.exception.MateriaExistingException;
import com.nexo.nexoeducativo.exception.UsuarioNotAuthorizedException;
import com.nexo.nexoeducativo.exception.UsuarioNotFoundException;
import com.nexo.nexoeducativo.models.dto.request.DesplegableMateriaView;
import com.nexo.nexoeducativo.models.dto.request.MateriaDTO;
import com.nexo.nexoeducativo.models.dto.request.MateriaView;
import com.nexo.nexoeducativo.models.entities.Curso;
import com.nexo.nexoeducativo.models.entities.Escuela;
import com.nexo.nexoeducativo.models.entities.Materia;
import com.nexo.nexoeducativo.models.entities.MateriaCurso;
import com.nexo.nexoeducativo.models.entities.MateriaEscuela;
import com.nexo.nexoeducativo.models.entities.Rol;
import com.nexo.nexoeducativo.models.entities.Usuario;
import com.nexo.nexoeducativo.repository.CursoEscuelaRepository;
import com.nexo.nexoeducativo.repository.CursoRepository;
import com.nexo.nexoeducativo.repository.EscuelaRepository;
import com.nexo.nexoeducativo.repository.MateriaCursoRepository;
import com.nexo.nexoeducativo.repository.MateriaEscuelaRepository;
import com.nexo.nexoeducativo.repository.MateriaRepository;
import com.nexo.nexoeducativo.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MateriaService {
    
    @Autowired
     private MateriaRepository materiaRepository;
    
    @Autowired
    private MateriaCursoRepository materiaCursoRepository;
    
    @Autowired
    private MateriaEscuelaRepository materiaEscuelaRepository;
    
    @Autowired
    private CursoRepository cursoRepository;
    
    @Autowired
    private EscuelaRepository escuelaRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private CursoEscuelaRepository cursoEscuelaRepository;
      private static final Logger LOGGER = LoggerFactory.getLogger(MateriaService.class);
    
    public void crearMateria(String nombre, Escuela e) {
        //inserto lo ingresado en el dto en las entidades correspondientes
        /*Curso c = cursoRepository.findById(m.getIdCurso())
                .orElseThrow(() -> new CursoNotFound("El curso no existe"));

        Escuela e = escuelaRepository.findById(m.getIdEscuela())
                .orElseThrow(() -> new EscuelaNotFoundException("La escuela no existe"));
        
        Usuario u=usuarioRepository.findById(m.getIdProfesor()).
                orElseThrow(() -> new UsuarioNotFoundException("El profesor no existe"));
        u.setIdUsuario(m.getIdProfesor());
        
        Rol rol = usuarioRepository.findRolidrolByIdUsuario(u.getIdUsuario());*/

        Materia materia = new Materia();
        materia.setNombre(nombre);
        //materia.setMateriaCursoList(materiaCursoList); FALTA ESTO
        //materia.setMateriaEscuelaList(materiaEscuelaList); FALTA ESTO

        MateriaEscuela me = new MateriaEscuela();
        me.setMateriaIdMateria(materia);
        me.setEscuelaIdEscuela(e);

       /* MateriaCurso mc = new MateriaCurso();
        mc.setCursoIdCurso(c);
        mc.setDia(m.getDia());
        mc.setHoraFin(m.getHoraFin());
        mc.setHoraInicio(m.getHoraInicio());
        mc.setMateriaIdMateria(materia);
        mc.setProfesor(u);*/
        //mc.setMateriaCursoMaterialList(materiaCursoMaterialList); DUDOSO DE SI VA O NO

        //ver si ya existe la materia a ingresar en esa escuela
        
        //si la escuela esta inactica
        if (cursoEscuelaRepository.existsByCursoIdCursoAndEscuelaIdEscuela(e.getIdEscuela()) == 0) {
            //excepcion de ejemplo, solo para comporobar si efectivamente funciona la query
            throw new CursoNotFound("La escuela esta inactiva");
        }
        
        //si el usuario es profesor
       
            //validar si ya existe una materia en ese mismo horario o que no se suponga. Ejemplo: biologia 1 12:00 - 13:00 2b lunes y jueves
        //NO ES VALIDO: biologia 1 12:30 a 13:30 2b lunes y jueves
   
                materiaRepository.save(materia);
                materiaEscuelaRepository.save(me);
                //materiaCursoRepository.save(mc);
            }

    
    
    @Transactional 
    public int borrarMateria(int idCurso, int idMateria){
        Curso cursoIdCurso=new Curso();
        cursoIdCurso.setIdCurso(idCurso);
        Materia materiaIdMateria=new Materia();
        materiaIdMateria.setIdMateria(idMateria);
        
        int seBorroCorrectamente=materiaCursoRepository.deleteByCursoIdCursoAndMateriaIdMateria(cursoIdCurso, materiaIdMateria);
        return seBorroCorrectamente;
    }
    
    public List<DesplegableMateriaView> verMaterias (Curso cursoIdCurso){
        List<DesplegableMateriaView> materias=materiaCursoRepository.verMaterias(cursoIdCurso);
        return materias;
    }
    
    public List<DesplegableMateriaView> mostrarMateriasProfe(Curso cursoIdCurso, Usuario profesor){
        List<DesplegableMateriaView> materias= materiaCursoRepository.findNombresMateriasPorCursoYProfesor(cursoIdCurso, profesor);
        return materias;
    }
    
    public List<String> verMateriasEscuela (Escuela escuelaIdEscuela){
        List<String> verMateriasEscuela=materiaEscuelaRepository.materiasSegunEscuela(escuelaIdEscuela);
        return verMateriasEscuela;
    }
    
    public List<DesplegableMateriaView> verMateriasSegunHijo(Integer id){
        Usuario usuarioIdUsuario=usuarioRepository.findById(id).orElseThrow(
        ()-> new UsuarioNotFoundException("No existe el hijo seleccionado"));
        List<DesplegableMateriaView> verMaterias=materiaRepository.materiasSegunHijo(usuarioIdUsuario);
        return verMaterias;
    }
    
}
