
package com.nexo.nexoeducativo.models.dto.request;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MateriaView implements Serializable {
    private String nombre;
    private String nombreProfesor;
    private String apellidoProfesor;
    
}
