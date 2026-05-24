package com.nextdate.backend;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

// Verificar la estructura del monorepo y que las dependencias entre modulos sean las correctas  
public class ModulithStructureTests {
    
    @Test
    void verifyModulithStructure() {
        
        // Analiza todos los modulos y sus dependencias
        ApplicationModules modules = ApplicationModules.of(BackendApplication.class);
        
        // Imprimir los modulos detectados
        System.out.println(modules);

        // Verifica que las dependencias entre modulos sean las correctas  
        modules.verify();
    }
}
    