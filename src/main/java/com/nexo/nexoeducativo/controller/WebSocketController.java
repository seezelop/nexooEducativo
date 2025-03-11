package com.nexo.nexoeducativo.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.handler.annotation.Payload;
import com.nexo.nexoeducativo.models.dto.request.Mensaje;
import java.security.Principal;
import org.springframework.messaging.simp.annotation.SendToUser;

@Controller
public class WebSocketController {

    @MessageMapping("/enviar/mensajePrivado")
    @SendToUser("/privado") // Envía el mensaje solo al destinatario
    public Mensaje enviarMensajePrivado(@Payload Mensaje mensaje, Principal principal) {
        System.out.println("Mensaje privado enviado por: " + principal.getName());
        return mensaje;
    }
}