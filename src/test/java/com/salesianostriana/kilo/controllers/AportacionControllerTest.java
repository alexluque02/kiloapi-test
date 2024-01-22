package com.salesianostriana.kilo.controllers;

import com.salesianostriana.kilo.dtos.aportaciones.AportacionesReponseDTO;
import com.salesianostriana.kilo.entities.*;
import com.salesianostriana.kilo.entities.keys.DetalleAportacionPK;
import com.salesianostriana.kilo.services.AportacionService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AportacionController.class)
class AportacionControllerTest {

    @Autowired
    MockMvc mockMvc;

    //@Autowired
    //ObjectMapper objectMapper;

    @MockBean
    AportacionService aportacionService;

    @Test
    void getAllAportaciones() throws Exception{
        Clase clase = new Clase();
        Aportacion aportacion = new Aportacion(1L, LocalDate.now(), clase, null);
        DetalleAportacion detalle = new DetalleAportacion();
        DetalleAportacionPK pk = new DetalleAportacionPK(1L, aportacion.getId());
        detalle.setAportacion(aportacion);
        detalle.setDetalleAportacionPK(pk);
        detalle.setCantidadKg(20.0);
        aportacion.setDetalleAportaciones(List.of(detalle));
        TipoAlimento tipoAlimento = new TipoAlimento(1L, "Lentejas", List.of(detalle), null);
        KilosDisponibles kilosDisponibles = new KilosDisponibles(tipoAlimento, 1L, 10.0);
        tipoAlimento.setKilosDisponibles(kilosDisponibles);
        detalle.setTipoAlimento(tipoAlimento);
        List<Aportacion> aportacions = new ArrayList<>();
        aportacions.add(aportacion);

        Mockito.when(aportacionService.findAllAportaciones()).thenReturn(aportacions.stream().map(AportacionesReponseDTO::of).toList());

        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/aportacion/").contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
                //.andExpect(jsonPath("$.clase", Matchers.is(c1)));
        //.andExpect(jsonPath("$", hasSize(1)));
        //.andExpect(jsonPath("$[2].name", is("customer3")));
    }

    @Test
    void getDetallesAportacion() throws Exception {
        Clase c1 = new Clase();
        Aportacion aportacion = new Aportacion();
        aportacion.setClase(c1);
        DetalleAportacion detalleAportacion = new DetalleAportacion();
        detalleAportacion.setAportacion(aportacion);

        List<Aportacion> aportacions = List.of(
            aportacion
        );

        Mockito.when(aportacionService.findById(1L)).thenReturn(Optional.of(aportacions.get(0)));

        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/aportacion/1").contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.clase", Matchers.is(c1)));
                //.andExpect(jsonPath("$", hasSize(1)));
                //.andExpect(jsonPath("$[2].name", is("customer3")));
    }
    @Test
    void getDetallesAportacionNotFound() throws Exception{
        Mockito.when(aportacionService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(
                MockMvcRequestBuilders.get("/aportacion/1").contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound());
    }

}